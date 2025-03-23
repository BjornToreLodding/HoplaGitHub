//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import Foundation

// MARK: - Hike Model
struct Hike: Codable, Identifiable {
    let id: String
    let name: String
    let pictureUrl: String
    let averageRating: Int
    let isFavorite: Bool
}


// MARK: - Filters for Hikes
enum HikeFilter: String, CaseIterable, Identifiable, Codable {
    case asphalt = "Asphalt"
    case gravel = "Gravel"
    case parking = "Parking"
    case forest = "Forest"
    case mountain = "Mountain"
    
    var id: String { self.rawValue }
}

struct HikeResponse: Codable {
    let trails: [Hike]  // Matches "trails" in JSON
    let pageNumber: Int
    let pageSize: Int
}



// MARK: - Filter bar Options
enum FilterOption: String, CaseIterable, Identifiable {
    case map
    case location
    case heart
    case star
    case arrow
    
    var id: String { self.rawValue }
    
    var systemImage: String {
        switch self {
        case .map: return "map"
        case .location: return "location"
        case .heart: return "heart"
        case .star: return "star"
        case .arrow: return "chevron.down"
        }
    }
}

// MARK: - Fetching Hikes from Backend
class HikeService {
    static let shared = HikeService()
    @Published var hikes: [Hike] = []
    
    private let baseURL = "https://hopla.onrender.com/trails/all"
    
    func fetchHikes(page: Int, completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            completion(.failure(NSError(domain: "", code: 401, userInfo: [NSLocalizedDescriptionKey: "Unauthorized - No Token"])))
            return
        }
        
        let urlString = "\(baseURL)?pageNumber=\(page)&pageSize=10"
        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Request error:", error.localizedDescription)
                completion(.failure(error))
                return
            }
            
            guard let data = data else {
                completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                return
            }
            
            do {
                // Print the raw JSON data to inspect it
                if let jsonString = String(data: data, encoding: .utf8) {
                    print("📜 Raw JSON Data:\n", jsonString)
                }
                
                // Decode the response into TrailsResponse
                let decodedResponse = try JSONDecoder().decode(HikeResponse.self, from: data)
                DispatchQueue.main.async {
                    completion(.success(decodedResponse)) // Pass the decoded response to the completion handler
                }
            } catch {
                print("❌ Error decoding hikes: \(error.localizedDescription)")
                completion(.failure(error)) // Pass error if decoding fails
            }
        }.resume()
    }
}


// MARK: - Main View
struct Hikes: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var selectedFilter: FilterOption = .map
    @State private var searchText: String = ""
    @State private var hikes: [Hike] = []
    @State private var isLoading = false
    @State private var currentPage = 1
    @State private var canLoadMore = true  // Track if more data is available
    
    var body: some View {
        VStack(spacing: 0) {
            filterBar
            searchBar
            
            if isLoading && hikes.isEmpty {
                ProgressView("Loading Hikes...")
            } else {
                ScrollView {
                    LazyVStack(spacing: 10) {
                        ForEach(hikes.indices, id: \.self) { index in
                            HikeCard(hike: hikes[index])
                                .onAppear {
                                    if index == hikes.count - 1 { // If last item is visible, load more
                                        loadMoreHikes()
                                    }
                                }
                        }
                        
                        if isLoading {
                            ProgressView("Loading more hikes...")
                        }
                    }
                }
            }
        }
        .onAppear {
            fetchHikes()
        }
        .navigationBarHidden(true)
    }
    
    private func fetchHikes() {
        guard !isLoading else { return }  // Prevent multiple requests
        isLoading = true
        
        HikeService.shared.fetchHikes(page: currentPage) { result in
            DispatchQueue.main.async {
                isLoading = false
                switch result {
                case .success(let response):
                    if response.trails.isEmpty {
                        canLoadMore = false  // No more data available
                    } else {
                        hikes.append(contentsOf: response.trails)
                        currentPage += 1
                    }
                case .failure(let error):
                    print("❌ Error fetching hikes: \(error.localizedDescription)")
                }
            }
        }
    }
    
    private func loadMoreHikes() {
        if canLoadMore {
            fetchHikes()
        }
    }
    
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterOption.allCases) { option in
                    Image(systemName: option.systemImage).tag(option)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .frame(height: 30)
        //.background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
    
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search hikes...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}




// MARK: - Hike Card
struct HikeCard: View {
    let hike: Hike  // Change from @Binding
    
    var body: some View {
        VStack {
            AsyncImage(url: URL(string: hike.pictureUrl)) { phase in
                if let image = phase.image {
                    image.resizable().scaledToFill()
                } else {
                    Color.gray
                }
            }
            .frame(height: 150)
            .clipped()
            
            HStack {
                Text(hike.name)
                    .font(.headline)
                Spacer()
                Text("\(hike.averageRating)⭐️")
                    .font(.subheadline)
            }
            .padding()
            
            Button(action: {
                print("Favorite button tapped for: \(hike.name)")
            }) {
                Image(systemName: (hike.isFavorite ?? false) ? "heart.fill" : "heart")
                    .foregroundColor((hike.isFavorite ?? false) ? .red : .gray)
            }
        }
        .background(Color.white)
        .cornerRadius(10)
        .shadow(radius: 3)
    }
}




// MARK: - Star Rating With Tap Gesture
struct StarRating: View {
    @Binding var rating: Int  // Use a Binding to allow changes
    
    var body: some View {
        HStack {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
                    .onTapGesture {
                        rating = index  // Update rating on tap
                    }
            }
        }
    }
}

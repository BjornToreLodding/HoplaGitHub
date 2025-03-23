//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import Foundation
import CoreLocation


// MARK: - Hike Model
struct Hike: Codable, Identifiable {
    let id: String
    let name: String
    let pictureUrl: String
    let averageRating: Int
    let isFavorite: Bool
    let distance: Double?
    let latitude: Double?  // Add latitude
    let longitude: Double? // Add longitude
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
    @State private var canLoadMore = true
    @State private var likedHikes: [String] = []
    @State private var userLocation: CLLocation? = nil
    @StateObject private var locationManager = LocationManager()
    
    var body: some View {
        VStack(spacing: 0) {
            filterBar
            searchBar
            
            if isLoading && hikes.isEmpty {
                ProgressView("Loading Hikes...")
            } else {
                ScrollView {
                    LazyVStack(spacing: 10) {
                        ForEach(filteredHikes().indices, id: \.self) { index in
                            HikeCard(hike: filteredHikes()[index], likedHikes: $likedHikes)
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
            // Request user's location when view appears
            fetchHikes()
        }
        .onChange(of: locationManager.userLocation) { newLocation in
            userLocation = newLocation
        }
        .navigationBarHidden(true)
    }
    
    private func fetchHikes() {
        guard !isLoading else { return }  // Prevent multiple requests
        isLoading = true
        
        // Fetch hikes based on selected filter
        if selectedFilter == .heart {
            // No need to fetch hikes again, just filter them for favorites
            filterFavoriteHikes()
        } else {
            // Fetch all hikes
            fetchAllHikes(page: currentPage)
        }
    }
    
    private func fetchAllHikes(page: Int) {
        isLoading = true
        HikeService.shared.fetchHikes(page: page) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    if response.trails.isEmpty {
                        canLoadMore = false  // No more data available
                    } else {
                        // Append hikes from the response
                        hikes.append(contentsOf: response.trails)
                        currentPage += 1
                    }
                case .failure(let error):
                    print("❌ Error fetching hikes: \(error.localizedDescription)")
                }
                isLoading = false
            }
        }
    }

    private func filterFavoriteHikes() {
        // Only filter for favorites when the heart filter is active
        hikes = hikes.filter { $0.isFavorite }
    }
    
    private func loadMoreHikes() {
        if canLoadMore {
            fetchHikes()
        }
    }
    
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterOption.allCases, id: \.self) { option in
                    Image(systemName: option.systemImage).tag(option)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .frame(height: 30)
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
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
    
    // Function to search through hikes
    private func filteredHikes() -> [Hike] {
        var filtered = hikes
        
        // Apply search filter
        if !searchText.isEmpty {
            filtered = filtered.filter {
                $0.name.lowercased().contains(searchText.lowercased())
            }
        }
        
        // Apply filter based on selected filter option
        switch selectedFilter {
        case .map:
            // Show hikes with most stars first and newest added
            filtered.sort {
                if $0.averageRating == $1.averageRating {
                    return $0.id > $1.id  // Sort by ID for newest first
                }
                return $0.averageRating > $1.averageRating
            }
        case .location:
            // Sort hikes based on proximity
            if let userLocation = userLocation {
                filtered = filtered.filter { hike in
                    guard let latitude = hike.latitude, let longitude = hike.longitude else {
                        return false
                    }
                    return true
                }
                
                filtered.sort {
                    if let latitude1 = $0.latitude, let longitude1 = $1.longitude,
                       let latitude2 = $1.latitude, let longitude2 = $1.longitude {
                        let location1 = CLLocation(latitude: latitude1, longitude: longitude1)
                        let location2 = CLLocation(latitude: latitude2, longitude: longitude2)
                        
                        let distance1 = userLocation.distance(from: location1)
                        let distance2 = userLocation.distance(from: location2)
                        
                        return distance1 < distance2
                    }
                    return false
                }
            }

        case .heart:
            // Already filtered when the heart filter is selected
            break
        default:
            break
        }
        
        return filtered
    }
}


// MARK: - Hike Card
struct HikeCard: View {
    let hike: Hike
    @Binding var likedHikes: [String]  // Bind to the list of liked hikes
    
    var body: some View {
        ZStack {
            // Image background
            AsyncImage(url: URL(string: hike.pictureUrl)) { phase in
                if let image = phase.image {
                    image.resizable().scaledToFill()
                } else {
                    Color.gray
                }
            }
            .frame(height: 150)
            .clipped()
            
            // Heart icon at the top-right of the image
            Button(action: {
                toggleFavorite(for: hike)
            }) {
                Image(systemName: likedHikes.contains(hike.id) ? "heart.fill" : "heart")
                    .foregroundColor(likedHikes.contains(hike.id) ? .red : .gray)
                    .padding(10)
            }
            .frame(width: 30, height: 30)
            .position(x: UIScreen.main.bounds.width - 40, y: 20)
            
            // Text content
            VStack {
                Spacer()
                HStack {
                    Text(hike.name)
                        .font(.headline)
                    Spacer()
                    StarRating(rating: .constant(hike.averageRating))
                        .frame(width: 100)
                }
                .padding()
            }
        }
        .background(Color.white)
        .cornerRadius(10)
        .shadow(radius: 3)
    }
    
    // Function to like hikes
    private func toggleFavorite(for hike: Hike) {
        if let index = likedHikes.firstIndex(of: hike.id) {
            likedHikes.remove(at: index) // Remove from liked hikes if already liked
        } else {
            likedHikes.append(hike.id) // Add to liked hikes if not already liked
        }
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

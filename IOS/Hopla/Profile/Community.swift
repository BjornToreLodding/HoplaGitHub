
import SwiftUI
import Foundation


// MARK: - Hike Model
struct Stable: Identifiable, Decodable {
    let stableId: String
    let stableName: String
    let distance: Double
    let member: Bool
    let pictureUrl: String
    
    // Conforming to Identifiable protocol
    var id: String {
        return stableId
    }
}




class StableViewModel: ObservableObject {
    @Published var stables: [Stable] = []
    
    func fetchStables(search: String? = nil, latitude: Double, longitude: Double, pageSize: Int? = nil, pageNumber: Int? = nil) {
        let baseURL = "https://hopla.onrender.com/stables/all"
        var urlComponents = URLComponents(string: baseURL)!
        
        var queryItems: [URLQueryItem] = [
            URLQueryItem(name: "latitude", value: "\(latitude)"),
            URLQueryItem(name: "longitude", value: "\(longitude)")
        ]
        
        // Add optional parameters if they are provided
        if let search = search, !search.isEmpty {
            queryItems.append(URLQueryItem(name: "search", value: search))
        }
        if let pageSize = pageSize {
            queryItems.append(URLQueryItem(name: "pagesize", value: "\(pageSize)"))
        }
        if let pageNumber = pageNumber {
            queryItems.append(URLQueryItem(name: "pagenumber", value: "\(pageNumber)"))
        }
        
        // Set query items
        urlComponents.queryItems = queryItems
        
        guard let url = urlComponents.url else { return }
        
        // Debugging: print the final URL
        print("Request URL: \(url)")
        
        // Make the API call
        var request = URLRequest(url: url)
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        request.httpMethod = "GET"
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let httpResponse = response as? HTTPURLResponse {
                print("HTTP Status Code: \(httpResponse.statusCode)")
            }
            
            if let data = data, !data.isEmpty {
                DispatchQueue.main.async {
                    do {
                        let decodedResponse = try JSONDecoder().decode([Stable].self, from: data)
                        self.stables = decodedResponse
                        
                        // Print out the details for every stable
                        for stable in decodedResponse {
                            print("Stable ID: \(stable.stableId)")
                            print("Stable Name: \(stable.stableName)")
                            print("Distance: \(stable.distance)")
                            print("Member: \(stable.member)")
                            print("Picture URL: \(stable.pictureUrl)")
                            print("-------------------------")
                        }
                    } catch {
                        print("Decoding error: \(error)")
                    }
                }
            } else {
                print("Received empty or invalid data")
            }
        }.resume()
    }
}




// MARK: - Filter bar Options
enum FilterCommunity: String, CaseIterable, Identifiable {
    case location
    case heart
    
    var id: String { self.rawValue }
    
    var systemImage: String {
        switch self {
        case .location: return "location"
        case .heart: return "heart"
        }
    }
}

// MARK: - Main View
struct Community: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var selectedFilter: FilterCommunity = .location
    @State private var searchText: String = ""
    @StateObject private var viewModel = StableViewModel()
    
    var filteredStables: [Stable] {
        let lowercasedSearchText = searchText.lowercased()
        return viewModel.stables.filter { stable in
            (selectedFilter == .heart ? stable.member : true) &&
            (searchText.isEmpty || stable.stableName.lowercased().contains(lowercasedSearchText))
        }
    }
    
    var body: some View {
        VStack {
            filterBar
            searchBar
            NavigationView {
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(filteredStables) { stable in
                            StableCard(stable: stable)
                        }
                    }
                    .padding(.horizontal)
                }
                .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
            }
            .navigationBarBackButtonHidden(true)
        }
        .onAppear {
            viewModel.fetchStables(latitude: 60.8, longitude: 10.7)
        }
    }
    
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterCommunity.allCases) { option in
                    Image(systemName: option.systemImage).tag(option)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
    
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search stables...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}



// MARK: - Stable Card
struct StableCard: View {
    let stable: Stable
    
    var body: some View {
        NavigationLink(destination: CommunityChat(stable: stable)) {
            VStack {
                ZStack(alignment: .topTrailing) { // Align items to the top-right
                    AsyncImage(url: URL(string: stable.pictureUrl)) { image in
                        image.resizable()
                    } placeholder: {
                        Color.gray
                    }
                    .scaledToFill()
                    .frame(width: 370, height: 150)
                    .clipped()
                
                    // Dark overlay for better text visibility
                    Color.black.opacity(0.4)
                        .frame(width: 370, height: 150)
                    
                    // Heart icon
                    Image(systemName: stable.member ? "heart.fill" : "heart")
                        .font(.system(size: 20))
                        .foregroundColor(stable.member ? .red : .white)
                        .padding(10) // Padding for positioning
                        .padding([.top, .trailing], 10) // Adjust position
                    
                    
                    VStack {
                        Spacer()
                        HStack {
                            Text(stable.stableName)
                                .foregroundStyle(.white)
                                .padding(.leading, 10)
                            Spacer()
                        }
                    }
                }
            }
            .clipShape(Rectangle())
            .shadow(radius: 3)
        }
        .buttonStyle(PlainButtonStyle())
    }
}



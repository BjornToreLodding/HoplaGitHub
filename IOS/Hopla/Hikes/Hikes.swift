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
struct Hike: Codable, Identifiable, Equatable {
    let id: String
    let name: String
    let pictureUrl: String
    let averageRating: Int
    var isFavorite: Bool
    let distance: Double?
    let latitude: Double?
    let longitude: Double?
    let filters: [HikeFilter]?
}


// MARK: - Filters for Hikes
struct HikeFilter: Codable, Identifiable, Equatable {
    let id: String
    let name: String
    let displayName: String
    let type: String
    let options: [String]
    let value: String
    let defaultValue: String
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
class HikeService: ObservableObject {
    static let shared = HikeService()
    @Published var hikes: [Hike] = []
    
    private let baseURL = "https://hopla.onrender.com/trails/all"
    private let locationBaseURL = "https://hopla.onrender.com/trails/list"
    private let favoriteBaseURL = "https://hopla.onrender.com/trails/favorites"
    private let relationBaseURL = "https://hopla.onrender.com/trails/relations"
    
    
    // Function to fetch hikes based on page number
    func fetchHikes(page: Int, completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            completion(.failure(NSError(domain: "", code: 401, userInfo: [NSLocalizedDescriptionKey: "Unauthorized - No Token"])))
            return
        }
        
        let urlString = "\(baseURL)?pageNumber=\(page)&pageSize=20"
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
    
    // New function to fetch hikes based on user's location (latitude and longitude)
    func fetchHikesByLocation(latitude: Double, longitude: Double, completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            completion(.failure(NSError(domain: "", code: 401, userInfo: [NSLocalizedDescriptionKey: "Unauthorized - No Token"])))
            return
        }
        
        let urlString = "\(locationBaseURL)?latitude=\(latitude)&longitude=\(longitude)"
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
    
    func fetchFavoriteHikes(page: Int, pageSize: Int, completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            completion(.failure(NSError(domain: "", code: 401, userInfo: [NSLocalizedDescriptionKey: "Unauthorized - No Token"])))
            return
        }

        let urlString = "\(favoriteBaseURL)?pagenumber=\(page)&pagesize=\(20)"
        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }

            guard let data = data else {
                completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                return
            }

            do {
                let decodedResponse = try JSONDecoder().decode(HikeResponse.self, from: data)
                DispatchQueue.main.async {
                    completion(.success(decodedResponse))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func fetchRelationHikes(page: Int, pageSize: Int, completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            completion(.failure(NSError(domain: "", code: 401, userInfo: [NSLocalizedDescriptionKey: "Unauthorized - No Token"])))
            return
        }

        let urlString = "\(relationBaseURL)?friends=true&following=true&pagenumber=\(page)&pagesize=\(pageSize)"
        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }

            guard let data = data else {
                completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                return
            }

            do {
                let decodedResponse = try JSONDecoder().decode(HikeResponse.self, from: data)
                DispatchQueue.main.async {
                    completion(.success(decodedResponse))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
    
    func toggleFavorite(for hike: Hike, completion: @escaping (Bool) -> Void) {
        let isFavoriting = !hike.isFavorite // ✅ Correct toggle behavior
        
        guard let url = URL(string: "https://hopla.onrender.com/trails/favorite") else {
            completion(false)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = isFavoriting ? "POST" : "DELETE"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        
        let body: [String: Any] = ["TrailId": hike.id]
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body, options: [])
        } catch {
            print("JSON serialization failed: \(error)")
            completion(false)
            return
        }

        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    print("Favorite error:", error.localizedDescription)
                    completion(false)
                    return
                }

                if let httpResponse = response as? HTTPURLResponse {
                    print("Favorite HTTP status:", httpResponse.statusCode)

                    if let responseData = data {
                        print("Raw API response:", String(data: responseData, encoding: .utf8) ?? "No response body")
                    }

                    completion((200...299).contains(httpResponse.statusCode))
                } else {
                    completion(false)
                }
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
    @State private var isShowingFilterOptions = false
    @ObservedObject var viewModel: HikeService


    var body: some View {
        VStack(spacing: 0) {
            filterBar
            searchBar

            if isLoading && hikes.isEmpty {
                ProgressView("Loading Hikes...")
            } else if filteredHikes().isEmpty {
                Text("No hikes found.")
                    .foregroundColor(.gray)
                    .padding()
            } else {
                if isLoading && hikes.isEmpty {
                    ProgressView("Loading Hikes...")
                } else if filteredHikes().isEmpty {
                    Text("No hikes found.")
                        .foregroundColor(.gray)
                        .padding()
                } else {
                    hikeList
                }
            }
        }
        .onAppear {
            userLocation = locationManager.userLocation
            fetchHikes()
        }
        .onChange(of: locationManager.userLocation) { newLocation in
            userLocation = newLocation
        }
        .onChange(of: selectedFilter) { newFilter in
            handleFilterChange(newFilter)
        }
        .navigationBarHidden(true)
    }
    
    private func handleFilterChange(_ filter: FilterOption) {
        switch filter {
        case .map:
            resetAndFetch()
        case .location:
            if let location = userLocation {
                fetchHikesByLocation(location)
            }
        case .heart:
            resetAndFetchFavorites()
        case .star:
            hikes = []
            currentPage = 1
            fetchRelationHikes()
        case .arrow:
            withAnimation {
                isShowingFilterOptions.toggle()
            }
        }
    }

    
    private var hikeList: some View {
        ScrollView {
            LazyVStack {
                ForEach(filteredHikes()) { hike in
                    HikeCard(
                        hike: hike,
                        likedHikes: $likedHikes,
                        toggleFavoriteAction: { selectedHike in
                            handleToggleFavorite(for: selectedHike)
                        },
                        viewModel: viewModel
                    )
                    .onAppear {
                        // Trigger load more when nearing the end of the list
                        if hike == hikes.last {
                            loadMoreHikes()
                        }
                    }
                    .padding()
                }

                if isLoading {
                    HStack {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                }
            }
        }
    }


    func handleToggleFavorite(for hike: Hike) {
        viewModel.toggleFavorite(for: hike) { success in
            DispatchQueue.main.async {
                if success {
                    self.hikes = self.hikes.map { currentHike in
                        var updatedHike = currentHike
                        if updatedHike.id == hike.id {
                            updatedHike.isFavorite.toggle() // ✅ Toggle AFTER API success
                        }
                        return updatedHike
                    }
                } else {
                    print("Failed to toggle favorite")
                }
            }
        }
    }


    
    private func toggleFavorite(for hike: Hike) {
        if let index = likedHikes.firstIndex(of: hike.id) {
            likedHikes.remove(at: index)
        } else {
            likedHikes.append(hike.id)
        }

        // Update `isFavorite` directly within the hike list
        hikes = hikes.map { currentHike in
            var updatedHike = currentHike
            if updatedHike.id == hike.id {
                updatedHike.isFavorite.toggle()
            }
            return updatedHike
        }
    }



    // MARK: - Fetch Methods
    
    private func fetchRelationHikes() {
        guard !isLoading else { return }
        isLoading = true

        HikeService.shared.fetchRelationHikes(page: currentPage, pageSize: 20) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    hikes.append(contentsOf: response.trails)
                    currentPage += 1
                    canLoadMore = !response.trails.isEmpty
                case .failure(let error):
                    print("❌ Error fetching friend hikes: \(error.localizedDescription)")
                }
                isLoading = false
            }
        }
    }



    private func resetAndFetch() {
        hikes = []
        currentPage = 1
        canLoadMore = true
        fetchHikes()
    }

    private func fetchHikes() {
        guard !isLoading else { return }
        isLoading = true

        HikeService.shared.fetchHikes(page: currentPage) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    hikes.append(contentsOf: response.trails)
                    currentPage += 1
                    canLoadMore = !response.trails.isEmpty
                case .failure(let error):
                    print("❌ Error fetching hikes:", error.localizedDescription)
                }
                isLoading = false
            }
        }
    }

    private func fetchHikesByLocation(_ location: CLLocation) {
        guard !isLoading else { return }
        isLoading = true

        HikeService.shared.fetchHikesByLocation(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude) { result in
            DispatchQueue.main.async(execute: DispatchWorkItem {
                
                switch result {
                    
                case .success(let response):
                    print("📍 Raw Hike Data:", response.trails)
                    self.hikes = response.trails.sorted { (hike1, hike2) in
                        guard let hike1Lat = hike1.latitude,
                              let hike1Long = hike1.longitude,
                              let hike2Lat = hike2.latitude,
                              let hike2Long = hike2.longitude else { return false }

                        let hike1Location = CLLocation(latitude: hike1Lat, longitude: hike1Long)
                        let hike2Location = CLLocation(latitude: hike2Lat, longitude: hike2Long)

                        let distance1 = location.distance(from: hike1Location)
                        let distance2 = location.distance(from: hike2Location)

                        print("📏 Hike 1 Distance:", distance1, "📏 Hike 2 Distance:", distance2) // ✅ Debug distance calculations

                        return distance1 < distance2 // ✅ Sort by proximity
                    }

                    self.canLoadMore = false // No pagination for location-based fetch
                case .failure(let error):
                    print("❌ Error fetching hikes by location:", error.localizedDescription)
                }
                self.isLoading = false
            })
        }

    }


    
    private func fetchFavoriteHikes(page: Int, pageSize: Int = 20) {
        guard !isLoading else { return }
        isLoading = true

        HikeService.shared.fetchFavoriteHikes(page: page, pageSize: pageSize) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    // Append the fetched hikes to the existing list of hikes
                    hikes.append(contentsOf: response.trails)
                    currentPage += 1
                    canLoadMore = !response.trails.isEmpty
                case .failure(let error):
                    print("❌ Error fetching favorite hikes:", error.localizedDescription)
                }
                isLoading = false
            }
        }
    }
    
    private func resetAndFetchFavorites() {
        // Reset currentPage and hikes array when switching to favorites
        hikes = []
        currentPage = 1
        canLoadMore = true
        fetchFavoriteHikes(page: currentPage)
    }

    private func fetchAllHikesIfNeeded() {
        guard !isLoading else { return }
        isLoading = true
        var page = 1
        var allHikes = [Hike]()

        func loadNextPage() {
            HikeService.shared.fetchHikes(page: page) { result in
                DispatchQueue.main.async {
                    switch result {
                    case .success(let response):
                        if response.trails.isEmpty {
                            canLoadMore = false
                            hikes = allHikes
                            isLoading = false
                        } else {
                            allHikes.append(contentsOf: response.trails)
                            page += 1
                            loadNextPage()
                        }
                    case .failure(let error):
                        print("❌ Error fetching all hikes: \(error.localizedDescription)")
                        isLoading = false
                    }
                }
            }
        }

        loadNextPage()
    }

    private func loadMoreHikes() {
        guard !isLoading && canLoadMore else { return }

        switch selectedFilter {
        case .map:
            fetchHikes()
        case .location:
            if let location = userLocation {
                    print("📍 Fetching hikes by user location")
                    fetchHikesByLocation(location)
                } else {
                    print("❌ User location is nil!")
                }
        case .heart:
            fetchAllHikesIfNeeded()
        case .star:
            fetchRelationHikes()
        case .arrow:
            // Do nothing — arrow is UI-only
            break
        @unknown default:
            print("Unhandled filter option")
        }
    }

    // MARK: - Filtering

    private func filteredHikes() -> [Hike] {
        var filtered = hikes

        if !searchText.isEmpty {
            filtered = filtered.filter {
                $0.name.lowercased().contains(searchText.lowercased())
            }
        }

        if selectedFilter == .heart {
            filtered = filtered.filter { $0.isFavorite }
        }

        return filtered
    }

    // MARK: - UI

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
        .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
    }

    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search hikes...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .fill(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground)
                        .color(for: colorScheme).opacity(0.2))
                )
        }
        .padding(.horizontal)
    }
}



// MARK: - Hike Card
struct HikeCard: View {
    let hike: Hike
    @Binding var likedHikes: [String]
    var toggleFavoriteAction: (Hike) -> Void
    @ObservedObject var viewModel: HikeService  // This works now

    var body: some View {
        NavigationLink(destination: HikesDetails(hike: hike)) {
            VStack(alignment: .leading, spacing: 8) {
                ZStack(alignment: .topTrailing) {
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
                    
                    // Heart icon
                    Button(action: {
                        toggleFavoriteAction(hike) // ✅ Trigger API + UI update
                    }) {
                        Image(systemName: hike.isFavorite ? "heart.fill" : "heart")
                            .foregroundColor(hike.isFavorite ? .red : .gray)
                            .padding(10)
                    }
                }

                // Hike name and rating
                HStack {
                    Text(hike.name)
                        .font(.headline)
                    Spacer()
                    StarRating(rating: .constant(hike.averageRating))
                        .frame(width: 100)
                }
                .padding(.horizontal)

                // Filters
                if let filters = hike.filters, !filters.isEmpty {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Filters:")
                            .font(.subheadline)
                            .bold()
                        ForEach(filters, id: \.id) { filter in
                            HStack {
                                Text("\(filter.displayName):")
                                    .font(.caption)
                                    .foregroundColor(.gray)
                                Text(displayValue(for: filter))
                                    .font(.caption)
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 10)
                }
            }
            .background(Color(.systemBackground))
            .cornerRadius(10)
            .shadow(radius: 4)
        }
    }
    
    private func toggleFavorite(for hike: Hike) {
        if let index = likedHikes.firstIndex(of: hike.id) {
            likedHikes.remove(at: index)
        } else {
            likedHikes.append(hike.id)
        }
    }
    
    private func displayValue(for filter: HikeFilter) -> String {
        if filter.type == "Bool" {
            return filter.value.lowercased() == "true" ? "Yes" : "No"
        } else {
            return filter.value
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

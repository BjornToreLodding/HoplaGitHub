//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import Foundation
import CoreLocation
import GoogleMaps



// MARK: - Hike Model
struct Hike: Codable, Identifiable, Equatable {
    let id: String
    var name: String
    var description: String?
    let pictureUrl: String
    let averageRating: Int
    var isFavorite: Bool
    let distance: Double?
    var latitude: Double?
    var longitude: Double?
    let filters: [HikeFilter]?

    enum CodingKeys: String, CodingKey {
        case id, name, description, pictureUrl, averageRating, isFavorite, distance, filters
        case latMean
        case longMean
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try container.decode(String.self, forKey: .id)
        name = try container.decode(String.self, forKey: .name)
        description = try? container.decode(String.self, forKey: .description)
        pictureUrl = try container.decode(String.self, forKey: .pictureUrl)
        averageRating = try container.decode(Int.self, forKey: .averageRating)
        isFavorite = try container.decode(Bool.self, forKey: .isFavorite)
        distance = try? container.decode(Double.self, forKey: .distance)
        filters = try? container.decode([HikeFilter].self, forKey: .filters)

        // Decode latMean/longMean if available
        latitude = try? container.decode(Double.self, forKey: .latMean)
        longitude = try? container.decode(Double.self, forKey: .longMean)

        // If latMean/longMean are missing, attempt to decode from raw keys
        if latitude == nil || longitude == nil {
            let raw = try decoder.singleValueContainer().decode([String: AnyDecodable].self)

            if let lat = raw["latitude"]?.value as? Double {
                latitude = lat
            }

            if let lon = raw["longitude"]?.value as? Double {
                longitude = lon
            }
        }
    }


    
    // Custom encoding below
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)

        try container.encode(id, forKey: .id)
        try container.encode(name, forKey: .name)
        try container.encodeIfPresent(description, forKey: .description)
        try container.encode(pictureUrl, forKey: .pictureUrl)
        try container.encode(averageRating, forKey: .averageRating)
        try container.encode(isFavorite, forKey: .isFavorite)
        try container.encodeIfPresent(distance, forKey: .distance)
        try container.encodeIfPresent(filters, forKey: .filters)

        try container.encodeIfPresent(latitude, forKey: .latMean)
        try container.encodeIfPresent(longitude, forKey: .longMean)
    }
    
    init(
        id: String,
        name: String,
        description: String? = nil,
        pictureUrl: String,
        averageRating: Int,
        isFavorite: Bool,
        distance: Double? = nil,
        latitude: Double? = nil,
        longitude: Double? = nil,
        filters: [HikeFilter]? = nil
    ) {
        self.id = id
        self.name = name
        self.description = description
        self.pictureUrl = pictureUrl
        self.averageRating = averageRating
        self.isFavorite = isFavorite
        self.distance = distance
        self.latitude = latitude
        self.longitude = longitude
        self.filters = filters
    }

}

struct AnyDecodable: Decodable {
    let value: Any

    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()

        if let int = try? container.decode(Int.self) {
            value = int
        } else if let double = try? container.decode(Double.self) {
            value = double
        } else if let string = try? container.decode(String.self) {
            value = string
        } else if let bool = try? container.decode(Bool.self) {
            value = bool
        } else if let nested = try? container.decode([String: AnyDecodable].self) {
            value = nested
        } else if let array = try? container.decode([AnyDecodable].self) {
            value = array
        } else {
            value = ()
        }
    }
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
    
    // Add the parameter to check if the map view is active
    func systemImage(isMapViewActive: Bool) -> String {
        switch self {
        case .map:
            return isMapViewActive ? "list.dash" : "map"
        case .location: return "location"
        case .heart: return "heart"
        case .star: return "star"
        case .arrow: return "chevron.down"
        }
    }
}


//MARK: - The filters of the hikes
struct TrailFilter: Identifiable, Codable {
    let id: String
    let name: String
    let displayName: String
    let type: FilterType
    let options: [String]
    let defaultValue: FilterValue
}

enum FilterType: String, Codable {
    case multiEnum = "MultiEnum"
    case enumType = "Enum"
    case bool = "Bool"
    case int = "Int"
}

enum FilterValue: Codable {
    case string(String)
    case stringArray([String])
    case int(Int)
    case bool(Bool)  // Added the bool case
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        
        if let array = try? container.decode([String].self) {
            self = .stringArray(array)
        } else if let string = try? container.decode(String.self) {
            self = .string(string)
        } else if let int = try? container.decode(Int.self) {
            self = .int(int)
        } else if let bool = try? container.decode(Bool.self) {  // Decode Bool if possible
            self = .bool(bool)
        } else {
            throw DecodingError.typeMismatch(FilterValue.self, DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Unknown FilterValue"))
        }
    }
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        switch self {
        case .string(let value):
            try container.encode(value)
        case .stringArray(let value):
            try container.encode(value)
        case .int(let value):
            try container.encode(value)
        case .bool(let value):  // Encode Bool if needed
            try container.encode(value)
        }
    }
}

struct MapTrail: Codable, Identifiable {
    let id: String
    let name: String
    let latMean: Double?
    let longMean: Double?
    
    // If you want properties with more intuitive names:
    var latitude: Double? { latMean }
    var longitude: Double? { longMean }
}


//MARK: - To view hikes on a map
struct HikeMapView: View {
    @Binding var hikes: [Hike]
    @StateObject private var locationManager = LocationManager()

    var body: some View {
        MapContainerView(hikes: hikes, locationManager: locationManager)
            .edgesIgnoringSafeArea(.all)
            .onAppear {
                print("🗺️ HikeMapView appeared with \(hikes.count) hikes")
            }
    }
}


// MARK: - Fetching Hikes from Backend
class HikeService: ObservableObject {
    static let shared = HikeService()
    @Published var hikes: [Hike] = []
    @State private var trailFilters: [TrailFilter] = []
    @State private var selectedOptions: [String: Any] = [:]
    
    private let baseURL = "https://hopla.onrender.com/trails/all"
    private let locationBaseURL = "https://hopla.onrender.com/trails/list"
    private let favoriteBaseURL = "https://hopla.onrender.com/trails/favorites"
    private let relationBaseURL = "https://hopla.onrender.com/trails/relations"
    
    func fetchTrailFilters() {
        guard let url = URL(string: "https://hopla.onrender.com/trailfilters/all") else { return }
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data {
                do {
                    let filters = try JSONDecoder().decode([TrailFilter].self, from: data)
                    DispatchQueue.main.async {
                        self.trailFilters = filters
                    }
                } catch {
                    print("Decoding error: \(error)")
                }
            }
        }.resume()
    }
    
    func applySelectedFilters() {
        var urlComponents = URLComponents(string: "https://hopla.onrender.com/hikes")! // Example endpoint
        urlComponents.queryItems = selectedOptions.map { key, value in
            if let val = value as? String {
                return URLQueryItem(name: key, value: val)
            } else if let val = value as? Bool {
                return URLQueryItem(name: key, value: val ? "true" : "false")
            } else if let val = value as? Int {
                return URLQueryItem(name: key, value: "\(val)")
            } else if let val = value as? Set<String> {
                return URLQueryItem(name: key, value: val.joined(separator: ","))
            } else {
                return nil
            }
        }.compactMap { $0 }
        
        guard let url = urlComponents.url else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data {
                do {
                    let fetchedHikes = try JSONDecoder().decode([Hike].self, from: data)
                    DispatchQueue.main.async {
                        self.hikes = fetchedHikes
                    }
                } catch {
                    print("Decoding hikes failed: \(error)")
                }
            }
        }.resume()
    }
    
    
    
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
    
    //MARK: - To fetch coordinates and show on map
    func fetchTrailsForMap(latitude: Double, longitude: Double, zoomLevel: Int, completion: @escaping ([MapTrail]) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            return
        }

        let urlString = "https://hopla.onrender.com/trails/map?latitude=\(latitude)&longitude=\(longitude)&zoomlevel=\(zoomLevel)"
        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error fetching map trails: \(error.localizedDescription)")
                return
            }

            guard let data = data else {
                print("❌ No data received")
                return
            }

            do {
                if let jsonString = String(data: data, encoding: .utf8) {
                    print("📜 Raw Map JSON Data:\n", jsonString)
                }
                let mapTrails = try JSONDecoder().decode([MapTrail].self, from: data)
                DispatchQueue.main.async {
                    completion(mapTrails)
                }
            } catch {
                print("❌ Error decoding map trails: \(error.localizedDescription)")
                completion([])
            }
        }.resume()
    }

    
    
    //MARK: - Fetch filtered hikes
    func fetchFilteredHikes(selectedOptions: [String: Any], completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        guard var urlComponents = URLComponents(string: "https://hopla.onrender.com/trails/all") else {
            completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid URL"])))
            return
        }
        
        var queryItems: [URLQueryItem] = []
        for (key, value) in selectedOptions {
            if let stringValue = value as? String {
                queryItems.append(URLQueryItem(name: key, value: stringValue))
            } else if let boolValue = value as? Bool {
                queryItems.append(URLQueryItem(name: key, value: boolValue ? "true" : "false"))
            } else if let intValue = value as? Int {
                queryItems.append(URLQueryItem(name: key, value: "\(intValue)"))
            } else if let setValue = value as? Set<String> {
                queryItems.append(URLQueryItem(name: key, value: setValue.joined(separator: ",")))
            }
        }
        
        if !queryItems.isEmpty {
            urlComponents.queryItems = queryItems
        }
        
        guard let url = urlComponents.url else {
            completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Could not build filtered URL"])))
            return
        }
        
        // Debug: Print the URL
        print("Filtered URL: \(url.absoluteString)")
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        if let token = TokenManager.shared.getToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async { completion(.failure(error)) }
                return
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "No data received"])))
                }
                return
            }
            
            do {
                let decodedResponse = try JSONDecoder().decode(HikeResponse.self, from: data)
                DispatchQueue.main.async {
                    completion(.success(decodedResponse))
                }
            } catch {
                DispatchQueue.main.async {
                    completion(.failure(error))
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
    @State private var trailFilters: [TrailFilter] = []
    @State private var selectedOptions: [String: Any] = [:]
    @State private var isMapViewActive: Bool = false
    @State private var mapTrails: [MapTrail] = []

    
    var body: some View {
        VStack(spacing: 0) {
            filterBar
            searchBar

            if isShowingFilterOptions {
                        makeFilterDropdownBar(
                            trailFilters: trailFilters,
                            selectedOptions: $selectedOptions,
                            isShowing: $isShowingFilterOptions,
                            applyAction: {
                                // When "Apply Filters" is tapped, fetch filtered hikes.
                                HikeService.shared.fetchFilteredHikes(selectedOptions: selectedOptions) { result in
                                    switch result {
                                    case .success(let response):
                                        //self.hikes = response.trails
                                        self.hikes = filteredHikes()
                                    case .failure(let error):
                                        print("Error applying filters: \(error.localizedDescription)")
                                    }
                                }
                                // Dismiss the filter view.
                                isShowingFilterOptions = false
                            }
                        )
                    }

            if isLoading && hikes.isEmpty {
                ProgressView("Loading Hikes...")
                    .padding()
            } else if filteredHikes().isEmpty {
                Text("No hikes found.")
                    .foregroundColor(.gray)
                    .padding()
            } else {
                hikeDisplay
            }
        }
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
        .onAppear {
            userLocation = locationManager.userLocation
            if isMapViewActive {
                // Update mapTrails instead of hikes
                if let location = userLocation {
                    HikeService.shared.fetchTrailsForMap(
                        latitude: location.coordinate.latitude,
                        longitude: location.coordinate.longitude,
                        zoomLevel: 14
                    ) { mapResults in
                        self.mapTrails = mapResults
                    }
                }
            } else {
                fetchHikes() // Regular fetching for list view
            }
        }
        .onChange(of: selectedFilter) { newValue in
            if newValue == .map {
                isMapViewActive = true
                loadTrailsOnMap() // This updates mapTrails
            }
        }
        .onChange(of: locationManager.userLocation) { newLocation in
            userLocation = newLocation
        }
        .navigationBarHidden(true)
    }
    
    @ViewBuilder
    private var hikeDisplay: some View {
        if isMapViewActive {
            MapTrailsView(trails: $mapTrails)
        } else {
            hikeList
        }
    }
    
    func fetchTrailFilters() {
        trailFilters = [
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780001",
                name: "SurfaceType",
                displayName: "Underlag",
                type: .multiEnum,
                options: ["Gravel", "Sand", "Asphalt", "Dirt"],
                defaultValue: .stringArray(["Gravel", "Dirt"])
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780002",
                name: "Difficulty",
                displayName: "Vanskelighetsgrad",
                type: .enumType,
                options: ["Easy", "Medium", "Hard"],
                defaultValue: .string("Easy")
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780003",
                name: "WinterAccessible",
                displayName: "Åpen om vinteren",
                type: .bool,
                options: [],
                defaultValue: .bool(false)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780004",
                name: "HasBridge",
                displayName: "Har bro over elv",
                type: .bool,
                options: [],
                defaultValue: .bool(false)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780005",
                name: "StrollerFriendly",
                displayName: "Tilrettelagt for vogn",
                type: .bool,
                options: [],
                defaultValue: .bool(false)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780006",
                name: "TrafficLevel",
                displayName: "Biltrafikk",
                type: .enumType,
                options: ["Ingen", "Lite", "Middels", "Mye"],
                defaultValue: .string("Lite")
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780007",
                name: "CrowdLevel",
                displayName: "Folk langs veien",
                type: .enumType,
                options: ["Sjelden", "Noe", "Mye"],
                defaultValue: .string("Noe")
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780008",
                name: "SuitableForChildren",
                displayName: "Egner seg for barnevogn",
                type: .bool,
                options: [],
                defaultValue: .bool(false)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780009",
                name: "ForestArea",
                displayName: "Skogsområde",
                type: .bool,
                options: [],
                defaultValue: .bool(true)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780010",
                name: "SwimmingSpot",
                displayName: "Mulighet for bading",
                type: .bool,
                options: [],
                defaultValue: .bool(false)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780011",
                name: "Insects",
                displayName: "Mengde innsekter",
                type: .int,
                options: [],
                defaultValue: .int(0)
            ),
            TrailFilter(
                id: "12345678-0000-0000-0101-123456780012",
                name: "Predetors",
                displayName: "Rovdyr",
                type: .enumType,
                options: [
                    "Ingen rovdyr (trygg familieløype)",
                    "Rykter om hyener",
                    "Løvespor observert",
                    "Quetzalcoatlus luftrom. Svært farlig"
                ],
                defaultValue: .string("ingen")
            ),
            TrailFilter(
                id: "b4d58319-cad1-4f0e-9bc9-ceba325e9d31",
                name: "Custom13",
                displayName: "lus",
                type: .multiEnum,
                options: ["ja", "litt", "noen", "masse"],
                defaultValue: .stringArray(["litt", "noen", "masse"])
            ),
            TrailFilter(
                id: "4943a808-859b-4eff-8b39-e5a5e3d0e00c",
                name: "Custom14",
                displayName: "Drittunger",
                type: .enumType,
                options: ["Nei", "noen", "masse snørrette barnehagebarn"],
                defaultValue: .string("masse snørrette barnehagebarn")
            )
        ]
    }
    
    // This function returns only the hikes that match every filter in selectedOptions.
    func filterHikesLocally(selectedOptions: [String: Any], hikes: [Hike]) -> [Hike] {
        return hikes.filter { hike in
            // Ensure that for every filter in selectedOptions, the hike's filters array contains a matching value.
            guard let hikeFilters = hike.filters else {
                return false
            }
            
            // For each selected option key/value in the dictionary:
            for (key, selectedValue) in selectedOptions {
                // Find the hike filter with the matching name (case-insensitive).
                guard let filter = hikeFilters.first(where: { $0.name.lowercased() == key.lowercased() }) else {
                    // If the hike does not even have that filter, consider it a non-match.
                    return false
                }
                
                // Depending on the type of selectedValue, check for a match:
                if let selectedStr = selectedValue as? String {
                    // For enum filters: split the filter's value (which might be comma separated)
                    let values = filter.value
                        .split(separator: ",")
                        .map { $0.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() }
                    if !values.contains(selectedStr.lowercased()) {
                        return false
                    }
                } else if let selectedSet = selectedValue as? Set<String> {
                    // For multiEnum filters: check that at least one of the selected options is included.
                    let values = filter.value
                        .split(separator: ",")
                        .map { $0.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() }
                    let matches = selectedSet.contains { selected in
                        values.contains(selected.lowercased())
                    }
                    if !matches {
                        return false
                    }
                } else if let selectedBool = selectedValue as? Bool {
                    // Convert filter's value (as string) to a Bool by comparing with "true".
                    let filterBool = (filter.value.lowercased() == "true")
                    if filterBool != selectedBool {
                        return false
                    }
                } else if let selectedInt = selectedValue as? Int {
                    if Int(filter.value) != selectedInt {
                        return false
                    }
                }
            }
            // If every selected option is satisfied, then include this hike.
            return true
        }
    }

    
    
    
    private func applySelectedFilters() {
        // Apply logic here
        print("Filters applied: \(selectedOptions)")
        // You might want to refilter hikes based on selectedOptions
    }
    
    
    private func handleFilterChange(_ filter: FilterOption) {
        switch filter {
        case .map:
            resetAndFetch() // ✅ Call side-effect function outside

            if isMapViewActive {
                HikeMapView(hikes: $hikes) // ✅ Return a View
            } else {
                List(hikes) { hike in
                    hikeList // ✅ This must return a view
                }
            }

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
                if isShowingFilterOptions {
                    fetchTrailFilters()
                }
            }
        }
    }
    
    
    private var hikeList: some View {
        ScrollView {
            LazyVStack {
                ForEach(filteredHikes()) { hike in
                    HikeCard(
                        hike: hike,
                        trailFilters: trailFilters,
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
    
    //MARK: - Loading trails on map
    func loadTrailsOnMap() {
        guard let location = locationManager.userLocation else {
            print("❌ User location is nil in loadTrailsOnMap")
            return
        }

        HikeService.shared.fetchTrailsForMap(
            latitude: location.coordinate.latitude,
            longitude: location.coordinate.longitude,
            zoomLevel: 14
        ) { mapResults in
            print("✅ Fetched \(mapResults.count) trails for map")
            self.mapTrails = mapResults  // Assign to mapTrails (of type [MapTrail])
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
    
    //MARK: - The filter selection for hikes
    func makeFilterDropdownBar(
        trailFilters: [TrailFilter],
        selectedOptions: Binding<[String: Any]>,
        isShowing: Binding<Bool>,
        applyAction: @escaping () -> Void
    ) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                // For each filter, display the UI...
                ForEach(trailFilters) { filter in
                    VStack(alignment: .leading) {
                        Text(filter.displayName).bold()
                        
                        switch filter.type {
                        case .bool:
                            Toggle(isOn: Binding(
                                get: {
                                    selectedOptions.wrappedValue[filter.name] as? Bool ?? false
                                },
                                set: { newVal in
                                    selectedOptions.wrappedValue[filter.name] = newVal
                                }
                            )) {
                                Text("Yes")
                            }
                            
                        case .enumType:
                            SwiftUI.Picker("Choose", selection: Binding(
                                get: {
                                    selectedOptions.wrappedValue[filter.name] as? String ?? ""
                                },
                                set: { newVal in
                                    selectedOptions.wrappedValue[filter.name] = newVal
                                }
                            )) {
                                ForEach(filter.options, id: \.self) { option in
                                    Text(option)
                                }
                            }
                            .pickerStyle(SegmentedPickerStyle())
                            
                        case .multiEnum:
                            VStack(alignment: .leading) {
                                ForEach(filter.options, id: \.self) { option in
                                    Toggle(option, isOn: Binding(
                                        get: {
                                            let current = selectedOptions.wrappedValue[filter.name] as? Set<String> ?? []
                                            return current.contains(option)
                                        },
                                        set: { isOn in
                                            var current = selectedOptions.wrappedValue[filter.name] as? Set<String> ?? []
                                            if isOn {
                                                current.insert(option)
                                            } else {
                                                current.remove(option)
                                            }
                                            selectedOptions.wrappedValue[filter.name] = current
                                        }
                                    ))
                                }
                            }
                            
                        case .int:
                            Stepper(
                                value: Binding(
                                    get: { selectedOptions.wrappedValue[filter.name] as? Int ?? 0 },
                                    set: { newVal in
                                        selectedOptions.wrappedValue[filter.name] = newVal
                                    }
                                ),
                                in: 0...10
                            ) {
                                Text("\(selectedOptions.wrappedValue[filter.name] as? Int ?? 0)")
                            }
                        }
                    }
                }
                
                // Clear All button
                Button("Clear All Filters") {
                    selectedOptions.wrappedValue.removeAll()
                }
                .padding()
                .background(Color.gray.opacity(0.3))
                .cornerRadius(8)
                .foregroundColor(.black)
                
                // Apply Filters button.
                Button("Apply Filters") {
                    print("Selected Options: \(selectedOptions.wrappedValue)")
                    // Call the apply action (this may use your network call or local filter)
                    applyAction()
                    // Dismiss the dropdown view.
                    isShowing.wrappedValue = false
                }
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .padding()
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
        // Start with all hikes.
        var filtered = hikes
        
        // First, filter by search text (if any).
        if !searchText.isEmpty {
            filtered = filtered.filter {
                $0.name.lowercased().contains(searchText.lowercased())
            }
        }
        
        // Optionally, if using the heart filter you might filter for favorites.
        if selectedFilter == .heart {
            filtered = filtered.filter { $0.isFavorite }
        }
        
        // Then filter by the selected options, if any:
        if !selectedOptions.isEmpty {
            filtered = filterHikesLocally(selectedOptions: selectedOptions, hikes: filtered)
        }
        
        return filtered
    }

    
    // MARK: - UI
    
    private var filterBar: some View {
        HStack {
            ForEach(FilterOption.allCases, id: \.self) { option in
                Button(action: {
                    if option == .map {
                        isMapViewActive.toggle() // Toggle between map and list
                    }
                    selectedFilter = option
                    handleFilterChange(option)
                }) {
                    Image(systemName: option.systemImage(isMapViewActive: isMapViewActive))
                        .foregroundColor(selectedFilter == option ? .blue : .gray)
                        .bold()
                        .padding()
                }
            }
        }
        .frame(width: 420, height: 30)
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
    let trailFilters: [TrailFilter]
    
    @Binding var likedHikes: [String]
    var toggleFavoriteAction: (Hike) -> Void
    @ObservedObject var viewModel: HikeService  // This works now
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationLink(destination: HikesDetails(hike: hike, trailFilters: trailFilters)) {
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
                    
                    // Heart button
                    Button {
                        toggleFavoriteAction(hike)
                    } label: {
                        ZStack {
                            // Black “stroke” heart, slightly larger
                            Image(systemName: "heart.fill")
                                .font(.system(size: 26))
                                .foregroundColor(.black)
                            // Colored fill heart on top
                            Image(systemName: "heart.fill")
                                .font(.system(size: 24))
                                .foregroundColor(hike.isFavorite ? .red : .gray)
                        }
                        .padding(10)
                    }

                }
                
                // Hike name and rating
                HStack {
                    Text(hike.name)
                        .font(.headline)
                        .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
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
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        ForEach(filters, id: \.id) { filter in
                            HStack {
                                Text("\(filter.displayName):")
                                    .font(.caption)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                Text(displayValue(for: filter))
                                    .font(.caption)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
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

//MARK: - The map view
struct MapContainerView: UIViewRepresentable {
    let hikes: [Hike]
    @ObservedObject var locationManager: LocationManager

    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView(frame: .zero)
        mapView.isMyLocationEnabled = true
        mapView.settings.myLocationButton = true
        return mapView
    }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        guard let userLocation = locationManager.userLocation else {
            print("❌ No user location available.")
            return
        }

        print("📍 updateUIView called. Number of hikes: \(hikes.count)")
        for hike in hikes {
            print("➡️ Hike:", hike.name, hike.latitude ?? 0, hike.longitude ?? 0)
        }

        mapView.clear()

        let path = GMSMutablePath()
        for coordinate in locationManager.coordinates {
            path.add(CLLocationCoordinate2D(latitude: coordinate.lat, longitude: coordinate.long))
        }

        let polyline = GMSPolyline(path: path)
        polyline.strokeWidth = 5
        polyline.strokeColor = .blue
        polyline.map = mapView

        var bounds = GMSCoordinateBounds()
        for hike in hikes {
            guard let lat = hike.latitude, let lon = hike.longitude else {
                print("❌ Skipping hike without coordinates: \(hike.name)")
                continue
            }

            let position = CLLocationCoordinate2D(latitude: lat, longitude: lon)
            bounds = bounds.includingCoordinate(position)

            let marker = GMSMarker(position: position)
            marker.title = hike.name
            marker.map = mapView

            print("✅ Marker added for \(hike.name) at \(lat), \(lon)")
        }

        if hikes.count > 0 {
            let update = GMSCameraUpdate.fit(bounds, withPadding: 50)
            mapView.animate(with: update)
            print("🎯 Camera updated to fit bounds")
        }
    }
}

//MARK: - To display trails as pins on map
struct MapTrailsView: View {
    @Binding var trails: [MapTrail]
    @StateObject private var locationManager = LocationManager()

    var body: some View {
        MapContainerViewMap(trails: trails, locationManager: locationManager)
            .edgesIgnoringSafeArea(.all)
            .onAppear {
                print("🗺️ MapTrailsView appeared with \(trails.count) trails")
            }
    }
}

struct MapContainerViewMap: UIViewRepresentable {
    let trails: [MapTrail]
    @ObservedObject var locationManager: LocationManager

    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView(frame: .zero)
        mapView.isMyLocationEnabled = true
        mapView.settings.myLocationButton = true
        return mapView
    }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        guard let _ = locationManager.userLocation else {
            print("❌ No user location available.")
            return
        }

        mapView.clear()
        var bounds = GMSCoordinateBounds()

        for trail in trails {
            guard let lat = trail.latitude, let lon = trail.longitude else {
                print("❌ Skipping trail without coordinates: \(trail.name)")
                continue
            }

            let position = CLLocationCoordinate2D(latitude: lat, longitude: lon)
            bounds = bounds.includingCoordinate(position)

            let marker = GMSMarker(position: position)
            marker.title = trail.name
            marker.map = mapView

            print("✅ Marker added for \(trail.name) at \(lat), \(lon)")
        }

        if trails.count > 0 {
            let update = GMSCameraUpdate.fit(bounds, withPadding: 50)
            mapView.animate(with: update)
            print("🎯 Camera updated to fit bounds")
        }
    }
}

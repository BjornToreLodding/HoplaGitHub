import SwiftUI
import Foundation
import CoreLocation
import GoogleMaps

// MARK: - Fetching Hikes from Backend
// swiftlint:disable:next type_body_length
class HikeService: ObservableObject {
    static let shared = HikeService()
    @Published var hikes: [Hike] = []
    @State private var trailFilters: [TrailFilter] = []
    @State private var selectedOptions: [String: Any] = [:]
    
    // For testing
    private let session: URLSession
    
    init(session: URLSession = .shared) {
        self.session = session
        if ProcessInfo.processInfo.environment["UITEST_MODE"] == "true" {
          loadStubHikes()
        }
      }
    
    // MARK: – UITest stub flag & data
    private let isUITest: Bool = ProcessInfo.processInfo.environment["UITEST_MODE"] == "true"
    private var stubbedHikes: [Hike] = []
    
    // Synchronously load “stubHikes.json” from the main bundle.
    private func loadStubHikes() {
        guard
            let url = Bundle.main.url(forResource: "stubHikes", withExtension: "json"),
            let data = try? Data(contentsOf: url),
            let response = try? JSONDecoder().decode(HikeResponse.self, from: data)
        else {
            print("⚠️ Couldn’t load stubHikes.json")
            return
        }
        stubbedHikes = response.trails
        // Also publish them immediately so your view shows data on appear:
        DispatchQueue.main.async {
            self.hikes = response.trails
        }
    }
    
    
    private let baseURL = "https://hopla.onrender.com/trails/all"
    private let locationBaseURL = "https://hopla.onrender.com/trails/list"
    private let favoriteBaseURL = "https://hopla.onrender.com/trails/favorites"
    private let relationBaseURL = "https://hopla.onrender.com/trails/relations"
    func fetchTrailFilters() {
        guard let url = URL(string: "https://hopla.onrender.com/trailfilters/all") else { return }
        session.dataTask(with: url) { data, response, error in
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
        session.dataTask(with: url) { data, response, error in
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
        // If in UITest, immediately return our stubbed data:
        if isUITest {
            let perPage = 20
            let start = (page - 1) * perPage
            let slice = Array(stubbedHikes.dropFirst(start).prefix(perPage))
            let resp = HikeResponse(trails: slice,
                                    pageNumber: page,
                                    pageSize: slice.count)
            DispatchQueue.main.async {
                completion(.success(resp))
            }
            return
        }
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
        session.dataTask(with: request) { data, response, error in
                // 1) Network error first
                if let error = error {
                    completion(.failure(error))
                    return
                }

                // 2) Cast to HTTPURLResponse so we can read statusCode
                guard let httpResp = response as? HTTPURLResponse else {
                    let err = NSError(
                      domain: "HikeService",
                      code: -1,
                      userInfo: [NSLocalizedDescriptionKey: "Invalid response"]
                    )
                    completion(.failure(err))
                    return
                }

                // 3) If not 2xx, immediately fail with that code
                guard (200...299).contains(httpResp.statusCode) else {
                    let err = NSError(
                      domain: "HikeService",
                      code: httpResp.statusCode,
                      userInfo: [NSLocalizedDescriptionKey: HTTPURLResponse.localizedString(forStatusCode: httpResp.statusCode)]
                    )
                    completion(.failure(err))
                    return
                }

                // 4) Now it’s safe to decode JSON
                guard let data = data else {
                    let err = NSError(
                      domain: "HikeService",
                      code: -1,
                      userInfo: [NSLocalizedDescriptionKey: "No data received"]
                    )
                    completion(.failure(err))
                    return
                }

                do {
                    let decoded = try JSONDecoder().decode(HikeResponse.self, from: data)
                    completion(.success(decoded))
                } catch {
                    completion(.failure(error))
                }
            }
            .resume()    }
    // New function to fetch hikes based on user's location (lat and long)
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
        session.dataTask(with: request) { data, response, error in
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
        }.resume() }
    func fetchFavoriteHikes(page: Int, pageSize: Int, completion: @escaping (Result<HikeResponse, Error>) -> Void) {
        if isUITest {
            // stub: just return an empty page, or filter stubbedHikes where isFavorite == true
            let favs = stubbedHikes.filter { $0.isFavorite }
            let slice = Array(favs.dropFirst((page-1)*pageSize).prefix(pageSize))
            let resp = HikeResponse(trails: slice,
                                    pageNumber: page,
                                    pageSize: slice.count)
            DispatchQueue.main.async { completion(.success(resp)) }
            return }
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            completion(.failure(NSError(domain: "", code: 401, userInfo: [NSLocalizedDescriptionKey: "Unauthorized - No Token"])))
            return }
        let urlString = "\(favoriteBaseURL)?pagenumber=\(page)&pagesize=\(20)"
        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return }
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        session.dataTask(with: request) { data, response, error in
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
        session.dataTask(with: request) { data, response, error in
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
        if isUITest {
            // immediately succeed and update our published array
            DispatchQueue.main.async {
                if let idx = self.stubbedHikes.firstIndex(where: { $0.id == hike.id }) {
                    self.stubbedHikes[idx].isFavorite.toggle()
                    self.hikes = self.hikes.map {
                        var hke = $0
                        if hke.id == hike.id { hke.isFavorite.toggle() }
                        return hke
                    }
                }
                completion(true)
            }
            return
        }
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
        session.dataTask(with: request) { data, response, error in
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
        session.dataTask(with: request) { data, response, error in
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
        session.dataTask(with: request) { data, response, error in
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

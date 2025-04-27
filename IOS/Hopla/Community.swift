
import SwiftUI
import Foundation
import CoreLocation



// MARK: - Hike Model
struct Stable: Identifiable, Decodable, Equatable {
    let stableId: String
    let stableName: String
    let distance: Double?
    var member: Bool
    let pictureUrl: String?
    
    // Conforming to Identifiable protocol
    var id: String {
        return stableId
    }
    static func == (lhs: Stable, rhs: Stable) -> Bool {
        return lhs.stableId == rhs.stableId
    }
    
}

class StableViewModel: ObservableObject {
    @Published var stables: [Stable] = []
    private var locationManager: LocationManager
    private var currentPage = 1
    private var isLastPage = false
    @Published var isLoading = false // Track loading state
    
    
    func loadMoreStablesIfNeeded(search: String? = nil, latitude: Double, longitude: Double) {
        guard !isLastPage else { return } // Stop fetching if already on the last page
        
        fetchStables(search: search, latitude: latitude, longitude: longitude, pageNumber: currentPage) { [weak self] in
            guard let self = self else { return }
            
            // If fewer than the requested number of stables are returned, assume it's the last page
            if self.stables.count < (self.currentPage * 20) { // Assuming pageSize is 20
                self.isLastPage = true
            } else {
                self.currentPage += 1
            }
        }
    }
    
    
    init(locationManager: LocationManager) {
        self.locationManager = locationManager
        NotificationCenter.default.addObserver(self, selector: #selector(handleLocationUpdate(_:)), name: .didUpdateLocation, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self, name: .didUpdateLocation, object: nil)
    }
    
    @objc private func handleLocationUpdate(_ notification: Notification) {
        guard let location = notification.object as? CLLocation else { return }
        fetchStables(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
    }
    
    func toggleMembership(for stable: Stable) {
        if let index = stables.firstIndex(where: { $0.stableId == stable.stableId }) {
            stables[index].member.toggle() // Optimistically update UI
            
            let isJoining = stables[index].member
            
            // Define a completion handler
            let completion: (Bool) -> Void = { success in
                DispatchQueue.main.async { [weak self] in
                    guard let self = self else { return }
                    if success {
                        // Use user location dynamically instead of hardcoded data
                        if let userLocation = self.locationManager.userLocation {
                            self.fetchStables(
                                latitude: userLocation.coordinate.latitude,
                                longitude: userLocation.coordinate.longitude
                            )
                        } else {
                            print("User location not available.")
                        }
                    } else {
                        // Revert the toggle if the operation fails
                        self.stables[index].member.toggle()
                        print("Failed to update membership status on server.")
                    }
                }
            }
            
            // Call the appropriate server method based on membership status
            if isJoining {
                joinStableOnServer(for: stables[index], completion: completion)
            } else {
                leaveStableOnServer(for: stables[index], completion: completion)
            }
        }
    }

    
    
    private func joinStableOnServer(for stable: Stable, completion: @escaping (Bool) -> Void) {
        let url = URL(string: "https://hopla.onrender.com/stables/join")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        
        let body: [String: Any] = ["stableId": stable.stableId]
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body, options: [])
        } catch {
            print("Failed to serialize data: \(error)")
            completion(false)
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error joining stable:", error.localizedDescription)
                completion(false)
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                print("Join HTTP Status Code:", httpResponse.statusCode)
                completion((200...299).contains(httpResponse.statusCode))
            } else {
                completion(false)
            }
        }.resume()
    }
    
    
    
    private func leaveStableOnServer(for stable: Stable, completion: @escaping (Bool) -> Void) {
        let url = URL(string: "https://hopla.onrender.com/stables/leave")!
        var request = URLRequest(url: url)
        request.httpMethod = "DELETE"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        
        let body: [String: Any] = ["stableId": stable.stableId]
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body, options: [])
        } catch {
            print("Failed to serialize data: \(error)")
            completion(false)
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error leaving stable:", error.localizedDescription)
                completion(false)
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                print("Leave HTTP Status Code:", httpResponse.statusCode)
                completion((200...299).contains(httpResponse.statusCode))
            } else {
                completion(false)
            }
        }.resume()
    }
    
    
    func fetchStables(search: String? = nil, latitude: Double, longitude: Double, pageSize: Int = 20, pageNumber: Int = 1, completion: (() -> Void)? = nil) {
        guard !isLoading else { return } // Prevent duplicate fetches
        isLoading = true // Set loading to true
        
        let baseURL = "https://hopla.onrender.com/stables/all"
        var urlComponents = URLComponents(string: baseURL)!
        
        var queryItems: [URLQueryItem] = [
            URLQueryItem(name: "latitude", value: "\(latitude)"),
            URLQueryItem(name: "longitude", value: "\(longitude)"),
            URLQueryItem(name: "pagesize", value: "\(pageSize)"),
            URLQueryItem(name: "pagenumber", value: "\(pageNumber)")
        ]
        
        if let search = search, !search.isEmpty {
            queryItems.append(URLQueryItem(name: "search", value: search))
        }
        
        urlComponents.queryItems = queryItems
        
        guard let url = urlComponents.url else { return }
        
        var request = URLRequest(url: url)
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        request.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: request) { [weak self] data, response, error in
            DispatchQueue.main.async {
                self?.isLoading = false // Set loading to false when the fetch is done
                
                if let httpResponse = response as? HTTPURLResponse {
                    print("HTTP Status Code: \(httpResponse.statusCode)")
                }
                
                if let data = data, !data.isEmpty {
                    do {
                        let decodedResponse = try JSONDecoder().decode([Stable].self, from: data)
                        
                        // Safely unwrap self
                        guard let self = self else { return }
                        
                        // Filter out duplicates based on stableId before appending
                        let newStables = decodedResponse.filter { stable in
                            !self.stables.contains(where: { $0.stableId == stable.stableId })
                        }
                        self.stables += newStables
                        
                        // Invoke the completion closure if provided
                        completion?()
                    } catch {
                        print("Decoding error: \(error)")
                    }
                } else {
                    print("Received empty or invalid data")
                }
            }
        }.resume()
    }
    
    
    
    
    func createStable(formData: [String: Any], completion: @escaping () -> Void) {
        let url = URL(string: "https://hopla.onrender.com/stables/create")!
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        
        let boundary = "Boundary-\(UUID().uuidString)"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        
        for (key, value) in formData {
            body.append("--\(boundary)\r\n".data(using: .utf8)!)
            
            if key == "Image", let imageData = value as? Data {
                body.append("Content-Disposition: form-data; name=\"\(key)\"; filename=\"stall.jpg\"\r\n".data(using: .utf8)!)
                body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
                body.append(imageData)
                body.append("\r\n".data(using: .utf8)!)
            } else {
                body.append("Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n".data(using: .utf8)!)
                body.append("\(value)\r\n".data(using: .utf8)!)
            }
        }
        
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        request.httpBody = body
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let httpResponse = response as? HTTPURLResponse {
                print("HTTP Status Code: \(httpResponse.statusCode)")
            }
            
            if let data = data {
                let responseString = String(data: data, encoding: .utf8) ?? "Invalid response"
                print("Raw Response: \(responseString)")
                
                DispatchQueue.main.async {
                    do {
                        let decodedResponse = try JSONDecoder().decode([String: String].self, from: data)
                        if let stableId = decodedResponse["stableId"], let message = decodedResponse["message"] {
                            print(message) // Success message
                            
                            // Create a new stable object and add it to the list
                            let newStable = Stable(
                                stableId: stableId,
                                stableName: formData["Name"] as? String ?? "Unknown",
                                distance: nil, // Now allowed
                                member: false,
                                pictureUrl: nil // Now allowed
                            )
                            self.stables.append(newStable) // Update the UI immediately
                            completion()
                        }
                    } catch {
                        print("Error decoding response: \(error)")
                    }
                }
            } else if let error = error {
                print("Request failed with error: \(error)")
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
    @StateObject private var viewModel = StableViewModel(locationManager: LocationManager())
    @State private var isShowingAddStableSheet = false // State to control modal sheet
    @StateObject private var locationManager = LocationManager()
    
    var filteredStables: [Stable] {
        let lowercasedSearchText = searchText.lowercased()
        return viewModel.stables.filter { stable in
            (selectedFilter == .heart ? stable.member : true) &&
            (searchText.isEmpty || stable.stableName.lowercased().contains(lowercasedSearchText))
        }
    }
    
    var body: some View {
        ZStack {
            mainContent
            floatingButton
        }
    }
    
    private var mainContent: some View {
        VStack {
            filterBar
            searchBar
            navigationContent
            
            // Show loading indicator when stables are being fetched
            if viewModel.isLoading {
                ProgressView("Loading...")
                    .padding()
                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                    .cornerRadius(10)
                    .shadow(radius: 5)
            }
        }
        .onAppear(perform: fetchInitialStables)
    }

    
    private var navigationContent: some View {
        NavigationView {
            ScrollView {
                stableList
                    .padding(.horizontal)
            }
            .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
        }
        .navigationBarBackButtonHidden(true)
    }
    
    private var stableList: some View {
        VStack(spacing: 10) {
            ForEach(filteredStables) { stable in
                StableCard(viewModel: viewModel, stable: stable)
                    .onAppear {
                        handleInfiniteScroll(for: stable)
                    }
            }
        }
    }
    
    private var floatingButton: some View {
        VStack {
            Spacer()
            HStack {
                Spacer()
                Button(action: {
                    isShowingAddStableSheet.toggle()
                }) { Image(systemName: "plus.circle.fill")
                      .resizable()
                      .frame(width: 60, height: 60)
                      .symbolRenderingMode(.palette)
                      .foregroundStyle(
                        .white,
                        AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                          .color(for: colorScheme)
                      )
                      .padding()
                }
                .sheet(isPresented: $isShowingAddStableSheet) {
                    AddStableView(viewModel: viewModel)
                }
            }
            .padding()
        }
    }
    
    private func fetchInitialStables() {
        if let userLocation = locationManager.userLocation {
            viewModel.fetchStables(latitude: userLocation.coordinate.latitude, longitude: userLocation.coordinate.longitude)
        } else {
            print("User location not available, using default coordinates.")
            viewModel.fetchStables(latitude: 59.9139, longitude: 10.7522) // Default coordinates, Oslo
        }
    }
    
    private func handleInfiniteScroll(for stable: Stable) {
        if stable == filteredStables.last {
            if let userLocation = locationManager.userLocation {
                viewModel.loadMoreStablesIfNeeded(
                    search: searchText,
                    latitude: userLocation.coordinate.latitude,
                    longitude: userLocation.coordinate.longitude
                )
            }
        }
    }
    

    // Filter bar
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterCommunity.allCases) { option in
                    Image(systemName: option.systemImage).tag(option)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
    }
    
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search stables...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10)
                    .fill(
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme)
                            .opacity(0.2))
                )
        }
        .padding(.horizontal)
    }
}

struct AddStableView: View {
    @Environment(\.dismiss) var dismiss
    @ObservedObject var viewModel: StableViewModel
    @StateObject private var locationManager = LocationManager()
    
    
    @State private var name: String = ""
    @State private var description: String = ""
    @State private var latitude: String = ""
    @State private var longitude: String = ""
    @State private var isPrivateGroup: Bool = false
    @State private var selectedImage: UIImage? // Store the selected image
    @State private var showImagePicker = false // Controls the image picker
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Stable Details")) {
                    TextField("Name", text: $name)
                    TextField("Description", text: $description)
                    TextField("Latitude", text: $latitude)
                        .keyboardType(.decimalPad)
                    TextField("Longitude", text: $longitude)
                        .keyboardType(.decimalPad)
                    Toggle(isOn: $isPrivateGroup) {
                        Text("Private Group")
                    }
                }
                
                Section(header: Text("Image Selection")) {
                    Button(action: { showImagePicker = true }) {
                        Text("Select Image")
                    }
                    
                    if let image = selectedImage {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 100)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                }
                
                Section {
                    Button("Save") {
                        addStable()
                    }
                    .disabled(name.isEmpty || description.isEmpty || latitude.isEmpty || longitude.isEmpty || selectedImage == nil)
                }
            }
            .navigationTitle("Add Stable")
            .navigationBarItems(trailing: Button("Cancel") {
                dismiss()
            })
            .sheet(isPresented: $showImagePicker) {
                ImagePicker(sourceType: .photoLibrary, selectedImage: $selectedImage, showImagePicker: $showImagePicker)
            }
        }
    }
    
    private func addStable() {
        guard let userLocation = locationManager.userLocation else {
            print("User location not available")
            return
        }
        
        var formData: [String: Any] = [
            "Name": name,
            "Description": description,
            "Latitude": userLocation.coordinate.latitude,
            "Longitude": userLocation.coordinate.longitude,
            "PrivateGroup": isPrivateGroup
        ]
        
        // Convert UIImage to Data
        if let imageData = selectedImage?.jpegData(compressionQuality: 0.8) {
            formData["Image"] = imageData
        }
        
        // Create the stable on the backend
        viewModel.createStable(formData: formData) {
            viewModel.fetchStables(latitude: userLocation.coordinate.latitude, longitude: userLocation.coordinate.longitude)
            dismiss() // Close the sheet when stable is added
        }
    }
}



// MARK: - Stable Card
struct StableCard: View {
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var viewModel: StableViewModel // Add this line to observe changes in ViewModel
    let stable: Stable
    
    var body: some View {
        // NavigationLink for entire card navigation
        NavigationLink(destination: CommunityChat(stable: stable, scrollToBottom: true)) {
            VStack {
                ZStack(alignment: .topTrailing) {
                    AsyncImage(url: URL(string: stable.pictureUrl ?? "https://example.com/default-image.jpg")) { image in
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
                    
                    // Heart icon - tap to toggle membership
                    Button(action: {
                        viewModel.toggleMembership(for: stable)
                    }) {
                        Image(systemName: stable.member ? "heart.fill" : "heart")
                            .font(.system(size: 20))
                            .foregroundColor(stable.member ? .red : .white)
                            .padding(10)
                            .padding([.top, .trailing], 10)
                    }
                    
                    VStack {
                        Spacer()
                        HStack {
                            Text(stable.stableName)
                                .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                .padding(.leading, 10)
                            Spacer()
                        }
                    }
                }
            }
            // Ensure the whole card is tappable for navigation
            .contentShape(Rectangle())
            .clipShape(Rectangle())
            .shadow(radius: 3)
        }
        .buttonStyle(PlainButtonStyle()) // Prevent default button styling on NavigationLink
    }
}


//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//
// swiftlint:disable file_length
import SwiftUI
import Foundation
import CoreLocation
import GoogleMaps

// To decode the data
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

// MARK: - The filters of the hikes
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

// To decode the filters
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

// The trails displayed on the map
struct MapTrail: Codable, Identifiable {
    let id: String
    let name: String
    let latMean: Double?
    let longMean: Double?
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
                print("HikeMapView appeared with \(hikes.count) hikes")
            }
    }
}

// MARK: - Main View
// swiftlint:disable type_body_length
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
    
    // To "heart"/like a trail
    private var toggleFavoriteHandler: (Hike) -> Void {
        { hike in
            viewModel.toggleFavorite(for: hike) { success in
                DispatchQueue.main.async {
                    guard success else { return }
                    hikes = hikes.map {
                        var copy = $0
                        if copy.id == hike.id {
                            copy.isFavorite.toggle()
                        }
                        return copy
                    }
                }
            }
        }
    }
    
    var body: some View {
        VStack(spacing: 0) {
            FilterBarView(
                selectedFilter: $selectedFilter,
                isMapViewActive: $isMapViewActive,
                isShowingFilterOptions: $isShowingFilterOptions,
                colorScheme: colorScheme
            )
            .accessibilityIdentifier("HikesFilterBar")
            searchBar
            if isShowingFilterOptions {
                FilterContentView(
                    filter:                 selectedFilter,
                    hikes:                  $hikes,
                    isMapViewActive:        $isMapViewActive,
                    userLocation:           $userLocation,
                    currentPage:            $currentPage,
                    canLoadMore:            $canLoadMore,
                    isShowingFilterOptions: $isShowingFilterOptions,
                    likedHikes:             $likedHikes,
                    trailFilters:           trailFilters,
                    toggleFavoriteAction:   toggleFavoriteHandler,
                    viewModel:              viewModel
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
    
    // To display the trails either in list or map form
    @ViewBuilder
    private var hikeDisplay: some View {
        if isMapViewActive {
            MapTrailsView(trails: $mapTrails)
                .accessibilityIdentifier("HikesMapView")
        } else {
            hikeList
        }
    }
    
    // Fetch trail filters
    func fetchTrailFilters() {
        trailFilters = TrailFilter.allDefaults
    }
    
    // This function returns only the hikes that match every filter in selectedOptions.
    func filterHikesLocally(selectedOptions: [String: Any], hikes: [Hike]) -> [Hike] {
        return hikes.filter { hike in
            // Ensure that for every filter in selectedOptions, the hike's filters array contains a matching value
            guard let hikeFilters = hike.filters else {
                return false
            }
            
            // For each selected option key/value in the dictionary:
            for (key, selectedValue) in selectedOptions {
                // Find the hike filter with the matching name (case-insensitive)
                guard let filter = hikeFilters.first(where: { $0.name.lowercased() == key.lowercased() }) else {
                    // If the hike does not even have that filter, consider it a non-match.
                    return false
                }
                
                // Depending on the type of selectedValue, check for a match:
                if let selectedStr = selectedValue as? String {
                    // For enum filters: split the filter's value
                    let values = filter.value
                        .split(separator: ",")
                        .map { $0.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() }
                    if !values.contains(selectedStr.lowercased()) {
                        return false
                    }
                } else if let selectedSet = selectedValue as? Set<String> {
                    // For multiEnum filters: check that at least one of the selected options is included
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
                    // Convert filter's value (as string) to a Bool by comparing with "true"
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
            // If every selected option is satisfied, then include this hike
            return true
        }
    }
    
    // Apply the filters
    private func applySelectedFilters() {
        print("Filters applied: \(selectedOptions)")
    }
    
    // The list of trails
    private var hikeList: some View {
        ScrollView {
            LazyVStack {
                ForEach(Array(filteredHikes().enumerated()), id: \.offset) { index, hike in
                    ZStack {
                        // 1) Invisible “count” badge for .matching(identifier: "HikeCard_")
                        Color.clear
                            .frame(width:1, height:1)
                            .accessibilityIdentifier("HikeCard_")
                        // 2) Real card, indexed
                        HikeCard(
                            hike: hike,
                            trailFilters: trailFilters,
                            likedHikes: $likedHikes,
                            toggleFavoriteAction: { selectedHike in
                                toggleFavoriteHandler(selectedHike)
                            },
                            viewModel: viewModel
                        )
                        .accessibilityIdentifier("HikeCard_\(index)")
                    }
                    .onAppear {
                        // Trigger load more when nearing the end of the list
                        if index == filteredHikes().count - 1 {
                            loadMoreHikes()
                        }
                    }
                    .padding()
                }
                if isLoading {
                    HStack {
                        Spacer()
                        ProgressView()
                            .accessibilityIdentifier("HikesLoadingSpinner")
                        Spacer()
                    }
                }
            }
        }
    }
    
    //MARK: - Loading trails on map
    func loadTrailsOnMap() {
        guard let location = locationManager.userLocation else {
            print("User location is nil in loadTrailsOnMap")
            return
        }
        
        HikeService.shared.fetchTrailsForMap(
            latitude: location.coordinate.latitude,
            longitude: location.coordinate.longitude,
            zoomLevel: 14
        ) { mapResults in
            print("Fetched \(mapResults.count) trails for map")
            self.mapTrails = mapResults  // Assign to mapTrails (of type [MapTrail])
        }
    }
    
    // To like/unlike a trail
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
                        switch filter.type { // Switch for the different filters
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
                    // Call the apply action
                    applyAction()
                    // Dismiss the dropdown view
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
                    print("Error fetching friend hikes: \(error.localizedDescription)")
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
                    print("Error fetching hikes:", error.localizedDescription)
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
                        print("📏 Hike 1 Distance:", distance1, "📏 Hike 2 Distance:", distance2) // Debug distance calculations
                        return distance1 < distance2 // Sort by proximity
                    }
                    self.canLoadMore = false // No pagination for location-based fetch
                case .failure(let error):
                    print("Error fetching hikes by location:", error.localizedDescription)
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
                    print("Error fetching favorite hikes:", error.localizedDescription)
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
    
    // To fetch more trails
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
                        print("Error fetching all hikes: \(error.localizedDescription)")
                        isLoading = false
                    }
                }
            }
        }
        loadNextPage()
    }
    
    // Fetch more trails
    private func loadMoreHikes() {
        guard !isLoading && canLoadMore else { return }
        switch selectedFilter {
        case .map:
            fetchHikes()
        case .location:
            if let location = userLocation {
                print("Fetching hikes by user location")
                fetchHikesByLocation(location)
            } else {
                print("User location is nil!")
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
        // Start with all hikes
        var filtered = hikes
        
        // First, filter by search text (if any).
        if !searchText.isEmpty {
            filtered = filtered.filter {
                $0.name.lowercased().contains(searchText.lowercased())
            }
        }
        
        // If using the heart filter, filter for favorites
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
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search hikes...", text: $searchText)
                .accessibilityIdentifier("HikesSearchField")
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
// swiftlint:enable type_body_length

// The filters from the database
private extension TrailFilter {
    static let allDefaults: [TrailFilter] = [
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
// swiftlint:enable file_length

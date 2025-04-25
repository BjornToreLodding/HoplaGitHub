import SwiftUI
import Foundation
import CoreLocation
import GoogleMaps

/// A SwiftUI View that encapsulates all of  FilterOption logic
struct FilterContentView: View {
    // MARK: Inputs
    let filter: FilterOption
    @Binding var hikes: [Hike]
    @Binding var isMapViewActive: Bool
    @Binding var userLocation: CLLocation?
    @Binding var currentPage: Int
    @Binding var canLoadMore: Bool
    @Binding var isShowingFilterOptions: Bool
    @Binding var likedHikes: [String]
    let trailFilters: [TrailFilter]
    let toggleFavoriteAction: (Hike) -> Void


    // Inject your service or view-model
    @ObservedObject var viewModel: HikeService

    // MARK: View Body
    @ViewBuilder
    var body: some View {
        switch filter {
        case .map:
            mapOrListView()
                .onAppear(perform: loadMapOrList)

        case .location:
            // Immediately trigger the location-based fetch
            EmptyView()
                .onAppear {
                    if let loc = userLocation {
                        fetchHikesByLocation(loc)
                    }
                }

        case .heart:
            EmptyView()
                .onAppear(perform: resetAndFetchFavorites)

        case .star:
            EmptyView()
                .onAppear(perform: fetchRelationHikes)

        case .arrow:
            Button(action: toggleFilterOptions) {
                Image(systemName: "slider.horizontal.3")
            }
        }
    }

    // MARK: Subviews & Helpers

    @ViewBuilder
    private func mapOrListView() -> some View {
        if isMapViewActive {
            HikeMapView(hikes: $viewModel.hikes)
        } else {
            List(hikes) { hike in
                HikeCard(
                                    hike:                 hike,
                                    trailFilters:         trailFilters,
                                    likedHikes:           $likedHikes,
                                    toggleFavoriteAction: toggleFavoriteAction,
                                    viewModel:            viewModel
                                )
            }
        }
    }

    private func loadMapOrList() {
        // reset pagination if needed
        resetAndFetch()
    }

    private func resetAndFetch() {
        hikes = []
        currentPage = 1
        canLoadMore = true

        viewModel.fetchHikes(page: currentPage) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    // Now `response.trails` exists
                    self.hikes.append(contentsOf: response.trails)
                    self.currentPage += 1
                    self.canLoadMore = !response.trails.isEmpty

                case .failure(let error):
                    print("❌ Error fetching hikes:", error.localizedDescription)
                }
            }
        }
    }

    private func fetchHikesByLocation(_ loc: CLLocation) {
        viewModel.fetchHikesByLocation(
            latitude:  loc.coordinate.latitude,
            longitude: loc.coordinate.longitude
        ) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    // Now you can access response.trails
                    self.hikes = response.trails
                    self.canLoadMore = false

                case .failure(let error):
                    print("❌ Error fetching hikes by location:", error.localizedDescription)
                }
            }
        }
    }

    private func resetAndFetchFavorites() {
        hikes = []
        currentPage = 1
        canLoadMore = true

        // pass both page AND pageSize
        viewModel.fetchFavoriteHikes(page: currentPage, pageSize: 20) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    self.hikes.append(contentsOf: response.trails)
                    self.canLoadMore = !response.trails.isEmpty

                case .failure(let error):
                    print("❌ Error fetching favorite hikes:", error.localizedDescription)
                }
            }
        }
    }

    private func fetchRelationHikes() {
        // pass both page AND pageSize
        viewModel.fetchRelationHikes(page: currentPage, pageSize: 20) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let response):
                    self.hikes.append(contentsOf: response.trails)
                    self.canLoadMore = !response.trails.isEmpty

                case .failure(let error):
                    print("❌ Error fetching friend hikes:", error.localizedDescription)
                }
            }
        }
    }

    private func toggleFilterOptions() {
        withAnimation {
            isShowingFilterOptions.toggle()
            if isShowingFilterOptions {
                viewModel.fetchTrailFilters()
            }
        }
    }
}


struct FilterBarView: View {
    @Binding var selectedFilter: FilterOption
    @Binding var isMapViewActive: Bool
    @Binding var isShowingFilterOptions: Bool   // ← add this
    let colorScheme: ColorScheme

    var body: some View {
        HStack {
            ForEach(FilterOption.allCases, id: \.self) { option in
                Button {
                    // 1) Always update the selected filter
                    selectedFilter = option

                    // 2) Special-case the `.map` and `.arrow` buttons
                    switch option {
                    case .map:
                        isMapViewActive.toggle()
                    case .arrow:
                        isShowingFilterOptions.toggle()
                    default:
                        break
                    }
                } label: {
                    Image(systemName: option.systemImage(isMapViewActive: isMapViewActive))
                        .foregroundColor(selectedFilter == option ? .blue : .gray)
                        .bold()
                        .padding()
                }
            }
        }
        .frame(width: 420, height: 30)
        .background(
          AdaptiveColor(light: .lightGreen, dark: .darkGreen)
            .color(for: colorScheme)
        )
    }
}

/// A standalone handler that knows how to toggle a Hike’s favorite state
struct HandleToggleFavorite {
    /// A binding to your array of hikes, so we can update it in place
    let hikes: Binding<[Hike]>
    /// Your service or view-model that actually performs the API call
    let viewModel: HikeService

    /// Call this like `handler(hike)` to flip the heart on/off
    func callAsFunction(_ hike: Hike) {
        viewModel.toggleFavorite(for: hike) { success in
            DispatchQueue.main.async {
                guard success else {
                    print("❌ Failed to toggle favorite")
                    return
                }
                // Update our local array immutably
                hikes.wrappedValue = hikes.wrappedValue.map { current in
                    var updated = current
                    if updated.id == hike.id {
                        updated.isFavorite.toggle()
                    }
                    return updated
                }
            }
        }
    }
}


// MARK: - The map view
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

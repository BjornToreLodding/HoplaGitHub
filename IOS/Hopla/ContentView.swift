import SwiftUI
import GoogleMaps
import GooglePlaces

struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme
    @AppStorage("isDarkMode") private var isDarkMode = false // Global dark mode setting
    
    var body: some View {
        ZStack {
            // Background
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)
        }
        .preferredColorScheme(isDarkMode ? .dark : .light)
    }
}

// Define MapView to embed Google Maps
struct MapView: UIViewRepresentable {
    @StateObject var locationManager = LocationManager()

    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView(frame: .zero)
        mapView.isMyLocationEnabled = true
        mapView.settings.myLocationButton = true

        // Wait for user location before setting camera
        if let userLocation = locationManager.userLocation {
            let camera = GMSCameraPosition.camera(
                withLatitude: userLocation.coordinate.latitude,
                longitude: userLocation.coordinate.longitude,
                zoom: 15
            )
            mapView.camera = camera
        }

        return mapView
    }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        if let userLocation = locationManager.userLocation {
            let newCamera = GMSCameraPosition.camera(
                withLatitude: userLocation.coordinate.latitude,
                longitude: userLocation.coordinate.longitude,
                zoom: 15
            )
            mapView.animate(to: newCamera)
        }
    }
}



import SwiftUI
import GoogleMaps

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
    @ObservedObject var locationManager = LocationManager()

    func makeUIView(context: Context) -> GMSMapView {
            // Initialize the GMSMapView with a default camera
            let camera = GMSCameraPosition.camera(withLatitude: 0, longitude: 0, zoom: 15) // Default location
            let mapView = GMSMapView(frame: .zero, camera: camera)
            mapView.isMyLocationEnabled = true // Enable user location
            mapView.settings.myLocationButton = true // Optional: Show location button
            
            return mapView
        }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        if let userLocation = locationManager.userLocation {
            let newCamera = GMSCameraPosition.camera(
                withLatitude: userLocation.coordinate.latitude,
                longitude: userLocation.coordinate.longitude,
                zoom: 15
            )
            mapView.animate(to: newCamera) // Move camera to user location
        }
    }
}

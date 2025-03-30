import SwiftUI
import GoogleMaps
import GooglePlaces

struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme
    @AppStorage("isDarkMode") private var isDarkMode = false // Global dark mode setting
    @StateObject var locationManager = LocationManager() // Keep locationManager here

    var body: some View {
        ZStack {
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)

            MapView(locationManager: locationManager) // Pass location manager
                .edgesIgnoringSafeArea(.all)
        }
        .preferredColorScheme(isDarkMode ? .dark : .light)
    }
}


// Define MapView to embed Google Maps
struct MapView: UIViewRepresentable {
    @ObservedObject var locationManager: LocationManager

    func makeUIView(context: Context) -> GMSMapView {
        let mapView = GMSMapView(frame: .zero)
        mapView.isMyLocationEnabled = true
        mapView.settings.myLocationButton = true

        return mapView
    }

    func updateUIView(_ mapView: GMSMapView, context: Context) {
        guard let userLocation = locationManager.userLocation else { return }

        let camera = GMSCameraPosition.camera(
            withLatitude: userLocation.coordinate.latitude,
            longitude: userLocation.coordinate.longitude,
            zoom: 15
        )

        mapView.animate(to: camera)
    }
}




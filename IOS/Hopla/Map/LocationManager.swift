//
//  LocationManager.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 03/03/2025.
//

import CoreLocation

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private var locationManager = CLLocationManager()

    @Published var userLocation: CLLocation? = nil
    @Published var latitude: Double?
    @Published var longitude: Double?

    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()

        // Check if authorization is already granted
        checkLocationAuthorization()
        print("Authorization status: \(locationManager.authorizationStatus.rawValue)")
    }

    func checkLocationAuthorization() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            switch self.locationManager.authorizationStatus {
            case .authorizedWhenInUse, .authorizedAlways:
                self.locationManager.startUpdatingLocation()
                print("✅ Location access granted!")
            case .denied, .restricted:
                print("❌ Location access denied.")
            case .notDetermined:
                print("📍 Location permission not asked yet. Requesting now...")
                self.locationManager.requestWhenInUseAuthorization() // Ensure it's called on main thread
            @unknown default:
                print("⚠️ Unknown location authorization status.")
            }
        }
    }


    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationAuthorization() // Check again when permission changes
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        userLocation = location
        latitude = location.coordinate.latitude
        longitude = location.coordinate.longitude
        print("Latitude: \(latitude ?? 0), Longitude: \(longitude ?? 0)")
        print("Updated location: \(location.coordinate.latitude), \(location.coordinate.longitude)")
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to get location: \(error.localizedDescription)")
    }
}


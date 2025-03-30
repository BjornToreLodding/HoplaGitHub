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

        checkLocationAuthorization()
        print("Authorization status: \(locationManager.authorizationStatus.rawValue)")
    }

    func checkLocationAuthorization() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            if #available(iOS 14.0, *) {
                switch self.locationManager.authorizationStatus {
                case .authorizedWhenInUse, .authorizedAlways:
                    self.locationManager.startUpdatingLocation()
                    print("✅ Location access granted!")
                case .denied, .restricted:
                    print("❌ Location access denied.")
                    NotificationCenter.default.post(name: .locationAccessDenied, object: nil)
                case .notDetermined:
                    print("📍 Location permission not asked yet. Requesting now...")
                    self.locationManager.requestWhenInUseAuthorization()
                @unknown default:
                    print("⚠️ Unknown location authorization status.")
                }
            } else {
                switch CLLocationManager.authorizationStatus() {
                case .authorizedWhenInUse, .authorizedAlways:
                    self.locationManager.startUpdatingLocation()
                    print("✅ Location access granted!")
                case .denied, .restricted:
                    print("❌ Location access denied.")
                    NotificationCenter.default.post(name: .locationAccessDenied, object: nil)
                case .notDetermined:
                    print("📍 Location permission not asked yet. Requesting now...")
                    self.locationManager.requestWhenInUseAuthorization()
                @unknown default:
                    print("⚠️ Unknown location authorization status.")
                }
            }
        }
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationAuthorization()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        DispatchQueue.main.async {
            self.userLocation = location
            self.latitude = location.coordinate.latitude
            self.longitude = location.coordinate.longitude
            print("Latitude: \(self.latitude ?? 0), Longitude: \(self.longitude ?? 0)")
            print("Updated location: \(location.coordinate.latitude), \(location.coordinate.longitude)")
        }
        NotificationCenter.default.post(name: .didUpdateLocation, object: location)
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to get location: \(error.localizedDescription)")
    }
    
    
}

extension Notification.Name {
        static let didUpdateLocation = Notification.Name("didUpdateLocation")
        static let locationAccessDenied = Notification.Name("locationAccessDenied")
    }

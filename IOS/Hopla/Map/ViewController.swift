//
//  ViewController.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/03/2025.
//

import UIKit
import CoreLocation
import GoogleMaps

class ViewController: UIViewController, CLLocationManagerDelegate {
    
    var locationManager = CLLocationManager() // Initialize directly

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set up the location manager
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        // Request location permission when the view appears
        checkLocationAuthorization()
    }

    func checkLocationAuthorization() {
        let status = locationManager.authorizationStatus
        print("🔍 Current Authorization Status: \(status.rawValue)") // Debugging

        switch status {
        case .notDetermined:
            print("📍 Permission not asked yet. Requesting now...")
            locationManager.requestWhenInUseAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            print("✅ Location access granted!")
            locationManager.startUpdatingLocation()
        case .restricted, .denied:
            print("❌ Location access denied. Please enable it in settings.")
        @unknown default:
            print("⚠️ Unknown location authorization status.")
        }
    }
    
    // Get current location
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.first {
            print("📍 Current location: \(location.coordinate.latitude), \(location.coordinate.longitude)")
            locationManager.stopUpdatingLocation() // Stop updates after obtaining the location
        }
    }

    // Handle errors in location manager
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("⚠️ Failed to get location: \(error.localizedDescription)")
    }
}


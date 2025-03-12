//
//  AppDelegate.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 03/03/2025.
//

import UIKit
import GoogleMaps
import CoreLocation

class AppDelegate: NSObject, UIApplicationDelegate, CLLocationManagerDelegate {
    var locationManager = CLLocationManager()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        GMSServices.provideAPIKey("AIzaSyC-2qlkvP8M1pgfnRMG0rr76SlxaI6jzwQ")

        // Request location permissions
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        
        return true
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .notDetermined:
            print("📍 Location permission not yet determined.")
        case .restricted, .denied:
            print("⛔ Location access denied.")
        case .authorizedWhenInUse, .authorizedAlways:
            print("✅ Location permission granted!")
            locationManager.startUpdatingLocation()
        @unknown default:
            print("⚠️ Unknown location authorization status.")
        }
    }
}

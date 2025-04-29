//
//  AppDelegate.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 03/03/2025.
//

import UIKit
import GoogleMaps
import CoreLocation
import KeychainAccess

class AppDelegate: NSObject, UIApplicationDelegate, CLLocationManagerDelegate {
    var locationManager = CLLocationManager()
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // 0) If running under UI tests, clear defaults & keychain:
        let args = ProcessInfo.processInfo.arguments
        if args.contains("-UITest_ResetAuthentication") {
            UserDefaults.standard.removeObject(forKey: "isLoggedIn")
            do {
                try Keychain(service: "com.yourcompany.Hopla").removeAll()
            } catch {
                print("⚠️ Could not clear keychain: \(error)")
            }
        }
        // 1) Google Maps setup
        GMSServices.provideAPIKey("AIzaSyC-2qlkvP8M1pgfnRMG0rr76SlxaI6jzwQ")
        
        // 2) Request location permissions
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

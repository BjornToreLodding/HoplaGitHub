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
    // Core Location manager instance to handle location updates and permissions
    var locationManager = CLLocationManager()
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Grab any launch arguments passed in (UI tests)
        let args = ProcessInfo.processInfo.arguments
        
        // 0a) If running under UI tests, pre-set the “isLoggedIn” flag before SwiftUI initializes
        if args.contains("-UITestMode") {
            UserDefaults.standard.set(true, forKey: "isLoggedIn")
        }
        
        // 0b) If tests need a clean slate, remove the login flag and clear keychain
        if args.contains("-UITest_ResetAuthentication") {
            // Remove the login flag from UserDefaults
            UserDefaults.standard.removeObject(forKey: "isLoggedIn")
            do {
                // Wipe all entries in the app’s keychain namespace
                try Keychain(service: "com.yourcompany.Hopla").removeAll()
            } catch {
                // Log any errors encountered while clearing the keychain
                print("Could not clear keychain: \(error)")
            }
        }
        
        // 1) Initialize Google Maps SDK with the API key
        GMSServices.provideAPIKey("AIzaSyC-2qlkvP8M1pgfnRMG0rr76SlxaI6jzwQ")
        
        // 2) Set up location manager to ask for and start receiving location updates
        locationManager.delegate = self                                    // Route delegate callbacks here
        locationManager.requestWhenInUseAuthorization()                    // Ask user for "when in use" permission
        locationManager.startUpdatingLocation()                            // Begin location updates immediately
        
        return true  // Indicate successful launch setup
    }
    
    // CLLocationManagerDelegate callback whenever authorization status changes
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .notDetermined:
            // User hasn’t been prompted yet
            print("Location permission not yet determined.")
        case .restricted, .denied:
            // User explicitly denied or system-wide restriction
            print("Location access denied.")
        case .authorizedWhenInUse, .authorizedAlways:
            // Permission granted in some form
            print("Location permission granted!")
            locationManager.startUpdatingLocation()  // (Re)start updates if needed
        @unknown default:
            // Future cases not covered by current SDK
            print("Unknown location authorization status.")
        }
    }
}

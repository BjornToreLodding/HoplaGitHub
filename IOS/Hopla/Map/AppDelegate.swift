//
//  AppDelegate.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 03/03/2025.
//

import UIKit
import GoogleMaps

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        GMSServices.provideAPIKey("AIzaSyC-2qlkvP8M1pgfnRMG0rr76SlxaI6jzwQ") // Google Maps API key
        return true
    }
}


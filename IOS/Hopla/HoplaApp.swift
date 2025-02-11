//
//  HoplaApp.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 21/01/2025.
//

import SwiftUI

@main
struct HoplaApp: App {
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login state

    var body: some Scene {
        WindowGroup {
            if isLoggedIn {
                ContentView() // Go to Home if logged in
            } else {
                Login() // Go to Login if not logged in
            }
        }
    }
}

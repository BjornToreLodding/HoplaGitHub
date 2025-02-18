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
    @StateObject private var vm = ViewModel() // Initialize ViewModel

    var body: some Scene {
        WindowGroup {
            if isLoggedIn {
                ContentView()
                    .environmentObject(vm) // Pass ViewModel to environment
            } else {
                Login()
            }
        }
    }
}

//
//  HoplaApp.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 21/01/2025.
//

import SwiftUI
import GoogleMaps
import CoreLocation

@main
struct HoplaApp: App {
    @AppStorage("isLoggedIn") private var isLoggedIn = false
    @AppStorage("isDarkMode") private var isDarkMode = false
    @StateObject private var vm = ViewModel()
    @Environment(\.colorScheme) var colorScheme
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    @StateObject private var locationManager = LocationManager()

    var body: some Scene {
        WindowGroup {
            ZStack {
                // Background Color
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea(edges: .all)
                
                VStack(spacing: 0) {
                    // Only show the logo and green background if the user is logged in
                    if isLoggedIn {
                        VStack {
                            Image("LogoUtenBakgrunn")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 100, height: 40)
                                .padding(.bottom, 5)
                        }
                        .frame(maxWidth: .infinity)
                        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    }

                    // Main App Content
                    if isLoggedIn {
                        MainTabView()
                            .environmentObject(vm)
                    } else {
                        Login()
                    }
                }

            }
            .onAppear {
                setupNavigationBar(for: colorScheme)
                setupTabBarAppearance(for: colorScheme)
                
                if let lat = locationManager.latitude, let lon = locationManager.longitude {
                    print("Current Location - Latitude: \(lat), Longitude: \(lon)")
                } else {
                    print("Waiting for location...")
                }
            }
            .onChange(of: colorScheme) { newColorScheme in
                setupNavigationBar(for: newColorScheme)
                setupTabBarAppearance(for: newColorScheme)
            }
            .preferredColorScheme(isDarkMode ? .dark : .light)
        }
    }
}

struct MainTabView: View {
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        TabView {
            NavigationStack { Home() }
                .tabItem {
                    Image(systemName: "house")
                    Text("Home")
                }
            
            NavigationStack { Hikes() }
                .tabItem {
                    Image(systemName: "map")
                    Text("Hikes")
                }
            
            NavigationStack { NewHike() }
                .tabItem {
                    Image(systemName: "plus.circle")
                    Text("New Hike")
                }
            
            NavigationStack { Community() }
                .tabItem {
                    Image(systemName: "person.2.circle")
                    Text("Community")
                }
            
            NavigationStack { Profile(loginViewModel: LoginViewModel()) }
                .tabItem {
                    Image(systemName: "person")
                    Text("Profile")
                }
        }
        .tint(colorScheme == .dark ? .white : .black)
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
}

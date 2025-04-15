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
    @StateObject var vm = ViewModel(profileViewModel: ProfileViewModel()) 
    @StateObject private var loginViewModel = LoginViewModel() // Create an instance
    @Environment(\.colorScheme) var colorScheme
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    @State private var navigationPath = NavigationPath()
    @StateObject private var locationManager = LocationManager()
    @StateObject private var myHikeVM = MyHikeViewModel()
    
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea(edges: .all)
                
                VStack(spacing: 0) {
                    if isLoggedIn {
                        VStack {
                            Image("LogoUtenBakgrunn")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 100, height: 40)
                                .padding(.bottom, 5)
                        }
                        .frame(maxWidth: .infinity)
                        .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    }
                    
                    if isLoggedIn {
                        MainTabView(navigationPath: $navigationPath)
                            .environmentObject(vm)
                            .environmentObject(loginViewModel)
                            .environmentObject(locationManager)
                            .environmentObject(myHikeVM)
                    } else {
                        Login(viewModel: LoginViewModel(), loginViewModel: loginViewModel)
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
    @Binding var navigationPath: NavigationPath
    @EnvironmentObject var vm: ViewModel
    @StateObject private var loginViewModel = LoginViewModel()
    
    var body: some View {
        TabView {
            NavigationStack { Home() }
                .tabItem {
                    Image(systemName: "house")
                    Text("Home")
                }
            
            NavigationStack { Hikes(viewModel: HikeService()) }
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
            
            NavigationStack {
                Profile(
                    profileViewModel: ProfileViewModel(), loginViewModel: LoginViewModel(),
                    
                    navigationPath: $navigationPath
                )
            }
            .tabItem {
                Image(systemName: "person")
                Text("Profile")
            }
        }
        .tint(colorScheme == .dark ? .white : .black)
        .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
    }
}


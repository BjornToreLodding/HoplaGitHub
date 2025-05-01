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
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate
    @AppStorage("isLoggedIn") private var isLoggedIn = false
    @AppStorage("isDarkMode") private var isDarkMode = false
    
    @StateObject var vm = ViewModel(profileViewModel: ProfileViewModel())
    @StateObject private var loginViewModel = LoginViewModel()
    @StateObject private var locationManager = LocationManager()
    @StateObject private var myHikeVM = MyHikeViewModel()
    @State private var navigationPath = NavigationPath()
    
    init() {
        let args = CommandLine.arguments
        
        // legacy reset-data tests used this one:
        if args.contains("-UITest_ResetData") {
          UserDefaults.standard.set(true, forKey: "isLoggedIn")
        }
        // your new mode for profile/tests:
        if args.contains("-UITestMode") {
          UserDefaults.standard.set(true, forKey: "isLoggedIn")
        }
        
        // your existing reset-auth cleanup
        if args.contains("-UITest_ResetAuthentication") {
          UserDefaults.standard.removeObject(forKey: "isLoggedIn")
          // keychain wipe…
        }

        setupTabBarAppearance(forDarkMode: isDarkMode)
        setupNavigationBar(forDarkMode: isDarkMode)
      }
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                // Full‑screen main background
                AdaptiveColor.background
                    .color(for: isDarkMode ? .dark : .light)
                    .ignoresSafeArea()
                
                VStack(spacing: 0) {
                    if isLoggedIn {
                        header
                    }
                    
                    if isLoggedIn {
                        MainTabView(navigationPath: $navigationPath,
                                    isDarkMode: isDarkMode)
                        .environmentObject(vm)
                        .environmentObject(loginViewModel)
                        .environmentObject(locationManager)
                        .environmentObject(myHikeVM)
                        .accessibilityIdentifier("homeScreenRoot")
                    } else {
                        Login(viewModel: LoginViewModel(),
                              loginViewModel: loginViewModel)
                    }
                }
            }
            .onAppear {
                DispatchQueue.main.async {
                    setupTabBarAppearance(forDarkMode: isDarkMode)
                    setupNavigationBar(forDarkMode: isDarkMode)
                }
            }
            .onChange(of: isDarkMode) { newVal in
                setupNavigationBar(forDarkMode: newVal)
                setupTabBarAppearance(forDarkMode: newVal)
            }
            .preferredColorScheme(isDarkMode ? .dark : .light)
        }
    }
    
    private var header: some View {
        HStack {
            Spacer()
            Image("logo_white_without_background")
                .resizable()
                .scaledToFit()
                .frame(width: 100, height: 40)
            Spacer()
        }
        .frame(height: 50)
        .background(
            AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                .color(for: isDarkMode ? .dark : .light)
        )
    }
}

struct MainTabView: View {
    @Binding var navigationPath: NavigationPath
    let isDarkMode: Bool
    
    // Select the Hikes tab (index 1) when launched with -UITest_ResetData
    @State private var selectedTab = ProcessInfo.processInfo.arguments.contains("-UITest_ResetData") ? 1 : 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack { Home() }
                .tabItem { Image(systemName: "house"); Text("Home") }
                .tag(0)
            
            NavigationStack { Hikes(viewModel: HikeService.shared) }
                .tabItem { Image(systemName: "map"); Text("Hikes") }
                .tag(1)
            
            NavigationStack { NewHike() }
                .tabItem { Image(systemName: "plus.circle"); Text("New Hike") }
                .tag(2)
            
            NavigationStack { Community() }
                .tabItem { Image(systemName: "person.2.circle"); Text("Community") }
                .tag(3)
            
            NavigationStack {
                Profile(profileViewModel: ProfileViewModel(),
                        loginViewModel: LoginViewModel(),
                        navigationPath: $navigationPath)
            }
            .tabItem { Image(systemName: "person"); Text("Profile") }
            .tag(4)
        }
    }
}

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
    // Bridge to the UIKit AppDelegate for Google Maps & location setup
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate
    // Persisted flag for user login state
    @AppStorage("isLoggedIn") private var isLoggedIn = false
    // Persisted flag for dark/light mode preference
    @AppStorage("isDarkMode") private var isDarkMode = false
    
    // Main view models as state objects for dependency injection
    @StateObject var vm = ViewModel(profileViewModel: ProfileViewModel())
    @StateObject private var loginViewModel = LoginViewModel()
    @StateObject private var locationManager = LocationManager()
    @StateObject private var myHikeVM = MyHikeViewModel()
    // Navigation path for programmatic navigation within the app
    @State private var navigationPath = NavigationPath()
    
    init() {
        let args = CommandLine.arguments
        
        // Legacy UI test reset-data mode: mark as logged in
        if args.contains("-UITest_ResetData") {
            UserDefaults.standard.set(true, forKey: "isLoggedIn")
        }
        // New UI test mode: also mark as logged in
        if args.contains("-UITestMode") {
            UserDefaults.standard.set(true, forKey: "isLoggedIn")
        }
        // UI test reset-auth: clear login flag and (optionally) keychain
        if args.contains("-UITest_ResetAuthentication") {
            UserDefaults.standard.removeObject(forKey: "isLoggedIn")
            // TODO: wipe keychain entries here if needed
        }
        
        // Configure appearance initially based on stored dark mode setting
        setupTabBarAppearance(forDarkMode: isDarkMode)
        setupNavigationBar(forDarkMode: isDarkMode)
    }
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                // Background that adapts to light/dark mode
                AdaptiveColor.background
                    .color(for: isDarkMode ? .dark : .light)
                    .ignoresSafeArea()
                
                VStack(spacing: 0) {
                    // Show custom header only when logged in
                    if isLoggedIn {
                        header
                    }
                    
                    if isLoggedIn {
                        // Main tabbed interface injected with all needed view models
                        MainTabView(navigationPath: $navigationPath,
                                    isDarkMode: isDarkMode)
                        .environmentObject(vm)
                        .environmentObject(loginViewModel)
                        .environmentObject(locationManager)
                        .environmentObject(myHikeVM)
                        .accessibilityIdentifier("homeScreenRoot")
                    } else {
                        // Login screen when not authenticated
                        Login(viewModel: LoginViewModel(),
                              loginViewModel: loginViewModel)
                    }
                }
            }
            .onAppear {
                // Reapply appearance settings on view appearance
                DispatchQueue.main.async {
                    setupTabBarAppearance(forDarkMode: isDarkMode)
                    setupNavigationBar(forDarkMode: isDarkMode)
                }
            }
            .onChange(of: isDarkMode) { newVal in
                // Update appearance whenever dark mode setting changes
                setupNavigationBar(forDarkMode: newVal)
                setupTabBarAppearance(forDarkMode: newVal)
            }
            // Enforce the preferred color scheme for the window
            .preferredColorScheme(isDarkMode ? .dark : .light)
        }
    }
    
    /// Custom header shown above the tabs when logged in
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
    // Binding to control navigation path from parent
    @Binding var navigationPath: NavigationPath
    // Propagate dark mode into child views if needed
    let isDarkMode: Bool
    
    // Pre-select the Hikes tab for specific UI test argument
    @State private var selectedTab = ProcessInfo.processInfo.arguments.contains("-UITest_ResetData") ? 1 : 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Home tab
            NavigationStack { Home() }
                .tabItem { Image(systemName: "house"); Text("Home") }
                .tag(0)
            
            // Hikes tab showing map of hikes
            NavigationStack { Hikes(viewModel: HikeService.shared) }
                .tabItem { Image(systemName: "map"); Text("Hikes") }
                .tag(1)
            
            // New Hike creation tab
            NavigationStack { NewHike() }
                .tabItem { Image(systemName: "plus.circle"); Text("New Hike") }
                .tag(2)
            
            // Community tab for social features
            NavigationStack { Community() }
                .tabItem { Image(systemName: "person.2.circle"); Text("Community") }
                .tag(3)
            
            // Profile tab with dependency injection for profile/login VMs
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

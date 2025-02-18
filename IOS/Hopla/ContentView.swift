import SwiftUI

// Struct with hikes
struct Hike: Identifiable {
    let id = UUID() // ID
    let name: String // Name of hike
    let imageName: String // Image name
}

struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme
    @AppStorage("isDarkMode") private var isDarkMode = false // Global dark mode setting
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login state
    @State private var path = NavigationPath() // Manage navigation manually
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Background
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea(edges: .all)
                
                // Bottom Green Rectangle
                VStack {
                    Spacer() // Pushes rectangle to the bottom
                    Rectangle()
                        .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                        .frame(height: 10)
                        .ignoresSafeArea(edges: .bottom) // Ensures it covers all bottom area
                }
                
                // Conditional View Display
                if isLoggedIn {
                    // Home screen if logged in
                    TabView {
                        Home()
                            .tabItem {
                                Image(systemName: "house")
                                Text("Home")
                            }
                        
                        Hikes()
                            .tabItem {
                                Image(systemName: "map")
                                Text("Hikes")
                            }
                        
                        NewHike()
                            .tabItem {
                                Image(systemName: "plus.circle")
                                Text("New Hike")
                            }
                        
                        Community()
                            .tabItem {
                                Image(systemName: "person.2.circle")
                                Text("Community")
                            }
                        Profile()
                            .tabItem {
                                Image(systemName: "person")
                                Text("Profile")
                            }
                        
                    }
                    .tint(colorScheme == .dark ? .white : .black)
                    .onAppear {
                        setupTabBarAppearance(for: colorScheme) // Apply correct tab bar color
                    }
                    
                    // Overlay Logo
                    VStack {
                        Image("LogoUtenBakgrunn")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 100, height: 40)
                            .padding(.top, -10)
                        Spacer()
                    }
                    .frame(maxWidth: .infinity)
                } else {
                    // Login screen if not logged in
                    Login() // Your login screen
                }
            }
            .onAppear {
                setupNavigationBar(for: colorScheme)
                setupTabBarAppearance(for: colorScheme)
            }
            .onChange(of: colorScheme) { newColorScheme in
                setupNavigationBar(for: newColorScheme)
                setupTabBarAppearance(for: newColorScheme) // Ensure the tab bar updates
            }
            .preferredColorScheme(isDarkMode ? .dark : .light)
        }
    }
}

#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

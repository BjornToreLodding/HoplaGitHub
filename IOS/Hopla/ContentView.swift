// File to view everything together

import SwiftUI

// Struct with hikes
struct Hike: Identifiable {
    let id = UUID() // ID
    let name: String // Name of hike
    let imageName: String // Image name
}

struct ContentView: View {
    // To detect light/dark mode
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Background color for the entire app
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea() // Ensures it covers the whole screen
                
                TabView { // Tab at the bottom of screen
                    Home()
                        .tabItem {
                            Image(systemName: "house") // Symbol name
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
                            Text("New hike")
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
            }
            .toolbar { // Logo
                ToolbarItem(placement: .principal) {
                    Image("LogoUtenBakgrunn")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 100, height: 50) // Size
                        
                }
            }
            .navigationBarTitleDisplayMode(.inline) // Inline navigation bar title (removes extra space)
        }
        .onAppear {
            setupNavigationBar(for: colorScheme) // Ensure navbar updates on view load
        }
        .onChange(of: colorScheme) {
            // Navbar updates immediately when switching between light and dark mode
            setupNavigationBar(for: colorScheme)
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

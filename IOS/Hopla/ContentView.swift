// File to view everything together

import SwiftUI

// Struct with hikes
struct Hike: Identifiable {
    let id = UUID() // ID
    let name: String // Name of hike
    let imageName: String // Image name
}

struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationStack {
            ZStack {
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea()
                
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
                    
                    // Nested NavigationStack inside TabView for Profile
                    ProfileNavigationWrapper()
                        .tabItem {
                            Image(systemName: "person")
                            Text("Profile")
                        }
                }
            }
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Image("LogoUtenBakgrunn")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 100, height: 50)
                }
            }
            .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear {
            setupNavigationBar(for: colorScheme)
        }
        .onChange(of: colorScheme) {
            setupNavigationBar(for: colorScheme)
        }
    }
}

struct ProfileNavigationWrapper: View {
    var body: some View {
        NavigationStack {
            Profile()
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

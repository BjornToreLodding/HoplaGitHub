import SwiftUI

// Struct with hikes
struct Hike: Identifiable {
    let id = UUID()
    let name: String
    let imageName: String // Added property for image name
}

// Content view
struct ContentView: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    var body: some View {
        TabView {
            Home()
                .tabItem {
                    Image(systemName: "house")
                    Text("Hjem")
                }
            Map()
                .tabItem {
                    Image(systemName: "map")
                    Text("Kart")
                }
            Profile()
                .tabItem {
                    Image(systemName: "person")
                    Text("Profil")
                }
        }
    }
}

// Preview
#Preview {
    ContentView()
}

// Home tab
struct Home: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    let hikes = [
            Hike(name: "Gjøvikrunden", imageName: "Gjøvik.jpg"),
            Hike(name: "Preikestolen", imageName: "preikestolen.jpg"),
            Hike(name: "Vågan", imageName: "Gjøvik.jpg"),
            Hike(name: "Bobby", imageName: "preikestolen.jpg"),
            Hike(name: "Våganes", imageName: "Gjøvik.jpg"),
            Hike(name: "Bob", imageName: "preikestolen.jpg")
        ]
    
 var body: some View {
     NavigationStack {
         ZStack {
             // Background
             (AdaptiveColor.background.color(for: colorScheme))
                 .ignoresSafeArea() // Ensure it covers the entire screen
             List(hikes) { hike in // List of hikes
                 HStack {
                     if let uiImage = UIImage(named: hike.imageName) {
                         // Custom image
                         Image(uiImage: uiImage)
                             .resizable()
                             .scaledToFill()
                             .frame(width: 100, height: 100)
                     } else {
                         // SF Symbol
                         Image(systemName: hike.imageName)
                             .resizable()
                             .scaledToFit()
                             .frame(width: 100, height: 100)
                             .foregroundColor(.green)
                     }
                     Text(hike.name)
                         .font(.headline)
                         .padding(.horizontal, 20)
                     Spacer()
                 }
                 .padding()
                 .listRowBackground(AdaptiveColor.background.color(for: colorScheme))
                 .onTapGesture { // When clicking on a hike
                     print(hike.name)
                 }
             }
             .scrollContentBackground(.hidden) // Removes the default list background
         }
         .navigationTitle("Løyper")
     }
 }
}

struct Profile: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    var body: some View {
        Text("Profil")
    }
}

struct Map: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    var body: some View {
        Text("Kart")
    }
}

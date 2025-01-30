//
//  Home.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Home: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    @State private var selectedFilter = "All Posts" // Track selected filter
    
    // Sample data
    let posts = [
        (image: "HorseImage", comment: "This is the first post."),
        (image: "HorseImage2", comment: "This is the second post."),
        (image: "HorseImage3", comment: "This is the third post."),
    ]
    
    var body: some View {
        NavigationStack {
            // Custom filter bar below the logo
            filterBar
            ZStack {
                // Background color for the entire app
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea()

                VStack {
                    
                    // Posts list
                    ScrollView {
                        VStack(spacing: 10) {
                            ForEach(posts, id: \.image) { post in
                                PostContainer(imageName: post.image, comment: post.comment)
                            }
                        }
                        .padding()
                    }
                }
            }
        }
            .navigationTitle("Posts") // This comes from ContentView's NavigationStack
        }
    
    // MARK: - Filter Bar Below Logo
        private var filterBar: some View {
            HStack {
                Picker("Filter", selection: $selectedFilter) {
                    Text("All Posts").tag("All Posts")
                    Text("Friends").tag("Friends")
                    Text("Area").tag("Area")
                    Text("Popular").tag("Popular")
                    Text("Updates").tag("Updates")
                }
                .pickerStyle(SegmentedPickerStyle()) // Makes it look like a real navigation bar
            }
            .background(Color.lighterGreen)
        }
}

// The posts
struct PostContainer: View {
    var imageName: String
    var comment: String
    
    var body: some View {
        VStack {
            Image(imageName)
                .resizable()
                .scaledToFill()
                .frame(width: 360, height: 260) // Square image container
                .clipped()
            
            Text(comment)
                .padding(.top, 50)
                .font(.body)
        }
        .frame(width: 340, height: 320) // Square container
        .padding()
        .background(Color.white) // Light background for each post container
    }
}

#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

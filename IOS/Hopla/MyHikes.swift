//
//  MyHikes.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//

import SwiftUI

struct MyHikes: View {
    @Environment(\.colorScheme) var colorScheme
    
    // Sample data
    let posts = [
        (image: "HorseImage", comment: "This is the first post."),
        (image: "HorseImage2", comment: "This is the second post."),
        (image: "HorseImage3", comment: "This is the third post."),
    ]
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Background
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea(edges: .all)
                
                VStack {
                    // Top of screen is green
                    Rectangle()
                        .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                        .frame(height: 110)
                        .edgesIgnoringSafeArea(.top)
                        .padding(.top, -430)
                }
                // ScrollView with Posts
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(posts, id: \.image) { post in
                            PostContainer(imageName: post.image, comment: post.comment, colorScheme: colorScheme)
                        }
                    }
                    .padding()
                }
            }
            .padding(.top, 20)
        }
        .navigationTitle("My Hikes") // Keeps navigation title
    }
}




#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

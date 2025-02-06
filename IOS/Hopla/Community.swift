//
//  Community.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Community: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    @State private var selectedFilter = "Location" // Track selected filter
    
    // Sample data
    let groups = [
        (image: "Group", comment: "This is the first group."),
        (image: "Group2", comment: "This is the second group."),
        (image: "Group3", comment: "This is the third group."),
    ]
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) { // Ensure no extra spacing
                filterBar
                ZStack {
                    // Background color for the entire app
                    AdaptiveColor.background.color(for: colorScheme)
                        .ignoresSafeArea()
                    VStack {
                        // Groups list
                        ScrollView {
                            VStack(spacing: 10) {
                                ForEach(groups, id: \.image) { post in
                                    GroupContainer(imageName: post.image, comment: post.comment)
                                }
                            }
                            .padding()
                        }
                    }
                }
            }
        }
    }
    // MARK: - Filter Bar Below Logo
        private var filterBar: some View {
            HStack {
                Picker("Filter", selection: $selectedFilter) {
                    Text("Location").tag("Location")
                    Text("Liked").tag("Liked")
                }
                .padding(.top, 30)
                .pickerStyle(SegmentedPickerStyle()) // Makes it look like a real navigation bar
            }
            .frame(height: 60)
            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme)) // Dynamic background
        }
}


// The posts
struct GroupContainer: View {
    var imageName: String
    var comment: String
    
    var body: some View {
        VStack {
            Image(imageName)
                .resizable()
                .scaledToFill()
                .frame(width: 360, height: 100) // Square image container
                .clipped()
            
            Text(comment)
                .padding(.bottom, 5)
                .font(.body)
                .multilineTextAlignment(.center)
        }
        .frame(width: 340, height: 110) // Square container
        .padding()
        .background(Color.white) // Light background for each group container
    }
}


#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

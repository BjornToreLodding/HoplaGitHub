//
//  Home.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Home: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    // Track selected filter
    @State private var selectedFilter: String = "globe"

    // To select a filter
    enum FilterOption: String, CaseIterable, Identifiable {
            case globe
            case location
            case people
            case star
            case warning

            var id: String { self.rawValue }

            var systemImage: String {
                switch self {
                case .globe: return "globe"
                case .location: return "location"
                case .people: return "person.2"
                case .star: return "star"
                case .warning: return "exclamationmark.circle"
                }
            }
        }
    
    // Sample data
    let posts = [
        (image: "HorseImage", comment: "This is the first post."),
        (image: "HorseImage2", comment: "This is the second post."),
        (image: "HorseImage3", comment: "This is the third post."),
    ]
    
    var body: some View {
        NavigationStack {
            ZStack {
                Rectangle()
                    .fill(AdaptiveColor.background.color(for: colorScheme))
                    .ignoresSafeArea()
                
                VStack {
                    filterBar // Place inside VStack to avoid pushing content down
                    
                    ScrollView {
                        VStack(spacing: 10) {
                            ForEach(posts, id: \.image) { post in
                                PostContainer(imageName: post.image, comment: post.comment, colorScheme: colorScheme)
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
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                Image(systemName: "globe").tag("globe")
                Image(systemName: "location").tag("location")
                Image(systemName: "person.2").tag("person.2")
                Image(systemName: "star").tag("star")
                Image(systemName: "exclamationmark.circle").tag("exclamationmark.circle")
            }
            .padding(.top, 30)
            .pickerStyle(SegmentedPickerStyle()) // Makes it look like a real navigation bar
        }
        .frame(height: 60)
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme)) // Dynamic background
    }
}


struct PostContainer: View {
    var imageName: String
    var comment: String
    var colorScheme: ColorScheme // Add colorScheme as a parameter
    
    var body: some View {
        ZStack {
            Rectangle()
                .fill(AdaptiveColor(light: .white, dark: .darkPostBackground).color(for: colorScheme)) // Dynamic background
                .ignoresSafeArea()

            VStack {
                Image(imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 360, height: 260) // Square image container
                    .clipped()
                
                Text(comment)
                    .padding(.top, 50)
                    .font(.body)
                    .adaptiveTextColor(light: .black, dark: .white) // Text color adapts to theme
            }
            .frame(width: 340, height: 320) // Square container
            .padding()
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

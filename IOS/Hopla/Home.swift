//
//  Home.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Home: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var selectedFilter: String = "globe"

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

                VStack(spacing: 0) { // **Spacing set to 0**
                    filterBar

                    ScrollView {
                        VStack(spacing: 10) {
                            ForEach(posts, id: \.image) { post in
                                PostContainer(imageName: post.image, comment: post.comment, colorScheme: colorScheme)
                            }
                        }
                    }
                    .padding(.top, 0) // **Remove unnecessary padding**
                }
            }
        }
    }

    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                Image(systemName: "globe").tag("globe")
                Image(systemName: "location").tag("location")
                Image(systemName: "person.2").tag("person.2")
                Image(systemName: "star").tag("star")
                Image(systemName: "exclamationmark.circle").tag("exclamationmark.circle")
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
}

struct PostContainer: View {
    var imageName: String
    var comment: String
    var colorScheme: ColorScheme

    var body: some View {
        ZStack {
            Rectangle()
                .fill(AdaptiveColor(light: .white, dark: .darkPostBackground).color(for: colorScheme))
                .ignoresSafeArea()

            VStack {
                Image(imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 360, height: 260)
                    .clipped()

                Text(comment)
                    .padding(.top, 10) 
                    .font(.body)
                    .adaptiveTextColor(light: .black, dark: .white)
            }
            .frame(width: 370, height: 320)
            .padding()
        }
    }
}


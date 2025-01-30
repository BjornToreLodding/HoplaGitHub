//
//  Profile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Profile: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    
    var body: some View {
        NavigationStack { // Navigation
            ZStack {
                // Background color for the entire app
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea()
                VStack {
                    Image("Profile")
                        .resizable()
                        .frame(width:100, height:100)
                        .clipShape(Circle()) // Image is circle
                }
                .navigationTitle("Profile") // Title of nav bar
            }
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

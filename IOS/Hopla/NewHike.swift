//
//  NewHike.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct NewHike: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    
    var body: some View {
        NavigationStack { // Navigation
            ZStack {
                // Background color for the entire app
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea()
                VStack {
                    Text("New Hike")
                }
                .navigationTitle("New Hike") // Title of nav bar
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

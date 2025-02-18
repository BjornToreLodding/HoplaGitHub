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
        NavigationStack {
            ZStack {
                // Background color for the entire app
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea()
                VStack {
                    // Top of screen is green
                    Rectangle()
                        .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                        .frame(height: 110)
                        .edgesIgnoringSafeArea(.top)
                        .padding(.top, -420)
                }
            }
            .navigationTitle("New Hike") // Title of nav bar
            .toolbar {
                ToolbarItem(placement: .principal) { // Puts the logo in the center
                    Image("LogoUtenBakgrunn")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 100, height: 40)
                }
            }
            .toolbarBackground(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme), for: .navigationBar)
            .toolbarColorScheme(.light, for: .navigationBar) // Ensures icons and text are readable
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

//
//  NewHike.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import GoogleMaps
import GooglePlaces

struct NewHike: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Embed MapView here so it takes the entire screen
                MapView()
                    .edgesIgnoringSafeArea(.all) // This will make the map take up the entire screen
                
                VStack {
                    // Top of screen is green
                    Rectangle()
                        .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                        .frame(height: 110)
                        .edgesIgnoringSafeArea(.top)
                        // Remove the large negative padding to ensure the rectangle is visible
                        .padding(.top, 0) // Adjust this value to move the rectangle down if needed
                    
                    Spacer()
                    
                    // The time, distance and start/stop button
                    HStack {
                        Text("Time")
                            .frame(maxWidth: .infinity, alignment: .center) // Make Text take up equal space and align it in the center
                        
                        Button(action: {}) {
                            Text("Start")
                                .foregroundColor(.white)  // Set the text color to white (or any color)
                                .frame(width: 70, height: 70)  // Define width and height to make it a circle
                                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                .clipShape(Circle())  // Make the button circular
                                .overlay(Circle().stroke(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme), lineWidth: 4))  // Optional: Adds a border around the circle
                        }
                        .frame(maxWidth: .infinity, alignment: .center) // Make Button take up equal space and align it in the center
                        
                        Text("Distance")
                            .frame(maxWidth: .infinity, alignment: .center) // Make Text take up equal space and align it in the center
                    }

                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 75)
                    .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
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

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
    @Environment(\.colorScheme) var colorScheme
    @StateObject private var locationManager = LocationManager()

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) { // Use VStack to stack the map and bottom bar
                MapView(locationManager: locationManager)
                    .frame(maxHeight: .infinity) // Let the map take available space
                    .edgesIgnoringSafeArea(.top) // Only ignore top safe area to keep Google Maps buttons visible
                
                HStack {
                    Text("Time")
                        .frame(maxWidth: .infinity, alignment: .center)
                    
                    Button(action: {}) {
                        Text("Start")
                            .foregroundColor(.white)
                            .frame(width: 70, height: 70)
                            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                            .clipShape(Circle())
                            .overlay(Circle().stroke(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme), lineWidth: 4))
                    }
                    .frame(maxWidth: .infinity, alignment: .center)
                    
                    Text("Distance")
                        .frame(maxWidth: .infinity, alignment: .center)
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 75)
                .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
            }
            .toolbarBackground(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme), for: .navigationBar)
            .toolbarColorScheme(.light, for: .navigationBar)
        }
    }
}

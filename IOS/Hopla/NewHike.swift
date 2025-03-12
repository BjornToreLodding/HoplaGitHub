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
    @StateObject private var locationManager = LocationManager() // Shared instance

    var body: some View {
        NavigationStack {
            ZStack {
                MapView(locationManager: locationManager) // Pass it to MapView
                    .edgesIgnoringSafeArea(.all)
                
                VStack {
                    HStack {
                        Text("Time")
                            .frame(maxWidth: .infinity, alignment: .center)
                        
                        Button(action: {}) {
                            Text("Start")
                                .foregroundColor(.white)
                                .frame(width: 70, height: 70)
                                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
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
            }
            .toolbarBackground(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme), for: .navigationBar)
            .toolbarColorScheme(.light, for: .navigationBar)
        }
    }
}


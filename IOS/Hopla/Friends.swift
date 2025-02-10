//
//  Friends.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//

import SwiftUI

struct Friends: View {
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationView {
            VStack {
                // Top of screen is green
                Rectangle()
                    .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .frame(height: 110)
                    .edgesIgnoringSafeArea(.top)
                    .padding(.top, -430)
                
                HStack {
                    Text("Pil tilbake")
                    Text("Friends")
                    Text("Antall")
                }
                .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme)) // Dynamic background
                
                Spacer()
                
                Text("Search bar")
                    .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                
                Spacer()
                Rectangle()
                    .frame(width: 370, height: 60)
                    .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .padding(.top, 10)
            }
            .navigationTitle("Friends")
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

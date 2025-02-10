//
//  Following.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//

import SwiftUI

struct Following: View {
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationView {
            VStack {
                ZStack {
                    Rectangle()
                        .frame(width: 370, height: 70)
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                    Rectangle()
                        .frame(width: 350, height: 50)
                        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                }
                Spacer()
                Text("Halla")
                    .underline()
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

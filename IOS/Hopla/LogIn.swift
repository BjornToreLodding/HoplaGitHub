//
//  LogIn.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 09/02/2025.
//

import SwiftUI

struct Login: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    // Track selected filter
    @State private var selectedFilter: String = "location"
    @State private var username: String = "" // Username
    @State private var password: String = "" // Password
    
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
                    Image("LogoUtenBakgrunn")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 100, height: 100)
                        .padding(.top, 100)
                    Spacer()
                    Text("Username")
                        .frame(width: 360, height: 30)
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))

                    TextField("Bob", text: $username)
                        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                        .frame(width: 360)
                        .multilineTextAlignment(.center)
                    
                    Text("Password")
                        .frame(width: 360, height: 30)
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))

                    TextField("Example?", text: $password)
                        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                        .frame(width: 360)
                        .multilineTextAlignment(.center)
                }
            }
        }
        // Settings Button
        NavigationLink(destination: Home()) {
            HStack {
                Text("Log in")
            }
            .padding()
            .frame(width: 200, height: 50)
            .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
        }
        VStack {
            Text("Not a member? Sign up")
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

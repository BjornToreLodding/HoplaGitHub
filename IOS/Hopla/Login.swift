//
//  LogIn.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 09/02/2025.
//

import SwiftUI

struct Login: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    @State private var username: String = "" // Username
    @State private var password: String = "" // Password
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login state
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Main background color
                Rectangle()
                    .fill(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                    .ignoresSafeArea() // Fill the entire screen
                
                VStack {
                    // Logo
                    Image("LogoUtenBakgrunn")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 170, height: 170)
                        .padding(.top, 100)
                    
                    Spacer()
                    
                    // Username Label & TextField
                    Text("Username")
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                        .padding(.top, 50)
                    
                    TextField("Enter your username", text: $username)
                        .padding()
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        .cornerRadius(8)
                        .frame(width: 300)
                        .multilineTextAlignment(.center)
                    
                    // Password Label & SecureField
                    Text("Password")
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                        .padding(.top, 20)
                    
                    SecureField("Enter your password", text: $password)
                        .padding()
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        .cornerRadius(8)
                        .frame(width: 300)
                        .multilineTextAlignment(.center)
                    
                    // Log In Button
                    Button(action: {
                        // Check credentials and set isLoggedIn flag
                        isLoggedIn = true
                    }) {
                        Text("Log In")
                            .foregroundColor(.white)
                            .padding()
                            .frame(width: 200, height: 50)
                            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                            .cornerRadius(8)
                    }
                    .padding(.top, 30)
                    
                    Spacer()
                    
                    // Sign Up Link
                    VStack {
                        Text("Not a member? Sign up")
                            .underline()
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            .padding(.top, 20)
                    }
                }
            }
            .onChange(of: isLoggedIn) { _ in
                // Navigate to Home after login
                if isLoggedIn {
                    // You can handle any post-login setup here
                }
            }
        }
        .navigationBarHidden(true) // Hide navigation bar on this screen
    }
}

#Preview("English") {
    Login()
}

#Preview("Norsk") {
    Login()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

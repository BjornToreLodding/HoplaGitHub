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
                    
                    Button(action: {
                        // Simulating login, replace with actual authentication logic
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
                    .navigationBarBackButtonHidden(true) // Hide the back arrow
                    
                    
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
            .navigationBarHidden(true) // Hide navigation bar on the login screen
        }
    }
}


#Preview("English") {
    Login()
}

#Preview("Norsk") {
    Login()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

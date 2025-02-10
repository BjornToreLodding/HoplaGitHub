//
//  Settings.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//
import SwiftUI

struct Settings: View {
    @Environment(\.colorScheme) var colorScheme
    @AppStorage("isDarkMode") private var isDarkMode = false
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login status
    @AppStorage("isEnglishSelected") private var isEnglishSelected = false
    
    // State to manage the showing of the alert
    @State private var showLogoutAlert = false
    @State private var showDeleteUserAlert = false // State for delete user alert
    @State private var password = "" // User's input for password
    @State private var isPasswordCorrect = false // Check if password is correct
    @State private var passwordInputError = false // Track if password is wrong

    var body: some View {
        ZStack {
            // Ensure the whole background is green
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)
            
            VStack {
                // Top green rectangle (Navigation Bar background effect)
                Rectangle()
                    .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .frame(height: 110)
                    .edgesIgnoringSafeArea(.top)
                    .padding(.top, -435)
            }
            
            NavigationView {
                Form {
                    Section(header: Text(LocalizedStringKey("Display"))) {
                        Toggle(isOn: $isDarkMode) {
                            Text(LocalizedStringKey("Dark mode"))
                        }
                    }
                    Section(header: Text(LocalizedStringKey("The app must reload to apply language change"))) {
                        Toggle(isOn: $isEnglishSelected) {
                            Text(isEnglishSelected ? "English" : "English")
                        }
                        .onChange(of: isEnglishSelected) { _ in
                            changeLanguage()
                        }
                    }
                    
                    // Log Out Section
                    Section {
                        Button(action: {
                            showLogoutAlert = true // Trigger the alert when tapped
                        }) {
                            Text("Log Out")
                                .foregroundColor(.red)
                        }
                    }
                    
                    // Delete User Section
                    Section {
                        Button(action: {
                            showDeleteUserAlert = true // Trigger delete user alert
                        }) {
                            Text("Delete User")
                                .foregroundColor(.red) // You can change the color to suit your design
                        }
                    }
                }
                .background(AdaptiveColor.background.color(for: colorScheme)) // Set Form background
                .scrollContentBackground(.hidden) // Hide default Form background
                .navigationTitle(LocalizedStringKey("Settings"))
                .foregroundColor(AdaptiveColor.text.color(for: colorScheme))
                .alert(isPresented: $showLogoutAlert) {
                    Alert(
                        title: Text("Log Out"),
                        message: Text("Are you sure you want to log out?"),
                        primaryButton: .destructive(Text("Log Out")) {
                            logOut() // Call the logOut function if confirmed
                        },
                        secondaryButton: .cancel() // Close the alert if cancelled
                    )
                }
                .alert(isPresented: $showDeleteUserAlert) {
                    Alert(
                        title: Text("Delete User"),
                        message: Text("Are you sure you want to delete your user?"),
                        primaryButton: .default(Text("Confirm")) {
                            // Show password input dialog (could be a new view for better UX)
                            passwordInputError = false
                            // Proceed to show a new view or alert for password input
                        },
                        secondaryButton: .cancel() // Close the alert if cancelled
                    )
                }
            }
            .preferredColorScheme(isDarkMode ? .dark : .light)
        }
        .onAppear {
            setupNavigationBar(for: colorScheme)
            setupTabBarAppearance(for: colorScheme)
        }
    }
    
    // Log out function
    private func logOut() {
        isLoggedIn = false // Change login status to false
        print("User logged out successfully.") // Debugging log
    }

    // To change language
    private func changeLanguage() {
        let languageCode = isEnglishSelected ? "English" : "nb_NO"
        UserDefaults.standard.set([languageCode], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
        exit(0) // Restart app to apply language change
    }
    
    // Password check for Delete User
    private func confirmPassword() {
        // For now, we can check against a hardcoded password (you can update this later with actual logic)
        let correctPassword = "1234" // Hardcoded password (replace this with your actual logic)
        
        if password == correctPassword {
            // Proceed to delete user and log out
            logOut() // Log out the user
            // Implement actual user deletion logic here (e.g., from a database)
        } else {
            // Show an error if password is incorrect
            passwordInputError = true
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

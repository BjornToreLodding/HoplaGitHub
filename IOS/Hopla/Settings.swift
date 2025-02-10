//
//  Settings.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//
import SwiftUI

struct Settings: View {
    @Environment(\.colorScheme) var colorScheme
    // Use AppStorage for persistence
    @AppStorage("isDarkMode") private var isDarkMode = false
    
    // For language selection
    @AppStorage("isEnglishSelected") private var isEnglishSelected = false

    var body: some View {
        ZStack {
            // Ensure the whole background is green
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all) // Covers top & bottom
            
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
                    }
                    .onChange(of: isEnglishSelected) { _ in
                        changeLanguage()
                    }
                }
                .background(AdaptiveColor.background.color(for: colorScheme)) // Set Form background
                .scrollContentBackground(.hidden) // Hide default Form background
                .navigationTitle(LocalizedStringKey("Settings"))
                .foregroundColor(AdaptiveColor.text.color(for: colorScheme))
            }
            .preferredColorScheme(isDarkMode ? .dark : .light)
        }
        .onAppear {
            setupNavigationBar(for: colorScheme)
            setupTabBarAppearance(for: colorScheme)
        }
    }


    // To change language
    private func changeLanguage() {
        let languageCode = isEnglishSelected ? "English" : "nb_NO"
        UserDefaults.standard.set([languageCode], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
        exit(0) // Restart app to apply language change
    }
}


#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

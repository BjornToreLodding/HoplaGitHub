//
//  Settings.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//
import SwiftUI

struct Settings: View {
    // Use AppStorage for persistence
    @AppStorage("isDarkMode") private var isDarkMode = false
    
    // For language selection
    @AppStorage("isEnglishSelected") private var isEnglishSelected = false

    var body: some View {
        NavigationView { // Settings selection
            Form {
                Section(header: Text(LocalizedStringKey("Display"))) {
                    Toggle(isOn: $isDarkMode) {
                        Text(LocalizedStringKey("Dark mode"))
                    }
                }
                Section(header: Text(LocalizedStringKey("The app must reload to apply language change"))) {
                    Toggle(isOn: $isEnglishSelected) {
                        Text(isEnglishSelected ? "English" : "Norwegian")
                    }
                }
                .onChange(of: isEnglishSelected) { _ in
                    changeLanguage()
                }
            }
            .navigationTitle(LocalizedStringKey("Settings"))
        }
        .preferredColorScheme(isDarkMode ? .dark : .light)
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

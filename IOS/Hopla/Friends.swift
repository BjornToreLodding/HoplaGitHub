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
            Form {
                Section(header: Text("Display")) {
                    Toggle(isOn: .constant(true), label: {
                        Text("Dark mode")
                    })
                    Toggle(isOn: .constant(true), label: {
                        Text("English")
                    })
                }
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

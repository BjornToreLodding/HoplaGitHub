//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Hikes: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    @State private var selectedFilter = "Map" // Track selected filter

    let hikes = [
        Hike(name: "Gjøvikrunden", imageName: "Gjøvik.jpg"),
        Hike(name: "Preikestolen", imageName: "Preikestolen.jpg"),
        Hike(name: "Vågan", imageName: "Gjøvik.jpg"),
        Hike(name: "Bobby", imageName: "Preikestolen.jpg"),
        Hike(name: "Våganes", imageName: "Gjøvik.jpg"),
        Hike(name: "Bob", imageName: "Preikestolen.jpg")
    ]
    

    var body: some View {
        NavigationStack {
            filterBar
            ZStack {
                // To select filters for hikes
                
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea()
                
                List(hikes) { hike in
                    HStack {
                        if let uiImage = UIImage(named: hike.imageName) {
                            Image(uiImage: uiImage)
                                .resizable()
                                .scaledToFill()
                                .frame(width: 100, height: 100)
                        } else {
                            Image(systemName: hike.imageName)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 100, height: 100)
                                .foregroundColor(.green)
                        }
                        Text(hike.name)
                            .font(.headline)
                            .padding(.horizontal, 20)
                        Spacer()
                    }
                    .padding()
                    .listRowBackground(AdaptiveColor.background.color(for: colorScheme))
                    .onTapGesture {
                        print(hike.name)
                    }
                }
                .scrollContentBackground(.hidden)
            }
            .onAppear {
                setupNavigationBar(for: colorScheme) // Ensure navbar updates on view load
            }
            .onChange(of: colorScheme) {
                // navbar updates immediately when switching between light and dark mode
                setupNavigationBar(for: colorScheme)
            }
            .navigationTitle("Hikes")
        }
    }
    // MARK: - Filter Bar Below Logo
        private var filterBar: some View {
            HStack {
                Picker("Filter", selection: $selectedFilter) {
                    Text("Map").tag("Map")
                    Text("Location").tag("Location")
                    Text("Liked").tag("Liked")
                    Text("Popular").tag("Popular")
                    Text("More").tag("More")
                }
                .pickerStyle(SegmentedPickerStyle()) // Makes it look like a real navigation bar
            }
            .background(Color.lighterGreen)
        }
}

#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

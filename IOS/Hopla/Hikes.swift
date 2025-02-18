//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Hikes: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    // Track selected filter
    @State private var selectedFilter: String = "location"

    let hikes = [
        Hike(name: "Gjøvikrunden", imageName: "Gjøvik.jpg"),
        Hike(name: "Preikestolen", imageName: "Preikestolen.jpg"),
        Hike(name: "Vågan", imageName: "Gjøvik.jpg"),
        Hike(name: "Bobby", imageName: "Preikestolen.jpg"),
        Hike(name: "Våganes", imageName: "Gjøvik.jpg"),
        Hike(name: "Bob", imageName: "Preikestolen.jpg")
    ]
    
    // To select a filter
    enum FilterOption: String, CaseIterable, Identifiable {
            case map
            case location
            case heart
            case star
            case arrow

            var id: String { self.rawValue }

            var systemImage: String {
                switch self {
                case .map: return "map"
                case .location: return "location"
                case .heart: return "heart"
                case .star: return "star"
                case .arrow: return "chevron.down"
                }
            }
        }
    

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) { // Ensure no extra spacing
                filterBar
                ZStack {
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
            }
            .navigationTitle("Hikes")
        }

    }
    // MARK: - Filter Bar Below Logo
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                Image(systemName: "map").tag("map")
                Image(systemName: "location").tag("location")
                Image(systemName: "heart").tag("heart")
                Image(systemName: "star").tag("star")
                Image(systemName: "chevron.down").tag("chevron.down")
            }
            .padding(.top, 30)
            .pickerStyle(SegmentedPickerStyle()) // Makes it look like a real navigation bar
        }
        .frame(height: 60)
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme)) // Dynamic background
    }
}

#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}


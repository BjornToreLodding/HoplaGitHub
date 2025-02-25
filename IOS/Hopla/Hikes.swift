//
//  Map.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

// MARK: - Hike Model
struct Hike: Identifiable {
    let id = UUID()
    let name: String
    let filter: String
    let rating: Int
    var isFavorite: Bool
    let imageName: String
}

// MARK: - Filter Options
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

// MARK: - Main View
struct Hikes: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var selectedFilter: String = "location"
    
    @State private var hikes: [Hike] = [
        Hike(name: "Boredalstien", filter: "Asfalt, Grus, Parkering", rating: 2, isFavorite: false, imageName: "HorseImage"),
        Hike(name: "Boredalstien", filter: "Asfalt, Grus, Parkering", rating: 4, isFavorite: false, imageName: "HorseImage2"),
        Hike(name: "Boredalstien", filter: "Asfalt, Grus, Parkering", rating: 5, isFavorite: true, imageName: "HorseImage3"),
        Hike(name: "Boredalstien", filter: "Asfalt, Grus, Parkering", rating: 3, isFavorite: false, imageName: "HorseImage")
    ]
    
    var body: some View {
        VStack {
            // Top Filter Bar
            filterBar
            
            // Scrollable Hike List
            ScrollView {
                VStack(spacing: 10) {
                    ForEach($hikes) { $hike in
                        HikeCard(hike: $hike)
                    }
                    .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                }
                .padding(.horizontal)
            }
        }
        .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
    }
    
    // MARK: - Filter Bar
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterOption.allCases) { option in
                    Image(systemName: option.systemImage).tag(option.rawValue)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
            .padding(.top, 30)
        }
        .frame(height: 60)
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
}

// MARK: - Hike Card
struct HikeCard: View {
    @Binding var hike: Hike
    
    var body: some View {
        VStack {
            ZStack(alignment: .topLeading) {
                // Image
                Image(hike.imageName)
                    .resizable()
                    .scaledToFill()
                    .frame(height: 150)
                    .clipped()
                
                // Black overlay
                    Color.black.opacity(0.4)
                        .frame(height: 150)
                        .frame(maxWidth: .infinity)
                
                // Heart Button (top right)
                HStack {
                    Spacer()
                    Button(action: {
                        hike.isFavorite.toggle()
                    }) {
                        Image(systemName: hike.isFavorite ? "heart.fill" : "heart")
                            .foregroundColor(hike.isFavorite ? .red : .white)
                            .padding()
                    }
                }
                
                // Hike Name (bottom left)
                VStack {
                    Spacer()
                    HStack {
                        Text(hike.name)
                            .foregroundStyle(.white)
                        Spacer()

                    }
                }
                
                // Star Rating (bottom right)
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        StarRating(rating: hike.rating)
                            .padding(.top, 40) // Padding applied outside the background
                            .foregroundStyle(.yellow)
                    }
                }
            }
            
            // Filters
            Text(hike.filter)
                .font(.subheadline)
                .frame(height: 20)
                .foregroundStyle(.black)
                .frame(maxWidth: .infinity, alignment: .leading) // Aligns text to the left
                .padding(.bottom, 10)
        }
        .clipShape(Rectangle())
        .shadow(radius: 3)
    }

}

// MARK: - Star Rating View
struct StarRating: View {
    let rating: Int
    
    var body: some View {
        HStack {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
            }
        }
    }
}

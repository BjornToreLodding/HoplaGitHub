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
    let filters: [HikeFilter]
    let rating: Int
    var isFavorite: Bool
    let imageName: String
    let description: String
}

// MARK: - Filters for Hikes
enum HikeFilter: String, CaseIterable, Identifiable {
    case asphalt = "Asphalt"
    case gravel = "Gravel"
    case parking = "Parking"
    case forest = "Forest"
    case mountain = "Mountain"
    
    var id: String { self.rawValue }
}

// MARK: - Filter bar Options
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
    @State private var selectedFilter: FilterOption = .location
    @State private var searchText: String = "" // Search bar
    
    @State private var hikes: [Hike] = [
        Hike(name: "Boredalstien", filters: [.asphalt, .forest, .gravel, .parking], rating: 2, isFavorite: false, imageName: "HorseImage", description: "An easy trail through a beautiful forest. There is parking available at the start and end of the trail."),
        Hike(name: "Fjellstien", filters: [.mountain, .forest], rating: 4, isFavorite: false, imageName: "HorseImage2", description: "A challenging trail with stunning mountain views. There is no parking available at the start and end of the trail."),
        Hike(name: "Skogsstien", filters: [.forest, .gravel, .parking], rating: 5, isFavorite: true, imageName: "HorseImage3", description: "This hike is a paradise for nature lovers. It tends to get very busy during peak season, so it is best to go early in the morning or late in the afternoon."),
        Hike(name: "Dalstien", filters: [.asphalt, .gravel], rating: 3, isFavorite: false, imageName: "HorseImage", description: "A difficult trail with beautiful views of the valley. It is best to go in the summer when the weather is good.")
    ]
    
    private var filteredHikes: [Hike] {
        let filtered = selectedFilter == .heart ? hikes.filter { $0.isFavorite } : hikes
        return searchText.isEmpty ? filtered : filtered.filter { $0.name.lowercased().contains(searchText.lowercased()) }
    }
    
    var body: some View {
        VStack(spacing: 0) {
            // Place filterBar and searchBar at the top with no extra spacing between them
            filterBar
            searchBar
        }
        .frame(maxWidth: .infinity)
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
        ZStack {
            // Main content wrapped in NavigationView
            NavigationView {
                // Scrollable content, adjusting padding and top space
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(filteredHikes, id: \.id) { hike in
                            HikeCard(hike: binding(for: hike))
                        }
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                    }
                }
                .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
            }
        }
        .navigationBarHidden(true)
    }
    
    
    
    private func binding(for hike: Hike) -> Binding<Hike> {
        guard let index = hikes.firstIndex(where: { $0.id == hike.id }) else {
            fatalError("Hike not found in list")
        }
        return $hikes[index]
    }
    
    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(FilterOption.allCases) { option in
                    Image(systemName: option.systemImage).tag(option)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .frame(height: 30)
        .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
    }
    
    // MARK: - Search bar
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search hikes...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}


// MARK: - Hike Card

struct HikeCard: View {
    @Binding var hike: Hike
    
    var body: some View {
        NavigationLink(destination: HikesDetails(hike: hike)) {
            VStack {
                ZStack(alignment: .top) {
                    Image(hike.imageName)
                        .resizable()
                        .scaledToFill()
                        .frame(height: 150)
                        .clipped()
                    
                    Color.black.opacity(0.4)
                        .frame(height: 150)
                        .frame(maxWidth: .infinity)
                    
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
                    
                    VStack {
                        Spacer()
                        HStack {
                            Text(hike.name)
                                .foregroundStyle(.white)
                                .padding(.leading, 10)
                            Spacer()
                        }
                    }
                    
                    VStack {
                        Spacer()
                        HStack {
                            Spacer()
                            StarRating(rating: .constant(hike.rating))
                                .padding(.top, 40)
                                .padding(.trailing, 10)
                                .foregroundStyle(.yellow)
                        }
                    }
                }
                
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(hike.filters) { filter in
                            Text(filter.rawValue)
                                .font(.subheadline)
                                .padding(.horizontal, 5)
                                .padding(.vertical, 5)
                                .background(Color.green.opacity(0.2))
                                .cornerRadius(10)
                        }
                    }
                    .padding(.bottom, 8)
                    .padding(.leading, 5)
                }
                
            }
            .clipShape(Rectangle())
            .shadow(radius: 3)
        }
        .buttonStyle(PlainButtonStyle()) // Removes default navigation link styling
    }
}

// MARK: - Star Rating With Tap Gesture
struct StarRating: View {
    @Binding var rating: Int  // Use a Binding to allow changes
    
    var body: some View {
        HStack {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
                    .onTapGesture {
                        rating = index  // Update rating on tap
                    }
            }
        }
    }
}

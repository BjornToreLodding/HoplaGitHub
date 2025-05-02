import SwiftUI
import Foundation
import CoreLocation
import GoogleMaps

// MARK: - Hike Card
struct HikeCard: View {
    let hike: Hike
    let trailFilters: [TrailFilter]
    @Binding var likedHikes: [String]
    var toggleFavoriteAction: (Hike) -> Void
    @ObservedObject var viewModel: HikeService
    @Environment(\.colorScheme) var colorScheme
    var body: some View {
        NavigationLink(destination: HikesDetails(hike: hike, trailFilters: trailFilters)) {
            VStack(alignment: .leading, spacing: 8) {
                ZStack(alignment: .topTrailing) {
                    // Image background
                    AsyncImage(url: URL(string: hike.pictureUrl)) { phase in
                        if let image = phase.image {
                            image.resizable().scaledToFill()
                        } else {
                            Color.gray
                        }
                    }
                    .frame(height: 150)
                    .clipped()
                    
                    // Heart button
                    Button {
                        toggleFavoriteAction(hike)
                    } label: {
                        ZStack {
                            // Black “stroke” heart, slightly larger
                            Image(systemName: "heart.fill")
                                .font(.system(size: 26))
                                .foregroundColor(.black)
                            // Colored fill heart on top
                            Image(systemName: "heart.fill")
                                .font(.system(size: 24))
                                .foregroundColor(hike.isFavorite ? .red : .gray)
                        }
                        .padding(10)
                    }
                    // UITests
                    .accessibilityIdentifier("FavoriteButton_\(index)")
                    .accessibilityLabel("Favorite")
                    .accessibilityValue(hike.isFavorite ? "true" : "false")
                }
                
                // Hike name and rating
                HStack {
                    Text(hike.name)
                        .font(.headline)
                        .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    Spacer()
                    StarRating(rating: .constant(hike.averageRating))
                        .frame(width: 100)
                }
                .padding(.horizontal)
                
                // Filters
                if let filters = hike.filters, !filters.isEmpty {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Filters:")
                            .font(.subheadline)
                            .bold()
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        ForEach(filters, id: \.id) { filter in
                            HStack {
                                Text("\(filter.displayName):")
                                    .font(.caption)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                Text(displayValue(for: filter))
                                    .font(.caption)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 10)
                }
            }
            .background(Color(.systemBackground))
            .cornerRadius(10)
            .shadow(radius: 4)
        }
    }
    
    // Toggle favorite trails
    private func toggleFavorite(for hike: Hike) {
        if let index = likedHikes.firstIndex(of: hike.id) {
            likedHikes.remove(at: index)
        } else {
            likedHikes.append(hike.id)
        }
    }
    
    // Display the true/false values from code to yes/no in app
    private func displayValue(for filter: HikeFilter) -> String {
        if filter.type == "Bool" {
            return filter.value.lowercased() == "true" ? "Yes" : "No"
        } else {
            return filter.value
        }
    }
}

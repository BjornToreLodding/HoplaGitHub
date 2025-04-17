import SwiftUI

struct HikesDetails: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    let hike: Hike
    let trailFilters: [TrailFilter]
    
    @State private var userRating: Int = 0
    
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                
                // 2. Image
                HikeImageView(hike: hike)
                
                // 3. Filters
                HikeFiltersView(hike: hike, trailFilters: trailFilters)
                
                // 4. Buttons
                HikeButtonsView(hike: hike)
                
                // 5. Description
                Text("Description of trail")
                    .frame(width: 370, height: 70)
                    .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                    .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                
                // 6. Rating
                VStack {
                    HStack {
                        Text("Rating:")
                            .frame(maxWidth: .infinity, alignment: .leading) // Aligns to the left
                        StarRating(rating: .constant(hike.averageRating))
                            .frame(maxWidth: .infinity, alignment: .trailing) // Aligns to the right
                    }
                    .padding(.horizontal)
                    
                    HStack {
                        Text("My rating:")
                            .frame(maxWidth: .infinity, alignment: .leading) // Aligns to the left
                        StarRating(rating: $userRating)
                            .frame(maxWidth: .infinity, alignment: .trailing) // Aligns to the right
                    }
                    .padding(.horizontal)
                }
                .frame(width: 370, height: 70)
                .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                
                // 8. Update box
                NavigationLink(destination: HikeUpdate(trailId: hike.id)) {
                    Text("View Updates")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                
                Spacer()
            }
            .padding(.top)
            .onAppear {
                print("Trail filters:", trailFilters.map { $0.name })
                print("Hike filters:", hike.filters?.map { $0.id } ?? [])
            }
        }
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
        .navigationBarTitleDisplayMode(.inline)
    }
}



// Image section
struct HikeImageView: View {
    let hike: Hike
    
    var body: some View {
        ZStack {
            AsyncImage(url: URL(string: hike.pictureUrl)) { phase in
                switch phase {
                case .success(let image):
                    image.resizable()
                        .scaledToFill()
                        .frame(width: 370, height: 250)
                        .clipped()
                case .failure:
                    Color.gray // Placeholder color in case of failure
                        .frame(width: 370, height: 250)
                case .empty:
                    Color.gray // Placeholder color when loading
                        .frame(width: 370, height: 250)
                @unknown default:
                    Color.gray // Fallback
                        .frame(width: 370, height: 250)
                }
            }
        }
    }
}


struct HikeFiltersView: View {
    @Environment(\.colorScheme) var colorScheme
    let hike: Hike
    let trailFilters: [TrailFilter]
    
    var body: some View {
        ZStack {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack {
                    if !trailFilters.isEmpty {
                        filterChips
                    } else {
                        Text("Loading filters...")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }
                }
                .padding(.horizontal)
            }
        }
        .frame(width: 370, height: 60)
        .background(
            AdaptiveColor(
                light: .lightPostBackground,
                dark: .darkPostBackground
            ).color(for: colorScheme)
        )
        .foregroundStyle(
            AdaptiveColor(
                light: .textLightBackground,
                dark: .textDarkBackground
            ).color(for: colorScheme)
        )
    }
    
    
    private var filterChips: some View {
        let matchingFilters: [(TrailFilter, HikeFilter)] = {
            guard let hikeFilters = hike.filters else { return [] }
            
            // ✅ You can print here
            print("Hike filters:", hikeFilters.map { $0.id })
            print("Trail filters:", trailFilters.map { $0.id })
            
            return hikeFilters.compactMap { hikeFilter in
                trailFilters.first(where: { $0.id == hikeFilter.id }).map { trailFilter in
                    (trailFilter, hikeFilter)
                }
            }
        }()
        
        return Group {
            ForEach(matchingFilters.filter { shouldDisplay($0.0) }, id: \.0.id) { (trailFilter, hikeFilter) in
                let values = getValues(from: trailFilter, using: hikeFilter)
                ForEach(values, id: \.self) { value in
                    Text(value)
                        .font(.subheadline)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(8)
                }
            }
        }
    }
    
    
    
    
    
    
    // MARK: - Helpers
    
    /// Only show "Difficulty" and "SurfaceType" filters
    private func shouldDisplay(_ filter: TrailFilter) -> Bool {
        let ignored = ["Custom13", "Custom14"]
        return !ignored.contains(filter.name)
    }
    
    /// Extract displayed values based on the filter type
    private func getValues(from trailFilter: TrailFilter, using hikeFilter: HikeFilter) -> [String] {
        switch trailFilter.type {
        case .multiEnum:
            return hikeFilter.value
                .split(separator: ",")
                .map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }
            
        case .enumType:
            return [hikeFilter.value]
            
        case .bool:
            if hikeFilter.value.lowercased() == "true" {
                return [trailFilter.displayName]
            }
            
        case .int:
            return ["\(trailFilter.displayName): \(hikeFilter.value)"]
        }
        
        return []
    }
}


// Hikes button
struct HikeButtonsView: View {
    @Environment(\.colorScheme) var colorScheme
    
    let hike: Hike
    
    var body: some View {
        HStack(alignment: .center) {
            NavigationLink(destination: StartHike()) {
                Text("Start hike")
                    .frame(width: 120, height: 50)
            }
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
            
            NavigationLink(destination: AddNewUpdateView(trailId: hike.id)) {
                Text("New update")
                    .frame(width: 120, height: 50)
            }
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
        }
        .frame(alignment: .center)
    }
}

struct StarsView: View {
    let rating: Int
    
    var body: some View {
        HStack(spacing: 4) {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
            }
        }
    }
}

struct StarsPicker: View {
    @Binding var rating: Int
    
    var body: some View {
        HStack(spacing: 4) {
            ForEach(1...5, id: \.self) { index in
                Image(systemName: index <= rating ? "star.fill" : "star")
                    .foregroundColor(.yellow)
                    .onTapGesture {
                        rating = index
                    }
            }
        }
    }
}

import SwiftUI

//MARK: - For the user rating
struct RateRequest: Codable {
  let TrailId: String
  let Rating: Int
}


struct HikesDetails: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    @State private var hike: Hike
      let trailFilters: [TrailFilter]

      init(hike: Hike, trailFilters: [TrailFilter]) {
        _hike = State(initialValue: hike)
        self.trailFilters = trailFilters
      }
    
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
                      StarDisplay(rating: hike.averageRating)
                  }
                  .padding(.horizontal)

                  HStack {
                    Text("My rating:")
                      StarRating(rating: $userRating)
                          .onChange(of: userRating) { _ in submitRating() }
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
    
    //MARK: - To submit rating
    private func submitRating() {
        // 1) Only accept 1–5
        guard (1...5).contains(userRating) else { return }

        // 2) Grab token & URL
        guard let token = TokenManager.shared.getToken(),
              let url   = URL(string: "https://hopla.onrender.com/trails/rate") else {
            print("❌ No token or bad URL")
            return
        }

        // 3) Optimistic local update
        let oldCount = hike.ratingCount ?? 0
        let total    = hike.averageRating * Double(oldCount)
        let newCount = oldCount + 1

        hike.ratingCount   = newCount
        hike.averageRating = (total + Double(userRating)) / Double(newCount)

        // 4) Build request
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json; charset=utf-8",
                         forHTTPHeaderField: "Content-Type")

        let payload = ["TrailId": hike.id, "Rating": userRating] as [String: Any]
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: payload)
            print("▶️ JSON →", String(data: request.httpBody!, encoding: .utf8)!)
        } catch {
            print("❌ Couldn’t serialize JSON:", error)
            return
        }

        // 5) Send & reconcile
        URLSession.shared.dataTask(with: request) { data, resp, err in
            DispatchQueue.main.async {
                if let err = err {
                    print("❌ Rate failed:", err)
                    return
                }
                guard let http = resp as? HTTPURLResponse else {
                    print("❌ No HTTP response")
                    return
                }
                print("📡 Rate status:", http.statusCode)
                if (200...299).contains(http.statusCode) {
                    fetchTrailDetails()
                } else {
                    let msg = data.flatMap { String(data: $0, encoding: .utf8) } ?? "<no body>"
                    print("Server error:", msg)
                }
            }
        }.resume()
    }

    
    //MARK: - To fetch details on a trail
    private func fetchTrailDetails() {
      guard let token = TokenManager.shared.getToken() else { return }
      guard let url   = URL(string: "https://hopla.onrender.com/trails/\(hike.id)") else { return }

      var req = URLRequest(url: url)
      req.httpMethod = "GET"
      req.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

      URLSession.shared.dataTask(with: req) { data, resp, err in
        guard
          err == nil,
          let data = data,
          let updated = try? JSONDecoder().decode(Hike.self, from: data)
        else { return }

        DispatchQueue.main.async {
          self.hike = updated
        }
      }
      .resume()
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

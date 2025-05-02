//
//  HikeUpdate.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 26/02/2025.
//
import SwiftUI

// MARK: - Model for Trail Update
struct TrailUpdate: Codable, Identifiable {
    let id: String
    let comment: String
    let pictureUrl: String?
    let condition: Int
    let createdAt: String
    let alias: String
}

// MARK: - HikeUpdate View (Displays updates) with custom header
struct HikeUpdate: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) var colorScheme
    @State private var trailUpdates: [TrailUpdate] = []
    @State private var isLoading = false
    var trailId: String // Pass the trailId
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // ─── Custom Header ───────────────────────────────────
                ZStack {
                    AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                        .color(for: colorScheme)
                        .frame(maxWidth: .infinity)
                    // Centered title
                    Text("Trail Updates")
                        .font(.custom("ArialNova", size: 20))
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    // Leading back arrow
                    HStack {
                        Button(action: { dismiss() }) {
                            Image(systemName: "arrow.left")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 24, height: 24)
                                .foregroundStyle(
                                    AdaptiveColor(light: .lightModeTextOnGreen,
                                                  dark: .darkModeTextOnGreen)
                                    .color(for: colorScheme)
                                )
                        }
                        Spacer()
                    }
                    .padding(.horizontal, 16)
                }
                .frame(height: 40)
                // ─── Scrollable Content ───────────────────────────────
                ScrollView {
                    VStack(alignment: .leading, spacing: 12) {
                        if isLoading {
                            ProgressView("Loading updates...")
                                .padding()
                        } else if trailUpdates.isEmpty {
                            Text("No updates found for this trail.")
                                .foregroundColor(.gray)
                                .padding()
                        } else {
                            ForEach(trailUpdates) { update in
                                VStack(alignment: .leading, spacing: 8) {
                                    Text(update.alias)
                                        .font(.headline)
                                    Text(update.comment)
                                        .font(.subheadline)
                                    if let urlString = update.pictureUrl,
                                       let url = URL(string: urlString) {
                                        AsyncImage(url: url) { image in
                                            image
                                                .resizable()
                                                .scaledToFit()
                                                .frame(height: 200)
                                        } placeholder: {
                                            ProgressView()
                                        }
                                    }
                                    Text("Condition: \(update.condition)")
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                    Text("Updated on: \(formatDate(from: update.createdAt))")
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                }
                                .padding()
                                .background(
                                    AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground)
                                        .color(for: colorScheme)
                                )
                                .cornerRadius(8)
                            }
                        }
                    }
                    .padding()
                }
                .frame(maxWidth: .infinity)
                .background(
                    AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                        .color(for: colorScheme)
                )
            }
            .navigationBarBackButtonHidden(true)
        }
        .onAppear(perform: fetchTrailUpdates)
    }
    
    // Fetch trail updates
    private func fetchTrailUpdates() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        guard let url = URL(string: "https://hopla.onrender.com/trails/updates?trailId=\(trailId)") else {
            print("Invalid URL")
            return
        }
        isLoading = true
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            defer { DispatchQueue.main.async { self.isLoading = false } }
            if let error = error {
                print("Error fetching trail updates:", error.localizedDescription)
                return
            }
            if let data = data {
                do {
                    let updates = try JSONDecoder().decode([TrailUpdate].self, from: data)
                    DispatchQueue.main.async { self.trailUpdates = updates }
                } catch {
                    print("Failed to decode trail updates:", error.localizedDescription)
                }
            }
        }.resume()
    }
}

// MARK: - Date formatting helper
private func formatDate(from isoString: String) -> String {
    let formatter = ISO8601DateFormatter()
    formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
    if let date = formatter.date(from: isoString) {
        let df = DateFormatter()
        df.dateFormat = "dd/MM/yyyy"
        return df.string(from: date)
    }
    return "Unknown Date"
}

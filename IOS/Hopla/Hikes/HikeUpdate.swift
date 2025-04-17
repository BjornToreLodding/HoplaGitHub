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

// MARK: - HikeUpdate View (Displays updates)
struct HikeUpdate: View {
    @State private var trailUpdates: [TrailUpdate] = []
    @State private var isLoading = false
    var trailId: String // Pass the trailId
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Text("Trail Updates")
                    .font(.title)
                    .bold()
                
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
                            if let urlString = update.pictureUrl, let url = URL(string: urlString) {
                                AsyncImage(url: url) { image in
                                    image.resizable().scaledToFit().frame(height: 200)
                                } placeholder: {
                                    ProgressView()
                                }
                            }
                            Text("Condition: \(update.condition)")
                                .font(.caption)
                                .foregroundColor(.gray)
                            Text("Updated on: \(update.createdAt)")
                                .font(.caption)
                                .foregroundColor(.gray)
                        }
                        .padding()
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(8)
                    }
                }
            }
            .padding()
        }
        .onAppear(perform: fetchTrailUpdates)
    }
    
    private func fetchTrailUpdates() {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            return
        }
        guard let url = URL(string: "https://hopla.onrender.com/trails/updates?trailId=\(trailId)") else {
            print("❌ Invalid URL")
            return
        }
        isLoading = true
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            defer { DispatchQueue.main.async { self.isLoading = false } }
            
            if let error = error {
                print("❌ Error fetching trail updates:", error.localizedDescription)
                return
            }
            if let httpResponse = response as? HTTPURLResponse {
                print("📡 Status code:", httpResponse.statusCode)
            }
            if let data = data {
                do {
                    let updates = try JSONDecoder().decode([TrailUpdate].self, from: data)
                    DispatchQueue.main.async {
                        self.trailUpdates = updates
                        print("✅ Fetched trail updates:", updates)
                    }
                } catch {
                    print("❌ Failed to decode trail updates:", error.localizedDescription)
                }
            }
        }.resume()
    }
}

//
//  MyHikes.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//

import Foundation
import SwiftUI
import Combine

// MARK: - MyHike Model
struct MyHike: Codable, Identifiable {
    let id: String
    let trailName: String
    let length: Double
    let duration: Double
    let pictureUrl: String?
}

// MARK: - API Response Model
struct MyHikeResponse: Codable {
    let userHikes: [MyHike]
    let page: Int?
    let size: Int?
}

// MARK: - ViewModel
class MyHikeViewModel: ObservableObject {
    @Published var myHikes: [MyHike] = []
    
    private var cancellable: AnyCancellable?
    
    func fetchMyHikes() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/userhikes/user")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { [weak self] data, response, error in
            guard let self = self else { return }

            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200, let data = data else {
                print("Invalid response or status code")
                return
            }
            
            do {
                let decodedResponse = try JSONDecoder().decode(MyHikeResponse.self, from: data)
                DispatchQueue.main.async {
                    self.myHikes = decodedResponse.userHikes
                }
            } catch {
                print("Error decoding hike details:", error.localizedDescription)
            }
        }.resume()
    }

}


// MARK: - MyHikes View
struct MyHikes: View {
    @Environment(\.colorScheme) var colorScheme
    @StateObject private var viewModel = MyHikeViewModel() // Use ViewModel
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Background
                AdaptiveColor.background.color(for: colorScheme)
                    .ignoresSafeArea(edges: .all)
                
                // ScrollView with Hikes
                ScrollView {
                    VStack(spacing: 10) {
                        ForEach(viewModel.myHikes) { myHike in
                            MyHikePostContainer(
                                imageName: myHike.pictureUrl!,
                                comment: myHike.trailName,
                                length: myHike.length,
                                duration: myHike.duration,
                                colorScheme: colorScheme
                            )
                        }
                    }
                    .padding()
                }
            }
            .padding(.top, 20)
        }
        .navigationTitle("My Hikes")
        .onAppear {
            viewModel.fetchMyHikes() // Use ViewModel to fetch data
        }
    }
}

// MARK: - PostContainer View
struct MyHikePostContainer: View {
    var imageName: String
    var comment: String
    var length: Double
    var duration: Double
    var colorScheme: ColorScheme
    
    var body: some View {
        VStack(spacing: 5) {
            // Load image from URL
            if let url = URL(string: imageName), !imageName.isEmpty {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFit()
                            .frame(width: 100, height: 100)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    case .failure:
                        Image(systemName: "exclamationmark.triangle") // Fallback in case of error
                            .resizable()
                            .scaledToFit()
                            .frame(width: 100, height: 100)
                            .foregroundColor(.red)
                    case .empty:
                        ProgressView() // Show loading indicator
                    @unknown default:
                        EmptyView()
                    }
                }
            } else {
                Image(systemName: "photo") // Default placeholder
                    .resizable()
                    .scaledToFit()
                    .frame(width: 100, height: 100)
            }
            
            // Hike Details
            Text(comment)
                .font(.headline)
                .padding(.top, 5)
            
            Text("Length: \(String(format: "%.2f", length)) km")
                .font(.subheadline)
                .foregroundColor(.gray)
            
            Text("Duration: \(String(format: "%.2f", duration)) min")
                .font(.subheadline)
                .foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AdaptiveColor.background.color(for: colorScheme))
        .cornerRadius(10)
        .shadow(radius: 5)
    }
}

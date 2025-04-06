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
    let TrailButton: Bool?
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
    @Published var isLoading = false
    
    private var cancellable: AnyCancellable?
    private var currentPage: Int = 3 // Starting point
    private let pageSize = 4
    private var hasMorePages = true

    func fetchMyHikes() {
        guard !isLoading else { return }
        guard hasMorePages else { return }

        isLoading = true

        guard let token = TokenManager.shared.getToken(),
              let userId = TokenManager.shared.getUserId() else {
            print("❌ No token or user ID found.")
            return
        }

        let urlString = "https://hopla.onrender.com/userhikes/user?userId=\(userId)&pageNumber=\(currentPage)&pageSize=\(pageSize)"
        print("📤 Final request URL:", urlString)

        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            isLoading = false
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { [weak self] data, response, error in
            guard let self = self else { return }

            defer {
                DispatchQueue.main.async {
                    self.isLoading = false
                }
            }


            if let data = data, let rawResponse = String(data: data, encoding: .utf8) {
                print("📡 Raw API Response:", rawResponse)
            }

            if let error = error {
                print("❌ Request error:", error.localizedDescription)
                return
            }

            guard let httpResponse = response as? HTTPURLResponse,
                  httpResponse.statusCode == 200,
                  let data = data else {
                print("❌ Invalid response or status code")
                return
            }

            do {
                let decodedResponse = try JSONDecoder().decode(MyHikeResponse.self, from: data)
                DispatchQueue.main.async {
                    if decodedResponse.userHikes.isEmpty {
                        self.hasMorePages = false
                    } else {
                        self.myHikes.append(contentsOf: decodedResponse.userHikes)
                        self.currentPage += 1
                    }
                    print("✅ Total Hikes:", self.myHikes.count)
                }
            } catch {
                print("❌ Error decoding:", error.localizedDescription)
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
                    LazyVStack(spacing: 10) {
                        ForEach(viewModel.myHikes.indices, id: \.self) { index in
                            let myHike = viewModel.myHikes[index]
                            MyHikePostContainer(
                                imageName: myHike.pictureUrl ?? "https://your-default-image-url.com",
                                comment: myHike.trailName,
                                length: myHike.length,
                                duration: myHike.duration,
                                colorScheme: colorScheme
                            )
                            .onAppear {
                                if index == viewModel.myHikes.count - 1 {
                                    // User scrolled to last hike
                                    viewModel.fetchMyHikes()
                                }
                            }
                        }
                        
                    }
                    .padding()
                    
                    if viewModel.isLoading {
                        ProgressView("Loading more hikes...")
                            .padding()
                    }
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
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
            
            Text("Duration: \(String(format: "%.2f", duration)) min")
                .font(.subheadline)
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AdaptiveColor.background.color(for: colorScheme))
        .cornerRadius(10)
        .shadow(radius: 5)
    }
}

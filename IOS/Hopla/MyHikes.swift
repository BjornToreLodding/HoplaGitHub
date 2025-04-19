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
    var trailName: String
    let trailId: String? // ✅ Optional TrailId
    let length: Double
    let duration: Double
    var pictureUrl: String?
    var title: String // ✅ Matches `title` in API
    var comment: String // ✅ Matches `comment` instead of `description`
    var horseName: String? // ✅ Matches `horseName`
    var trailButton: Bool // ✅ Matches API `trailButton`
    
    // Custom initializer from dictionary (in case of manual parsing)
    init(from dictionary: [String: Any]) {
        self.id = dictionary["id"] as? String ?? UUID().uuidString
        self.trailName = dictionary["trailName"] as? String ?? "Unnamed"
        self.trailId = dictionary["trailId"] as? String
        self.length = Double(dictionary["length"] as? String ?? "0") ?? 0.0
        self.duration = Double(dictionary["duration"] as? String ?? "0") ?? 0.0
        self.pictureUrl = dictionary["pictureUrl"] as? String
        self.title = dictionary["title"] as? String ?? "Unnamed Hike"
        self.comment = dictionary["comment"] as? String ?? "No description provided"
        self.horseName = dictionary["horseName"] as? String
        self.trailButton = dictionary["trailButton"] as? Bool ?? false
    }
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
    @Published var rawApiResponse: String = ""
    
    private var cancellable: AnyCancellable?
    private var currentPage: Int = 1
    private let pageSize = 4
    private var hasMorePages = true
    
    private var fetchedIds = Set<String>()
    
    func reloadHikes() {
        self.currentPage   = 1
        self.hasMorePages  = true
        self.myHikes       = []
        fetchMyHikes()
      }
    
    func fetchMyHikes() {
        guard !isLoading else { return }
        guard hasMorePages else { return }
        
        isLoading = true
        
        guard let token = TokenManager.shared.getToken(),
              let userId = TokenManager.shared.getUserId() else {
            print("❌ No token or user ID found.")
            return
        }
        
        let urlString = """
          https://hopla.onrender.com/userhikes/user?
          userId=\(userId)
          &pageNumber=\(currentPage)
          &pageSize=\(pageSize)
          """
          .replacingOccurrences(of: "\n", with: "")

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
            defer { DispatchQueue.main.async { self?.isLoading = false } }
            guard let self = self else { return }
            
            defer {
                DispatchQueue.main.async {
                    self.isLoading = false
                }
            }
            
            if let data = data {
                DispatchQueue.main.async {
                    self.rawApiResponse = String(data: data, encoding: .utf8) ?? "No data" // ✅ Store response
                    print("📡 Raw API Response:", self.rawApiResponse)
                }
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
                    let resp = try JSONDecoder().decode(MyHikeResponse.self, from: data)
                    DispatchQueue.main.async {
                      let page = resp.userHikes
                      
                      // Reverse the batch so within it newest→oldest
                      let reversed = page.reversed()
                      
                      // Filter out anything whose id we've already inserted
                      let newOnes = reversed.filter { self.fetchedIds.insert($0.id).inserted }

                      if newOnes.isEmpty {
                        self.hasMorePages = false
                      } else {
                        if self.currentPage == 1 {
                          self.myHikes = newOnes
                        } else {
                          self.myHikes.append(contentsOf: newOnes)
                        }
                        self.currentPage += 1
                      }
                    }
                  } catch {
                    print("Decoding error:", error)
                  }        }
        .resume()
    }
}




// MARK: - MyHikes View
struct MyHikes: View {
    @EnvironmentObject var viewModel: MyHikeViewModel
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // Custom header matches MyHorses
                HeaderViewMyHikes(colorScheme: colorScheme)
                
                NavigationStack {
                    ZStack {
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                            .color(for: colorScheme)
                            .edgesIgnoringSafeArea(.all)
                        ScrollView {
                            LazyVStack(spacing: 10) {
                                ForEach(viewModel.myHikes.indices, id: \.self) { index in
                                    let hike = viewModel.myHikes[index]
                                    NavigationLink(destination: MyHikesDetails(hike: hike, myHikes: $viewModel.myHikes)) {
                                        MyHikePostContainer(
                                            trailName: hike.trailName,
                                            title: hike.title,
                                            imageName: hike.pictureUrl ?? "",
                                            comment: hike.comment,
                                            length: hike.length,
                                            duration: hike.duration,
                                            colorScheme: colorScheme
                                        )
                                    }
                                    .buttonStyle(PlainButtonStyle())
                                    .onAppear {
                                        if index == viewModel.myHikes.count - 1 {
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
                }
                .navigationBarBackButtonHidden(true)
            }
            .onAppear {
                // only load if we haven’t already
                if viewModel.myHikes.isEmpty {
                    viewModel.fetchMyHikes()
                }
            }
            // Custom back button overlay
            CustomBackButton(colorScheme: colorScheme)
        }
    }
}

struct HeaderViewMyHikes: View {
    var colorScheme: ColorScheme
    
    var body: some View {
        Text("My activity")
            .font(.title)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}




// MARK: - PostContainer View
struct MyHikePostContainer: View {
    var trailName: String
    var title: String
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
            Text(title)
                .font(.title)
                .bold()
                .padding(.bottom, 2)
            
            Text("Trail Name: \(trailName)")
                .font(.headline)
                .padding(.bottom, 2)
            
            Text(comment)
                .font(.headline)
                .padding(.top, 5)
            
            Text("Length: \(String(format: "%.2f", length)) km")
                .font(.subheadline)
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
            
            Text("Duration: \(String(format: "%02d.%02d", Int(duration) / 60, Int(duration) % 60)) min")
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
        .cornerRadius(10)
        .shadow(radius: 5)
    }
}

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
    let trailId: String? // Optional TrailId
    let length: Double
    let duration: Double
    var pictureUrl: String?
    var title: String // Matches `title` in API
    var comment: String // Matches `comment` instead of `description`
    var horseName: String? // Matches `horseName`
    var trailButton: Bool // Matches API `trailButton`
    
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
        self.fetchedIds.removeAll()
        fetchMyHikes()
    }
    
    func fetchMyHikes() {
        // For UITests
        if ProcessInfo.processInfo.arguments.contains("-UITestMode") {
            // Build a stub from env vars, or fall back to defaults
            let env = ProcessInfo.processInfo.environment
            let id      = env["MOCK_HIKE_ID"]      ?? "hike1"
            let title   = env["MOCK_HIKE_TITLE"]   ?? "Morning Hike"
            let trail   = env["MOCK_HIKE_TRAIL"]   ?? "Sunny Trail"
            let comment = env["MOCK_HIKE_COMMENT"] ?? "Great view"
            let length  = Double(env["MOCK_HIKE_LENGTH"]  ?? "10.5") ?? 10.5
            let dur     = Double(env["MOCK_HIKE_DURATION"] ?? "120")  ?? 120
            let stub = MyHike(
                id: id,
                trailName: trail,
                trailId: nil,
                length: length,
                duration: dur,
                pictureUrl: nil,
                title: title,
                comment: comment,
                horseName: nil,
                trailButton: false
            )
            DispatchQueue.main.async {
                self.myHikes = [stub]
                self.isLoading = false
            }
            return
        }
        guard !isLoading else { return }
        guard hasMorePages else { return }
        isLoading = true
        guard let token = TokenManager.shared.getToken(),
              let userId = TokenManager.shared.getUserId() else {
            print("No token or user ID found.")
            return
        }
        
        let urlString = """
          https://hopla.onrender.com/userhikes/user?
          pageNumber=\(currentPage)
          &pageSize=\(pageSize)
          """
            .replacingOccurrences(of: "\n", with: "")
        
        print("Final request URL:", urlString)
        
        guard let url = URL(string: urlString) else {
            print("Invalid URL")
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
                    self.rawApiResponse = String(data: data, encoding: .utf8) ?? "No data" // Store response
                    print("Raw API Response:", self.rawApiResponse)
                }
            }
            
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse,
                  httpResponse.statusCode == 200,
                  let data = data else {
                print("Invalid response or status code")
                return
            }
            
            do {
                let resp = try JSONDecoder().decode(MyHikeResponse.self, from: data)
                DispatchQueue.main.async {
                    let page = resp.userHikes
                    // Reverse the batch so within it newest→oldest
                    let reversed = page.reversed()
                    // Filter out anything whose id already inserted
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
                HeaderViewMyHikes(colorScheme: colorScheme)
                hikesNavigation
            }
            .onAppear {
                if viewModel.myHikes.isEmpty {
                    viewModel.fetchMyHikes()
                }
            }
            CustomBackButton(colorScheme: colorScheme)
        }
        .accessibilityIdentifier("myhikes_screen_root")
    }
    
    // 1) Pull the Navigation + list into its own small var
    @ViewBuilder
    private var hikesNavigation: some View {
        NavigationStack {
            ZStack {
                // 2) Background
                AdaptiveColor(light: .mainLightBackground,
                              dark:  .mainDarkBackground)
                .color(for: colorScheme)
                .ignoresSafeArea()         // fill behind the header too
                
                // 3) The ScrollView + LazyVStack
                hikesScrollView
            }
            .frame(maxWidth: .infinity,
                   maxHeight: .infinity)         // force the ZStack to fill
        }
        .navigationBarBackButtonHidden(true)
    }
    
    // 2) Background
    private var contentBackground: some View {
        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
            .color(for: colorScheme)
            .edgesIgnoringSafeArea(.all)
    }
    
    // 3) The ScrollView + LazyVStack
    @ViewBuilder
    private var hikesScrollView: some View {
        ScrollView {
            LazyVStack(spacing: 10) {
                ForEach(viewModel.myHikes) { hike in
                    hikeLink(for: hike)
                        .onAppear {
                            if hike.id == viewModel.myHikes.last?.id {
                                viewModel.fetchMyHikes()
                            }
                        }
                }
            }
            .padding()
            if viewModel.isLoading {
                ProgressView("Loading more hikes…").padding()
            }
        }
    }
    
    // 4) Extract each row into its own tiny function
    private func hikeLink(for hike: MyHike) -> some View {
        NavigationLink(
            destination: MyHikesDetails(hike: hike, myHikes: $viewModel.myHikes)
        ) {
            MyHikePostContainer(
                id:           hike.id,
                trailName:    hike.trailName,
                title:        hike.title,
                imageName:    hike.pictureUrl ?? "",
                comment:      hike.comment,
                length:       hike.length,
                duration:     hike.duration,
                colorScheme:  colorScheme
            )
        }
        .accessibilityIdentifier("myhike_\(hike.id)_navlink")
        .buttonStyle(PlainButtonStyle())
    }
}

struct HeaderViewMyHikes: View {
    var colorScheme: ColorScheme
    var body: some View {
        Text("My hikes")
            .font(.custom("ArialNova", size: 20))
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}

// MARK: - PostContainer View
struct MyHikePostContainer: View {
    let id: String
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
                .accessibilityIdentifier("myhike_\(id)_title_label")
                .font(.title)
                .bold()
                .padding(.bottom, 2)            
            Text("Trail Name: \(trailName)")
                .accessibilityIdentifier("myhike_\(id)_trail_label")
                .font(.headline)
                .padding(.bottom, 2)
            Text(comment)
                .accessibilityIdentifier("myhike_\(id)_comment_label")
                .font(.headline)
                .padding(.top, 5)
            Text("Length: \(String(format: "%.2f", length)) km")
                .accessibilityIdentifier("myhike_\(id)_length_label")
                .font(.subheadline)
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
            Text("Duration: \(String(format: "%02d.%02d", Int(duration) / 60, Int(duration) % 60)) min")
                .accessibilityIdentifier("myhike_\(id)_duration_label")
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
        .cornerRadius(10)
        .shadow(radius: 5)
    }
}

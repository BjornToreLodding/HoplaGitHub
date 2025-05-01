//
//  Home.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import CoreLocation

struct HomePost: Identifiable, Decodable {
    var id: String { entityId }

    let entityId: String
    let title: String
    let description: String
    let pictureUrl: String?
    let userAlias: String?
    let userProfilePicture: String?
    let likes: Int
    let isLikedByUser: Bool
    let createdAt: String
}



class HomeViewModel: ObservableObject {
    @Published var homePosts: [HomePost] = []
    private let session: URLSession
    
    // DESIGNATED INIT
        init(session: URLSession = .shared) {
            self.session = session
        }

    func fetchPosts(for filter: String, latitude: Double? = nil, longitude: Double? = nil) {
        let args = ProcessInfo.processInfo.arguments
            if args.contains("-UITestMode") {
                let stub = HomePost(
                  entityId: "stub-1",
                  title: ProcessInfo.processInfo.environment["MOCK_POST_TITLE"] ?? "Stub Title",
                  description: ProcessInfo.processInfo.environment["MOCK_POST_DESC"]  ?? "Stub Description",
                  pictureUrl: nil,
                  userAlias: ProcessInfo.processInfo.environment["MOCK_POST_ALIAS"] ?? "stub_user",
                  userProfilePicture: nil,
                  likes: Int(ProcessInfo.processInfo.environment["MOCK_POST_LIKES"] ?? "3") ?? 3,
                  isLikedByUser: false,
                  createdAt: "2025-05-01T12:00:00Z"
                )
                DispatchQueue.main.async {
                  self.homePosts = [stub]
                }
                return
            }
        
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            return
        }

        var urlString: String
        switch filter {
        case "globe":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses"
        case "person.2":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses&onlyFriendsAndFollowing=true"
        case "point.bottomleft.forward.to.point.topright.scurvepath":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses&onlyLikedTrails=true"
        case "location":
            if let lat = latitude, let long = longitude {
                urlString = "https://hopla.onrender.com/feed/all?userlat=\(lat)&userlong=\(long)&radius=120"
            } else {
                print("❌ Missing latitude/longitude for location filter")
                return
            }
        case "hand.thumbsup":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses&sort=likes"
        default:
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses"
        }

        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return
        }
        
        print("🌍 Fetching posts with URL: \(urlString)")


        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        session.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error: \(error)")
                return
            }

            guard let data = data else {
                print("❌ No data received")
                return
            }

            do {
                let decoded = try JSONDecoder().decode(FeedResponse.self, from: data)
                DispatchQueue.main.async {
                    self.homePosts = decoded.items
                }
            } catch {
                print("❌ Decoding error: \(error)")
            }
        }.resume()
    }

    func likePost(entityId: String) {
        if ProcessInfo.processInfo.arguments.contains("-UITestMode") {
            DispatchQueue.main.async {
              self.updatePost(entityId: entityId, liked: true)
            }
            return
          }
            guard let token = TokenManager.shared.getToken() else {
                print("❌ No token found")
                return
            }

            guard let url = URL(string: "https://hopla.onrender.com/reactions") else { return }

            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")

            let body: [String: String] = ["EntityId": entityId]
            request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        session.dataTask(with: request) { _, response, error in
                if let error = error {
                    print("❌ Like error: \(error)")
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else { return }

                DispatchQueue.main.async {
                    if httpResponse.statusCode == 200 {
                        self.updatePost(entityId: entityId, liked: true)
                    } else if httpResponse.statusCode == 400 {
                        print("⚠️ Already liked")
                    }
                }
            }.resume()
        }

        func unlikePost(entityId: String) {
            if ProcessInfo.processInfo.arguments.contains("-UITestMode") {
                DispatchQueue.main.async {
                  self.updatePost(entityId: entityId, liked: false)
                }
                return
              }
            guard let token = TokenManager.shared.getToken() else {
                print("❌ No token found")
                return
            }

            guard let url = URL(string: "https://hopla.onrender.com/reactions") else { return }

            var request = URLRequest(url: url)
            request.httpMethod = "DELETE"
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")

            let body: [String: String] = ["EntityId": entityId]
            request.httpBody = try? JSONSerialization.data(withJSONObject: body)

            session.dataTask(with: request) { _, response, error in
                if let error = error {
                    print("❌ Unlike error: \(error)")
                    return
                }

                guard let httpResponse = response as? HTTPURLResponse else { return }

                DispatchQueue.main.async {
                    if httpResponse.statusCode == 200 {
                        self.updatePost(entityId: entityId, liked: false)
                    } else if httpResponse.statusCode == 404 {
                        print("⚠️ No like to remove")
                    }
                }
            }.resume()
        }

        private func updatePost(entityId: String, liked: Bool) {
            if let index = homePosts.firstIndex(where: { $0.entityId == entityId }) {
                var post = homePosts[index]
                let newLikes = liked ? post.likes + 1 : max(post.likes - 1, 0)
                homePosts[index] = HomePost(
                    entityId: post.entityId,
                    title: post.title,
                    description: post.description,
                    pictureUrl: post.pictureUrl,
                    userAlias: post.userAlias,
                    userProfilePicture: post.userProfilePicture,
                    likes: newLikes,
                    isLikedByUser: liked,
                    createdAt: post.createdAt
                )
            }
        }
}

struct FeedResponse: Decodable {
    let items: [HomePost]
}



struct Home: View {
    @Environment(\.colorScheme) var colorScheme
    @StateObject private var viewModel = HomeViewModel()
    @StateObject private var locationManager = LocationManager()
    @State private var selectedFilter: String = "globe"


    var body: some View {
        NavigationStack {
            ZStack {
                Rectangle()
                    .fill(AdaptiveColor.background.color(for: colorScheme))
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    filterBar
                    ScrollView {
                        VStack(spacing: 10) {
                            ForEach(viewModel.homePosts) { homePost in
                                PostContainer(
                                    post: homePost,
                                    colorScheme: colorScheme,
                                    viewModel: viewModel
                                )
                            }
                        }
                    }
                    .padding(.top, 0)
                    .onChange(of: selectedFilter) { newValue in
                        if newValue == "location" {
                            if let location = locationManager.userLocation {
                                viewModel.fetchPosts(for: newValue, latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
                            } else {
                                print("❌ Location not yet available")
                            }
                        } else {
                            viewModel.fetchPosts(for: newValue)
                        }
                    }
                    .onAppear {
                        if selectedFilter == "location", let location = locationManager.userLocation {
                            viewModel.fetchPosts(for: selectedFilter, latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
                        } else {
                            viewModel.fetchPosts(for: selectedFilter)
                        }
                    }

                }
            }
        }
        .accessibilityIdentifier("homeScreenRoot")
    }

    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                ForEach(["globe", "person.2", "point.bottomleft.forward.to.point.topright.scurvepath", "location", "hand.thumbsup"], id: \.self) { filter in
                    Image(systemName: filter)
                        .tag(filter)
                        .onTapGesture {
                            if selectedFilter == filter {
                                // Re-selecting the same filter
                                if filter == "location", let location = locationManager.userLocation {
                                    viewModel.fetchPosts(for: filter, latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
                                } else {
                                    viewModel.fetchPosts(for: filter)
                                }
                            }
                        }
                }
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 16)
        .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
    }
}

struct PostContainer: View {
    var post: HomePost
    var colorScheme: ColorScheme
    @ObservedObject var viewModel: HomeViewModel

    var formattedDate: String {
        let isoFormatter = ISO8601DateFormatter()
        if let date = isoFormatter.date(from: post.createdAt) {
            let formatter = DateFormatter()
            formatter.dateFormat = "dd.MM.yyyy HH.mm"
            return formatter.string(from: date)
        }
        return ""
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Header
            HStack {
                AsyncImage(url: URL(string: post.userProfilePicture ?? "")) { image in
                    image.resizable()
                } placeholder: {
                    ProgressView()
                }
                .frame(width: 40, height: 40)
                .clipShape(Circle())

                Text(post.userAlias ?? "Unknown user")
                    .font(.headline)
                    .adaptiveTextColor(light: .textLightBackground, dark: .textDarkBackground)

                Spacer()
            }

            Text(post.title)
                .accessibilityIdentifier("homepost_\(post.id)_title_label")
                .bold()
                .adaptiveTextColor(light: .textLightBackground, dark: .textDarkBackground)

            Text(post.description)
                .accessibilityIdentifier("homepost_\(post.id)_description_label")
                .adaptiveTextColor(light: .textLightBackground, dark: .textDarkBackground)

            if let url = URL(string: post.pictureUrl ?? "") {
                AsyncImage(url: url) { image in
                    image.resizable()
                } placeholder: {
                    ProgressView()
                }
                .scaledToFill()
                .frame(width: 350, height: 260)
                .clipped()
                .cornerRadius(2)
            }

            HStack {
                Button(action: {
                    if post.isLikedByUser {
                        viewModel.unlikePost(entityId: post.entityId)
                    } else {
                        viewModel.likePost(entityId: post.entityId)
                    }
                }) {
                    Image(systemName: post.isLikedByUser ? "heart.fill" : "heart")
                        .foregroundColor(post.isLikedByUser ? .red : .gray)
                }
                .accessibilityIdentifier("homepost_\(post.id)_like_button")
                .accessibilityValue(post.isLikedByUser ? "liked" : "not_liked")

                Text("\(post.likes)")
                    .accessibilityIdentifier("homepost_\(post.id)_likes_label")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                Spacer()

                Text(formattedDate)
                    .font(.footnote)
                    .foregroundColor(.gray)
            }
        }
        .padding()
        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
        .cornerRadius(10)
        .padding(.horizontal)
    }
}


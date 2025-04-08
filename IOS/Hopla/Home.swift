//
//  Home.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct HomePost: Identifiable, Decodable {
    var id: String { entityId }

    let entityId: String
    let title: String
    let description: String
    let pictureUrl: String
    let userAlias: String
    let userProfilePicture: String
    let likes: Int
    let isLikedByUser: Bool
    let createdAt: String
}

class HomeViewModel: ObservableObject {
    @Published var homePosts: [HomePost] = []

    func fetchPosts(for filter: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found")
            return
        }

        let urlString: String
        switch filter {
        case "globe":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horse"
        case "person.2":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses&onlyFriendsAndFollowing=true"
        case "point.bottomleft.forward.to.point.topright.scurvepath":
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses&onlyLikedTrails=true"
        case "location":
            urlString = "https://hopla.onrender.com/feed/all?userlat=60&userlong=10&radius=120"
        case "hand.thumpsup":
            urlString = "https://hopla.onrender.com/feed/all?sort=likes"
        default:
            urlString = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horse"
        }

        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
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
}

struct FeedResponse: Decodable {
    let items: [HomePost]
}



struct Home: View {
    @Environment(\.colorScheme) var colorScheme
    @StateObject private var viewModel = HomeViewModel()
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
                                    profileImageUrl: homePost.userProfilePicture,
                                    userAlias: homePost.userAlias,
                                    title: homePost.title,
                                    description: homePost.description,
                                    imageUrl: homePost.pictureUrl,
                                    createdAt: homePost.createdAt,
                                    colorScheme: colorScheme
                                )
                            }
                        }
                    }
                    .padding(.top, 0)
                    .onChange(of: selectedFilter) { newValue in
                        viewModel.fetchPosts(for: newValue)
                    }
                    .onAppear {
                        viewModel.fetchPosts(for: selectedFilter)
                    }
                }
            }
        }
    }

    private var filterBar: some View {
        HStack {
            SwiftUI.Picker("Filter", selection: $selectedFilter) {
                Image(systemName: "globe").tag("globe")
                Image(systemName: "person.2").tag("person.2")
                Image(systemName: "point.bottomleft.forward.to.point.topright.scurvepath").tag("point.bottomleft.forward.to.point.topright.scurvepath") // løype
                Image(systemName: "location").tag("location")
                Image(systemName: "hand.thumbsup").tag("hand.thumbsup") //.fill
            }
            .pickerStyle(SegmentedPickerStyle())
        }
        .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
    }
}

struct PostContainer: View {
    var profileImageUrl: String
    var userAlias: String
    var title: String
    var description: String
    var imageUrl: String
    var createdAt: String
    var colorScheme: ColorScheme
    
    

    var formattedDate: String {
        let isoFormatter = ISO8601DateFormatter()
        if let date = isoFormatter.date(from: createdAt) {
            let formatter = DateFormatter()
            formatter.dateFormat = "dd.MM.yyyy HH.mm"
            return formatter.string(from: date)
        }
        return ""
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Profile picture and user alias
            HStack(alignment: .center) {
                AsyncImage(url: URL(string: profileImageUrl)) { image in
                    image.resizable()
                } placeholder: {
                    ProgressView()
                }
                .frame(width: 40, height: 40)
                .clipShape(Circle())

                Text(userAlias)
                    .font(.headline)
                    .adaptiveTextColor(light: .textLightBackground, dark: .textDarkBackground)

                Spacer()
            }

            // Title
            Text(title)
                .bold()
                .adaptiveTextColor(light: .textLightBackground, dark: .textDarkBackground)

            // Description
            Text(description)
                .font(.body)
                .adaptiveTextColor(light: .textLightBackground, dark: .textDarkBackground)

            // Image
            AsyncImage(url: URL(string: imageUrl)) { image in
                image.resizable()
            } placeholder: {
                ProgressView()
            }
            .scaledToFill()
            .frame(maxWidth: .infinity)
            .frame(height: 260)
            .clipped()
            .cornerRadius(8)

            // Date and time (bottom right)
                Spacer()
                Text(formattedDate)
            
        }
        
        .padding()
        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}


//
//  FollowingDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 23/03/2025.
//

import SwiftUI

struct FollowingInfo: Identifiable, Decodable {
    var id: String
    var name: String
    var alias: String
    var profilePictureUrl: String?
    var description: String?
    var friendsCount: Int?    // Add friendsCount
    var horseCount: Int?      // Add horseCount
    var userHikes: [Post]?
    
    var relationStatus: PersonStatus
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case name = "name"
        case alias = "alias"
        case profilePictureUrl = "pictureUrl"
        case description = "description"
        case friendsCount = "friendsCount"    // Add mapping
        case horseCount = "horseCount"        // Add mapping
        case userHikes = "userHikes"
        case relationStatus 
    }
}



// MARK: - Header
struct FollowingDetailsHeader: View {
    var following: FollowingInfo?
    var colorScheme: ColorScheme
    
    var body: some View {
        VStack {
            if let following = following {
                Text(following.alias)
                    .font(.custom("ArialNova", size: 20))
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
            } else {
                Text("Loading...")
                    .font(.title)
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
            }
        }
    }
}

class FollowingDetailsViewModel: ObservableObject {
    @Published var followingDetails: FollowingInfo?
    @Published var isLoading = false
    
    func fetchFollowingDetails(userId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/users/profile?userId=\(userId)")!
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        isLoading = true
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                self.isLoading = false
            }
            
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200, let data = data else {
                print("Invalid response or status code")
                return
            }
            
            do {
                let followingDetails = try JSONDecoder().decode(FollowingInfo.self, from: data)
                DispatchQueue.main.async {
                    self.followingDetails = followingDetails
                }
            } catch {
                print("Error decoding following details:", error.localizedDescription)
            }
        }.resume()
    }
}

struct FollowingDetails: View {
    var userId: String
    @StateObject private var vm = FollowingDetailsViewModel()
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack {
            if vm.isLoading {
                ProgressView("Loading...")
            } else if let following = vm.followingDetails {
                ZStack {
                    VStack(spacing: 0) {
                        FollowingDetailsHeader(following: following, colorScheme: colorScheme)
                        NavigationStack {
                            ScrollView {
                                // Profile Picture
                                profilePictureView(following: following)
                                
                                actionButtonsView(for: following)
                                
                                // Following details in a white box
                                followingDetailsBox(following: following)
                                
                                // Description
                                descriptionView(following: following)
                                
                                // Hikes
                                hikesView(following: following)
                            }
                        }
                        .navigationBarBackButtonHidden(true)
                    }
                    CustomBackButton(colorScheme: colorScheme)
                }
                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
            } else {
                Text("No details available")
            }
        }
        .onAppear {
            vm.fetchFollowingDetails(userId: userId)
        }
    }
    
    private func profilePictureView(following: FollowingInfo) -> some View {
        if let urlString = following.profilePictureUrl, let url = URL(string: urlString) {
            return AnyView(
                AsyncImage(url: url) { image in
                    image.resizable()
                        .scaledToFill()
                        .frame(width: 200, height: 200)
                        .clipShape(Circle())
                        .overlay(
                          Circle()
                            .stroke(
                              AdaptiveColor(light: .lightPostBackground,
                                            dark:  .darkPostBackground)
                                .color(for: colorScheme),
                              lineWidth: 10
                            )
                        )
                        .padding(20)
                } placeholder: {
                    Circle()
                        .fill(Color.gray.opacity(0.5))
                        .frame(width: 200, height: 200)
                        .padding(20)
                }
            )
        } else {
            return AnyView(EmptyView()) // Return empty view if no profile picture
        }
    }

    private func followingDetailsBox(following: FollowingInfo) -> some View {
        return VStack(spacing: 0) {
            Text(following.name)
                .font(.title)
                .fontWeight(.bold)
            
            Text(following.alias)
                .font(.subheadline)
                .foregroundColor(.gray)
            
            HStack {
                Text("Friends: \(following.friendsCount ?? 0)")    // Show friends count
                Text("Horses: \(following.horseCount ?? 0)")        // Show horses count
            }
        }
        .padding()
    }

    private func descriptionView(following: FollowingInfo) -> some View {
        if let description = following.description {
            return AnyView(
                VStack(alignment: .leading) {
                    Text("Description:")
                        .font(.headline)
                    
                    Text(description)
                        .padding()
                        .frame(width: 370)
                        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                }
                
            )
        } else {
            return AnyView(EmptyView()) // Return empty view if no description
        }
    }
    
    private func hikesView(following: FollowingInfo) -> some View {
        if let hikes = following.userHikes, !hikes.isEmpty {
            return AnyView(
                VStack(alignment: .leading) {
                    Text("Hikes:")
                        .font(.headline)
                    
                    ForEach(hikes, id: \.id) { hike in
                        VStack(alignment: .leading) {
                            Text(hike.trailName)
                                .font(.subheadline)
                                .bold()
                            
                            Text("Length: \(String(format: "%.2f", hike.length)) km | Duration: \(String(format: "%.2f", hike.duration)) min")
                                .font(.caption)
                                .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                    }
                }
                .padding()
            )
        } else {
            return AnyView(EmptyView()) // Return empty view if no hikes
        }
    }
    
    //MARK: - Add friend/following buttons
    private func actionButtonsView(for following: FollowingInfo) -> some View {
        let relationVM = RelationViewModel()
        let vm = FollowingDetailsViewModel()

        func buttonTitle(for status: PersonStatus) -> String {
            switch status {
            case .none:      return "Follow"
            case .pending:   return "Request Pending"
            case .friends:   return "Unfriend"
            case .following: return "Unfollow"
            case .block:     return "Unblock"
            }
        }

        // pick your button background based on light/dark
        let bgColor = AdaptiveColor(
            light: .lightPostBackground,
            dark:  .darkPostBackground
        ).color(for: colorScheme)

        // pick your text color based on light/dark
        let textColor = AdaptiveColor(
            light: .textLightBackground,
            dark:  .textDarkBackground
        ).color(for: colorScheme)

        return VStack {
            Button(action: {
                switch following.relationStatus {
                case .none:
                    relationVM.changeRelation(to: .following, for: following.id) { success in
                        if success { vm.fetchFollowingDetails(userId: following.id) }
                    }
                case .pending:
                    relationVM.changeRelation(to: .friends, for: following.id) { success in
                        if success { vm.fetchFollowingDetails(userId: following.id) }
                    }
                case .friends, .following, .block:
                    relationVM.changeRelation(to: .none, for: following.id) { success in
                        if success { vm.fetchFollowingDetails(userId: following.id) }
                    }
                }
            }) {
                Text(buttonTitle(for: following.relationStatus))
                    .foregroundColor(textColor)
            }
            .buttonStyle(.borderedProminent)
            .tint(bgColor)
        }
    }
}

//
//  UserDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/03/2025.
//

import SwiftUI

struct UserInfo: Identifiable, Decodable {
    var id: String
    var name: String
    var alias: String
    var profilePictureUrl: String?
    var description: String?
    var friendsCount: Int?
    var horseCount: Int?
    var relationStatus: PersonStatus
    var userHikes: [Post]?
    var createdAt: String?
    var dob: String? = nil

    enum CodingKeys: String, CodingKey {
        case id
        case name
        case alias
        case profilePictureUrl = "pictureUrl"
        case description
        case friendsCount
        case horseCount
        case relationStatus
        case userHikes
        case createdAt = "created_at"
        case dob
    }
}

class UserDetailsViewModel: ObservableObject {
    @Published var userDetails: UserInfo?
    @Published var isLoading = false
    
    func fetchUserDetails(userId: String) {
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
                let decoder = JSONDecoder()
                let userDetails = try decoder.decode(UserInfo.self, from: data)
                DispatchQueue.main.async {
                    self.userDetails = userDetails
                }
            } catch {
                print("Error decoding user details:", error.localizedDescription)
            }
        }.resume()
    }
    
    // Future POST requests for following and adding as friend
    func followUser(userId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/userrelations/follow")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let body: [String: Any] = ["userId": userId]
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }
            
            // Handle after following
        }.resume()
    }
}

// MARK: - Header
struct UserDetailsHeader: View {
    var user: UserInfo?
    var colorScheme: ColorScheme
    
    var body: some View {
        VStack {
            if let user = user {
                Text(user.name)
                    .font(.title)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)
            } else {
                Text("Loading...")
                    .font(.title)
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)
            }
        }
    }
}


struct UserDetails: View {
    var userId: String
    @StateObject private var vm = UserDetailsViewModel()
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack {
            if vm.isLoading {
                ProgressView("Loading...")
            } else if let user = vm.userDetails {
                ZStack {
                    VStack(spacing: 0) {
                        UserDetailsHeader(user: user, colorScheme: colorScheme)
                        NavigationStack {
                            ScrollView {
                                // Profile Picture
                                profilePictureView(user: user)
                                
                                // User details in a white box
                                userDetailsBox(user: user)
                                
                                // Description
                                descriptionView(user: user)
                                
                                // Hikes
                                hikesView(user: user)
                                
                                // Action buttons (Follow, Add Friend)
                                actionButtonsView(user: user)
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
            vm.fetchUserDetails(userId: userId)
        }
    }
    
    private func profilePictureView(user: UserInfo) -> some View {
        if let urlString = user.profilePictureUrl, let url = URL(string: urlString) {
            return AnyView(
                AsyncImage(url: url) { image in
                    image.resizable()
                        .scaledToFill()
                        .frame(width: 200, height: 200)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(Color.white, lineWidth: 10))
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
    
    private func userDetailsBox(user: UserInfo) -> some View {
        return VStack(spacing: 0) {
            Text(user.name)
                .font(.title)
                .fontWeight(.bold)
            
            Text(user.alias)
                .font(.subheadline)
                .foregroundColor(.gray)
            
            HStack {
                Text("Friends: \(user.friendsCount ?? 0)")
                Text("Horses: \(user.horseCount ?? 0)")
            }
        }
        .padding()
    }

    private func descriptionView(user: UserInfo) -> some View {
        if let description = user.description {
            return AnyView(
                VStack(alignment: .leading) {
                    Text("Description:")
                        .font(.headline)
                    
                    Text(description)
                        .padding()
                        .frame(width: 370)
                        .background(Color.white)
                }
            )
        } else {
            return AnyView(EmptyView()) // Return empty view if no description
        }
    }
    
    private func hikesView(user: UserInfo) -> some View {
        if let hikes = user.userHikes, !hikes.isEmpty {
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
                                .foregroundStyle(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                    }
                }
                .padding()
            )
        } else {
            return AnyView(EmptyView()) // Return empty view if no hikes
        }
    }
    
    private func actionButtonsView(user: UserInfo) -> some View {
        VStack {
            if user.relationStatus == .pending {
                Button(action: {
                    vm.followUser(userId: user.id)
                }) {
                    Text("Follow")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(AdaptiveColor(light: .blue, dark: .green).color(for: colorScheme))
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding()
            }
            Button(action: {
                // Add friend logic
            }) {
                Text("Add Friend")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
            .padding()
        }
    }
}


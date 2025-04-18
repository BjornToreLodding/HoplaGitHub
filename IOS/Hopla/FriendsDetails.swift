//
//  FriendsDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 23/03/2025.
//

import SwiftUI

struct FriendInfo: Identifiable, Decodable {
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



class FriendDetailsViewModel: ObservableObject {
    @Published var friendDetails: FriendInfo?
    @Published var isLoading = false
    
    func fetchFriendDetails(friendId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/users/profile?userId=\(friendId)")!
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
                let friendDetails = try decoder.decode(FriendInfo.self, from: data)
                DispatchQueue.main.async {
                    self.friendDetails = friendDetails
                }
            } catch {
                print("Error decoding friend details:", error.localizedDescription)
                if let jsonString = String(data: data, encoding: .utf8) {
                    print("Received JSON:", jsonString) // Debugging line
                }
            }

        }.resume()
    }

    // Future POST request - for later
    func addFriend(userId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/userrelations/friends")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let body: [String: Any] = ["friendId": userId]
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }

            DispatchQueue.main.async {
                self.fetchFriendDetails(friendId: userId) // Refresh details after adding
            }
        }.resume()
    }
}


// MARK: - Header
struct FriendsDetailsHeader: View {
    var friend: FriendInfo?
    var colorScheme: ColorScheme
    
    var body: some View {
        VStack {
            if let friend = friend {
                Text(friend.name)
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

struct FriendsDetails: View {
    var friendId: String
    @StateObject private var vm = FriendDetailsViewModel()
    @StateObject private var relationVM = RelationViewModel()
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack {
            if vm.isLoading {
                ProgressView("Loading...")
            } else if let friend = vm.friendDetails {
                ZStack {
                    VStack(spacing: 0) {
                        FriendsDetailsHeader(friend: friend, colorScheme: colorScheme)
                        NavigationStack {
                            ScrollView {
                                // Profile Picture
                                profilePictureView(friend: friend)
                                
                                actionButtonsView(for: friend)
                                
                                // Friend details in a white box
                                friendDetailsBox(friend: friend)
                                
                                // Description
                                descriptionView(friend: friend)
                                
                                // Hikes
                                hikesView(friend: friend)
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
            vm.fetchFriendDetails(friendId: friendId)
        }
    }
    
    private func profilePictureView(friend: FriendInfo) -> some View {
        if let urlString = friend.profilePictureUrl, let url = URL(string: urlString) {
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

    private func friendDetailsBox(friend: FriendInfo) -> some View {
        return VStack(spacing: 0) {
            Text(friend.name)
                .font(.title)
                .fontWeight(.bold)
            
            Text(friend.alias)
                .font(.subheadline)
                .foregroundColor(.gray)
            
            HStack {
                Text("Friends: \(friend.friendsCount ?? 0)")
                Text("Horses: \(friend.horseCount ?? 0)")
            }
        }
        .padding()
    }

    
    private func descriptionView(friend: FriendInfo) -> some View {
        if let description = friend.description {
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
    
    private func hikesView(friend: FriendInfo) -> some View {
        if let hikes = friend.userHikes, !hikes.isEmpty {
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
                                .foregroundColor(.gray)
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
    
    //MARK: - Add friend/following buttons
    private func actionButtonsView(for friend: FriendInfo) -> some View {
        // Use the existing vm (FriendDetailsViewModel)
        
        // Helper function to return the appropriate button title based on the current relation status
        func buttonTitle(for status: PersonStatus) -> String {
            switch status {
            case .none: return "Add Friend"
            case .pending: return "Friend Request Pending"
            case .friends: return "Unfriend"
            case .following: return "Unfollow"
            case .block: return "Unblock"
            }
        }

        return VStack {
            // Add Friend / Pending / Remove Friend / Unfriend button based on the relation status
            Button(buttonTitle(for: friend.relationStatus)) {
                switch friend.relationStatus {
                case .none:
                    // Add Friend
                    relationVM.changeRelation(to: .pending, for: friend.id) { success in
                        if success {
                            // Use the existing `vm` to reload the friend details after the relation change
                            vm.fetchFriendDetails(friendId: friend.id)
                        }
                    }
                case .pending:
                    // Accept Friend Request
                    relationVM.changeRelation(to: .friends, for: friend.id) { success in
                        if success {
                            // Reload the friend details after the relation change
                            vm.fetchFriendDetails(friendId: friend.id)
                        }
                    }
                case .friends:
                    // Remove Friend / Unfriend
                    relationVM.changeRelation(to: .none, for: friend.id) { success in
                        if success {
                            // Reload the friend details after the relation change
                            vm.fetchFriendDetails(friendId: friend.id)
                        }
                    }
                case .following:
                    // Unfollow
                    relationVM.changeRelation(to: .none, for: friend.id) { success in
                        if success {
                            // Reload the friend details after the relation change
                            vm.fetchFriendDetails(friendId: friend.id)
                        }
                    }
                case .block:
                    // Unblock
                    relationVM.changeRelation(to: .none, for: friend.id) { success in
                        if success {
                            // Reload the friend details after the relation change
                            vm.fetchFriendDetails(friendId: friend.id)
                        }
                    }
                }
            }
            .buttonStyle(.borderedProminent)
        }
    }
}

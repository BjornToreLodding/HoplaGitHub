//
//  Friends.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//

import SwiftUI
import Combine


// MARK: - Friend Model
struct Friend: Identifiable, Decodable {
    var id: String
    var name: String
    var alias: String
    var profilePictureUrl: String?

    enum CodingKeys: String, CodingKey {
        case id = "friendId" // maps friendId to id
        case name = "friendName" // maps friendName to name
        case alias = "friendAlias" // maps friendAlias to alias
        case profilePictureUrl = "friendPictureURL" // maps friendPictureURL to profilePictureUrl
    }
}


// MARK: - Post Model / Hikes (userHikes)
struct Post: Identifiable, Decodable {
    var id: String
    var trailName: String
    var length: Double
    var duration: Double
    var pictureUrl: String?
}

// Enum for relation status
enum PersonStatus: String, Decodable {
    case friend = "FRIEND"
    case following = "FOLLOWING"
    case none = "NONE"
    case pending = "PENDING"
}


// MARK: - Header
struct FriendsHeaderView: View {
    var colorScheme: ColorScheme
    
    var body: some View {
        Text("My friends")
            .font(.title)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}


class FriendViewModel: ObservableObject {
    @Published var friends: [Friend] = []
    @Published var searchText: String = "" // ✅ Added searchText

    var filteredFriends: [Friend] { // ✅ Moved filteredFriends inside FriendViewModel
        if searchText.isEmpty {
            return friends
        } else {
            return friends.filter { friend in
                friend.name.localizedCaseInsensitiveContains(searchText) ||
                friend.alias.localizedCaseInsensitiveContains(searchText)
            }
        }
    }

    func fetchFriends() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/userrelations/friends")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }

            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200, let data = data else {
                print("Invalid response or status code")
                return
            }

            do {
                // Print the raw JSON data for inspection
                if let jsonString = String(data: data, encoding: .utf8) {
                    print("Raw JSON Response: \(jsonString)")
                }

                let friends = try JSONDecoder().decode([Friend].self, from: data)
                DispatchQueue.main.async {
                    self.friends = friends
                }
            } catch {
                print("Error decoding friends:", error.localizedDescription)
            }
        }.resume()
    }



}



// MARK: - Friends View
struct Friends: View {
    @StateObject private var vm = FriendViewModel()
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                FriendsHeaderView(colorScheme: colorScheme)
                searchBar
                    .frame(maxWidth: .infinity)
                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                NavigationStack {
                    ZStack {
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                            .color(for: colorScheme)
                            .edgesIgnoringSafeArea(.all)
                        
                        FriendListView(vm: vm, colorScheme: colorScheme) // ✅ Pass vm directly
                    }
                }
                .navigationBarBackButtonHidden(true)
            }
            .onAppear {
                vm.fetchFriends()
            }
            CustomBackButton(colorScheme: colorScheme)
        }
    }

    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search friends...", text: $vm.searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}




// MARK: - Friend List
struct FriendListView: View {
    @ObservedObject var vm: FriendViewModel
    var colorScheme: ColorScheme

    var body: some View {
        ScrollView {
            VStack(spacing: 10) {
                ForEach(vm.filteredFriends) { friend in
                    NavigationLink(destination: FriendsDetails(friendId: friend.id)) {
                        FriendRowView(colorScheme: colorScheme, friend: friend)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
    }
}




// MARK: - Friend Row
struct FriendRowView: View {
    var colorScheme: ColorScheme
    var friend: Friend
    
    var body: some View {
        HStack {
            if let urlString = friend.profilePictureUrl, let url = URL(string: urlString) {
                AsyncImage(url: url) { image in
                    image.resizable()
                        .scaledToFill()
                        .frame(width: 60, height: 60)
                        .clipShape(Circle())
                } placeholder: {
                    Circle()
                        .fill(Color.gray.opacity(0.5))
                        .frame(width: 60, height: 60)
                }
            } else {
                Circle()
                    .fill(Color.gray.opacity(0.5))
                    .frame(width: 60, height: 60)
            }
            
            Text(friend.name)
                .font(.headline)
                .padding(.leading, 10)
            
            Spacer()
        }
        .padding()
        .frame(maxWidth: .infinity, minHeight: 80, alignment: .leading)
        .background(Color.white.opacity(0.8))
        .cornerRadius(10)
        .shadow(radius: 2)
        .padding(.horizontal)
    }
}



// MARK: - Add Friend Page
struct AddFriendPage: View {
    @State private var searchText: String = ""
    
    var body: some View {
        VStack {
            Text("Search for Users to Add")
                .font(.headline)
                .padding()
            
            TextField("Enter username...", text: $searchText)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            
            Spacer()
        }
        .navigationTitle("Add Friend")
    }
}


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
        case id = "friendId"
        case name = "friendName"
        case alias = "friendAlias"
        case profilePictureUrl = "friendPictureURL"
    }
}


class FriendViewModel: ObservableObject {
    @Published var friends: [Friend] = []
    
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
                let friends = try JSONDecoder().decode([Friend].self, from: data)
                if let jsonString = String(data: data, encoding: .utf8) {
                    print("Received JSON: \(jsonString)")
                }
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
        VStack(spacing: 0) {
            Text("My Friends")
                .font(.title)
                .fontWeight(.bold)
                .frame(maxWidth: .infinity)
                .frame(height: 40)
                .foregroundColor(.white)

            ScrollView {
                VStack(spacing: 10) {
                    ForEach(vm.friends) { friend in
                        FriendRowView(friend: friend)
                    }
                }
            }
        }
        .onAppear {
            vm.fetchFriends()
        }
    }
}

// MARK: - Friend Row
struct FriendRowView: View {
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


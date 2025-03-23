//
//  Following.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//

import SwiftUI
import Combine

// MARK: - Following Model
struct Following: Identifiable, Decodable {
    var id: String
    var name: String
    var alias: String
    var profilePictureUrl: String?

    enum CodingKeys: String, CodingKey {
        case id = "followingUserId"
        case name = "followingUserName"
        case alias = "followingUserAlias"
        case profilePictureUrl = "followingUserPicture"
    }
}

class FollowingViewModel: ObservableObject {
    @Published var following: [Following] = []
    
    func fetchFollowing() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/userrelations/following")!
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
                let following = try JSONDecoder().decode([Following].self, from: data)
                if let jsonString = String(data: data, encoding: .utf8) {
                    print("Received JSON: \(jsonString)")
                }
                DispatchQueue.main.async {
                    self.following = following
                }
            } catch {
                print("Error decoding following:", error.localizedDescription)
            }
        }.resume()
    }
}

// MARK: - Following View
struct FollowingView: View {
    @StateObject private var vm = FollowingViewModel()
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack(spacing: 0) {
            Text("Following")
                .font(.title)
                .fontWeight(.bold)
                .frame(maxWidth: .infinity)
                .frame(height: 40)
                .foregroundColor(.white)
            
            ScrollView {
                VStack(spacing: 10) {
                    ForEach(vm.following) { user in
                        FollowingRowView(user: user)
                    }
                }
            }
        }
        .onAppear {
            vm.fetchFollowing()
        }
    }
}

// MARK: - Following Row
struct FollowingRowView: View {
    var user: Following
    
    var body: some View {
        HStack {
            if let urlString = user.profilePictureUrl, let url = URL(string: urlString) {
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
            
            Text(user.name)
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


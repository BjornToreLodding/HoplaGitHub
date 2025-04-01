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
    var status: PersonStatus = .following // Always 'following' by default

    enum CodingKeys: String, CodingKey {
        case id = "followingUserId"
        case name = "followingUserName"
        case alias = "followingUserAlias"
        case profilePictureUrl = "followingUserPicture"
    }
}


// MARK: - Header
struct FollowingHeaderView: View {
    var colorScheme: ColorScheme
    
    var body: some View {
        Text("Following")
            .font(.title)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}

class FollowingViewModel: ObservableObject {
    @Published var following: [Following] = []
    @Published var allUsers: [User] = []
    @Published var filteredFollowing: [Following] = []
    
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
                    self.filteredFollowing = following
                }
            } catch {
                print("Error decoding following:", error.localizedDescription)
            }
        }.resume()
    }
    func fetchAllUsers() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/users/all")!
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
                let users = try JSONDecoder().decode([User].self, from: data)
                DispatchQueue.main.async {
                    self.allUsers = users
                }
            } catch {
                print("Error decoding all users:", error.localizedDescription)
            }
        }.resume()
    }
    
    func addFollowing(userId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/userrelations/addfollowing")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let body: [String: Any] = ["followingId": userId]
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }

            DispatchQueue.main.async {
                // Update list after adding
                self.fetchFollowing()
                self.fetchAllUsers()
            }
        }.resume()
    }
    
    private func updateFilteredFollowing() {
        if searchTextFollowing.isEmpty {
            filteredFollowing = following
        } else {
            filteredFollowing = following.filter { $0.name.localizedCaseInsensitiveContains(searchTextFollowing) }
        }
    }

    var searchTextFollowing: String = "" {
        didSet {
            updateFilteredFollowing()
        }
    }
}

// MARK: - Following View
struct FollowingView: View {
    @StateObject private var vm = FollowingViewModel()
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                FollowingHeaderView(colorScheme: colorScheme)
                searchBar
                    .frame(maxWidth: .infinity)
                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                NavigationStack {
                    ZStack {
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                            .color(for: colorScheme)
                            .edgesIgnoringSafeArea(.all)
                        
                        FollowingListView(vm: vm, colorScheme: colorScheme)
                    }
                }
                .navigationBarBackButtonHidden(true)
            }
            .onAppear {
                vm.fetchFollowing()
            }
            CustomBackButton(colorScheme: colorScheme)
            
            // Add Following button at the bottom-right corner
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    NavigationLink(destination: AddFollowingPage()) {
                        Image(systemName: "plus.circle.fill")
                            .resizable()
                            .frame(width: 60, height: 60)
                            .foregroundStyle(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                            .padding()
                    }
                }
            }
        }
    }

    // MARK: - Search Bar
    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search following...", text: $vm.searchTextFollowing)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}


// MARK: - Following List
struct FollowingListView: View {
    @ObservedObject var vm: FollowingViewModel
    var colorScheme: ColorScheme
    
    var body: some View {
        ScrollView {
            VStack(spacing: 10) {
                ForEach(vm.filteredFollowing) { following in
                    NavigationLink(destination: FollowingDetails(userId: following.id)) {
                        FollowingRowView(colorScheme: colorScheme, user: following)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
    }
}


// MARK: - Following Row
struct FollowingRowView: View {
    var colorScheme: ColorScheme
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

// MARK: - Add Following Page
struct AddFollowingPage: View {
    @State private var searchTextFollowing: String = ""
    @StateObject private var vm = FollowingViewModel()
    @Environment(\.colorScheme) var colorScheme

    var filteredUsers: [User] {
        if searchTextFollowing.isEmpty {
            return vm.allUsers
        } else {
            return vm.allUsers.filter { $0.name.localizedCaseInsensitiveContains(searchTextFollowing) }
        }
    }

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // The custom header
                FriendsHeaderView(colorScheme: colorScheme)
                
                // The search bar
                searchBar
                    .frame(maxWidth: .infinity)
                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))

                // ScrollView for the list of users
                ScrollView {
                    LazyVStack(spacing: 16) {
                        ForEach(filteredUsers) { user in
                            NavigationLink(destination: UserDetails(userId: user.id)) {
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

                                    if user.relationStatus == .following {
                                        Text("Following")
                                            .padding(8)
                                            .background(Color.gray)
                                            .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                            .cornerRadius(8)
                                    } else {
                                        Button(action: {
                                            vm.addFollowing(userId: user.id)
                                        }) {
                                            Text("Follow")
                                                .padding(8)
                                                .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                                                .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                                .cornerRadius(8)
                                        }
                                    }
                                }
                                .padding()
                                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                .shadow(radius: 2)
                            }
                        }
                    }
                    .padding(.horizontal)
                }
            }
            .onAppear {
                vm.fetchAllUsers()
            }

            // Custom back button
            CustomBackButton(colorScheme: colorScheme)
        }
        .navigationBarBackButtonHidden(true) // Hides the default back button
    }

    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search for Users to Follow...", text: $searchTextFollowing)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
}




func addUserToFollowing(_ following: Following) {
    // API call to either add the user as a friend or follow them
    // After successful action, update the status and refresh the list
}

func showUnfollowAlert(for following: Following) {
    // Show alert to confirm unfriend
    // If confirmed, make an API call to unfriend and update the UI
}

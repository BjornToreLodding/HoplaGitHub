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

// MARK: - User Model (All Users)
struct User: Identifiable, Decodable {
    var id: String
    var name: String?
    var alias: String?
    var profilePictureUrl: String?
    var relationStatus: PersonStatus?

    enum CodingKeys: String, CodingKey {
        case id               // matches "id"
        case name             // matches "name"
        case alias            // matches "alias"
        case profilePictureUrl = "pictureUrl"  // matches "pictureUrl"
        case relationStatus   // optional, may be missing
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
enum PersonStatus: String, Codable {
  case none     = "NONE"
  case pending  = "PENDING"
  case following = "FOLLOWING"
  case friends  = "FRIENDS"
  case block    = "BLOCK"
}


// MARK: - Header
struct FriendsHeaderView: View {
    var colorScheme: ColorScheme
    
    var body: some View {
        Text("My friends")
            .font(.custom("ArialNova", size: 20))
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}

// MARK: - Header
struct AddFriendsHeaderView: View {
    var colorScheme: ColorScheme
    
    var body: some View {
        Text("Add new friends")
            .font(.custom("ArialNova", size: 20))
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}

class FriendViewModel: ObservableObject {
    @Published var friends: [Friend] = []
    @Published var allUsers: [User] = []
    @Published var filteredFriends: [Friend] = []

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
                print("Fetched Friends:", friends) // Debugging line
                DispatchQueue.main.async {
                    self.friends = friends
                    self.filteredFriends = friends // Ensure filteredFriends is also updated
                }
            } catch {
                print("Error decoding friends:", error.localizedDescription)
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
            if let http = response as? HTTPURLResponse {
                print("🔔 fetchAllUsers statusCode:", http.statusCode)
            }
            guard let data = data else {
                print("No data returned")
                return
            }
            if let raw = String(data: data, encoding: .utf8) {
                print("🔍 fetchAllUsers raw JSON →", raw)
            }

            do {
                let decoder = JSONDecoder()
                // If your JSON uses snake_case or camelCase → uncomment:
                // decoder.keyDecodingStrategy = .convertFromSnakeCase

                let users = try decoder.decode([User].self, from: data)
                DispatchQueue.main.async {
                    self.allUsers = users
                }
                print("✅ Decoded users:", users)
            } catch {
                print("❌ Error decoding all users:", error)
            }
        }
        .resume()
    }


    func addFriend(userId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/userrelations/addfriend")!
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
                // Update list after adding
                self.fetchFriends()
                self.fetchAllUsers()
            }
        }.resume()
    }
    
    private func updateFilteredFriends() {
        if searchText.isEmpty {
            filteredFriends = friends
        } else {
            filteredFriends = friends.filter { $0.name.localizedCaseInsensitiveContains(searchText) }
        }
    }

    var searchText: String = "" {
        didSet {
            updateFilteredFriends()
        }
    }
}



// MARK: - Friends View
struct Friends: View {
    @StateObject private var vm = FriendViewModel()
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        NavigationStack {
            ZStack {
                // 1) Your main content
                VStack(spacing: 0) {
                    FriendsHeaderView(colorScheme: colorScheme)
                    searchBar
                        .frame(maxWidth: .infinity)
                        .background(
                            AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                                .color(for: colorScheme)
                        )
                    
                    // 2) Friends list fills the rest
                    ZStack {
                        // 1) Get a Color view from your AdaptiveColor
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                            .color(for: colorScheme)      // ← now this is a SwiftUI Color
                            .ignoresSafeArea()            // ← valid on Color (a View)

                        // 2) Your actual list on top of that background
                        FriendListView(vm: vm, colorScheme: colorScheme)
                    }
                    .onAppear {
                        vm.fetchFriends()
                    }
                }
                
                // 3) Custom back button (still works)
                CustomBackButton(colorScheme: colorScheme)
                
                // 4) Floating Add‑Friend button
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        NavigationLink(destination: AddFriendPage()) {
                            ZStack {
                                Circle()
                                    .fill(
                                        AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                                            .color(for: colorScheme)
                                    )
                                    .frame(width: 60, height: 60)
                                Image(systemName: "plus")
                                    .font(.system(size: 30, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                        .padding(.bottom, 30)
                        .padding(.trailing, 20)
                    }
                }
            }
            .navigationBarBackButtonHidden(true)
        }
    }


    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search friends...", text: $vm.searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10)
                    .fill(Color.gray.opacity(0.2)))
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
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
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
                .font(.custom("ArialNova", size: 16))
                .padding(.leading, 10)
                .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
            Spacer()
        }
        .padding()
        .frame(maxWidth: .infinity, minHeight: 80, alignment: .leading)
        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
        .cornerRadius(10)
        .shadow(radius: 2)
        .padding(.horizontal)
    }
}


// MARK: - Add Friend Page
struct AddFriendPage: View {
    @State private var searchText: String = ""
    @StateObject private var vm = FriendViewModel()
    @Environment(\.colorScheme) var colorScheme

    var filteredUsers: [User] {
        if searchText.isEmpty {
            return vm.allUsers
        } else {
            return vm.allUsers.filter {
                $0.name?
                    .localizedCaseInsensitiveContains(searchText)
                ?? false
            }
        }
    }


    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                AddFriendsHeaderView(colorScheme: colorScheme)
                searchBar
                    .frame(maxWidth: .infinity)
                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))

                // ScrollView to make the list scrollable
                ScrollView {
                    LazyVStack(spacing: 16) {
                        ForEach(filteredUsers) { user in
                            UserRowView(user: user, colorScheme: colorScheme)
                        }
                    }
                    .padding(.horizontal)
                }
                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
            }
            .onAppear {
                vm.fetchAllUsers()
            }

            CustomBackButton(colorScheme: colorScheme)  // Custom back button
        }
        .navigationBarBackButtonHidden(true)
    }


    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)
            TextField("Search for Users to Add...", text: $searchText)
                .textFieldStyle(PlainTextFieldStyle())
                .padding(8)
                .background(RoundedRectangle(cornerRadius: 10).fill(Color.gray.opacity(0.2)))
        }
        .padding(.horizontal)
    }
    
    struct UserRowView: View {
        let user: User
        let colorScheme: ColorScheme
        @StateObject private var vm = FriendViewModel()

        var body: some View {
            NavigationLink(destination: UserDetails(userId: user.id)) {
                HStack {
                    ProfileImageView(urlString: user.profilePictureUrl)

                    Text(user.name ?? "No name")
                        .font(.custom("ArialNova", size: 16))
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        .padding(.leading, 10)

                    Spacer()

                    if user.relationStatus == .friends {
                        Text("Friend")
                            .padding(8)
                            .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                            .foregroundColor(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                            .cornerRadius(8)
                    } else {
                        Button(action: {
                            vm.addFriend(userId: user.id)
                        }) {
                            Text(user.relationStatus == .pending ? "Pending" : "Add Friend")
                                .padding(8)
                                .background(user.relationStatus == .pending ? Color.orange : Color.green)
                                .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                .cornerRadius(8)
                        }
                    }
                }
                .padding()
                .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme)) //Correct
                .shadow(radius: 2)
            }
        }
    }

    struct ProfileImageView: View {
        let urlString: String?

        var body: some View {
            if let urlString = urlString, let url = URL(string: urlString) {
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
        }
    }

    
}


func addUserToFriends(_ friend: Friend) {
    // API call to either add the user as a friend or follow them
    // After successful action, update the status and refresh the list
}

func showUnfriendAlert(for friend: Friend) {
    // Show alert to confirm unfriend
    // If confirmed, make an API call to unfriend and update the UI
}


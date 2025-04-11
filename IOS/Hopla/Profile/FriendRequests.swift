//
//  FriendRequests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 11/04/2025.
//

import SwiftUI

struct FriendRequest: Identifiable, Decodable {
    var id: String
    var fromUserId: String
    var fromUserAlias: String
    var fromUserName: String
}

class FriendRequestViewModel: ObservableObject {
    @Published var friendRequests: [FriendRequest] = []

    func fetchFriendRequests() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        guard let url = URL(string: "https://hopla.onrender.com/userrelations/requests") else {
            print("Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error with the request: \(error.localizedDescription)")
                return
            }
            
            if let data = data {
                do {
                    let decodedRequests = try JSONDecoder().decode([FriendRequest].self, from: data)
                    DispatchQueue.main.async {
                        self.friendRequests = decodedRequests
                    }
                } catch {
                    print("Error decoding friend requests: \(error.localizedDescription)")
                }
            }
        }
        task.resume()
    }
    
    func handleAction(for request: FriendRequest, action: FriendAction) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url: String
        switch action {
        case .add:
            url = "https://hopla.onrender.com/userrelations/add/\(request.id)"
        case .decline:
            url = "https://hopla.onrender.com/userrelations/decline/\(request.id)"
        case .block:
            url = "https://hopla.onrender.com/userrelations/block/\(request.id)"
        }

        guard let requestUrl = URL(string: url) else {
            print("Invalid URL")
            return
        }

        var urlRequest = URLRequest(url: requestUrl)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
            if let error = error {
                print("Error with the action: \(error.localizedDescription)")
                return
            }

            DispatchQueue.main.async {
                // Remove the friend request from the list after performing the action
                self.friendRequests.removeAll { $0.id == request.id }
            }
        }
        task.resume()
    }
}

enum FriendAction {
    case add, decline, block
}

struct FriendRequests: View {
    @Environment(\.colorScheme) var colorScheme
    @StateObject private var viewModel = FriendRequestViewModel()

    var body: some View {
        NavigationView {
            List(viewModel.friendRequests) { request in
                HStack {
                    VStack(alignment: .leading) {
                        Text(request.fromUserName)
                            .font(.headline)
                        Text("@\(request.fromUserAlias)")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }

                    Spacer()

                    HStack(spacing: 8) {
                        Button(action: {
                            viewModel.handleAction(for: request, action: .add)
                        }) {
                            Image(systemName: "plus.app.fill")
                                .font(.system(size: 24))
                                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        }

                        Button(action: {
                            viewModel.handleAction(for: request, action: .decline)
                        }) {
                            Image(systemName: "x.square.fill")
                                .font(.system(size: 24))
                                
                                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        }

                        Button(action: {
                            viewModel.handleAction(for: request, action: .block)
                        }) {
                            Image(systemName: "hand.raised.fill")
                                .font(.system(size: 24))
                                
                                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        }
                    }
                    .padding(.horizontal)
                }
                .padding()
            }
            .navigationTitle("Friend Requests")
            .onAppear {
                viewModel.fetchFriendRequests()
            }
        }
    }
}


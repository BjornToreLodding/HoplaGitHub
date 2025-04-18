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
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) var colorScheme
    @StateObject private var viewModel = FriendRequestViewModel()
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Custom header
                ZStack {
                    AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                        .color(for: colorScheme)
                        .frame(maxWidth: .infinity)
                    Text("Friend Requests")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    HStack {
                        Button(action: { dismiss() }) {
                            Image(systemName: "arrow.left")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 24, height: 24)
                                .foregroundStyle(
                                    AdaptiveColor(light: .lightModeTextOnGreen,
                                                  dark: .darkModeTextOnGreen)
                                    .color(for: colorScheme)
                                )
                        }
                        Spacer()
                    }
                    .padding(.horizontal, 16)
                }
                .frame(height: 40)
                
                // List of requests
                ScrollView {
                    VStack(spacing: 12) {
                        ForEach(viewModel.friendRequests) { request in
                            HStack {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(request.fromUserName)
                                        .font(.headline)
                                    Text("@\(request.fromUserAlias)")
                                        .font(.subheadline)
                                        .foregroundColor(.gray)
                                }
                                Spacer()
                                HStack(spacing: 8) {
                                    Button { viewModel.handleAction(for: request, action: .add) } label: {
                                        Image(systemName: "plus.app.fill")
                                            .font(.system(size: 24))
                                            .foregroundColor(
                                                AdaptiveColor(light: .textLightBackground,
                                                              dark: .textDarkBackground)
                                                .color(for: colorScheme)
                                            )
                                    }
                                    Button { viewModel.handleAction(for: request, action: .decline) } label: {
                                        Image(systemName: "x.square.fill")
                                            .font(.system(size: 24))
                                            .foregroundColor(
                                                AdaptiveColor(light: .textLightBackground,
                                                              dark: .textDarkBackground)
                                                .color(for: colorScheme)
                                            )
                                    }
                                    Button { viewModel.handleAction(for: request, action: .block) } label: {
                                        Image(systemName: "hand.raised.fill")
                                            .font(.system(size: 24))
                                            .foregroundColor(
                                                AdaptiveColor(light: .textLightBackground,
                                                              dark: .textDarkBackground)
                                                .color(for: colorScheme)
                                            )
                                    }
                                }
                            }
                            .frame(width: 370)
                            .padding()
                            .background(
                                AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground)
                                    .color(for: colorScheme)
                            )
                            .cornerRadius(10)
                            .shadow(radius: 1)
                        }
                    }
                    .padding(.vertical, 12)
                }
                .background(
                    AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                        .color(for: colorScheme)
                )
                .onAppear { viewModel.fetchFriendRequests() }
            }
            .navigationBarBackButtonHidden(true)
        }
    }
}


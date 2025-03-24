//
//  FriendsDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 23/03/2025.
//

import SwiftUI
import Combine


struct FriendsDetails: View {
    var friend: Friend
    
    var body: some View {
        VStack {
            if let urlString = friend.profilePictureUrl, let url = URL(string: urlString) {
                AsyncImage(url: url) { image in
                    image.resizable()
                        .scaledToFill()
                        .frame(width: 100, height: 100)
                        .clipShape(Circle())
                } placeholder: {
                    Circle()
                        .fill(Color.gray.opacity(0.5))
                        .frame(width: 100, height: 100)
                }
            }
            
            Text(friend.name)
                .font(.title)
                .fontWeight(.bold)
            
            Text(friend.alias)
                .font(.headline)
                .foregroundColor(.gray)
            
            Text("Status: Friend") // Hardcoded since all are friends
            
            Spacer()
        }
        .padding()
        .navigationTitle(friend.name)
    }
}





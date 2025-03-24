//
//  FollowingDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 23/03/2025.
//

import SwiftUI
import Combine

struct FollowingDetails: View {
    var user: Following
    
    var body: some View {
        VStack {
            if let urlString = user.profilePictureUrl, let url = URL(string: urlString) {
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
            
            Text(user.name)
                .font(.title)
                .fontWeight(.bold)
            
            Text(user.alias)
                .font(.headline)
                .foregroundColor(.gray)
            
            Text("Status: Following") // Hardcoded since all are being followed
            
            Spacer()
        }
        .padding()
        .navigationTitle(user.name)
    }
}


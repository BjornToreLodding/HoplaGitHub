//
//  HorseDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 05/03/2025.
//

import SwiftUI

struct HorseDetails: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    
    let horse: Horse
    
    var body: some View {
        ZStack {      
            VStack(spacing: 0) {
                Text(horse.name)
                    .font(.title)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, alignment: .center) // Aligns text to the right
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)
                NavigationView {
                    VStack {
                        if let urlString = horse.horsePictureUrl, let url = URL(string: urlString) {
                            AsyncImage(url: url) { image in
                                image.resizable()
                                    .scaledToFill()
                                    .frame(width: 200, height: 200)
                                    .clipShape(Circle())
                                    .overlay(Circle().stroke(Color.white, lineWidth: 2))
                                    .shadow(radius: 5)
                                    .padding(.top, 20)
                            } placeholder: {
                                ProgressView()
                                    .frame(width: 200, height: 200)
                            }
                        } else {
                            Circle()
                                .fill(Color.gray)
                                .frame(width: 200, height: 200)
                                .padding(.top, 20)
                        }
                        
                        Spacer()
                    }
                    .edgesIgnoringSafeArea(.top) // Ensures it can be placed above navigation elements
                }
                .navigationBarBackButtonHidden(true) // Hides the default back button
            }
            
            // MARK: - Custom Back Button
            VStack {
                HStack {
                    Button(action: {
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "arrow.left")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 30, height: 30)
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                    .position(x: 25, y: 20) // Adjust for exact placement
                    
                    Spacer()
                }
                Spacer()
            }
        }
    }
}

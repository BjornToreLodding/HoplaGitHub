//
//  Profile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import PhotosUI // To select photos
import UIKit

struct Profile: View {
    @Environment(\.colorScheme) var colorScheme
    
    // Holds the information about the photo selected from the photo picker
    @State private var selectedItem: PhotosPickerItem? = nil
    
    // Stores the actual UIImage to display in the profile view
    @State private var profileImage: UIImage? = nil
    
    @State private var username: String = "" // username
    
    @State private var email: String = "" // e-mail
    
    var body: some View {
        ZStack {
            // Ensure the whole background is green
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all) // Covers top & bottom
            VStack {
                // Top of screen is green
                Rectangle()
                    .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .frame(height: 110)
                    .edgesIgnoringSafeArea(.top)
                    .padding(.top, -435)
            }

                VStack {
                    ZStack {
                        // Profile image section
                        Circle()
                            .frame(width: 200, height: 200)
                            .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            .padding(.top, 50)

                        if let profileImage {
                            Image(uiImage: profileImage)
                                .resizable()
                                .scaledToFill()
                                .frame(width: 180, height: 180)
                                .clipShape(Circle())
                                .padding(.top, 50)
                        } else {
                            Image("Profile")
                                .resizable()
                                .scaledToFill()
                                .frame(width: 180, height: 180)
                                .clipShape(Circle())
                                .padding(.top, 50)
                        }
                        /*
                        Circle()
                            .frame(width: 20, height: 10)
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            .padding(.leading, 270)
                         */
                    }
                    
                    // Profile Picture Picker
                    PhotosPicker(selection: $selectedItem, matching: .images, photoLibrary: .shared()) {
                        Text("Change profile picture")
                            .underline()
                            .padding(.top, 10)
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                    .onChange(of: selectedItem) { newItem in
                        if let newItem {
                            Task {
                                if let data = try? await newItem.loadTransferable(type: Data.self),
                                   let uiImage = UIImage(data: data) {
                                    DispatchQueue.main.async {
                                        profileImage = uiImage
                                    }
                                }
                            }
                        }
                    }

                    // Buttons & User Info
                    HStack(spacing: 10) {
                        NavigationLink(destination: MyHikes()) {
                            Text("My hikes")
                                .frame(width: 120, height: 50)
                                .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme)) // Box
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme)) // Text
                        }

                        NavigationLink(destination: Friends()) {
                            Text("Friends")
                                .frame(width: 120, height: 50)
                                .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme)) // Box
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme)) // Text
                        }

                        NavigationLink(destination: Following()) {
                            Text("Following")
                                .frame(width: 120, height: 50)
                                .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme)) // Box
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme)) // Text
                        }
                    }
                    .padding(.top, 20)
                    
                    // User Details Section
                    ZStack {
                        Rectangle()
                            .frame(width: 380, height: 240)
                            .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            .padding(.top, 20)

                        VStack {
                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(AdaptiveColor(light: .darkBeige, dark: .darkBrown).color(for: colorScheme))
                                .padding(.top, 10)

                            Text("Username")
                                .frame(width: 360, height: 30)
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))

                            TextField("Bob", text: $username)
                                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                .frame(width: 360)
                                .multilineTextAlignment(.center)

                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(AdaptiveColor(light: .darkBeige, dark: .darkBrown).color(for: colorScheme))

                            Text("E-mail")
                                .frame(width: 360, height: 30)
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))

                            TextField("bob@mail.no", text: $email)
                                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                .frame(width: 360)
                                .multilineTextAlignment(.center)

                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(AdaptiveColor(light: .darkBeige, dark: .darkBrown).color(for: colorScheme))

                            ChangePassword()
                        }
                    }

                    // Settings Button
                    NavigationLink(destination: Settings()) {
                        HStack {
                            Image(systemName: "gearshape")
                                .font(.system(size: 20))
                            Text("Settings")
                        }
                        .padding()
                        .frame(width: 200, height: 50)
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                    }

                    Spacer()
                }
            
        }
        .navigationTitle("Profile")
    }

}


#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

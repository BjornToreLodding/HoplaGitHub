//
//  Profile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import PhotosUI // To select photos

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
                            .foregroundColor(.white)
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
                    }
                    
                    // Profile Picture Picker
                    PhotosPicker(selection: $selectedItem, matching: .images, photoLibrary: .shared()) {
                        Text("Change profile picture")
                            .underline()
                            .padding(.top, 10)
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
                                .background(Color.white)
                                .foregroundColor(Color.black)
                        }

                        NavigationLink(destination: Friends()) {
                            Text("Friends")
                                .frame(width: 120, height: 50)
                                .background(Color.white)
                                .foregroundColor(Color.black)
                        }

                        NavigationLink(destination: Following()) {
                            Text("Following")
                                .frame(width: 120, height: 50)
                                .background(Color.white)
                                .foregroundColor(Color.black)
                        }
                    }
                    .padding(.top, 20)
                    
                    // User Details Section
                    ZStack {
                        Rectangle()
                            .frame(width: 380, height: 240)
                            .foregroundColor(.white)
                            .padding(.top, 20)

                        VStack {
                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(.darkBeige)
                                .padding(.top, 10)

                            Text("Username")
                                .frame(width: 360, height: 30)
                                .background(Color.white)

                            TextField("Bob", text: $username)
                                .background(Color.mainLightBackground)
                                .frame(width: 360)
                                .multilineTextAlignment(.center)

                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(.darkBeige)

                            Text("E-mail")
                                .frame(width: 360, height: 30)
                                .background(Color.white)

                            TextField("bob@mail.no", text: $email)
                                .background(Color.mainLightBackground)
                                .frame(width: 360)
                                .multilineTextAlignment(.center)

                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(.darkBeige)

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
                        .background(Color.white)
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

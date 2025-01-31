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
    
    var body: some View {
        ZStack {
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea()
            
            ScrollView {
                VStack {
                    ZStack {
                        Circle()
                            .frame(width: 200, height: 200)
                            .foregroundColor(.white)
                            .padding(.top, 10)
                        
                        if let profileImage { // The user's selected profile image
                            Image(uiImage: profileImage)
                                .resizable()
                                .scaledToFill()
                                .frame(width: 180, height: 180)
                                .clipShape(Circle())
                                .padding(.top, 10)
                            
                        } else { // The standard image for profile
                            Image("Profile")
                                .resizable()
                                .scaledToFill()
                                .frame(width: 180, height: 180)
                                .clipShape(Circle())
                                .padding(.top, 10)
                        }
                    }
                    
                    // Image picker for selecting a profile picture
                    PhotosPicker(selection: $selectedItem, matching: .images, photoLibrary: .shared()) {
                        Text("Change profile picture")
                            .underline()
                            .padding(.top, 10)
                    }
                    
                    // using older version because other method doesnt work
                    .onChange(of: selectedItem) { newItem in
                        // Handle image selection
                        if let newItem {
                            Task {
                                do {
                                    // Load the selected image asynchronously
                                    if let data = try? await newItem.loadTransferable(type: Data.self),
                                       let uiImage = UIImage(data: data) {
                                        // Update the profileImage on the main thread
                                        DispatchQueue.main.async {
                                            profileImage = uiImage
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // The tree buttons
                    NavigationView {
                            HStack(spacing: 10) { // Add spacing between buttons
                                NavigationLink(destination: MyHikes()) {
                                    Text("My hikes")
                                        .frame(width: 120, height: 50)
                                        .background(Color.white)
                                        .foregroundColor(Color.black) // Change from `foregroundStyle` to `foregroundColor`
                                        .shadow(radius: 5)
                                }
                                
                                NavigationLink(destination: Friends()) {
                                    Text("Friends")
                                        .frame(width: 120, height: 50)
                                        .background(Color.white)
                                        .foregroundColor(Color.black) // Change from `foregroundStyle` to `foregroundColor`
                                        .shadow(radius: 5)
                                }
                                
                                NavigationLink(destination: Following()) {
                                    Text("Following")
                                        .frame(width: 120, height: 50)
                                        .background(Color.white)
                                        .foregroundColor(Color.black) // Change from `foregroundStyle` to `foregroundColor`
                                        .shadow(radius: 5)
                                }
                            }
                    }
   
                    ZStack {
                        // White rectangle container
                        Rectangle()
                            .frame(width: 380, height: 250)
                            .foregroundColor(.white)
                            .padding(.top, 20)
                        
                        VStack {
                            Text("Username")
                                .frame(width: 360, height: 40)
                                .background(Color.darkBeige)
                                .padding(.top, 20)
                            
                            Text("Bob")
                                .frame(width: 360, height: 40)
                                .background(Color.lightBeige)
                            
                            Text("E-mail")
                                .frame(width: 360, height: 40)
                                .background(Color.darkBeige)
                            
                            Text("bob@mail.no")
                                .frame(width: 360, height: 40)
                                .background(Color.lightBeige)
                            
                            Text("Change password")
                                .frame(width: 360, height: 40)
                                .background(Color.darkBeige)
                        }
                    }
                    
                    // Button for settings
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

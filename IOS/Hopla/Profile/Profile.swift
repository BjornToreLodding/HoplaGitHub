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
    
    @EnvironmentObject var vm: ViewModel
    
    // Holds the information about the photo selected from the photo picker
    @State private var selectedItem: PhotosPickerItem? = nil
    
    // Stores the actual UIImage to display in the profile view
    @State private var profileImage: UIImage? = nil
    
    @State private var isShowingCamera = false // Camera
    
    @State private var username: String = "" // Username
    
    @State private var email: String = "" // E-mail
    
    @State private var navigationPath = NavigationPath()
    
    var body: some View {
        ZStack {
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)
            
            VStack {
                Rectangle()
                    .fill(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .frame(height: 110)
                    .edgesIgnoringSafeArea(.top)
                    .padding(.top, -420)
            }
            
            VStack {
                // Profile Image Section
                ZStack {
                    Circle()
                        .frame(width: 200, height: 200)
                        .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        .padding(.top, 70)
                    
                    if let profileImage = vm.image {
                        Image(uiImage: profileImage)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 180, height: 180)
                            .clipShape(Circle())
                            .padding(.top, 70)
                    } else {
                        Image("Profile")
                            .resizable()
                            .scaledToFill()
                            .frame(width: 180, height: 180)
                            .clipShape(Circle())
                            .padding(.top, 70)
                    }
                }
                
                HStack {
                    Button {
                        vm.source = .camera
                        vm.showPhotoPicker()
                    } label: {
                        Text("Camera")
                            .underline()
                    }
                    Button {
                        vm.source = .library
                        vm.showPhotoPicker()
                    } label: {
                        Text("Photos")
                            .underline()
                    }
                }
                .padding(.bottom, 10)
                
                
                // Buttons for Navigation
                HStack(spacing: 10) {
                    NavigationLink(destination: MyHikes()) {
                        Text("My hikes")
                            .frame(width: 120, height: 50)
                            .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                    
                    NavigationLink(destination: Friends()) {
                        Text("Friends")
                            .frame(width: 120, height: 50)
                            .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                    
                    NavigationLink(destination: Following()) {
                        Text("Following")
                            .frame(width: 120, height: 50)
                            .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                }
                
                
                // Profile Info Section
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
                        
                        Text("Email")
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
                
                Spacer()
            }
            
            // Settings button in the top-right corner
            HStack {
                Spacer()
                NavigationLink(destination: Settings(navigationPath: $navigationPath)) {
                    Image(systemName: "gearshape")
                        .font(.system(size: 24))
                        .padding()
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                }
                .padding(.trailing, 20)
                .padding(.top, 60)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topTrailing)
        }
        .sheet(isPresented: $vm.showPicker) {
            ImagePicker(sourceType: vm.source == .library ? .photoLibrary : .camera, selectedImage: $vm.image)
        }
        .navigationTitle("Profile")
    }

    
}


#Preview("English") {
    ContentView()
        .environmentObject(ViewModel())
}

#Preview("Norsk") {
    ContentView()
        .environmentObject(ViewModel())
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

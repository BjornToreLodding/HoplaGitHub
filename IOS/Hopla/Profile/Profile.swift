//
//  Profile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import PhotosUI // To select photos
import UIKit


class ProfileViewModel: ObservableObject {
    @Published var selectedImage: UIImage?
    
    func uploadProfileImage(image: UIImage, entityId: String, table: String, token: String) async {
        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            print("Kunne ikke konvertere bildet til Data")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/upload")!
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        var body = Data()

        // Legg til bilde
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"image\"; filename=\"profile.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        body.append(imageData)
        body.append("\r\n".data(using: .utf8)!)

        // Legg til `table`
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"table\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(table)\r\n".data(using: .utf8)!)

        // Legg til `entityId`
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"entityId\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(entityId)\r\n".data(using: .utf8)!)

        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        request.httpBody = body

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 {
                let responseString = String(data: data, encoding: .utf8)
                print("Upload successful: \(responseString ?? "No response")")
            } else {
                print("Upload failed: \(response)")
            }
        } catch {
            print("Error uploading image: \(error.localizedDescription)")
        }
    }
}


struct Profile: View {
    @Environment(\.colorScheme) var colorScheme
    @EnvironmentObject var vm: ViewModel
    
    @ObservedObject var loginViewModel: LoginViewModel
    @ObservedObject var viewModel: LoginViewModel

    
    // Holds the information about the photo selected from the photo picker
    @State private var selectedItem: PhotosPickerItem? = nil
    // Stores the actual UIImage to display in the profile view
    @State private var profileImage: UIImage? = nil
    @State private var isShowingCamera = false // Camera
    @State private var username: String = "" // Username
    @State private var email: String = "" // Email
    //@State private var navigationPath = NavigationPath()
    @Binding var navigationPath: NavigationPath

    
    var body: some View {
        ZStack {
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)
            
            VStack {
                if let user = loginViewModel.userProfile { // If data on user is available
                    // Profile Image Section
                    ZStack {
                        Circle()
                            .frame(width: 200, height: 200)
                            .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                            .padding(.top, 10)
                        
                        if let pictureUrl = user.pictureUrl, let url = URL(string: pictureUrl) {
                            AsyncImage(url: url) { image in
                                image.resizable()
                            } placeholder: {
                                ProgressView()
                            }
                            //.resizable()
                            .scaledToFill()
                            .frame(width: 180, height: 180)
                            .clipShape(Circle())
                            .padding(.top, 10)
                        } else {
                            Image("Profile")
                                .resizable()
                                .scaledToFill()
                                .frame(width: 180, height: 180)
                                .clipShape(Circle())
                                .padding(.top, 10)
                        }
                    }
                    
                    HStack {
                        Button {
                            vm.source = .camera
                            vm.showPhotoPicker()
                        } label: {
                            Text("Camera")
                                .font(.custom("ArialNova", size: 16))
                                .underline()
                        }
                        Button {
                            vm.source = .library
                            vm.showPhotoPicker()
                        } label: {
                            Text("Photos")
                                .font(.custom("ArialNova", size: 16))
                                .underline()
                        }
                    }
                    .padding(.bottom, 10)
                    
                    
                    // Buttons for Navigation
                    HStack(spacing: 10) {
                        NavigationLink(destination: MyHikes()) {
                            Text("My hikes")
                                .font(.custom("ArialNova", size: 20))
                                .frame(width: 180, height: 50)
                                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        }
                        
                        NavigationLink(destination: MyHorses()) {
                            Text("My horses")
                                .font(.custom("ArialNova", size: 20))
                                .frame(width: 180, height: 50)
                                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        }
                    }
                    HStack(spacing: 10) {
                        NavigationLink(destination: Friends()) {
                            Text("Friends")
                                .font(.custom("ArialNova", size: 20))
                                .frame(width: 180, height: 50)
                                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        }
                        
                        NavigationLink(destination: FollowingView()) {
                            Text("Following")
                                .font(.custom("ArialNova", size: 20))
                                .frame(width: 180, height: 50)
                                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                .foregroundColor(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
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
                                .font(.custom("ArialNova", size: 16))
                                .frame(width: 360, height: 30)
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            
                            Text(user.alias)
                                .font(.custom("ArialNova-Light", size: 16))
                                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                .frame(width: 360)
                                .multilineTextAlignment(.center)
                            
                            Rectangle()
                                .frame(width: 360, height: 3)
                                .foregroundColor(AdaptiveColor(light: .darkBeige, dark: .darkBrown).color(for: colorScheme))
                            
                            Text("Email")
                                .font(.custom("ArialNova", size: 16))
                                .frame(width: 360, height: 30)
                                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            
                            Text(user.email)
                                .font(.custom("ArialNova-Light", size: 16))
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
                    
                } else {
                    Text("Loading profile...")
                        .onAppear {
                            loginViewModel.fetchUserProfile()
                        }
                }
                
            }
            
            // Settings button in the top-right corner
            HStack {
                Spacer()
                NavigationLink(destination: Settings(navigationPath: $navigationPath, viewModel: viewModel)) {
                    Image(systemName: "gearshape")
                        .font(.system(size: 24))
                        .padding()
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                }
                .padding(.trailing, 20)
                .padding(.top, 20)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topTrailing)
        }
        .sheet(isPresented: $vm.showPicker) {
            ImagePicker(sourceType: vm.source == .library ? .photoLibrary : .camera, selectedImage: $vm.image)
        }
    }
}

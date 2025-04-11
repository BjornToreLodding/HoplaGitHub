//
//  Profile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import PhotosUI // To select photos
import UIKit
import Foundation



class ProfileViewModel: ObservableObject {
    @Published var userProfile: UserProfile?
    @Published var selectedImage: UIImage?
    
    
    func uploadProfileImage(image: UIImage, entityId: String, table: String, token: String) async {
        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            print("Could not convert image to Data")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/upload")!
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        let lineBreak = "\r\n"
        
        body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"image\"; filename=\"profile.jpg\"\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\(lineBreak)\(lineBreak)".data(using: .utf8)!)
        body.append(imageData)
        body.append("\(lineBreak)".data(using: .utf8)!)
        
        body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"table\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
        body.append("\(table)\(lineBreak)".data(using: .utf8)!)
        
        body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"entityId\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
        body.append("\(entityId)\(lineBreak)".data(using: .utf8)!)
        
        body.append("--\(boundary)--\(lineBreak)".data(using: .utf8)!)
        request.httpBody = body
        
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            if let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) {
                let uploadResponse = try JSONDecoder().decode(UploadResponse.self, from: data)
                print("Uploaded file saved at: \(uploadResponse.filePath)")
                DispatchQueue.main.async {
                    self.selectedImage = image // Update UI
                }
            } else {
                print("Upload failed with response: \(String(data: data, encoding: .utf8) ?? "No response")")
            }
        } catch {
            print("Error uploading image: \(error.localizedDescription)")
        }
    }
    
    func fetchUserProfile() async {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/users/profile")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse else {
                print("Invalid response")
                return
            }
            
            if httpResponse.statusCode == 200 {
                let user = try JSONDecoder().decode(UserProfile.self, from: data)
                
                DispatchQueue.main.async {
                    var finalDescription = user.description ?? TokenManager.shared.getUserDescription()
                    
                    if let serverDescription = user.description, !serverDescription.isEmpty {
                        TokenManager.shared.saveUserDescription(serverDescription)
                    }
                    
                    self.userProfile = UserProfile(
                        alias: user.alias,
                        name: user.name,
                        email: user.email,
                        pictureUrl: user.pictureUrl,
                        telephone: user.telephone,
                        description: finalDescription,
                        dob: user.dob
                    )
                    
                    print("Final User Profile:", self.userProfile ?? "No data")
                }
            } else {
                print("Failed to retrieve user profile. Status Code:", httpResponse.statusCode)
            }
        } catch {
            print("Error fetching user profile:", error.localizedDescription)
        }
    }
    
    
    
    
    // Function to update user info
    func updateUserInfo(token: String, userId: String) async {
        let url = URL(string: "https://hopla.onrender.com/users/update")!
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        guard let userProfile = userProfile else { return } // Ensure userProfile exists
        
        let alias = userProfile.alias
        let name = userProfile.name
        let telephone = userProfile.telephone ?? ""
        let description = userProfile.description ?? ""
        let year = userProfile.dob?.year ?? 0
        let month = userProfile.dob?.month ?? 0
        let day = userProfile.dob?.day ?? 0
        
        let dobDictionary: [String: Any] = [
            "Year": year,
            "Month": month,
            "Day": day
        ]
        
        let body: [String: Any] = [
            "Alias": alias,
            "Name": name,
            "Telephone": telephone,
            "Description": description,
            "DOB": dobDictionary
        ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: body, options: [])
            request.httpBody = jsonData
            
            let (data, response) = try await URLSession.shared.data(for: request)
            
            if let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) {
                print("User info updated successfully.")
                
                // Decode response data
                let updatedUser: UserProfile = try JSONDecoder().decode(UserProfile.self, from: data)
                
                DispatchQueue.main.async {
                    self.userProfile = updatedUser // Ensure ProfileViewModel has this property
                }
            } else {
                print("Update failed with response: \(String(data: data, encoding: .utf8) ?? "No response")")
            }
        } catch {
            print("Error updating user info: \(error.localizedDescription)")
        }
    }
    
}

struct UploadResponse: Decodable {
    let filePath: String
}


struct Profile: View {
    @Environment(\.colorScheme) var colorScheme
    @StateObject var vm: ViewModel
    
    @ObservedObject var loginViewModel: LoginViewModel
    @ObservedObject var profileViewModel: ProfileViewModel
    
    // Holds the information about the photo selected from the photo picker
    @State private var selectedItem: PhotosPickerItem? = nil
    // Stores the actual UIImage to display in the profile view
    @State private var profileImage: UIImage? = nil
    @State private var isShowingCamera = false // Camera
    @State private var username: String = "" // Username
    @State private var email: String = "" // Email
    //@State private var navigationPath = NavigationPath()
    @Binding var navigationPath: NavigationPath
    // State variables for the DatePicker selection
    @State private var selectedYear: Int = 2000
    @State private var selectedMonth: Int = 1
    @State private var selectedDay: Int = 1
    
    enum SourceType {
        case camera
        case library
    }
    
    // The initializer should ensure that all state properties are initialized
    init(profileViewModel: ProfileViewModel, loginViewModel: LoginViewModel, navigationPath: Binding<NavigationPath>) {
        self.profileViewModel = profileViewModel
        self.loginViewModel = loginViewModel
        self._vm = StateObject(wrappedValue: ViewModel(profileViewModel: profileViewModel)) // Initialize ViewModel with profileViewModel
        self._navigationPath = navigationPath // Bind navigationPath
    }
    
    
    var body: some View {
        ZStack {
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)
            ScrollView {
                // Settings button in the top-right corner
                HStack {
                    NavigationLink(destination: FriendRequests()) {
                        Image(systemName: "bell.fill")
                            .font(.system(size: 24))
                            .padding()
                            .padding(.leading, 10)
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    }
                    
                    Spacer()
                    
                    NavigationLink(destination: Settings(navigationPath: $navigationPath, viewModel: profileViewModel)) {
                        Image(systemName: "gearshape.fill")
                            .font(.system(size: 24))
                            .padding()
                            .padding(.trailing, 10)
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                
                VStack {
                    if let user = loginViewModel.userProfile { // If data on user is available
                        // Profile Image Section
                        ZStack {
                            Circle()
                                .frame(width: 200, height: 200)
                                .foregroundColor(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                            
                            
                            if let selectedImage = profileViewModel.selectedImage {
                                Image(uiImage: selectedImage)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 180, height: 180)
                                    .clipShape(Circle())
                                
                            } else if let pictureUrl = loginViewModel.userProfile?.pictureUrl, let url = URL(string: pictureUrl) {
                                AsyncImage(url: url) { image in
                                    image.resizable()
                                } placeholder: {
                                    ProgressView()
                                }
                                .scaledToFill()
                                .frame(width: 180, height: 180)
                                .clipShape(Circle())
                                
                            } else {
                                Image(systemName: "person.crop.circle.fill")
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 180, height: 180)
                                    .clipShape(Circle())
                                
                            }
                        }
                        
                        HStack {
                            Button("Camera") {
                                vm.source = .camera
                                vm.showPhotoPicker()
                            }
                            Button("Photos") {
                                vm.source = .library
                                vm.showPhotoPicker()
                            }
                        }
                        .sheet(isPresented: $vm.showPicker) {
                            ImagePicker(sourceType: vm.source == .library ? .photoLibrary : .camera, selectedImage: $vm.image, showImagePicker: $isShowingCamera)
                            // Ensure the profileImage is updated when vm.image changes
                                .onChange(of: vm.image) { newImage in
                                    profileImage = newImage
                                }
                        }
                        
                        Button("Upload") {
                            if let image = vm.image, let token = TokenManager.shared.getToken(), let userId = TokenManager.shared.getUserId() {
                                Task {
                                    await profileViewModel.uploadProfileImage(image: image, entityId: userId, table: "Users", token: token)
                                }
                            } else {
                                print("Image, token, or user ID is missing.")
                            }
                        }
                        .underline()
                        // Buttons for Navigation
                        HStack(spacing: 10) {
                            NavigationLink(destination: MyHikes()) {
                                Text("My hikes")
                                    .font(.custom("ArialNova", size: 20))
                                    .frame(width: 180, height: 50)
                                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                                    .foregroundColor(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                
                            }
                            
                            NavigationLink(destination: MyHorses()) {
                                Text("My horses")
                                    .font(.custom("ArialNova", size: 20))
                                    .frame(width: 180, height: 50)
                                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                                    .foregroundColor(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                
                            }
                        }
                        HStack(spacing: 10) {
                            NavigationLink(destination: Friends()) {
                                Text("Friends")
                                    .font(.custom("ArialNova", size: 20))
                                    .frame(width: 180, height: 50)
                                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                                    .foregroundColor(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                
                            }
                            
                            NavigationLink(destination: FollowingView()) {
                                Text("Following")
                                    .font(.custom("ArialNova", size: 20))
                                    .frame(width: 180, height: 50)
                                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                                    .foregroundColor(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                                
                            }
                        }
                        
                        
                        // Profile Info Section
                        ZStack {
                            Rectangle()
                                .frame(width: 380, height: 600)
                                .foregroundColor(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                                .padding(.top, 20)
                            
                            VStack {
                                Rectangle()
                                    .frame(width: 360, height: 3)
                                    .foregroundColor(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                    .padding(.top, 10)
                                
                                Text("Username")
                                    .font(.custom("ArialNova", size: 16))
                                    .frame(width: 360, height: 30)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                
                                Text(profileViewModel.userProfile?.alias ?? "Unknown Alias")
                                    .font(.custom("ArialNova-Light", size: 16))
                                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                    .frame(width: 360)
                                    .multilineTextAlignment(.center)
                                    .onAppear {
                                        print("UserProfile: \(String(describing: profileViewModel.userProfile))")
                                    }
                                
                                
                                Rectangle()
                                    .frame(width: 360, height: 3)
                                    .foregroundColor(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                
                                Text("Email")
                                    .font(.custom("ArialNova", size: 16))
                                    .frame(width: 360, height: 30)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                
                                Text(profileViewModel.userProfile?.email ?? "Unknown Email")
                                    .font(.custom("ArialNova-Light", size: 16))
                                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                    .frame(width: 360)
                                    .multilineTextAlignment(.center)
                                
                                Rectangle()
                                    .frame(width: 360, height: 3)
                                    .foregroundColor(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                
                                Text("Name")
                                    .font(.custom("ArialNova", size: 16))
                                    .frame(width: 360, height: 30)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                
                                Text(profileViewModel.userProfile?.name ?? "Unknown Name")
                                    .font(.custom("ArialNova-Light", size: 16))
                                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                    .frame(width: 360)
                                    .multilineTextAlignment(.center)
                                
                                Rectangle()
                                    .frame(width: 360, height: 3)
                                    .foregroundColor(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                
                                Text("Telephone")
                                    .font(.custom("ArialNova", size: 16))
                                    .frame(width: 360, height: 30)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                
                                Text(profileViewModel.userProfile?.telephone ?? "Not provided")
                                    .font(.custom("ArialNova-Light", size: 16))
                                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                    .frame(width: 360)
                                    .multilineTextAlignment(.center)
                                
                                Rectangle()
                                    .frame(width: 360, height: 3)
                                    .foregroundColor(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                
                                Text("Description")
                                    .font(.custom("ArialNova", size: 16))
                                    .frame(width: 360, height: 30)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                
                                Text(profileViewModel.userProfile?.description ?? "No description provided")
                                    .font(.custom("ArialNova-Light", size: 16))
                                    .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                                    .frame(width: 360)
                                    .multilineTextAlignment(.center)
                                    .onAppear {
                                        print("Fetched description:", profileViewModel.userProfile?.description ?? "No description found")
                                    }
                                
                                
                                Rectangle()
                                    .frame(width: 360, height: 3)
                                    .foregroundColor(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                
                                Text("Date of Birth")
                                    .font(.custom("ArialNova", size: 16))
                                    .frame(width: 360, height: 30)
                                    .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                
                                
                                
                                // DatePicker for updating DOB
                                VStack {
                                    Text("Select New Date of Birth")
                                        .font(.custom("ArialNova", size: 16))
                                        .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                                    
                                    HStack {
                                        // Year Picker
                                        SwiftUI.Picker("Year", selection: $selectedYear) {
                                            ForEach(1900..<2024, id: \.self) { year in
                                                Text("\(year)").tag(year)
                                            }
                                        }
                                        .pickerStyle(MenuPickerStyle())
                                        .frame(width: 100)
                                        
                                        // Month Picker
                                        SwiftUI.Picker("Month", selection: $selectedMonth) {
                                            ForEach(1..<13, id: \.self) { month in
                                                Text("\(month)").tag(month)
                                            }
                                        }
                                        .pickerStyle(MenuPickerStyle())
                                        .frame(width: 70)
                                        
                                        // Day Picker
                                        SwiftUI.Picker("Day", selection: $selectedDay) {
                                            ForEach(1..<32, id: \.self) { day in
                                                Text("\(day)").tag(day)
                                            }
                                        }
                                        .pickerStyle(MenuPickerStyle())
                                        .frame(width: 60)
                                    }
                                    
                                    // Button to update DOB
                                    Button(action: {
                                        guard let userProfile = profileViewModel.userProfile else { return }
                                        
                                        let updatedProfile = UserProfile(
                                            alias: userProfile.alias,
                                            name: userProfile.name,
                                            email: userProfile.email,
                                            pictureUrl: userProfile.pictureUrl,
                                            telephone: userProfile.telephone,
                                            description: userProfile.description,
                                            dob: DOB(year: selectedYear, month: selectedMonth, day: selectedDay)
                                        )
                                        
                                        profileViewModel.userProfile = updatedProfile // Reassign to trigger UI updates
                                        
                                        Task {
                                            await profileViewModel.updateUserInfo(token: "YourAuthToken", userId: "YourUserId")
                                        }
                                    }) {
                                        Text("Update DOB")
                                            .font(.custom("ArialNova-Light", size: 16))
                                            .frame(width: 200, height: 40)
                                            .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
                                            .foregroundColor(.white)
                                            .cornerRadius(8)
                                    }
                                    .padding(.top, 20)
                                }
                                .onAppear {
                                    // Perform the logic when the view appears
                                    if let userProfile = profileViewModel.userProfile, let dob = userProfile.dob {
                                        selectedYear = dob.year
                                        selectedMonth = dob.month
                                        selectedDay = dob.day
                                    } else {
                                        selectedYear = 2000 // Default year
                                        selectedMonth = 1
                                        selectedDay = 1
                                    }
                                }
                                
                                ChangePassword()
                            }
                            
                        }
                        .onAppear {
                            Task {
                                await profileViewModel.fetchUserProfile()
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
                
            }
        }
        .sheet(isPresented: $vm.showPicker) {
            ImagePicker(sourceType: vm.source == .library ? .photoLibrary : .camera, selectedImage: $vm.image, showImagePicker: $isShowingCamera)
        }
        
    }
}

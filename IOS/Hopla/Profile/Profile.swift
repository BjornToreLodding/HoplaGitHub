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

public struct DOB: Codable {
    var year: Int
    var month: Int
    var day: Int

    enum CodingKeys: String, CodingKey {
        case year = "year"
        case month = "month"
        case day = "day"
    }
}


public struct UserProfile: Codable {
    var alias: String?
    var name: String?
    var email: String
    var pictureUrl: String?
    var telephone: String?
    var description: String?
    var dob: DOB?

    enum CodingKeys: String, CodingKey {
        case alias = "alias"
        case name = "name"
        case email = "email"
        case pictureUrl = "pictureUrl"
        case telephone = "telephone"
        case description = "description"
        case dob = "dob"
    }
}




class ProfileViewModel: ObservableObject {
    @Published var userProfile: UserProfile?            // Original profile from the server
    @Published var draftProfile: UserProfile?             // Editable draft profile
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
                    self.selectedImage = image // Update UI with the new image
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
                    // Directly use the values from user without fallback
                    let fetchedProfile = UserProfile(
                        alias: user.alias,
                        name: user.name,
                        email: user.email,
                        pictureUrl: user.pictureUrl,
                        telephone: user.telephone,
                        description: user.description,
                        dob: user.dob
                    )
                    
                    // Update both original and draft copies
                    self.userProfile = fetchedProfile
                    self.draftProfile = fetchedProfile
                    print("Fetched profile:", self.userProfile ?? "No data")
                }
            } else {
                print("Failed to retrieve profile. Status Code:", httpResponse.statusCode)
            }
        } catch {
            print("Error fetching user profile:", error.localizedDescription)
        }
    }

    
    // Updated: Use the current draftProfile for the update, and then update both local copies.
    func updateUserInfo(token: String, userId: String, loginVM: LoginViewModel) async {
        let url = URL(string: "https://hopla.onrender.com/users/update")!
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        guard let draft = draftProfile else { return }
        
        // Build the payload using the data from the draft profile.
        let alias = draft.alias
        let name = draft.name
        let email = draft.email
        let telephone = draft.telephone ?? ""
        let description = draft.description ?? ""
        
        var body: [String: Any] = [
            "alias": alias,
            "name": name,
            "email": email,
            "telephone": telephone,
            "description": description
        ]
        
        // Include DOB if provided.
        if let dob = draft.dob {
            body["year"] = dob.year
            body["month"] = dob.month
            body["day"] = dob.day
        }
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: body, options: [])
            request.httpBody = jsonData
            
            let (data, response) = try await URLSession.shared.data(for: request)
            
            if let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) {
                print("User info updated successfully. Updating local version...")
                // Instead of re-fetching (which could override the draft with stale data),
                // update the local versions with the current draft.
                DispatchQueue.main.async {
                    self.userProfile = self.draftProfile  // Use the draft (latest version)
                    loginVM.userProfile = self.draftProfile
                }
            } else {
                let errorMessage = String(data: data, encoding: .utf8) ?? "No response"
                print("Update failed with response: \(errorMessage)")
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
    @State private var editingField: String? = nil

    // Holds the information about the photo selected from the photo picker
    @State private var selectedItem: PhotosPickerItem? = nil
    // Stores the actual UIImage to display in the profile view
    @State private var profileImage: UIImage? = nil
    @State private var isShowingCamera = false // Camera
    @Binding var navigationPath: NavigationPath
    // State variables for the DatePicker selection
    @State private var selectedYear: Int = 2000
    @State private var selectedMonth: Int = 1
    @State private var selectedDay: Int = 1
    @State private var isShowingImageForm = false
    @State private var selectedSource: SourceType? = nil
    @State private var tempSelectedImage: UIImage? = nil




    enum SourceType {
        case camera
        case library
    }

    // The initializer ensures that all state properties are initialized
    init(profileViewModel: ProfileViewModel, loginViewModel: LoginViewModel, navigationPath: Binding<NavigationPath>) {
        self.profileViewModel = profileViewModel
        self.loginViewModel = loginViewModel
        self._vm = StateObject(wrappedValue: ViewModel(profileViewModel: profileViewModel))
        self._navigationPath = navigationPath
    }

    private func saveUpdate() {
        if let token = TokenManager.shared.getToken(),
           let userId = TokenManager.shared.getUserId() {
            Task {
                await profileViewModel.updateUserInfo(token: token, userId: userId, loginVM: loginViewModel)
            }
        }
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

                    NavigationLink(destination: Settings(loginViewModel: LoginViewModel(), navigationPath: $navigationPath, viewModel: profileViewModel)) {
                        Image(systemName: "gearshape.fill")
                            .font(.system(size: 24))
                            .padding()
                            .padding(.trailing, 10)
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)

                VStack {
                    // Display the profile image section (unchanged)
                    if let user = profileViewModel.userProfile {
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
                            } else if let pictureUrl = user.pictureUrl, let url = URL(string: pictureUrl) {
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

                        // Main button to open the form
                        Button(action: {
                            isShowingImageForm = true
                        }) {
                            // underline the Text, not the Button
                            Text("Change Profile Picture")
                                .underline()
                                .font(.custom("ArialNova-Light", size: 16))
                        }
                        .padding(.bottom)


                        // Navigation Buttons (unchanged)
                        HStack(spacing: 10) {
                            NavigationLink(destination: MyHikes()) {
                                Text("My activity")
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
                        
                        // Profile Info Section: Bind to draftProfile for editing.
                        ZStack {
                            VStack(spacing: 12) {
                                // Username Field
                                fieldContainer(colorScheme: colorScheme) {
                                    EditableProfileField(
                                        title: "Username",
                                        fieldId: "alias",
                                        value: Binding(
                                            get: { profileViewModel.draftProfile?.alias ?? "" },
                                            set: { profileViewModel.draftProfile?.alias = $0 }
                                        ),
                                        editingField: $editingField,
                                        onSave: { _ in saveUpdate() }
                                    )
                                }
                                
                                // Email Field
                                fieldContainer(colorScheme: colorScheme) {
                                    EditableProfileField(
                                        title: "Email",
                                        fieldId: "email",
                                        value: Binding(
                                            get: { profileViewModel.draftProfile?.email ?? "" },
                                            set: { profileViewModel.draftProfile?.email = $0 }
                                        ),
                                        editingField: $editingField,
                                        onSave: { _ in saveUpdate() }
                                    )
                                }
                                
                                fieldContainer(colorScheme: colorScheme) {
                                    // Name Field
                                    EditableProfileField(
                                        title: "Name",
                                        fieldId: "name",
                                        value: Binding(
                                            get: { profileViewModel.draftProfile?.name ?? "" },
                                            set: { profileViewModel.draftProfile?.name = $0 }
                                        ),
                                        editingField: $editingField,
                                        onSave: { _ in saveUpdate() }
                                    )
                                }
                                
                                // Telephone Field
                                fieldContainer(colorScheme: colorScheme) {
                                    EditableProfileField(
                                        title: "Telephone",
                                        fieldId: "telephone",
                                        value: Binding(
                                            get: { profileViewModel.draftProfile?.telephone ?? "" },
                                            set: { profileViewModel.draftProfile?.telephone = $0 }
                                        ),
                                        editingField: $editingField,
                                        onSave: { _ in saveUpdate() }
                                    )
                                }
                                
                                // Description Field
                                fieldContainer(colorScheme: colorScheme) {
                                    EditableProfileField(
                                        title: "Description",
                                        fieldId: "description",
                                        value: Binding(
                                            get: { profileViewModel.draftProfile?.description ?? "" },
                                            set: { profileViewModel.draftProfile?.description = $0 }
                                        ),
                                        editingField: $editingField,
                                        onSave: { _ in saveUpdate() }
                                    )
                                }
                                
                                // Date of Birth Field
                                fieldContainer(colorScheme: colorScheme) {
                                    EditableDOBField(
                                        dob: Binding(
                                            get: { profileViewModel.draftProfile?.dob ?? DOB(year: 2000, month: 1, day: 1) },
                                            set: { profileViewModel.draftProfile?.dob = $0 }
                                        ),
                                        editingField: $editingField,
                                        onSave: { newDOB in
                                            saveUpdate()
                                        }
                                    )
                                }
                                
                                ChangeEmail(loginViewModel: LoginViewModel())
                                
                                ChangePassword(loginViewModel: LoginViewModel())
                            }
                            .padding()
                            
                        }
                        .onAppear {
                            Task {
                                await profileViewModel.fetchUserProfile()
                                loginViewModel.userProfile = profileViewModel.draftProfile
                            }
                            print("📡 Fetching user profile...")
                        }
                        
                        Spacer()
                        
                    } else {
                        Text("Loading profile...")
                            .onAppear {
                                Task {
                                    await profileViewModel.fetchUserProfile()
                                    loginViewModel.userProfile = profileViewModel.draftProfile
                                }
                            }
                    }
                }
            }
        }
        .sheet(isPresented: $isShowingImageForm) {
            VStack(spacing: 20) {
                Text("New Profile Picture")
                    .font(.title2)
                    .bold()
                    .padding(.top)

                HStack(spacing: 30) {
                    Button("Camera") {
                        selectedSource = .camera
                        vm.source = .camera
                        vm.showPhotoPicker()
                    }

                    Button("Photos") {
                        selectedSource = .library
                        vm.source = .library
                        vm.showPhotoPicker()
                    }
                }

                // Image picker sheet triggers inside the form
                .sheet(isPresented: $vm.showPicker) {
                  ImagePicker(
                    sourceType: selectedSource == .library ? .photoLibrary : .camera,
                    selectedImage: $tempSelectedImage, // ← bind *temp* only
                    showImagePicker: $isShowingCamera
                  )
                  .onChange(of: tempSelectedImage) { _ in
                    // as soon as the user taps “Done” in the picker,
                    // dismiss the picker sheet automatically:
                    vm.showPicker = false
                  }
                }
                
                if let tempImage = tempSelectedImage {
                    ZStack {
                        // Outer circle background
                        Circle()
                            .frame(width: 200, height: 200)
                            .foregroundColor(
                                AdaptiveColor(light: .lightPostBackground,
                                              dark: .darkPostBackground)
                                  .color(for: colorScheme)
                            )

                        // The picked image inset a bit
                        Image(uiImage: tempImage)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 180, height: 180)
                            .clipShape(Circle())
                    }
                    .padding(.vertical)
                }

                HStack {
                    Button("Cancel") {
                      tempSelectedImage = nil
                      selectedSource      = nil
                      vm.showPicker       = false    // in case it’s still open
                      isShowingImageForm  = false    // dismiss the “New Profile Picture” form
                    }

                    Spacer()

                    Button("Save") {
                      guard
                        let image   = tempSelectedImage,
                        let token   = TokenManager.shared.getToken(),
                        let userId  = TokenManager.shared.getUserId()
                      else {
                        print("Nothing selected or missing credentials.")
                        return
                      }

                      Task {
                        await profileViewModel.uploadProfileImage(
                          image: image,
                          entityId: userId,
                          table: "Users",
                          token: token
                        )
                        // only now update your UI/model:
                        profileViewModel.selectedImage = image

                        // clear and dismiss:
                        tempSelectedImage   = nil
                        selectedSource      = nil
                        isShowingImageForm  = false
                      }
                    }
                    .bold()
                }
                .padding(.horizontal)

                Spacer()
            }
            .padding()
        }
    }
}

@ViewBuilder
func fieldContainer<Content: View>(
    colorScheme: ColorScheme,
    @ViewBuilder content: () -> Content
) -> some View {
    content()
        .padding()
        .background(
            AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground)
                .color(for: colorScheme)
        )
        .cornerRadius(10)
        .shadow(radius: 2)
        .foregroundColor(
            AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground)
                .color(for: colorScheme)
        )
        .font(.custom("ArialNova", size: 16))
}




struct EditableProfileField: View {
    let title: String
    let fieldId: String
    @Binding var value: String
    // Global state to track which field is being edited
    @Binding var editingField: String?
    
    // Temporary value holder during editing
    @State private var tempValue: String = ""
    
    // Callback triggered when the field is saved.
    var onSave: (String) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.custom("ArialNova", size: 16))
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: .light))
            
            HStack {
                if editingField == fieldId {
                    // Editing Mode: show a TextField and a Save button
                    TextField("Enter \(title)", text: $tempValue)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .frame(maxWidth: 200)
                    Button(action: {
                        // Update the bound value and fire onSave callback
                        value = tempValue
                        onSave(tempValue)
                        editingField = nil
                    }) {
                        Image(systemName: "checkmark.circle")
                            .foregroundColor(.green)
                    }
                } else {
                    // Display Mode: show the saved value and an edit pencil
                    Text(value.isEmpty ? "Not provided" : value)
                        .font(.custom("ArialNova-Light", size: 16))
                        .frame(maxWidth: .infinity, alignment: .center)
                    Button(action: {
                        // Only enable edit if no field is active
                        if editingField == nil {
                            tempValue = value
                            editingField = fieldId
                        }
                    }) {
                        Image(systemName: "pencil")
                            .foregroundColor(editingField == nil ? .blue : .gray)
                    }
                    .disabled(editingField != nil)
                }
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 16)
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: .light))
        .cornerRadius(8)
    }
}

struct EditableDOBField: View {
    let title: String = "Date of Birth"
    @Binding var dob: DOB
    @Binding var editingField: String?
    var onSave: (DOB) -> Void

    // Local state for the picker values
    @State private var tempYear: Int = 2000
    @State private var tempMonth: Int = 1
    @State private var tempDay: Int = 1

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.custom("ArialNova", size: 16))
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: .light))
            if editingField == "dob" {
                HStack {
                    // Year Picker
                    SwiftUI.Picker("Year", selection: $tempYear) {
                        ForEach(1900..<2024, id: \.self) { year in
                            Text("\(year)").tag(year)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .frame(width: 100)
                    // Month Picker
                    SwiftUI.Picker("Month", selection: $tempMonth) {
                        ForEach(1..<13, id: \.self) { month in
                            Text("\(month)").tag(month)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .frame(width: 70)
                    // Day Picker
                    SwiftUI.Picker("Day", selection: $tempDay) {
                        ForEach(1..<32, id: \.self) { day in
                            Text("\(day)").tag(day)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .frame(width: 60)
                    
                    Button(action: {
                        // Create new DOB and save it
                        let newDOB = DOB(year: tempYear, month: tempMonth, day: tempDay)
                        dob = newDOB
                        onSave(newDOB)
                        editingField = nil
                    }) {
                        Image(systemName: "checkmark.circle")
                            .foregroundColor(.green)
                    }
                }
                .onAppear {
                    // Initialize temporary state with the current DOB
                    tempYear = dob.year
                    tempMonth = dob.month
                    tempDay = dob.day
                }
            } else {
                // Display mode: show DOB text and a pencil button
                HStack {
                    Text("\(dob.year)-\(dob.month)-\(dob.day)")
                        .font(.custom("ArialNova-Light", size: 16))
                        .frame(maxWidth: .infinity, alignment: .center)
                    Button(action: {
                        if editingField == nil {
                            editingField = "dob"
                        }
                    }) {
                        Image(systemName: "pencil")
                            .foregroundColor(editingField == nil ? .blue : .gray)
                    }
                    .disabled(editingField != nil)
                }
            }
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 16)
        .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: .light))
        .cornerRadius(8)
    }
}

//MARK: - Change email
struct ChangeEmail: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var isShowingSheet = false  // Controls popup visibility
    @State private var newEmail = ""
    @State private var currentPassword = ""
    @State private var localErrorMessage = ""
    
    // Inject the view model so that we can call changeEmail.
    @ObservedObject var loginViewModel: LoginViewModel

    var body: some View {
        VStack {
            Text("Change email")
                .font(.custom("ArialNova", size: 16))
                .frame(width: 360, height: 30)
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                .underline(true)
                .padding(.top, 10)
                .onTapGesture {
                    resetFields()
                    isShowingSheet = true
                }
        }
        .sheet(isPresented: $isShowingSheet, onDismiss: {
            resetFields()
        }) {
            VStack(spacing: 20) {
                Text("Write the new email and your current password to change your email.")
                    .font(.headline)
                    .multilineTextAlignment(.center)
                    .padding()

                TextField("Enter new email", text: $newEmail)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
                
                SecureField("Enter current password", text: $currentPassword)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()

                if !localErrorMessage.isEmpty {
                    Text(localErrorMessage)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                Button("Submit") {
                    // Validate a simple email format or non-empty if desired.
                    if newEmail.isEmpty || currentPassword.isEmpty {
                        localErrorMessage = "All fields must be filled out!"
                    } else {
                        Task {
                            await loginViewModel.changeEmail(newEmail: newEmail, password: currentPassword)
                            // Check whether an error was set.
                            if loginViewModel.errorMessage == nil {
                                isShowingSheet = false
                            } else {
                                localErrorMessage = loginViewModel.errorMessage ?? "Unknown error"
                            }
                        }
                    }
                }
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
                
                Button("Cancel") {
                    isShowingSheet = false
                }
                .padding()
            }
            .padding()
        }
    }
    
    // Helper function to clear fields
    private func resetFields() {
        newEmail = ""
        currentPassword = ""
        localErrorMessage = ""
    }
}



//
//  ViewModel.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 12/02/2025.
//
import SwiftUI
import PhotosUI

// Possible sources for selecting an image
enum ImageSource {
    case camera    // Capture a new photo with the device camera
    case library   // Pick an existing photo from the photo library
}

// ViewModel to manage image picking and uploading logic
class ViewModel: ObservableObject {
    // Which source to use when presenting the picker (camera or library)
    @Published var source: ImageSource = .library
    
    // Whether the image picker sheet/modal is currently shown
    @Published var showPicker = false
    
    // Holds the image the user selects; triggers upload in didSet
    @Published var image: UIImage? {
        didSet {
            // As soon as an image is assigned, start uploading it
            if let image = image {
                uploadSelectedImage(image)
            }
        }
    }
    
    // Reference to another ViewModel that handles profile-specific operations
    var profileViewModel: ProfileViewModel
    
    // Inject the ProfileViewModel dependency
    init(profileViewModel: ProfileViewModel) {
        self.profileViewModel = profileViewModel
    }
    
    /// Call this to present the photo picker UI
    func showPhotoPicker() {
        showPicker = true
    }
    
    /// Uploads the selected image using the ProfileViewModel
    private func uploadSelectedImage(_ image: UIImage) {
        // Ensure we have both an auth token and user ID
        guard let token = TokenManager.shared.getToken(),
              let userId = TokenManager.shared.getUserId() else {
            print("Image, token, or user ID is missing.")
            return
        }
        
        // Perform the upload asynchronously
        Task {
            await profileViewModel.uploadProfileImage(
                image: image,
                entityId: userId,
                table: "Users",
                token: token
            )
        }
    }
}

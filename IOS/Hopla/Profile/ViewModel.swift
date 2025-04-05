//
//  ViewModel.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 12/02/2025.
//

import SwiftUI
import PhotosUI

enum ImageSource {
    case camera
    case library
}

class ViewModel: ObservableObject {
    @Published var source: ImageSource = .library
    @Published var showPicker = false
    @Published var image: UIImage? {
        didSet {
            if let image = image {
                uploadSelectedImage(image)
            }
        }
    }
    
    var profileViewModel: ProfileViewModel // Reference to ProfileViewModel

    init(profileViewModel: ProfileViewModel) {
        self.profileViewModel = profileViewModel
    }

    func showPhotoPicker() {
        showPicker = true
    }

    private func uploadSelectedImage(_ image: UIImage) {
        guard let token = TokenManager.shared.getToken(),
              let userId = TokenManager.shared.getUserId() else {
            print("Image, token, or user ID is missing.")
            return
        }

        Task {
            await profileViewModel.uploadProfileImage(image: image, entityId: userId, table: "Users", token: token)
        }
    }
}

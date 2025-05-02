import SwiftUI
import UIKit

/// A SwiftUI wrapper for UIImagePickerController to select an image
struct ImagePicker: UIViewControllerRepresentable {
    /// Choose between camera or photo library
    var sourceType: UIImagePickerController.SourceType
    /// Binding to store the selected image
    @Binding var selectedImage: UIImage?
    /// Binding to control whether the picker is shown
    @Binding var showImagePicker: Bool

    /// Coordinator to bridge UIKit delegate callbacks back to SwiftUI
    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        /// Reference to the parent ImagePicker for updating bindings
        var parent: ImagePicker

        init(parent: ImagePicker) {
            self.parent = parent
        }

        /// Called when the user picks an image
        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]
        ) {
            // Try to get the original image from the info dictionary
            if let image = info[.originalImage] as? UIImage {
                DispatchQueue.main.async {
                    // Update the bound selectedImage
                    self.parent.selectedImage = image
                    // Dismiss the picker
                    self.parent.showImagePicker = false
                }
            } else {
                // If no image found, still dismiss the picker
                DispatchQueue.main.async {
                    self.parent.showImagePicker = false
                }
            }
        }

        /// Called when the user cancels without selecting an image
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            DispatchQueue.main.async {
                // Dismiss the picker on cancel
                self.parent.showImagePicker = false
            }
        }
    }

    /// Create the Coordinator instance for delegate callbacks
    func makeCoordinator() -> Coordinator {
        return Coordinator(parent: self)
    }

    /// Instantiate and configure the UIImagePickerController
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = sourceType               // Use specified source type
        picker.delegate = context.coordinator        // Set delegate to our Coordinator
        return picker
    }

    /// Update the view controller if SwiftUI state changes (unused here)
    func updateUIViewController(
        _ uiViewController: UIImagePickerController,
        context: Context
    ) {}
}


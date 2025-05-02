import SwiftUI
import UIKit

/// A SwiftUI wrapper to present the camera interface and capture an image
struct CameraView: UIViewControllerRepresentable {
    // Binding to store the image selected or captured in the parent SwiftUI view
    @Binding var selectedImage: UIImage?
    
    /// Creates the coordinator object to act as UIKit delegate
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    /// Instantiates and configures the UIImagePickerController
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera                       // Use the device camera
        picker.delegate = context.coordinator              // Delegate callbacks to our Coordinator
        picker.allowsEditing = true                        // Let user crop/edit before returning
        return picker
    }
    
    /// Updates the UIKit view when SwiftUI state changes (unused here)
    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
    
    // MARK: - Coordinator to bridge UIImagePickerControllerDelegate
    
    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        // Reference to the parent CameraView to update the binding
        let parent: CameraView
        
        init(_ parent: CameraView) {
            self.parent = parent
        }
        
        /// Called when the user picks or captures an image
        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]
        ) {
            // Try edited image first; fall back to original if editing wasn’t used
            if let image = info[.editedImage] as? UIImage ?? info[.originalImage] as? UIImage {
                parent.selectedImage = image  // Pass image back to SwiftUI binding
            }
            picker.dismiss(animated: true)    // Close the camera interface
        }
        
        /// Called when the user cancels without selecting an image
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)    // Simply close the camera interface
        }
    }
}

//
//  ViewModel.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 12/02/2025.
//

import SwiftUI

class ViewModel: ObservableObject {
    @Published var image: UIImage?
    @Published var showPicker: Bool = false
    @Published var source: Picker.Source = .library
    
    func showPhotoPicker() {
        if source == .camera {
            if !Picker.checkPermissions() {
                print("Ther is no camera on this device")
                return
            }
        }
        showPicker = true
    }
}
 

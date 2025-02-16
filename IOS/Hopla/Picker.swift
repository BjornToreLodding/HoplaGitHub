//
//  Picker.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 12/02/2025.
//

import UIKit

enum Picker {
    enum Source: String {
        case library
        case camera
    }
    
    static func checkPermissions() -> Bool {
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            return true
        } else {
            return false
        }
    }
}

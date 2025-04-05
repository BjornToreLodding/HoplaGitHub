//
//  UserProfile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 04/04/2025.
//

import Foundation

public struct DOB: Codable {
    let year: Int
    let month: Int
    let day: Int
}

public struct UserProfile: Codable {
    let alias: String
    let name: String
    let email: String
    let pictureUrl: String?
    let telephone: String?
    let description: String?
    let dob: DOB?  // Changed from String? to DOB
}

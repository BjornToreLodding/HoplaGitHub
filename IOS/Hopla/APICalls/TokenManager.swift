//
//  DecodeToken.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 04/03/2025.
//

import Foundation
import KeychainSwift

class TokenManager {
    static let shared = TokenManager() // Singleton instance
    
    private let keychain = KeychainSwift()
    private let tokenKey = "authToken"
    
    // Save the token securely
    func saveToken(_ token: String) {
        keychain.set(token, forKey: tokenKey)
    }
    
    // Retrieve the stored token
    func getToken() -> String? {
        let token = keychain.get(tokenKey)
        //print("Retrieved Token:", token ?? "nil")
        return token
    }
    
    
    // Delete the stored token
    func deleteToken() {
        keychain.delete(tokenKey)
    }
    
    // Check if the user is logged in
    func isLoggedIn() -> Bool {
        return getToken() != nil
    }
    
    // Decode the JWT token and extract user data
    func decodeToken() -> [String: Any]? {
        guard let token = getToken() else {
            print("No token found in keychain")
            return nil
        }
        
        let segments = token.split(separator: ".")
        
        guard segments.count == 3 else {
            print("JWT format incorrect:", token)
            return nil
        }
        
        var base64String = String(segments[1])
        
        base64String = base64String.replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        
        while base64String.count % 4 != 0 {
            base64String.append("=")
        }
        
        guard let data = Data(base64Encoded: base64String) else {
            print("Base64 decoding failed")
            return nil
        }
        
        do {
            let json = try JSONSerialization.jsonObject(with: data, options: [])
            print("Decoded Token Payload:", json)
            return json as? [String: Any]
        } catch {
            print("JWT Decoding Error:", error)
            return nil
        }
    }
    
    
    
    // Get the userId from the decoded token
    func getUserId() -> String? {
        guard let decodedToken = decodeToken() else {
            print("Decoded token is nil")
            return nil
        }
        
        print("Decoded Token Dictionary:", decodedToken)
        
        // Try different possible keys for user ID
        let userIdKeys = ["userId", "sub", "id", "nameid"]
        for key in userIdKeys {
            if let userId = decodedToken[key] as? String {
                print("Extracted User ID using key '\(key)':", userId)
                return userId
            }
        }
        
        print("User ID not found in token")
        return nil
    }
    
    private let descriptionKey = "userDescription"
    
    func saveUserDescription(_ description: String?) {
        if let description = description {
            UserDefaults.standard.set(description, forKey: "userDescription") // Persist description
            UserDefaults.standard.synchronize() // Force save (optional)
            print("Saved Description in UserDefaults:", description)
        } else {
            print("No description received to store")
        }
    }


    
    func getUserDescription() -> String? {
        let storedDescription = UserDefaults.standard.string(forKey: "userDescription") // Retrieve stored value
        print("Retrieved Description from UserDefaults:", storedDescription ?? "No description stored")
        return storedDescription
    }
}

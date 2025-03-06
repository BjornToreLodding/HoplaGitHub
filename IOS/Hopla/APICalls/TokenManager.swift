//
//  DecodeToken.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 04/03/2025.
//

import Foundation
import SwiftUI
import KeychainSwift

class TokenManager {
    static let shared = TokenManager()

    private let tokenKey = "user_token"

    // Store the token securely using KeychainSwift
    func storeToken(_ token: String) {
        let keychain = KeychainSwift()
        keychain.set(token, forKey: tokenKey)
    }

    // Retrieve the stored token using KeychainSwift
    func retrieveToken() -> String? {
        let keychain = KeychainSwift()
        return keychain.get(tokenKey)
    }

    // Check if the user is logged in
    func isLoggedIn() -> Bool {
        return retrieveToken() != nil
    }

    // Decode the JWT token and extract user data
    func decodeToken() -> [String: Any]? {
        guard let token = retrieveToken() else { return nil }
        let segments = token.split(separator: ".")
        
        guard segments.count == 3,
              let base64String = String(segments[1]).removingPercentEncoding,
              let data = Data(base64Encoded: base64String) else {
            return nil
        }
        
        do {
            let json = try JSONSerialization.jsonObject(with: data, options: [])
            return json as? [String: Any]
        } catch {
            return nil
        }
    }
}



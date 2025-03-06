//
//  LoginViewModel.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 04/03/2025.
//

import Foundation
import SwiftUI
import KeychainSwift
import KeychainAccess

class LoginViewModel: ObservableObject {
    @Published var errorMessage: String?
    @Published var isLoggedIn: Bool = false
    @Published var userProfile: UserProfile? // Store user profile data

    private let keychain = KeychainSwift()
    private let apiUrl = "https://hopla.onrender.com/users/login/"
    
    struct LoginRequest: Codable {
        let email: String
        let password: String
    }

    struct LoginResponse: Codable {
        let token: String
        let userId: String
        let email: String
        let name: String
        let alias: String
        let pictureUrl: String?
    }

    struct UserProfile: Codable {
        let alias: String
        let name: String
        let email: String
        let pictureUrl: String?
    }

    func login(email: String, password: String) {
        guard let url = URL(string: apiUrl) else {
            DispatchQueue.main.async { self.errorMessage = "Invalid URL" }
            return
        }

        let loginData = LoginRequest(email: email, password: password)
        guard let jsonData = try? JSONEncoder().encode(loginData) else {
            DispatchQueue.main.async { self.errorMessage = "Failed to encode login data" }
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = jsonData

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    self.errorMessage = "Request error: \(error.localizedDescription)"
                }
                return
            }

            guard let httpResponse = response as? HTTPURLResponse, let data = data else {
                DispatchQueue.main.async { self.errorMessage = "Invalid response" }
                return
            }

            if httpResponse.statusCode == 200 {
                do {
                    let loginResponse = try JSONDecoder().decode(LoginResponse.self, from: data)
                    DispatchQueue.main.async {
                        self.saveToken(loginResponse.token)
                        self.isLoggedIn = true
                        UserDefaults.standard.set(true, forKey: "isLoggedIn")
                        UserDefaults.standard.set(loginResponse.userId, forKey: "userId")

                        // Fetch the user profile after login
                        self.fetchUserProfile()
                    }
                } catch {
                    DispatchQueue.main.async { self.errorMessage = "Failed to decode response" }
                }
            } else if httpResponse.statusCode == 401 {
                DispatchQueue.main.async { self.errorMessage = "Invalid email or password" }
            } else {
                DispatchQueue.main.async { self.errorMessage = "Unexpected server error" }
            }
        }.resume()
    }

    private func saveToken(_ token: String) {
        keychain.set(token, forKey: "authToken")
    }

    func getToken() -> String? {
        return keychain.get("authToken")
    }

    func logout() {
        keychain.delete("authToken")
        DispatchQueue.main.async {
            self.isLoggedIn = false
            self.userProfile = nil // Clear profile data on logout
        }
    }

    func fetchUserProfile() {
        guard let token = getToken() else {
            print("No token found")
            return
        }

        let url = URL(string: "https://hopla.onrender.com/users/myprofile")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }

            guard let httpResponse = response as? HTTPURLResponse, let data = data else {
                print("Invalid response")
                return
            }

            if httpResponse.statusCode == 200 {
                do {
                    let user = try JSONDecoder().decode(UserProfile.self, from: data)
                    DispatchQueue.main.async {
                        self.userProfile = user
                    }
                } catch {
                    print("Error decoding user profile:", error.localizedDescription)
                }
            } else {
                print("Failed to retrieve user profile. Status Code:", httpResponse.statusCode)
            }
        }.resume()
    }
}


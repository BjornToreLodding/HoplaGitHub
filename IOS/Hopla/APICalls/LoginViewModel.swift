//
//  LoginViewModel.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 04/03/2025.
//

import Foundation
import SwiftUI
import KeychainAccess

class LoginViewModel: ObservableObject {
    @Published var errorMessage: String?
    @Published var isLoggedIn: Bool = false
    @Published var userProfile: UserProfile?

    private let apiUrl = "https://hopla.onrender.com/users/login/"
    
    struct LoginRequest: Codable {
        let email: String
        let password: String
    }

    struct LoginResponse: Codable {
        let token: String
        let userId: String
        let name: String
        let alias: String
        let pictureUrl: String?
        let telephone: String?
        let description: String?
        let dob: DOB?  // Changed from String? to DOB
        let redirect: String?
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
                    let decoder = JSONDecoder()
                    decoder.dateDecodingStrategy = .iso8601

                    let loginResponse = try decoder.decode(LoginResponse.self, from: data)
                    print("Login Response Description:", loginResponse.description ?? "No description in response")

                    DispatchQueue.main.async {
                        TokenManager.shared.saveToken(loginResponse.token)
                        TokenManager.shared.saveUserDescription(loginResponse.description) // Store description

                        print("Stored Description in TokenManager:", TokenManager.shared.getUserDescription() ?? "No description stored") // Debugging print

                        self.isLoggedIn = true
                        UserDefaults.standard.set(true, forKey: "isLoggedIn")
                        UserDefaults.standard.set(loginResponse.userId, forKey: "userId")

                        self.fetchUserProfile() // Fetch user profile after login
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.errorMessage = "Failed to decode response: \(error.localizedDescription)"
                    }
                }
            } else if httpResponse.statusCode == 401 {
                DispatchQueue.main.async { self.errorMessage = "Invalid email or password" }
            } else {
                DispatchQueue.main.async { self.errorMessage = "Unexpected server error" }
            }
        }.resume()
    }
    
    func logout() {
        TokenManager.shared.deleteToken() // Use the TokenManager
        DispatchQueue.main.async {
            self.isLoggedIn = false
            self.userProfile = nil
        }
    }
    
    func fetchUserProfile() {
        guard let token = TokenManager.shared.getToken() else { // Use the TokenManager
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/users/profile")!
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
                    print("Fetched description before update:", TokenManager.shared.getUserDescription() ?? "No description found")

                    DispatchQueue.main.async {
                        var finalDescription = user.description ?? TokenManager.shared.getUserDescription()

                        // Save latest description from server if available
                        if let serverDescription = user.description, !serverDescription.isEmpty {
                            TokenManager.shared.saveUserDescription(serverDescription)
                        }

                        self.userProfile = UserProfile(
                            alias: user.alias,
                            name: user.name,
                            email: user.email,
                            pictureUrl: user.pictureUrl,
                            telephone: user.telephone,
                            description: finalDescription,
                            dob: user.dob
                        )

                        print("Final User Profile:", self.userProfile ?? "No data")
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

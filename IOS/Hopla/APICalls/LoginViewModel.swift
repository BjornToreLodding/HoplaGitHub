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
    @Published var user: LoginResponse? // To store user details for later use
    
    private let keychain = KeychainSwift() // Instance for secure storage
    
    private let apiUrl = "https://hopla.onrender.com/users/login/" // API endpoint
    
    struct LoginRequest: Codable {
        let email: String
        let password: String
    }
    
    // Info about user in login screen
    struct LoginResponse: Codable {
        let token: String
        let userId: String
        let email: String
        let name: String
        let alias: String
        let pictureUrl: String?
    }
    
    // Info about user in profile screen
    struct UserProfile: Codable {
        let alias: String
        let name: String
        let email: String
        let pictureUrl: String?
    }

    
    func login(email: String, password: String) {
        guard let url = URL(string: apiUrl) else {
            DispatchQueue.main.async {
                self.errorMessage = "Invalid URL"
            }
            return
        }
        
        let loginData = LoginRequest(email: email, password: password)
        guard let jsonData = try? JSONEncoder().encode(loginData) else {
            DispatchQueue.main.async {
                self.errorMessage = "Failed to encode login data"
            }
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST" // POST method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = jsonData
        
        // Make the request
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    self.errorMessage = "Request error: \(error.localizedDescription)"
                    print("Request error:", error.localizedDescription)
                }
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                DispatchQueue.main.async {
                    self.errorMessage = "Invalid response"
                    print("Invalid response")
                }
                return
            }
            
            if httpResponse.statusCode == 200, let data = data { // OK
                do {
                    let loginResponse = try JSONDecoder().decode(LoginResponse.self, from: data)
                    DispatchQueue.main.async {
                        self.saveToken(loginResponse.token)
                        self.isLoggedIn = true
                        UserDefaults.standard.set(true, forKey: "isLoggedIn")
                        UserDefaults.standard.set(loginResponse.userId, forKey: "userId") // Store userId
                        print("Login successful, Token:", loginResponse.token)
                        
                        self.fetchUserProfile { user in
                            if let user = user {
                                print("User Profile:")
                                print("Email:", user.email)
                                print("Name:", user.name)
                                print("Alias:", user.alias)
                                print("Picture URL:", user.pictureUrl ?? "No picture available")
                            } else {
                                print("Failed to retrieve user profile.")
                            }
                        }
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.errorMessage = "Failed to decode response"
                        print("Decoding error:", error.localizedDescription)
                    }
                }
            }
 else if httpResponse.statusCode == 401 { // Error
                DispatchQueue.main.async {
                    self.errorMessage = "Invalid email or password"
                    print("Login failed: Invalid credentials")
                }
            } else {
                DispatchQueue.main.async { // Error
                    self.errorMessage = "Unexpected server error"
                    print("Unexpected server error, Status Code:", httpResponse.statusCode)
                }
            }
        }.resume()
    }
    
    // Function to save token
    private func saveToken(_ token: String) {
        keychain.set(token, forKey: "authToken")
    }
    
    // Function to get token
    func getToken() -> String? {
        return keychain.get("authToken")
    }
    
    // Function to delete token when logging out
    func logout() {
        keychain.delete("authToken")
        DispatchQueue.main.async {
            self.isLoggedIn = false
        }
    }
    
    // Function to fetch the users info from token
    func fetchUserProfile(completion: @escaping (UserProfile?) -> Void) {
        guard let token = getToken() else {
            print("No token found")
            completion(nil)
            return
        }

        let url = URL(string: "https://hopla.onrender.com/users/myprofile")! // API URL

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                completion(nil)
                return
            }

            guard let httpResponse = response as? HTTPURLResponse else {
                print("Invalid response")
                completion(nil)
                return
            }

            print("User profile request status code:", httpResponse.statusCode)

            if httpResponse.statusCode == 200, let data = data {
                do {
                    let user = try JSONDecoder().decode(UserProfile.self, from: data)
                    DispatchQueue.main.async {
                        completion(user)
                    }
                } catch {
                    print("Error decoding user profile:", error.localizedDescription)
                    completion(nil)
                }
            } else {
                print("Failed to retrieve user profile. Status Code:", httpResponse.statusCode)
                completion(nil)
            }
        }.resume()
    }
}

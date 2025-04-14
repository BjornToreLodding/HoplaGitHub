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
        @Published var successMessage: String?
        @Published var isLoggedIn: Bool = false
        @Published var userProfile: UserProfile?
        
        private let loginUrl = "https://hopla.onrender.com/users/login/"
        private let registerUrl = "https://hopla.onrender.com/users/register"
        private let changePasswordUrl = "https://hopla.onrender.com/users/change-password"
        
        struct LoginRequest: Codable {
            let email: String
            let password: String
        }
        
        struct LoginResponse: Codable {
            let token: String
            let userId: String
            let name: String?
            let alias: String?
            let pictureUrl: String?
            let telephone: String?
            let description: String?
            let dob: DOB?
            let redirect: String?
        }
    
    // MARK: - Register a New User
    
    func register(email: String, password: String, completion: @escaping (Bool, String?) -> Void) {
        guard let url = URL(string: registerUrl) else {
            DispatchQueue.main.async { self.errorMessage = "Invalid registration URL" }
            completion(false, "Invalid URL")
            return
        }
        
        // Note: The registration endpoint expects keys "Email" and "Password" per your example.
        let registerData: [String: Any] = [
            "Email": email,
            "Password": password
        ]
        
        guard let jsonData = try? JSONSerialization.data(withJSONObject: registerData, options: []) else {
            DispatchQueue.main.async { self.errorMessage = "Failed to encode registration data" }
            completion(false, "Failed to encode registration data")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = jsonData
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    self.errorMessage = "Registration error: \(error.localizedDescription)"
                    completion(false, error.localizedDescription)
                }
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                DispatchQueue.main.async {
                    self.errorMessage = "Invalid registration response"
                    completion(false, "Invalid response")
                }
                return
            }
            
            if (200...299).contains(httpResponse.statusCode) {
                // Assuming the registration endpoint returns no token until confirmation.
                DispatchQueue.main.async {
                    self.successMessage = "Registration successful! Please check your email to confirm your account."
                    completion(true, nil)
                }
            } else {
                let errorMsg = String(data: data ?? Data(), encoding: .utf8) ?? "Unknown error"
                DispatchQueue.main.async {
                    self.errorMessage = "Registration failed: \(errorMsg)"
                    completion(false, errorMsg)
                }
            }
        }.resume()
    }
    
    // MARK: - Login Existing User
    
    func login(email: String, password: String) {
        guard let url = URL(string: loginUrl) else {
            DispatchQueue.main.async { self.errorMessage = "Invalid login URL" }
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
                DispatchQueue.main.async { self.errorMessage = "Request error: \(error.localizedDescription)" }
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse, let data = data else {
                DispatchQueue.main.async { self.errorMessage = "Invalid login response" }
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
                        TokenManager.shared.saveUserDescription(loginResponse.description)
                        print("Stored Description:", TokenManager.shared.getUserDescription() ?? "No description")
                        
                        self.isLoggedIn = true
                        UserDefaults.standard.set(true, forKey: "isLoggedIn")
                        UserDefaults.standard.set(loginResponse.userId, forKey: "userId")
                        
                        self.fetchUserProfile() // Load user profile after login
                    }
                } catch {
                    DispatchQueue.main.async { self.errorMessage = "Failed to decode response: \(error.localizedDescription)" }
                }
            } else if httpResponse.statusCode == 401 {
                DispatchQueue.main.async { self.errorMessage = "Invalid email or password" }
            } else {
                DispatchQueue.main.async { self.errorMessage = "Unexpected server error" }
            }
        }.resume()
    }
    
    // MARK: - Logout
    
    func logout() {
        TokenManager.shared.deleteToken()
        DispatchQueue.main.async {
            self.isLoggedIn = false
            self.userProfile = nil
        }
    }
    
    // MARK: - Fetch User Profile
    
    func fetchUserProfile() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/users/profile")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Profile request error:", error.localizedDescription)
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse, let data = data else {
                print("Invalid profile response")
                return
            }
            
            if httpResponse.statusCode == 200 {
                do {
                    let user = try JSONDecoder().decode(UserProfile.self, from: data)
                    print("Fetched description:", TokenManager.shared.getUserDescription() ?? "No description")
                    
                    DispatchQueue.main.async {
                        var finalDescription = user.description ?? TokenManager.shared.getUserDescription()
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
                    print("Error decoding profile:", error.localizedDescription)
                }
            } else {
                print("Failed to retrieve profile. Status Code:", httpResponse.statusCode)
            }
        }.resume()
    }
    
    //MARK: - Change password
    func changePassword(oldPassword: String, newPassword: String, confirmPassword: String) async {
            guard let token = TokenManager.shared.getToken() else {
                DispatchQueue.main.async {
                    self.errorMessage = "User token not available."
                }
                return
            }
            
            guard let url = URL(string: changePasswordUrl) else {
                DispatchQueue.main.async {
                    self.errorMessage = "Invalid change password URL."
                }
                return
            }
            
            var request = URLRequest(url: url)
            request.httpMethod = "PUT"
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            
            let body: [String: Any] = [
                "OldPassword": oldPassword,
                "NewPassword": newPassword,
                "ConfirmPassword": confirmPassword
            ]
            
            do {
                let jsonData = try JSONSerialization.data(withJSONObject: body, options: [])
                request.httpBody = jsonData
                
                let (data, response) = try await URLSession.shared.data(for: request)
                
                if let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) {
                    DispatchQueue.main.async {
                        self.successMessage = "Password changed successfully!"
                        self.errorMessage = nil
                    }
                } else {
                    let errorMsg = String(data: data, encoding: .utf8) ?? "Unknown error"
                    DispatchQueue.main.async {
                        self.errorMessage = "Password change failed: \(errorMsg)"
                        self.successMessage = nil
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    self.errorMessage = "Error: \(error.localizedDescription)"
                    self.successMessage = nil
                }
            }
        }
}


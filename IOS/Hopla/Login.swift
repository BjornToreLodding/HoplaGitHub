//
//  LogIn.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 09/02/2025.
//

import SwiftUI
import KeychainAccess // For token

struct Login: View {
    @Environment(\.colorScheme) var colorScheme // Detect light/dark mode
    
    @State private var isShowingForgottenPassword = false // True when clicking on text
    @State private var isShowingSignUp = false // Sheet for creating a new user
    @State private var password: String = "" // Already a user: Password
    @State private var email: String = "" // Already a user: Email
    @State private var username: String = "" // Creating a username
    @State private var newPassword: String = "" // When creating a new user
    @State private var confirmNewPassword: String = "" // When creating a new user
    @State private var newEmail: String = "" // // When creating a new user
    @State private var passwordMismatchWarning: String? = nil // Check if password is the same
    
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login state
    
    @StateObject private var viewModel = LoginViewModel()
    
    var body: some View {
        NavigationStack {
            ZStack {
                // Main background color
                Rectangle()
                    .fill(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                    .ignoresSafeArea() // Fill the entire screen
                
                VStack {
                    // Logo
                    Image("LogoUtenBakgrunn")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 170, height: 170)
                        .padding(.top, 100)
                    
                    Text("Hopla")
                        .font(.system(size: 60, weight: .bold, design: .rounded))
                    
                    Spacer()
                    
                    // MARK: - Text fields
                    
                    // Username Label & TextField
                    Text("Email")
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                        .padding(.top, 50)
                    
                    TextField("Enter your email", text: $email)
                        .padding()
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        .cornerRadius(8)
                        .frame(width: 300)
                        .multilineTextAlignment(.center)
                    
                    // Password Label & SecureField
                    Text("Password")
                        .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                        .padding(.top, 20)
                    
                    SecureField("Enter your password", text: $password)
                        .padding()
                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        .cornerRadius(8)
                        .frame(width: 300)
                        .multilineTextAlignment(.center)
                    
                    
                    // MARK: - Forgotten password
                    VStack {
                        Text("Forgotten password?")
                            .frame(width: 360, height: 30)
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            .underline(true)
                            .padding(.top, 10)
                            .onTapGesture { // When clicking on change password
                                resetEmail() // Reset email when opening
                                isShowingForgottenPassword = true // Show sheet
                            }
                    }
                    .sheet(isPresented: $isShowingForgottenPassword, onDismiss: {
                        resetEmail() // Reset fields when closing
                    }) {
                        VStack(spacing: 20) {
                            Text("Enter your email address to reset your password:")
                                .font(.headline)
                            
                            TextField("Enter email:", text: $email)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .padding()
                            
                            Button("Send") {
                                resetEmail()
                                isShowingForgottenPassword = false
                            }
                            .padding()
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                            
                            Button("Cancel") {
                                isShowingForgottenPassword = false
                            }
                            .padding()
                        }
                        .padding()
                    }
                    
                    
                    // MARK: - Login
                    
                    Button(action: {
                        if email.isEmpty || password.isEmpty {
                            viewModel.errorMessage = "Email and password cannot be empty!"
                        } else {
                            viewModel.login(email: email, password: password)
                        }
                    }) {
                        Text("Log In")
                            .foregroundColor(.white)
                            .padding()
                            .frame(width: 200, height: 50)
                            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                            .cornerRadius(8)
                    }
                    .padding(.top, 30)
                    .alert(isPresented: Binding<Bool>(
                        get: { viewModel.errorMessage != nil },
                        set: { _ in viewModel.errorMessage = nil }
                    )) {
                        Alert(title: Text("Login Failed"), message: Text(viewModel.errorMessage ?? ""), dismissButton: .default(Text("OK")))
                    }
                    .padding(.top, 30)
                    .navigationBarBackButtonHidden(true) // Hide the back arrow
                    
                    Spacer()
                    
                    // MARK: - Sign up
                    
                    VStack {
                        Text("Not a member? Sign up")
                            .frame(width: 360, height: 30)
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                            .underline(true)
                            .padding(.top, 10)
                            .onTapGesture {
                                resetEmail()
                                isShowingSignUp = true
                            }
                    }
                    .sheet(isPresented: $isShowingSignUp, onDismiss: {
                        resetTextFields()
                    }) {
                        VStack(spacing: 20) {
                            Text("Enter your email address")
                                .font(.headline)
                            
                            TextField("Enter email address", text: $newEmail)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .padding()
                            
                            Text("Enter a password")
                                .font(.headline)
                            
                            SecureField("Password", text: $newPassword)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .padding()
                            
                            Text("Confirm password")
                                .font(.headline)
                            
                            SecureField("Confirm password", text: $confirmNewPassword)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .padding()
                            
                            // Show warning if passwords do not match
                            if let warning = passwordMismatchWarning {
                                Text(warning)
                                    .foregroundColor(.red)
                                    .font(.caption)
                            }
                            
                            Text("Enter username")
                                .font(.headline)
                            
                            TextField("Username", text: $username)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                                .padding()
                            
                            Button(action: {
                                if newPassword == confirmNewPassword {
                                    password = newPassword
                                    isLoggedIn = true
                                    passwordMismatchWarning = nil // Clear warning if passwords match
                                } else {
                                    passwordMismatchWarning = "Passwords do not match!"
                                }
                            }) {
                                Text("Join now")
                                    .foregroundColor(.white)
                                    .padding()
                                    .frame(width: 200, height: 50)
                                    .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                                    .cornerRadius(8)
                            }
                            
                            Button("Cancel") {
                                isShowingSignUp = false
                            }
                            .padding()
                        }
                        .padding()
                    }
                }
                .onAppear {
                                if let token = viewModel.getToken(), !token.isEmpty {
                                    viewModel.isLoggedIn = true
                                }
                            }
            }
            .navigationBarHidden(true) // Hide navigation bar on the login screen
        }
    }
    
    
    // MARK: - Functions Sheets
    
    // Resets email
    private func resetEmail() {
        email = ""
    }
    
    // Resets all fields sign up
    private func resetTextFields() {
        username = ""
        newEmail = ""
        newPassword = ""
    }
}

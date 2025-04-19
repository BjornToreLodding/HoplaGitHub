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
    @State private var allowStatistics = false
    @State private var isShowingStatsInfo = false
    @State private var showSignUpSuccessAlert = false
    
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login state
    
    //@StateObject private var viewModel = LoginViewModel()
    @ObservedObject var viewModel: LoginViewModel
    @ObservedObject var loginViewModel: LoginViewModel
    
    // Convenience computed property
        private var isSignUpFormValid: Bool {
            return
                !newEmail.isEmpty &&
                isValidEmail(newEmail) &&
                isValidPassword(newPassword) &&
                newPassword == confirmNewPassword &&
                allowStatistics
        }

    
    var body: some View {
        NavigationStack {
            ZStack {
                // Main background color
                Rectangle()
                    .fill(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                    .ignoresSafeArea() // Fill the entire screen
                
                //   GeometryReader { geometry in
                
                VStack {
                    VStack {
                        // Logo
                        Image("LogoUtenBakgrunn")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 170, height: 170)
                        
                        Text("Hopla")
                            .font(.system(size: 50, weight: .bold, design: .rounded))
                        
                        Spacer()
                    }
                    .padding(.top, 150)
                    
                    
                    
                    // MARK: - Text fields
                    
                    // Username Label & TextField
                    Text("Email")
                        .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        .padding(.top, 10)
                    
                    TextField("Enter your email", text: $email)
                        .padding()
                        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                        .cornerRadius(8)
                        .frame(width: 300)
                        .multilineTextAlignment(.center)
                    
                    // Password Label & SecureField
                    Text("Password")
                        .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                        .padding(.top, 20)
                    
                    SecureField("Enter your password", text: $password)
                        .padding()
                        .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
                        .cornerRadius(8)
                        .frame(width: 300)
                        .multilineTextAlignment(.center)
                    
                    
                    // MARK: - Forgotten password
                    VStack {
                        Text("Forgotten password?")
                            .frame(width: 360, height: 30)
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
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
                            
                            let normalizedEmail = email.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() // To clear whitespaces
                            
                            Button("Send") {
                                Task {
                                    await viewModel.resetPasswordRequest(email: normalizedEmail)
                                }
                                // Optionally, clear the email field and dismiss the sheet.
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
                        } else if !isValidEmail(email){
                            viewModel.errorMessage = "Enter a valid email!"
                        } else {
                            viewModel.login(email: email, password: password)
                        }
                    }) {
                        Text("Log In")
                            .foregroundColor(.white)
                            .padding()
                            .frame(width: 200, height: 50)
                            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                            .cornerRadius(8)
                    }
                    
                    .alert(isPresented: Binding<Bool>(
                        get: { viewModel.errorMessage != nil },
                        set: { _ in viewModel.errorMessage = nil }
                    )) {
                        Alert(title: Text("Login Failed"), message: Text(viewModel.errorMessage ?? ""), dismissButton: .default(Text("OK")))
                    }
                    .padding(.top, 30)
                    .navigationBarBackButtonHidden(true) // Hide the back arrow
                    
                    
                    // MARK: - Sign up
                    
                    VStack {
                        Text("Not a member? Sign up")
                            .frame(width: 360, height: 30)
                            .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                            .underline(true)
                            .padding(.top, 10)
                            .onTapGesture {
                                resetEmail()
                                isShowingSignUp = true
                            }
                    }
                    
                    Spacer()
                    
                        .sheet(isPresented: $isShowingSignUp, onDismiss: {
                                            // reset everything
                                            newEmail = ""
                                            newPassword = ""
                                            confirmNewPassword = ""
                                            allowStatistics = false
                                            passwordMismatchWarning = nil
                                        }) {
                                            VStack(spacing: 16) {
                                                Text("Register New Account")
                                                    .font(.headline)

                                                TextField("Enter email address", text: $newEmail)
                                                    .textFieldStyle(RoundedBorderTextFieldStyle())

                                                SecureField("Enter password", text: $newPassword)
                                                    .textFieldStyle(RoundedBorderTextFieldStyle())

                                                SecureField("Confirm password", text: $confirmNewPassword)
                                                    .textFieldStyle(RoundedBorderTextFieldStyle())

                                                Text("Password must be at least 9 characters and include 1 uppercase, 1 lowercase, 1 number, and 1 special symbol.")
                                                    .font(.caption)
                                                    .foregroundColor(.gray)
                                                    .multilineTextAlignment(.center)

                                                if let warning = passwordMismatchWarning {
                                                    Text(warning)
                                                        .foregroundColor(.red)
                                                        .font(.caption)
                                                        .multilineTextAlignment(.center)
                                                }

                                                // ——— checkbox + info ———
                                                HStack {
                                                    Button { allowStatistics.toggle() }
                                                    label: {
                                                        Image(systemName: allowStatistics ? "checkmark.square" : "square")
                                                    }

                                                    Text("Allow collection of statistics")
                                                        .font(.caption)

                                                    Spacer()

                                                    Button { isShowingStatsInfo = true }
                                                    label: {
                                                        Image(systemName: "questionmark.circle")
                                                    }
                                                    .alert("Statistics Collection", isPresented: $isShowingStatsInfo) {
                                                        Button("OK", role: .cancel) { }
                                                    } message: {
                                                        Text("We collect anonymous usage data to help improve Hopla—no personal info is ever shared.")
                                                    }
                                                }
                                                .padding(.horizontal)

                                                // ——— Join now ———
                                                Button {
                                                    // final sanity check
                                                    guard isValidPassword(newPassword) else {
                                                        passwordMismatchWarning = "Password doesn’t meet requirements."
                                                        return
                                                    }
                                                    guard newPassword == confirmNewPassword else {
                                                        passwordMismatchWarning = "Passwords do not match!"
                                                        return
                                                    }

                                                    viewModel.register(email: newEmail, password: newPassword) { success, message in
                                                        if success {
                                                            // dismiss + show confirmation
                                                            isShowingSignUp = false
                                                            showSignUpSuccessAlert = true
                                                        } else {
                                                            passwordMismatchWarning = message
                                                        }
                                                    }
                                                } label: {
                                                    Text("Join now")
                                                        .foregroundColor(.white)
                                                        .frame(width: 200, height: 50)
                                                        .background(
                                                            // conditional color
                                                            isSignUpFormValid
                                                                ? AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme)
                                                                : Color.gray
                                                        )
                                                        .cornerRadius(8)
                                                }
                                                .disabled(!isSignUpFormValid)

                                                Button("Cancel") {
                                                    isShowingSignUp = false
                                                }
                                                .padding(.top, 8)
                                            }
                                            .padding()
                                        }
                                        // ← show alert on main screen
                                        .alert("User created successfully", isPresented: $showSignUpSuccessAlert) {
                                            Button("OK", role: .cancel) { }
                                        } message: {
                                            Text("Please check your mail to validate your account.")
                                        }

                }
                .onAppear {
                    DispatchQueue.main.async {
                        if let token = TokenManager.shared.getToken(), !token.isEmpty {
                            viewModel.isLoggedIn = true
                        }
                    }
                }

                .frame(maxWidth: .infinity, alignment: .center) // Ensure content is centered
                .frame(maxHeight: .infinity) // Limit height to prevent overflow
                //  }
                //.padding(.bottom, 100)
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
    
    private func isValidEmail(_ email: String) -> Bool {
        let emailFormat = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let predicate = NSPredicate(format: "SELF MATCHES %@", emailFormat)
        return predicate.evaluate(with: email)
    }
    
    private func isValidPassword(_ password: String) -> Bool {
        // ≥9 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special
        let passwordFormat = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{9,}$"
        let predicate = NSPredicate(format: "SELF MATCHES %@", passwordFormat)
        return predicate.evaluate(with: password)
    }

}

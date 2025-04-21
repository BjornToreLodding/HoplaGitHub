//
//  Settings.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 31/01/2025.
//
import SwiftUI

struct Settings: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    @ObservedObject var loginViewModel: LoginViewModel
    @AppStorage("isDarkMode") private var isDarkMode = false
    @AppStorage("isLoggedIn") private var isLoggedIn = false // Track login status
    @AppStorage("isEnglishSelected") private var isEnglishSelected = false
    @Binding var navigationPath: NavigationPath // Receive path from ContentView
    
    // State to manage the showing of the alert
    @State private var showLogoutAlert = false
    @State private var showDeleteUserAlert = false
    @State private var showPasswordConfirmation = false
    @State private var password = "" // Store the entered password
    @FocusState private var isPasswordFieldFocused: Bool // To auto-focus password field
    @State private var showReportSheet = false // Report
    
    var viewModel: ProfileViewModel
   
    
    var body: some View {
        ZStack {
            // Ensure the whole background is green
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea(edges: .all)
            VStack(spacing: 0) {
                Text("Settings")
                    .font(.title)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, alignment: .center) // Aligns text to the right
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)
                
                NavigationView {
                    Form {
                        Section(header: Text(LocalizedStringKey("Display"))) {
                            Toggle(isOn: $isDarkMode) {
                                Text(LocalizedStringKey("Dark Mode"))
                            }
                        }
                        
                        Section(header: Text(LocalizedStringKey("The app must reload to apply language change"))) {
                            Toggle(isOn: $isEnglishSelected) {
                                Text("English")
                            }
                            .onChange(of: isEnglishSelected) { _ in
                                changeLanguage()
                            }
                        }
                        
                        // Send a Report Button
                        // Inside your Settings view's Form or VStack:
                        Button(action: {
                            showReportSheet = true
                        }) {
                            Text("Report an Issue")
                        }
                        .sheet(isPresented: $showReportSheet) {
                            ReportIssueView(showReportSheet: $showReportSheet)
                        }

                        
                        // Log out button
                        Button(action: {
                            // Show the logout confirmation alert
                            showLogoutAlert = true
                        }) {
                            Text("Log Out")
                                .foregroundColor(.red)
                        }
                        .alert(isPresented: $showLogoutAlert) {
                            Alert(
                                title: Text("Are you sure you want to log out?"),
                                message: Text("You will be logged out of your account."),
                                primaryButton: .destructive(Text("Log Out")) {
                                    // Log Out Section
                                    NavigationLink(destination: Login(viewModel: LoginViewModel(), loginViewModel: LoginViewModel())) {
                                        Text("Log Out")
                                            .foregroundColor(.red)
                                    }
                                    isLoggedIn = false
                                },
                                secondaryButton: .cancel()
                            )
                        }

                        
                        // Delete User Button
                        Button(action: {
                            showDeleteUserAlert = true
                        }) {
                            Text("Delete User")
                                .foregroundColor(.red)
                        }
                        .alert("Are you sure you want to delete your user?", isPresented: $showDeleteUserAlert) {
                            Button("Cancel", role: .cancel) {}
                            
                            Button("Continue", role: .destructive) {
                                showPasswordConfirmation = true
                            }
                        } message: {
                            Text("This action cannot be undone.")
                        }
                    }
                    .background(AdaptiveColor.background.color(for: colorScheme)) // Set Form background
                    .scrollContentBackground(.hidden) // Hide default Form background
                    .foregroundColor(AdaptiveColor.text.color(for: colorScheme))
                    .sheet(isPresented: $showPasswordConfirmation) {
                        PasswordConfirmationView(password: $password,
                                                 isLoggedIn: $isLoggedIn,
                                                 showPasswordConfirmation: $showPasswordConfirmation,
                                                 loginViewModel: loginViewModel)
                    }

                }
                .navigationBarBackButtonHidden(true) // Hides the default back button
                .preferredColorScheme(isDarkMode ? .dark : .light)
            }
            // MARK: - Custom Back Button
            VStack {
                HStack {
                    Button(action: {
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "arrow.left")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 30, height: 30)
                            .foregroundColor(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                    }
                    .position(x: 25, y: 20)
                    
                    Spacer()
                }
                Spacer()
            }
        }
        .onAppear {
            setupNavigationBar(forDarkMode: isDarkMode)
            setupTabBarAppearance(forDarkMode: isDarkMode)
        }
    }
    
    
    
    // To change language
    private func changeLanguage() {
        let languageCode = isEnglishSelected ? "English" : "nb_NO"
        UserDefaults.standard.set([languageCode], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
        exit(0) // Restart app to apply language change
    }
    
    
    // Custom Password Confirmation Popup
    struct PasswordConfirmationView: View {
        @Binding var password: String
        @Binding var isLoggedIn: Bool
        @Binding var showPasswordConfirmation: Bool
        @FocusState private var isPasswordFieldFocused: Bool
        
        // Accept the loginViewModel to perform deletion.
        @ObservedObject var loginViewModel: LoginViewModel

        var body: some View {
            VStack(spacing: 20) {
                Text("Confirm Deletion")
                    .font(.headline)
                
                SecureField("Enter your password", text: $password)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(8)
                    .focused($isPasswordFieldFocused)
                
                Button("Delete User", role: .destructive) {
                    verifyPasswordAndDelete()
                }
                .padding()
                .disabled(password.isEmpty)
                
                Button("Cancel") {
                    showPasswordConfirmation = false
                }
                .padding()
            }
            .padding()
            .onAppear {
                isPasswordFieldFocused = true
            }
        }
        
        // Instead of static checking, we call deleteUser on the view model.
        private func verifyPasswordAndDelete() {
            Task {
                await loginViewModel.deleteUser(password: password)
                if loginViewModel.errorMessage == nil {
                    // On successful deletion, mark user as logged out and dismiss the sheet.
                    isLoggedIn = false
                    showPasswordConfirmation = false
                }
            }
        }
    }

    
    
    // Custom Report Issue Form
    struct ReportIssueView: View {
        @Binding var showReportSheet: Bool
        @State private var reportTopic: String = ""
        @State private var reportDescription: String = ""
        @StateObject private var reportViewModel = ReportViewModel()
        
        var body: some View {
            NavigationView {
                VStack {
                    Text("Report an Issue")
                        .font(.headline)
                        .padding()
                    
                    // Topic field (e.g., report category)
                    TextField("Enter topic", text: $reportTopic)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(8)
                        .padding(.horizontal)
                    
                    // Description field (report message)
                    TextEditor(text: $reportDescription)
                        .frame(height: 150)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(8)
                        .padding(.horizontal)
                    
                    // Display potential errors or success messages:
                    if let error = reportViewModel.errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .padding(.horizontal)
                    }
                    if let success = reportViewModel.successMessage {
                        Text(success)
                            .foregroundColor(.green)
                            .padding(.horizontal)
                    }
                    
                    HStack {
                        Button("Cancel") {
                            showReportSheet = false
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(10)
                        
                        Button("Submit") {
                            Task {
                                // Ensure both topic and description are filled.
                                guard !reportTopic.isEmpty, !reportDescription.isEmpty else {
                                    reportViewModel.errorMessage = "Please fill out both fields."
                                    return
                                }
                                
                                // Use TokenManager to get the current user's profile ID:
                                guard let entityId = TokenManager.shared.getUserId() else {
                                    reportViewModel.errorMessage = "Unable to retrieve profile ID."
                                    return
                                }
                                
                                // For now, EntityName is "Profile". In future, you can adapt this.
                                await reportViewModel.submitReport(entityId: entityId,
                                                                   entityName: "Profile",
                                                                   category: reportTopic,
                                                                   message: reportDescription)
                                // Close the sheet on success:
                                if reportViewModel.successMessage != nil {
                                    showReportSheet = false
                                }
                            }
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                        .disabled(reportTopic.isEmpty || reportDescription.isEmpty)
                    }
                    .padding()
                }
                .navigationBarTitle("Send a Report", displayMode: .inline)
            }
        }
    }
}


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
    
    @ObservedObject var viewModel: LoginViewModel
    
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
                    .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
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
                                    NavigationLink(destination: Login(viewModel: viewModel, loginViewModel: LoginViewModel())) {
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
                        PasswordConfirmationView(password: $password, isLoggedIn: $isLoggedIn, showPasswordConfirmation: $showPasswordConfirmation)
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
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                    .position(x: 25, y: 20) // Adjust for exact placement
                    
                    Spacer()
                }
                Spacer()
            }
        }
        .onAppear {
            setupNavigationBar(for: colorScheme)
            setupTabBarAppearance(for: colorScheme)
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
                    verifyPassword()
                }
                .padding()
                .disabled(password.isEmpty) // Disable button if password is empty
                
                Button("Cancel") {
                    showPasswordConfirmation = false
                }
                .padding()
            }
            .padding()
            .onAppear {
                isPasswordFieldFocused = true // Auto-focus the password field
            }
        }
        
        // Function to verify the password
        private func verifyPassword() {
            let correctPassword = "test" // Replace with actual password check
            if password == correctPassword {
                isLoggedIn = false // Log out the user
                showPasswordConfirmation = false // Close the popup
            } else {
                print("Something went wrong...")
            }
        }
    }
    
    
    // Custom Report Issue Form
    struct ReportIssueView: View {
        @Binding var showReportSheet: Bool
        @State private var reportTopic: String = ""
        @State private var reportDescription: String = ""
        
        var body: some View {
            NavigationView {
                VStack {
                    Text("Report an Issue")
                        .font(.headline)
                        .padding()
                    
                    // Topic TextField
                    TextField("Enter topic", text: $reportTopic)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(8)
                        .padding(.horizontal)
                    
                    // Description TextEditor
                    TextEditor(text: $reportDescription)
                        .frame(height: 150)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(8)
                        .padding(.horizontal)
                    
                    HStack {
                        Button("Cancel") {
                            showReportSheet = false
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(10)
                        
                        Button("Submit") {
                            submitReport()
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                        .disabled(reportTopic.isEmpty || reportDescription.isEmpty) // Disable if empty
                    }
                    .padding()
                }
                .navigationBarTitle("Send a Report", displayMode: .inline)
            }
        }
        
        // Function to handle submission
        private func submitReport() {
            // Replace with actual email
            print("Report submitted: \(reportTopic) - \(reportDescription)")
            showReportSheet = false // Close the sheet
        }
    }
}

#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

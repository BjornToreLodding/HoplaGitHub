import SwiftUI

struct ChangePassword: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var isShowingSheet = false // When clicking on text
    @State private var oldPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var localErrorMessage = ""
    
    // We inject the view model so that we can call changePassword.
    @ObservedObject var loginViewModel: LoginViewModel

    var body: some View {
        VStack {
            Text("Change password")
                .font(.custom("ArialNova", size: 16))
                .frame(width: 360, height: 30)
                .foregroundColor(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                .underline(true)
                .padding(.top, 10)
                .onTapGesture {
                    resetFields()  // Reset fields
                    isShowingSheet = true  // Show change password sheet
                }
        }
        .sheet(isPresented: $isShowingSheet, onDismiss: {
            resetFields()
        }) {
            VStack(spacing: 20) {
                Text("Change Password")
                    .font(.headline)
                
                SecureField("Enter old password", text: $oldPassword)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
                
                SecureField("Enter new password", text: $newPassword)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
                
                SecureField("Confirm new password", text: $confirmPassword)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding()
                
                if !localErrorMessage.isEmpty {
                    Text(localErrorMessage)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                Button("Submit") {
                    if newPassword == confirmPassword {
                        // Call the changePassword async method.
                        Task {
                            await loginViewModel.changePassword(oldPassword: oldPassword,
                                                                newPassword: newPassword,
                                                                confirmPassword: confirmPassword)
                            // Optionally, you could check if errorMessage is still nil
                            if loginViewModel.errorMessage == nil {
                                isShowingSheet = false
                            } else {
                                // Show the error message in this view as well.
                                localErrorMessage = loginViewModel.errorMessage ?? "Unknown error"
                            }
                        }
                    } else {
                        localErrorMessage = "New passwords do not match!"
                    }
                }
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
                
                Button("Cancel") {
                    isShowingSheet = false
                }
                .padding()
            }
            .padding()
        }
    }
    
    // Helper: resets all input fields
    private func resetFields() {
        oldPassword = ""
        newPassword = ""
        confirmPassword = ""
        localErrorMessage = ""
    }
}

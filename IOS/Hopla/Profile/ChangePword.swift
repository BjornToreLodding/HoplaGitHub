import SwiftUI

struct ChangePassword: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var isShowingSheet = false // True when clicking on text
    @State private var oldPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var errorMessage = ""
    
    var body: some View {
        VStack {
            Text("Change password")
                .font(.custom("ArialNova", size: 16))
                .frame(width: 360, height: 30)
                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                .underline(true)
                .padding(.top, 10)
                .onTapGesture { // When clicking on change password
                    resetFields() // Reset fields when opening
                    isShowingSheet = true // Show sheet
                }
        }
        .sheet(isPresented: $isShowingSheet, onDismiss: {
            resetFields() // Reset fields when closing
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
                
                if !errorMessage.isEmpty {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                Button("Submit") {
                    if newPassword == confirmPassword {
                        // Here you would add authentication to verify old password and update the new one
                        print("Password changed successfully!")
                        isShowingSheet = false
                    } else {
                        errorMessage = "New passwords do not match!"
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
    
    // Resets all input fields
    private func resetFields() {
        oldPassword = ""
        newPassword = ""
        confirmPassword = ""
        errorMessage = ""
    }
}

#Preview {
    ChangePassword()
}

import SwiftUI

class UserAuthViewModel: ObservableObject {
    @AppStorage("isLoggedIn") var isLoggedIn = false
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var errorMessage: String?

    func login() {
        let validEmail = "test"
        let validPassword = "test"

        if email == validEmail && password == validPassword {
            isLoggedIn = true
            errorMessage = nil
        } else {
            errorMessage = "Invalid email or password."
        }
    }

    func logout() {
        isLoggedIn = false
        email = ""
        password = ""
    }
}

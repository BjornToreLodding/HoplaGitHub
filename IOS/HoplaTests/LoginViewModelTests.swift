//
//  LoginViewModelTests.swift
//  HoplaTests
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//

import XCTest
import Combine
@testable import Hopla

final class LoginViewModelTests: XCTestCase {
    var viewModel: LoginViewModel!
    var cancellables = Set<AnyCancellable>()
    
    override func setUp() {
        super.setUp()
        // 1) Make an “ephemeral” config so nothing hits disk
        let config = URLSessionConfiguration.ephemeral
        // 2) Tell it to use our MockURLProtocol
        config.protocolClasses = [MockURLProtocol.self]
        // 3) Build the session and inject
        let session = URLSession(configuration: config)
        viewModel = LoginViewModel(session: session)
    }
    
    override func tearDown() {
        // clean up any stubs so tests don’t bleed into each other
        MockURLProtocol.stubResponses = [:]
        cancellables.removeAll()
        viewModel = nil
        super.tearDown()
    }
    
    func testLoginSuccess() {
        // Prepare a fake 200-OK JSON response
        let loginJSON = """
        {
          "token":"abc123",
          "userId":"u-001",
          "name":"Ane",
          "alias":null,
          "pictureUrl":null,
          "telephone":null,
          "description":"Hello!",
          "dob":null,
          "redirect":null
        }
        """.data(using: .utf8)!
        
        let loginURL = URL(string: "https://hopla.onrender.com/users/login/")!
        MockURLProtocol.stubResponses = [
          loginURL: (
            200,
            loginJSON,          // Data?
            nil as Error?       // Error?
          )
        ]
        
        let exp = expectation(description: "login succeeds and sets isLoggedIn = true")
        
        viewModel.$isLoggedIn
          .dropFirst()              // ignore the initial `false`
          .sink { isLogged in
            XCTAssertTrue(isLogged, "Expected isLoggedIn to flip to true on 200")
            exp.fulfill()
          }
          .store(in: &cancellables)
        
        viewModel.login(email: "test@example.com", password: "secret")
        
        wait(for: [exp], timeout: 1.0)
    }
    
    func testLoginUnauthorized() {
        // Stub a 401 Unauthorized
        let loginURL = URL(string: "https://hopla.onrender.com/users/login/")!
        MockURLProtocol.stubResponses = [
          loginURL: (
            401,
            Data("Unauthorized".utf8),
            nil as Error?
          )
        ]
        
        let exp = expectation(description: "login fails with 401 and sets errorMessage")
        
        viewModel.$errorMessage
          .dropFirst()              // ignore the initial `nil`
          .sink { msg in
            XCTAssertEqual(msg, "Invalid email or password")
            exp.fulfill()
          }
          .store(in: &cancellables)
        
        viewModel.login(email: "bad@example.com", password: "wrong")
        
        wait(for: [exp], timeout: 1.0)
    }
}

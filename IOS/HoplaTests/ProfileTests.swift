//
//  ProfileViewModelTests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/05/2025.
//


import XCTest
@testable import Hopla

final class ProfileTests: XCTestCase {
    var viewModel: ProfileViewModel!
    var session: URLSession!
    
    override func setUp() {
        super.setUp()
        // Reset any previous stubs
        MockURLProtocol.stubResponses = [:]
        
        // Make a URLSession that uses your mock protocol
        let config = URLSessionConfiguration.ephemeral
        config.protocolClasses = [MockURLProtocol.self]
        let session = URLSession(configuration: config)
        
        viewModel = ProfileViewModel(session: session)
    }
    
    override func tearDown() {
        MockURLProtocol.stubResponses = [:]
        session = nil
        viewModel = nil
        super.tearDown()
    }
    
    func testFetchUserProfile_Success() async throws {
        // Prepare a fake profile JSON
        let stubUser = UserProfile(
            alias: "jane_doe", name: "Jane Doe",
            email: "jane@example.com",
            pictureUrl: nil, telephone: nil,
            description: nil, dob: nil
        )
        let data = try JSONEncoder().encode(stubUser)
        let profileURL = URL(string: "https://hopla.onrender.com/users/profile")!
        
        // Map URL → (statusCode, Data?, Error?)
        MockURLProtocol.stubResponses[profileURL] = (200, data, nil)
        
        let exp = expectation(description: "Profile published")
        exp.expectedFulfillmentCount = 1
        
        // Observe when userProfile becomes non‐nil
        let cancellable = viewModel.$userProfile.sink { profile in
            if profile != nil {
                exp.fulfill()
            }
        }
        
        await viewModel.fetchUserProfile()
        
        // Wait up to 1 second for DispatchQueue.main.async to fire
        wait(for: [exp], timeout: 1.0)
        cancellable.cancel()
        
        XCTAssertEqual(viewModel.userProfile?.email, "jane@example.com")
        XCTAssertEqual(viewModel.draftProfile?.alias, "jane_doe")
    }
    
    
    func testUpdateUserInfo_Success() async {
        // Stub the update endpoint with a 204 No Content
        let updateURL = URL(string: "https://hopla.onrender.com/users/update")!
        MockURLProtocol.stubResponses[updateURL] = (204, nil, nil)
        
        // Give the viewModel a draft to send
        viewModel.draftProfile = UserProfile(
            alias: "a", name: "b", email: "c@d.com",
            pictureUrl: nil, telephone: nil,
            description: nil, dob: nil
        )
        
        await viewModel.updateUserInfo(
            token: "token",
            userId: "user-id",
            loginVM: LoginViewModel()
        )
        
        // If fetchUserProfile() is also called under the hood,
        // stub that too, or assert no crash.
    }
}

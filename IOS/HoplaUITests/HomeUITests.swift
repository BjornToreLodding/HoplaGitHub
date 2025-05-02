//
//  HomeUITests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/05/2025.
//
import XCTest

// Testing Home file
final class HomeUITests: XCTestCase {
    var app: XCUIApplication!
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        
        app = XCUIApplication()
        app.launchArguments += ["-UITestMode"]
        app.launchEnvironment = [
            "MOCK_POST_ALIAS": "jane_doe",
            "MOCK_POST_TITLE": "Hello World",
            "MOCK_POST_DESC": "This is a stub post.",
            "MOCK_POST_LIKES": "5"
        ]
        app.launch()
    }
    
    // Testing to display feed and like/unlike
    func testFeedDisplaysAndLikeToggles() {
        // 1) Wait for the home screen to appear
        let homeRoot = app.otherElements["homeScreenRoot"]
        XCTAssertTrue(homeRoot.waitForExistence(timeout: 5))
        
        // 2) Locate the stub post
        let postId   = "stub-1"
        let titleLbl = homeRoot.staticTexts["homepost_\(postId)_title_label"]
        let descLbl  = homeRoot.staticTexts["homepost_\(postId)_description_label"]
        let likeBtn  = homeRoot.buttons["homepost_\(postId)_like_button"]
        let countLbl = homeRoot.staticTexts["homepost_\(postId)_likes_label"]
        
        // 3) Assert initial state
        XCTAssertTrue(titleLbl.exists)
        XCTAssertEqual(titleLbl.label, "Hello World")
        XCTAssertTrue(descLbl.exists)
        XCTAssertEqual(descLbl.label, "This is a stub post.")
        XCTAssertEqual(countLbl.label, "5")
        XCTAssertEqual(likeBtn.value as? String, "not_liked")
        
        // 4) Tap like
        likeBtn.tap()
        
        // 5) After tap, count increments and accessibilityValue flips
        XCTAssertEqual(countLbl.label, "6")
        XCTAssertEqual(likeBtn.value as? String, "liked")
    }
}

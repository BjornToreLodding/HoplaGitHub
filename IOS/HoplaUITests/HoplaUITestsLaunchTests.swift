//
//  HoplaUITestsLaunchTests.swift
//  HoplaUITests
//
//  Created by Ane Marie Johnsen on 21/01/2025.
//
import XCTest

// A simple UI‐launch smoke test. It verifies that the app can start up
// under various UI configurations and captures a screenshot on launch.
final class HoplaUITestsLaunchTests: XCTestCase {
    
    // Run this test for each target application UI configuration (e.g. light/dark mode, different interface styles)
    override class var runsForEachTargetApplicationUIConfiguration: Bool {
        true
    }
    
    // Called before each test method in the class; disable further steps on failure
    override func setUpWithError() throws {
        continueAfterFailure = false
    }
    
    // A simple launch test that starts the app and takes a screenshot of the initial UI
    @MainActor
    func testLaunch() throws {
        let app = XCUIApplication()
        app.launch()  // Launch the Hopla app
        
        // Capture a screenshot of the launch screen
        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Launch Screen"
        attachment.lifetime = .keepAlways  // Keep attachment regardless of test success
        
        add(attachment)  // Add the screenshot to the test report
    }
}

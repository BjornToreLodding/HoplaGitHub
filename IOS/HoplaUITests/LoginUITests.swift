//
//  HoplaUITests.swift
//  HoplaUITests
//
//  Created by Ane Marie Johnsen on 21/01/2025.
//
import XCTest

// Login page tests
final class LoginUITests: XCTestCase {
    var app: XCUIApplication!
    
    override func setUpWithError() throws {
        continueAfterFailure = false
    }
    
    override func tearDownWithError() throws {
        // only terminate if we've actually created/assigned `app`
        app?.terminate()
        app = nil
    }
    
    // Login to home screen test
    @MainActor
    func testSuccessfulLogin_flowsToHomeScreen() {
        app = XCUIApplication()
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launchEnvironment["UITEST_MODE"] = "true"
        app.launch()
        
        let emailField = app.textFields["EmailField"]
        XCTAssertTrue(emailField.waitForExistence(timeout: 2), "EmailField didn’t appear")
        emailField.tap()
        emailField.typeText("valid@user.com")
        app.swipeUp()
        
        let passwordField = app.secureTextFields["PasswordField"]
        XCTAssertTrue(passwordField.waitForExistence(timeout: 2), "PasswordField didn’t appear")
        passwordField.tap()
        passwordField.typeText("CorrectHorseBatteryStaple1!")
        app.swipeUp()
        
        let loginButton = app.buttons["loginButton"]
        XCTAssertTrue(loginButton.waitForExistence(timeout: 1), "loginButton not found")
        loginButton.tap()
        
        // This should pass immediately under UITEST_MODE
        let homeRoot = app.otherElements["homeScreenRoot"]
        XCTAssertTrue(homeRoot.waitForExistence(timeout: 3), "Home screen never appeared")
    }
    
    // Show error alert when empty fields
    @MainActor
    func testEmptyCredentials_showsErrorAlert() {
        app = XCUIApplication()
        // still reset, even though we’re not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        // make sure the email & password fields exist:
        let emailField = app.textFields["EmailField"]
        XCTAssertTrue(emailField.waitForExistence(timeout: 1))
        XCTAssertEqual(emailField.placeholderValue, "Email")
        
        let passwordField = app.secureTextFields["PasswordField"]
        XCTAssertTrue(passwordField.waitForExistence(timeout: 1))
        XCTAssertEqual(passwordField.placeholderValue, "Password")
        
        // scroll up so the button is hittable
        app.swipeUp()
        
        let loginButton = app.buttons["loginButton"]
        XCTAssertTrue(loginButton.waitForExistence(timeout: 1), "loginButton not found")
        loginButton.tap()
        
        // now the real login logic runs and you get an alert
        let alert = app.alerts["Login Failed"]
        XCTAssertTrue(alert.waitForExistence(timeout: 2))
        XCTAssertEqual(
            alert.staticTexts.element(boundBy: 1).label,
            "Email and password cannot be empty!"
        )
        alert.buttons["OK"].tap()
    }
    
    // Test invalid email
    @MainActor
    func testInvalidEmail_showsErrorAlert() {
        app = XCUIApplication()
        // still reset, even though we’re not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        let emailField = app.textFields["EmailField"]
        XCTAssertTrue(emailField.waitForExistence(timeout: 2))
        emailField.tap()
        emailField.typeText("invalid-email")
        
        // scroll the form up so PasswordField comes into view
        app.swipeUp()
        
        let passwordField = app.secureTextFields["PasswordField"]
        XCTAssertTrue(passwordField.waitForExistence(timeout: 2))
        passwordField.tap()
        passwordField.typeText("Password123!")
        
        // swipe up again before tapping Log In:
        app.swipeUp()
        
        app.buttons["loginButton"].tap()
        
        let alert = app.alerts["Login Failed"]
        XCTAssertTrue(alert.waitForExistence(timeout: 2))
        XCTAssertEqual(alert.staticTexts.element(boundBy: 1).label,
                       "Enter a valid email!")
        alert.buttons["OK"].tap()
    }
    
    // Forgot Password Sheet
    @MainActor
    func testForgotPassword_sendsResetRequest() {
        app = XCUIApplication()
        // still reset, even though not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        // 1) Open the sheet
        app.staticTexts["Forgotten password?"].tap()
        
        // 2) Wait for and fill the email field
        let resetField = app.textFields["newUserEmailField"]
        XCTAssertTrue(resetField.waitForExistence(timeout: 2))
        resetField.tap()
        resetField.typeText("user@example.com")
        
        // 3) Tap Send
        app.buttons["SendResetPasswordButton"].tap()
        
        // 4) Wait for the sheet (and its text-field) to disappear
        XCTAssertFalse(
            resetField.waitForExistence(timeout: 2),
            "Forgot-password sheet didn’t dismiss after tapping Send"
        )
    }
    
    // Cancel the "forgot password" sheet
    @MainActor
    func testForgotPassword_cancelDismissesSheet() {
        app = XCUIApplication()
        // still reset, even though we’re not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        // 1) Bring up the sheet
        app.staticTexts["Forgotten password?"].tap()
        
        // 2) Wait for the field to appear
        let resetField = app.textFields["newUserEmailField"]
        XCTAssertTrue(resetField.waitForExistence(timeout: 2))
        
        // 3) Tap Cancel
        app.buttons["CancelResetPasswordButton"].tap()
        
        // 4) Wait *for the field to disappear*
        let disappears = NSPredicate(format: "exists == false")
        expectation(for: disappears, evaluatedWith: resetField, handler: nil)
        waitForExpectations(timeout: 2)
    }
    
    // Sign Up Sheet
    @MainActor
    func testSignUpSheet_uiElementsExist() {
        app = XCUIApplication()
        // still reset, even though we’re not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        app.staticTexts["Not a member? Sign up"].tap()
        
        let emailField = app.textFields["signUpEmailField"]
        let passwordField = app.secureTextFields["signUpPasswordField"]
        let confirmField = app.secureTextFields["signUpConfirmPasswordField"]
        let joinButton = app.buttons["Join now"]
        let cancelButton = app.buttons["cancelSignUp"]
        
        XCTAssertTrue(emailField.waitForExistence(timeout: 2))
        XCTAssertTrue(passwordField.exists)
        XCTAssertTrue(confirmField.exists)
        XCTAssertTrue(joinButton.exists)
        XCTAssertTrue(cancelButton.exists)
        
        cancelButton.tap()
        XCTAssertFalse(emailField.exists)
    }
    
    // Test different passwords
    @MainActor
    func testSignUpPasswordMismatchWarning() {
        // 1) Launch fresh
        app = XCUIApplication()
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        // 2) Open the sheet
        app.staticTexts["Not a member? Sign up"].tap()
        
        // 3) Fill in the e-mail
        let emailField = app.textFields["signUpEmailField"]
        XCTAssertTrue(emailField.waitForExistence(timeout: 2))
        emailField.tap()
        emailField.typeText("user@example.com")
        
        // 4) Fill in the password
        let pwField = app.secureTextFields["signUpPasswordField"]
        XCTAssertTrue(pwField.waitForExistence(timeout: 2))
        pwField.tap()
        pwField.typeText("Password1!")
        
        // 5) Fill in the different confirmation
        let confirmField = app.secureTextFields["signUpConfirmPasswordField"]
        XCTAssertTrue(confirmField.waitForExistence(timeout: 2))
        confirmField.tap()
        confirmField.typeText("Different1!")
        
        // 6) Explicitly dismiss the keyboard
        if app.keyboards.buttons["Return"].exists {
            app.keyboards.buttons["Return"].tap()
        } else if app.keyboards.buttons["Done"].exists {
            app.keyboards.buttons["Done"].tap()
        } else {
            // fallback tap outside
            app.tap()
        }
        
        // 7) Scroll so the stats checkbox is visible
        app.swipeUp()
        
        // 8) Now toggle the checkbox
        let statsToggle = app.buttons["statisticsCheckbox"]
        XCTAssertTrue(statsToggle.waitForExistence(timeout: 2))
        statsToggle.tap()
        
        // 9) Scroll again to bring the “Join now” button into view
        app.swipeUp()
        
        // 10) Wait for “Join now” to appear and actually become enabled
        let join = app.buttons["JoinNowButton"]
        XCTAssertTrue(join.waitForExistence(timeout: 2), "Join button never appeared")
        
        let enabled = NSPredicate(format: "isEnabled == true")
        expectation(for: enabled, evaluatedWith: join, handler: nil)
        waitForExpectations(timeout: 2)
        
        XCTAssertTrue(join.isEnabled, "Join button should be enabled when form is valid")
        join.tap()
        
        // 11) Finally, assert that the mismatch warning shows up
        let warning = app.staticTexts["PasswordMismatchWarning"]
        XCTAssertTrue(warning.waitForExistence(timeout: 2),
                      "Expected the “Passwords do not match!” warning to appear")
    }
    
    // Test stats alert description
    @MainActor
    func testStatsInfo_alertDescription() {
        app = XCUIApplication()
        // still reset, even though we’re not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        app.staticTexts["Not a member? Sign up"].tap()
        
        let infoButton = app.buttons.matching(identifier: "questionmark.circle").firstMatch
        XCTAssertTrue(infoButton.exists)
        infoButton.tap()
        
        let alert = app.alerts["Statistics Collection"]
        XCTAssertTrue(alert.waitForExistence(timeout: 2))
        XCTAssertTrue(alert.staticTexts["We collect usage data to help improve Hopla"].exists)
        alert.buttons["OK"].tap()
    }
    
    // Test join button when form is invalid
    @MainActor
    func testJoinButtonDisabledWhenFormInvalid() {
        app = XCUIApplication()
        // still reset, even though we’re not logged in yet
        app.launchArguments = ["-UITest_ResetAuthentication"]
        app.launch()
        
        app.staticTexts["Not a member? Sign up"].tap()
        
        let joinButton = app.buttons["Join now"]
        XCTAssertFalse(joinButton.isEnabled)
    }
    
    // Performance
    @MainActor
    func testLaunchPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                XCUIApplication().launch()
            }
        }
    }
}

//
//  ProfileUITests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/05/2025.
//
import XCTest

// Testing Profile file
final class ProfileUITests: XCTestCase {
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments += ["-UITestMode"]
        app.launchEnvironment = [
            "MOCK_USER_ALIAS": "jane_doe",
            "MOCK_USER_NAME":  "Jane Doe",
            "MOCK_USER_EMAIL": "jane@example.com"
        ]
        app.launch()
    }
    
    // Test edit alias
    func testEditAliasField() {
        app.tabBars.buttons["Profile"].tap()
        
        let aliasLabel = app.staticTexts["profile_alias_label"]
        XCTAssertTrue(aliasLabel.waitForExistence(timeout: 2))
        
        XCTAssertEqual(aliasLabel.label, "jane_doe")
        
        // now the edit button exists
        let editButton = app.buttons["profile_alias_edit_button"]
        XCTAssertTrue(editButton.exists)
        editButton.tap()
        
        let aliasField = app.textFields["profile_alias_textField"]
        XCTAssertTrue(aliasField.exists)
        aliasField.clearAndEnterText("new_alias")
        
        let saveButton = app.buttons["profile_alias_save_button"]
        XCTAssertTrue(saveButton.exists)
        saveButton.tap()
        
        // verify update
        XCTAssertEqual(aliasLabel.label, "new_alias")
    }
}

// Helper to clear existing text and type new text
extension XCUIElement {
    func clearAndEnterText(_ text: String) {
        tap()
        guard let stringValue = value as? String else {
            XCTFail("Failed to get current text value")
            return
        }
        // send delete for each character
        let deleteString = stringValue.map { _ in XCUIKeyboardKey.delete.rawValue }.joined()
        typeText(deleteString)
        typeText(text)
    }
}

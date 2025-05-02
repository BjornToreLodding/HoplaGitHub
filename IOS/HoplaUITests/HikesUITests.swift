//
//  HikesUITests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 29/04/2025.
//
import XCTest

// Testing Hikes file
final class HikesUITests: XCTestCase {
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments = ["-UITest_ResetData"]
        app.launch()
    }
    
    override func tearDown() {
        app.terminate()
        super.tearDown()
    }
    
    // Testing searching for hikes
    @MainActor
    func testSearchingFiltersList() {
        // 1) Focus the search field
        let search = app.textFields["HikesSearchField"]
        XCTAssertTrue(search.waitForExistence(timeout: 2))
        search.tap()
        
        // 2) Type a query that matches only one hike
        search.typeText("Everest\n")   // press Return
        
        // 3) Assert the list shows exactly one card
        let cards = app.otherElements.matching(identifier: "HikeCard_")
        XCTAssertEqual(cards.count, 1, "Search should narrow the list to one hike")
    }
}

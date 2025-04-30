//
//  HikesUITests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 29/04/2025.
//

import XCTest

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
    
    // MARK: – Search Flow
    
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
    
    // MARK: – Pagination
    
    @MainActor
    func testScrollLoadsMoreHikes() {
        // 1) Wait for initial load
        let firstCard = app.otherElements["HikeCard_0"]
        XCTAssertTrue(firstCard.waitForExistence(timeout: 5))
        
        // 2) Scroll to bottom
        let list = app.scrollViews.firstMatch
        list.swipeUp()
        list.swipeUp()
        
        // 3) Expect the loading spinner, then a new card
        let spinner = app.activityIndicators["HikesLoadingSpinner"]
        XCTAssertTrue(spinner.waitForExistence(timeout: 2))
        let newCard = app.otherElements["HikeCard_20"]
        XCTAssertTrue(newCard.waitForExistence(timeout: 5))
    }
    
    // MARK: – Map/List Toggle
    
    @MainActor
    func testTogglingMapView() {
        let toggle = app.buttons["HikesToggleMapButton"]
        XCTAssertTrue(toggle.exists)
        toggle.tap()
        
        // Should show map, not list
        XCTAssertTrue(app.otherElements["HikesMapView"].waitForExistence(timeout: 2))
        XCTAssertFalse(app.otherElements["HikeCard_0"].exists)
    }
    
    // MARK: – Favorite a Hike
    
    @MainActor
    func testFavoriteButtonTogglesHeart() {
      // 1) Launch & stub‐login
      app = XCUIApplication()
      app.launchArguments = ["-UITest_ResetAuthentication"]
      app.launchEnvironment["UITEST_MODE"] = "true"
      app.launch()

      // 2) Tap “Log In” (this shortcut immediately sets isLoggedIn = true)
      let login = app.buttons["loginButton"]
      XCTAssertTrue(login.waitForExistence(timeout: 2), "Login button never showed")
      login.tap()
        
        // 3) Switch to the “Hikes” tab
        let hikesTab = app.tabBars.buttons["Hikes"]
        XCTAssertTrue(hikesTab.waitForExistence(timeout: 2), "Hikes tab never appeared")
        hikesTab.tap()

      // 4) Now you’re on the Hikes view — wait for a card to appear
      let card = app.otherElements
        .matching(NSPredicate(format: "identifier BEGINSWITH %@", "HikeCard_"))
        .element(boundBy: 0)
      XCTAssertTrue(card.waitForExistence(timeout: 5), "No hike cards appeared")

      // 5) Grab its heart button and assert toggle
      let hikeID = String(card.identifier.dropFirst("HikeCard_".count))
      let heart = card.buttons["FavoriteButton_\(hikeID)"]
      XCTAssertTrue(heart.exists, "Heart button not found")
      XCTAssertEqual(heart.value as? String, "false")
      heart.tap()
      XCTAssertEqual(heart.value as? String, "true")
    }
}

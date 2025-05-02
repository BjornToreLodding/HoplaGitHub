//
//  ViewControllerTests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//
import XCTest
@testable import Hopla

// Testing ViewController file
final class ViewControllerTests: XCTestCase {
    var viewc: ViewController!
    var session: URLSession!
    
    override func setUp() {
        super.setUp()
        
        // 1) Prepare the mock session
        let config = URLSessionConfiguration.ephemeral
        config.protocolClasses = [TestURLProtocol.self]
        session = URLSession(configuration: config)
        
        // 2) Instantiate & inject
        viewc = ViewController()
        viewc.session = session
        
        // 3) Give a token so saveHike() doesn’t return early
        TokenManager.shared.saveToken("dummy")
    }
    
    override func tearDown() {
        TestURLProtocol.lastRequest = nil
        TestURLProtocol.stubResponseData = nil
        TestURLProtocol.requestExpectation = nil
        session = nil
        viewc = nil
        super.tearDown()
    }
    
    // Testing to save a hike
    func testSaveHike_sendsCorrectPOST() {
        // 4) Set up state
        viewc.distance = 1.23
        viewc.elapsedTime = 75              // 1m15s
        viewc.coordinates = [
            Coordinate(lat: 10, long: 20, timestamp: 1000),
            Coordinate(lat: 10.1, long: 20.1, timestamp: 1005)
        ]
        
        // 5) Stub out the network
        TestURLProtocol.stubStatusCode = 200
        TestURLProtocol.stubResponseData = Data()
        
        // 6) Create & assign the expectation
        let exp = expectation(description: "saveHike request fired")
        TestURLProtocol.requestExpectation = exp
        
        // 7) Call the method under test
        viewc.saveHike()
        
        // 8) Wait for startLoading() to fulfill it
        wait(for: [exp], timeout: 1.0)
        
        // 9) Now assert on the captured request
        guard let req = TestURLProtocol.lastRequest else {
            return XCTFail("No request captured")
        }
        XCTAssertEqual(req.httpMethod, "POST")
        XCTAssertEqual(
            req.url?.absoluteString,
            "https://hopla.onrender.com/userhikes/create"
        )
        XCTAssertEqual(
            req.value(forHTTPHeaderField: "Authorization"),
            "Bearer dummy"
        )
        
        // 10) And inspect the JSON body from our new buffer
        guard
            let body = TestURLProtocol.lastRequestBody,
            let json = try? JSONSerialization.jsonObject(with: body) as? [String:Any]
        else {
            return XCTFail("No or invalid JSON body")
        }
        XCTAssertEqual(json["Distance"] as? String, "1.23")
        XCTAssertEqual(json["Duration"] as? String, "1.25")
        
        if let coords = json["Coordinates"] as? [[String:Any]] {
            XCTAssertEqual(coords.count, 2)
            XCTAssertEqual(coords[0]["lat"] as? Double, 10)
            XCTAssertEqual(coords[1]["long"] as? Double, 20.1)
        } else {
            XCTFail("Coordinates missing")
        }
    }
}

//
//  ClassHikeServiceTests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//
import XCTest
@testable import Hopla

// Tests for file ClassHikeService
final class ClassHikeServiceTests: XCTestCase {
    var service: HikeService!
    
    override func setUp() {
        super.setUp()
        // 1) Create an ephemeral config
        let config = URLSessionConfiguration.ephemeral
        // 2) Tell it to use the MockURLProtocol
        config.protocolClasses = [MockURLProtocol.self]
        // 3) Build the session and inject
        let session = URLSession(configuration: config)
        service = HikeService(session: session)
    }
    
    override func tearDown() {
        MockURLProtocol.stubResponses = [:]
        service = nil
        super.tearDown()
    }
    
    func testFetchHikes_page1_success_withFullStub() {
        // 1) Load stubHikes.json from the test bundle
        let bundle = Bundle(for: type(of: self))
        guard let stubURL = bundle.url(forResource: "stubHikes", withExtension: "json"),
              let stubData = try? Data(contentsOf: stubURL) else {
            return XCTFail("Could not load stubHikes.json from test bundle")
        }
        
        // 2) Stub the exact endpoint URL the service will call
        let requestURL = URL(
            string: "https://hopla.onrender.com/trails/all?pageNumber=1&pageSize=20"
        )!
        MockURLProtocol.stubResponses = [
            requestURL: (200, stubData, nil as Error?)
        ]
        
        // 3) Fire the request and assert
        let exp = expectation(description: "fetchHikes succeeds with full stub data")
        service.fetchHikes(page: 1) { result in
            switch result {
            case .success(let resp):
                // We know stubHikes.json has 4 trails total on page 1
                XCTAssertEqual(resp.pageNumber, 1)
                XCTAssertEqual(resp.pageSize, 4)
                XCTAssertEqual(resp.trails.count, 4)
                
                // Check first trail’s key properties
                let first = resp.trails[0]
                XCTAssertEqual(first.id, "12345678-0000-0000-0021-123456780021")
                XCTAssertEqual(first.name, "Sjølystturen")
                XCTAssertEqual(first.pictureUrl,
                               "https://images.unsplash.com/photo-1504893524553-b855bce32c67?w=1020&h=420&fit=crop")
                XCTAssertTrue(first.isFavorite)
                
                // And maybe a nested filter
                let surfaceFilter = first.filters?.first { $0.name == "SurfaceType" }
                XCTAssertEqual(surfaceFilter?.options, ["Gravel","Sand","Asphalt","Dirt"])
                XCTAssertEqual(surfaceFilter?.value, "Dirt,Gravel")
                
                exp.fulfill()
                
            case .failure(let err):
                XCTFail("Expected success, got error: \(err)")
            }
        }
        
        wait(for: [exp], timeout: 1.0)
    }
    
    func testFetchHikes_unauthorized() {
        let url = URL(string: "https://hopla.onrender.com/trails/all?pageNumber=1&pageSize=20")!
        MockURLProtocol.stubResponses = [
            url: (401, Data("Unauthorized".utf8), nil as Error?)
        ]
        let exp = expectation(description: "fetchHikes fails with 401")
        service.fetchHikes(page: 1) { result in
            switch result {
            case .success:
                XCTFail("Should not succeed on 401")
            case .failure(let error as NSError):
                XCTAssertEqual(error.code, 401)
                exp.fulfill()
            }
        }
        wait(for: [exp], timeout: 1.0)
    }
    
    func testFetchHikesByLocation_success() {
        let bundle = Bundle(for: type(of: self))
        guard let stubURL = bundle.url(forResource: "stubHikes", withExtension: "json"),
              let stubData = try? Data(contentsOf: stubURL) else {
            return XCTFail("Could not load stubHikes.json")
        }
        
        let url = URL(
            string: "https://hopla.onrender.com/trails/list?latitude=10.0&longitude=20.0"
        )!
        MockURLProtocol.stubResponses = [
            url: (200, stubData, nil as Error?)
        ]
        
        let exp = expectation(description: "fetchHikesByLocation returns data")
        service.fetchHikesByLocation(latitude: 10.0, longitude: 20.0) { result in
            switch result {
            case .success(let resp):
                // e.g. verify that at least one trail in stubHikes.json is returned
                XCTAssertFalse(resp.trails.isEmpty)
                exp.fulfill()
            case .failure(let err):
                XCTFail("Unexpected error: \(err)")
            }
        }
        wait(for: [exp], timeout: 1.0)
    }
    
    func testToggleFavorite_postsCorrectMethod() {
        // Create a dummy Hike
        var hike = Hike(id: "fav1", name: "Fav Trail", description: "A cool trail!", pictureUrl: "https://images.unsplash.com/photo-1504893524553-b855bce32c67?w=1020&h=420&fit=crop",
                        averageRating: Int(4.5), isFavorite: false)
        // Stub the favorite endpoint to always return 200
        let url = URL(string: "https://hopla.onrender.com/trails/favorite")!
        MockURLProtocol.stubResponses = [
            url: (200, nil, nil as Error?)
        ]
        
        let exp = expectation(description: "toggleFavorite completes")
        service.toggleFavorite(for: hike) { success in
            XCTAssertTrue(success)
            exp.fulfill()
        }
        wait(for: [exp], timeout: 1.0)
    }
}

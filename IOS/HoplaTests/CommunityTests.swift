//
//  CommunityTests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//
import XCTest
import Combine
import CoreLocation
@testable import Hopla

// 1) Dummy LocationManager that never has a real location,
//    so toggleMembership won't cascade into fetchStables.
class DummyLocationManager: LocationManager {
    override var userLocation: CLLocation? {
        get { nil }       // always nil
        set { /* ignore */ }
    }
}

// Testing Community file
final class CommunityTests: XCTestCase {
    var viewModel: StableViewModel!
    var session: URLSession!
    var cancellables = Set<AnyCancellable>()
    
    override func setUp() {
        super.setUp()
        let config = URLSessionConfiguration.ephemeral
        config.protocolClasses = [MockURLProtocol.self]
        session = URLSession(configuration: config)
        
        // Inject dummy location manager + mock session
        viewModel = StableViewModel(
            locationManager: DummyLocationManager(),
            session: session
        )
    }
    
    override func tearDown() {
        MockURLProtocol.stubResponses = [:]
        cancellables.removeAll()
        viewModel = nil
        session = nil
        super.tearDown()
    }
    
    // Testing to fetch communities
    func testFetchStables_success() {
        // 1) Build a JSON payload matching EVERY field in your Stable model:
        let json = """
        [
          {
            "stableId":"s1",
            "stableName":"First Stable",
            "distance":null,
            "member":false,
            "pictureUrl":null
          }
        ]
        """.data(using: .utf8)!
        
        // 2) Stub the exact URL your viewModel will hit:
        let urlString = "https://hopla.onrender.com/stables/all" +
        "?latitude=59.9139&longitude=10.7522" +
        "&pagesize=20&pagenumber=1"
        let url = URL(string: urlString)!
        MockURLProtocol.stubResponses = [
            url: (200, json, nil as Error?)
        ]
        
        // 3) Observe the `stables` array for the update
        let exp = expectation(description: "stables updated")
        viewModel.$stables
            .dropFirst()
            .sink { stables in
                XCTAssertEqual(stables.count, 1)
                XCTAssertEqual(stables[0].stableId, "s1")
                XCTAssertEqual(stables[0].stableName, "First Stable")
                exp.fulfill()
            }
            .store(in: &cancellables)
        
        // 4) Trigger the fetch
        viewModel.fetchStables(
            latitude: 59.9139,
            longitude: 10.7522
        )
        
        wait(for: [exp], timeout: 1.0)
    }
    
    // Testing membership toggle
    func testToggleMembership_join() {
        // Seed with a non-member
        let stable = Stable(
            stableId: "s2",
            stableName: "Joinable",
            distance: nil,
            member: false,
            pictureUrl: nil
        )
        viewModel.stables = [stable]
        
        // Stub the join endpoint
        let joinURL = URL(string: "https://hopla.onrender.com/stables/join")!
        MockURLProtocol.stubResponses = [
            joinURL: (200, Data(), nil as Error?)
        ]
        
        // Expect it to flip `member` from false to true
        let exp = expectation(description: "joined")
        viewModel.$stables
            .dropFirst()
            .sink { arr in
                XCTAssertTrue(arr[0].member)
                exp.fulfill()
            }
            .store(in: &cancellables)
        
        viewModel.toggleMembership(for: stable)
        wait(for: [exp], timeout: 1.0)
    }
    
    // Testing to leave a community
    func testToggleMembership_leave() {
        // Seed with a member
        let stable = Stable(
            stableId: "s3",
            stableName: "Leavable",
            distance: nil,
            member: true,
            pictureUrl: nil
        )
        viewModel.stables = [stable]
        
        // Stub the leave endpoint
        let leaveURL = URL(string: "https://hopla.onrender.com/stables/leave")!
        MockURLProtocol.stubResponses = [
            leaveURL: (200, Data(), nil as Error?)
        ]
        
        // Expect it to flip `member` from true to false
        let exp = expectation(description: "left")
        viewModel.$stables
            .dropFirst()
            .sink { arr in
                XCTAssertFalse(arr[0].member)
                exp.fulfill()
            }
            .store(in: &cancellables)
        
        viewModel.toggleMembership(for: stable)
        wait(for: [exp], timeout: 1.0)
    }
}

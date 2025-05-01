//
//  MyHorsesTests.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/05/2025.
//
import XCTest
import Combine
import UIKit
@testable import Hopla

final class MyHorsesTests: XCTestCase {
    var vm: HorseViewModel!
    var session: URLSession!
    var cancellables = Set<AnyCancellable>()
    
    override func setUp() {
      super.setUp()
      let cfg = URLSessionConfiguration.ephemeral
      cfg.protocolClasses = [TestURLProtocol.self]
      session = URLSession(configuration: cfg)
      vm = HorseViewModel(session: session)
      TokenManager.shared.saveToken("dummy")

      TestURLProtocol.lastRequest = nil
      TestURLProtocol.lastRequestBody = nil
      TestURLProtocol.requestExpectation = nil
    }
    
    override func tearDown() {
        TestURLProtocol.requests = []
        TestURLProtocol.lastRequest = nil
        TestURLProtocol.lastRequestBody = nil
        TestURLProtocol.requestExpectation = nil
        session = nil
        vm = nil
        cancellables.removeAll()
        super.tearDown()
    }
    
    func testFetchHorses_success() {
        // 1) Stub a JSON array of one horse
        let json = """
        [
          {
            "id":"h1",
            "name":"Alpha",
            "breed":"X",
            "age":5,
            "horsePictureUrl":null,
            "dob":{
              "year":2020,
              "month":1,
              "day":1,
              "dayOfWeek":3,
              "dayOfYear":1,
              "dayNumber":1
            }
          }
        ]
        """.data(using: .utf8)!
        
        // 2) Configure the stub response
        TestURLProtocol.stubResponseData = json
        TestURLProtocol.stubStatusCode = 200
        
        // 3) Listen for the horses array to update
        let exp = expectation(description: "horses fetched")
        vm.$horses
            .dropFirst() // ignore initial empty
            .sink { horses in
                XCTAssertEqual(horses.count, 1)
                XCTAssertEqual(horses.first?.id, "h1")
                XCTAssertEqual(horses.first?.name, "Alpha")
                exp.fulfill()
            }
            .store(in: &cancellables)
        
        // 4) Trigger the network call
        vm.fetchHorses()
        
        wait(for: [exp], timeout: 1.0)
    }
    
    func testDeleteHorse_removesFromArray() {
        // 1) Seed two horses in the view model
        let h1 = Horse(id: "x1", name: "A", breed: nil, age: nil, horsePictureUrl: nil, dob: nil)
        let h2 = Horse(id: "x2", name: "B", breed: nil, age: nil, horsePictureUrl: nil, dob: nil)
        vm.horses = [h1, h2]
        
        // 2) Stub a successful DELETE (200)
        TestURLProtocol.stubStatusCode = 200
        TestURLProtocol.stubResponseData = Data()
        
        // 3) Expect the delete request
        let exp = expectation(description: "deleteHorse network")
        TestURLProtocol.requestExpectation = exp
        
        // 4) Call deleteHorse
        vm.deleteHorse(horseId: "x1")
        
        // 5) Wait and then verify the array was updated
        wait(for: [exp], timeout: 1.0)
        XCTAssertEqual(vm.horses.map(\.id), ["x2"])
    }
}

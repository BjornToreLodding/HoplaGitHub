//
//  HomeTest.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//

import XCTest
import Combine
@testable import Hopla

final class HomeTests: XCTestCase {
    var vm: HomeViewModel!
    var session: URLSession!
    var cancellables = Set<AnyCancellable>()

    override func setUp() {
        super.setUp()
        // 1) Prepare a mockable URLSession
        let config = URLSessionConfiguration.ephemeral
        config.protocolClasses = [MockURLProtocol.self]
        session = URLSession(configuration: config)

        // 2) Inject it
        vm = HomeViewModel(session: session)

        // 3) Fake out a token so Authorization header isn’t nil
        TokenManager.shared.saveToken("dummy-token")
    }

    override func tearDown() {
        MockURLProtocol.stubResponses = [:]
        cancellables.removeAll()
        vm = nil
        session = nil
        super.tearDown()
    }

    func testFetchPosts_globe_success() {
        // a) Build sample JSON matching FeedResponse.Items -> [HomePost]
        let json = """
        {
          "items":[
            {
              "entityId":"p1",
              "title":"Hello",
              "description":"World",
              "pictureUrl":null,
              "userAlias":null,
              "userProfilePicture":null,
              "likes":0,
              "isLikedByUser":false,
              "createdAt":"2025-04-30T12:00:00Z"
            }
          ]
        }
        """.data(using: .utf8)!

        // b) Stub the globe URL
        let urlStr = "https://hopla.onrender.com/feed/all?show=userhikes,trails,trailreviews,horses"
        let url = URL(string: urlStr)!
        MockURLProtocol.stubResponses = [ url:(200, json, nil as Error?) ]

        // c) Observe `homePosts`
        let exp = expectation(description: "posts loaded")
        vm.$homePosts
          .dropFirst()
          .sink { posts in
            XCTAssertEqual(posts.count, 1)
            XCTAssertEqual(posts[0].entityId, "p1")
            exp.fulfill()
          }
          .store(in: &cancellables)

        // d) Trigger
        vm.fetchPosts(for: "globe")

        wait(for: [exp], timeout: 1.0)
    }

    func testFetchPosts_location_success() {
        let json = """
        { "items":[] }
        """.data(using: .utf8)!
        let urlStr = "https://hopla.onrender.com/feed/all?userlat=10.0&userlong=20.0&radius=120"
        let url = URL(string: urlStr)!
        MockURLProtocol.stubResponses = [ url:(200, json, nil as Error?) ]

        let exp = expectation(description: "location posts")
        vm.$homePosts
          .dropFirst()
          .sink { posts in
            XCTAssertTrue(posts.isEmpty)
            exp.fulfill()
          }
          .store(in: &cancellables)

        vm.fetchPosts(for: "location", latitude: 10.0, longitude: 20.0)

        wait(for: [exp], timeout: 1.0)
    }

    func testLikePost_updatesModel() {
        // Seed one post
        let post = HomePost(
          entityId: "p2",
          title: "T2", description: "D2",
          pictureUrl: nil, userAlias: nil, userProfilePicture: nil,
          likes: 5, isLikedByUser: false,
          createdAt: "2025-04-30T12:00:00Z"
        )
        vm.homePosts = [post]

        // Stub POST /reactions
        let likeURL = URL(string: "https://hopla.onrender.com/reactions")!
        MockURLProtocol.stubResponses = [ likeURL:(200, Data(), nil as Error?) ]

        let exp = expectation(description: "like toggled")
        vm.$homePosts
          .dropFirst()
          .sink { posts in
            let pst = posts[0]
            XCTAssertTrue(pst.isLikedByUser)
            XCTAssertEqual(pst.likes, 6)
            exp.fulfill()
          }
          .store(in: &cancellables)

        vm.likePost(entityId: "p2")
        wait(for: [exp], timeout: 1.0)
    }

    func testUnlikePost_updatesModel() {
        let post = HomePost(
          entityId: "p3",
          title: "T3", description: "D3",
          pictureUrl: nil, userAlias: nil, userProfilePicture: nil,
          likes: 1, isLikedByUser: true,
          createdAt: "2025-04-30T12:00:00Z"
        )
        vm.homePosts = [post]

        let url = URL(string: "https://hopla.onrender.com/reactions")!
        MockURLProtocol.stubResponses = [ url:(200, Data(), nil as Error?) ]

        let exp = expectation(description: "unlike toggled")
        vm.$homePosts
          .dropFirst()
          .sink { posts in
            let pst = posts[0]
            XCTAssertFalse(pst.isLikedByUser)
            XCTAssertEqual(pst.likes, 0)
            exp.fulfill()
          }
          .store(in: &cancellables)

        vm.unlikePost(entityId: "p3")
        wait(for: [exp], timeout: 1.0)
    }
}

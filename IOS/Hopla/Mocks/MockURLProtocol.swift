//
//  MockURLProtocol.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//
import Foundation

class MockURLProtocol: URLProtocol {
  // This dictionary maps URL -> (statusCode, Data, Error?)
  static var stubResponses: [URL: (Int, Data?, Error?)] = [:]

  override class func canInit(with request: URLRequest) -> Bool {
    return true
  }
  override class func canonicalRequest(for request: URLRequest) -> URLRequest {
    return request
  }
  override func startLoading() {
    guard let url = request.url,
          let stub = MockURLProtocol.stubResponses[url] else {
      client?.urlProtocol(self, didFailWithError: URLError(.badURL))
      return
    }

    if let error = stub.2 {
      client?.urlProtocol(self, didFailWithError: error)
    } else {
      let response = HTTPURLResponse(
        url: url,
        statusCode: stub.0,
        httpVersion: nil,
        headerFields: ["Content-Type": "application/json"]
      )!
      client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
      if let data = stub.1 {
        client?.urlProtocol(self, didLoad: data)
      }
      client?.urlProtocolDidFinishLoading(self)
    }
  }
  override func stopLoading() { /* nothing */ }
}

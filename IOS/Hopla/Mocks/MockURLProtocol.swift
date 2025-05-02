//
//  MockURLProtocol.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//
import Foundation

class MockURLProtocol: URLProtocol {
    // Static dictionary to hold stubbed responses for specific URLs:
    // Key: URL, Value: tuple of (statusCode, optional Data, optional Error)
    static var stubResponses: [URL: (Int, Data?, Error?)] = [:]

    /// Determines whether this protocol can handle the given request.
    /// Here it returns true for all requests so we can intercept every network call.
    override class func canInit(with request: URLRequest) -> Bool {
        return true
    }

    /// Returns the canonical form of the request.
    /// For our mock, we simply return the request unchanged.
    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }

    /// Starts loading the request.
    /// We look up a stubbed response for the URL and send it back via the URLProtocolClient.
    override func startLoading() {
        // Ensure the request URL exists and we have a stub for it
        guard let url = request.url,
              let stub = MockURLProtocol.stubResponses[url] else {
            // No stub found: report a bad URL error
            client?.urlProtocol(self, didFailWithError: URLError(.badURL))
            return
        }

        // If the stub includes an error, immediately report it
        if let error = stub.2 {
            client?.urlProtocol(self, didFailWithError: error)
        } else {
            // Otherwise, build a HTTPURLResponse with the stubbed status code
            let response = HTTPURLResponse(
                url: url,
                statusCode: stub.0,
                httpVersion: nil,
                headerFields: ["Content-Type": "application/json"]
            )!
            // Tell the client we received a response
            client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
            // If there's stubbed data, send it to the client
            if let data = stub.1 {
                client?.urlProtocol(self, didLoad: data)
            }
            // Signal that loading is finished
            client?.urlProtocolDidFinishLoading(self)
        }
    }

    /// Stops loading the request.
    /// No cleanup needed for this mock implementation.
    override func stopLoading() { /* nothing to clean up */ }
}

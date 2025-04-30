//
//  TestURLProtocol.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 30/04/2025.
//
import Foundation
import XCTest

class TestURLProtocol: URLProtocol {
    static var lastRequest: URLRequest?
    static var lastRequestBody: Data?       
    static var stubResponseData: Data?
    static var stubStatusCode = 200

    /// XCTestExpectation your test sets before calling saveHike()
    static weak var requestExpectation: XCTestExpectation?

    override class func canInit(with request: URLRequest) -> Bool {
        return true
    }

    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }

    override func startLoading() {
        // 1) Capture the raw request
        TestURLProtocol.lastRequest = request

        // 2) Pull the body out of the stream (or fallback to httpBody)
        if let stream = request.httpBodyStream {
            stream.open()
            var data = Data()
            let bufferSize = 1024
            let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufferSize)
            while stream.hasBytesAvailable {
                let read = stream.read(buffer, maxLength: bufferSize)
                if read > 0 {
                    data.append(buffer, count: read)
                } else {
                    break
                }
            }
            buffer.deallocate()
            stream.close()
            TestURLProtocol.lastRequestBody = data
        } else {
            TestURLProtocol.lastRequestBody = request.httpBody
        }

        // 3) Let the test know we got the request
        DispatchQueue.main.async {
            TestURLProtocol.requestExpectation?.fulfill()
        }

        // 4) Send back a stubbed HTTP response
        let response = HTTPURLResponse(
          url: request.url!,
          statusCode: TestURLProtocol.stubStatusCode,
          httpVersion: nil,
          headerFields: nil
        )!
        client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
        if let data = TestURLProtocol.stubResponseData {
            client?.urlProtocol(self, didLoad: data)
        }
        client?.urlProtocolDidFinishLoading(self)
    }

    override func stopLoading() {}
}

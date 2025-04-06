//
//  ViewController.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 01/03/2025.
//

import UIKit
import CoreLocation
import GoogleMaps

class ViewController: UIViewController, CLLocationManagerDelegate {
    
    var locationManager = CLLocationManager()
    var timer: Timer?
    var elapsedTime: TimeInterval = 0
    var distance: Double = 0.0
    var coordinates: [Coordinate] = []
    var isTracking = false // ✅ Toggle between Start/Stop
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set up location tracking
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        // Request location permission when the view appears
        checkLocationAuthorization()
    }
    
    func checkLocationAuthorization() {
        let status = locationManager.authorizationStatus
        print("🔍 Current Authorization Status: \(status.rawValue)") // Debugging
        
        switch status {
        case .notDetermined:
            print("📍 Permission not asked yet. Requesting now...")
            locationManager.requestWhenInUseAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            print("✅ Location access granted!")
            locationManager.startUpdatingLocation()
        case .restricted, .denied:
            print("❌ Location access denied. Please enable it in settings.")
        @unknown default:
            print("⚠️ Unknown location authorization status.")
        }
    }
    
    // Handle errors in location manager
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("⚠️ Failed to get location: \(error.localizedDescription)")
    }
    
    //MARK: - Start and stop buttons NewHike
    func startTrackingHike() {
        elapsedTime = 0
        distance = 0.0
        coordinates = []

        // Start Timer
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            self.elapsedTime += 1
        }

        // Start Location Updates
        locationManager.startUpdatingLocation()
        isTracking = true
        print("🏃‍♂️ Hike started!")
    }

    func stopTrackingHike() {
        timer?.invalidate()
        timer = nil
        locationManager.stopUpdatingLocation()
        isTracking = false
        print("🛑 Hike stopped! Distance: \(distance) km, Time: \(formatTime(elapsedTime))")

        // Save the hike
        saveHike()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        
        let newCoordinate = Coordinate(lat: location.coordinate.latitude, long: location.coordinate.longitude, timestamp: Date().timeIntervalSince1970)
        
        if isTracking, let lastCoordinate = coordinates.last {
            distance += calculateDistance(from: lastCoordinate, to: newCoordinate)
        }
        
        coordinates.append(newCoordinate)
        print("📍 Updated location: \(location.coordinate.latitude), \(location.coordinate.longitude)")
    }

    func calculateDistance(from start: Coordinate, to end: Coordinate) -> Double {
        let startLocation = CLLocation(latitude: start.lat, longitude: start.long)
        let endLocation = CLLocation(latitude: end.lat, longitude: end.long)

        let distanceMeters = startLocation.distance(from: endLocation)
        print("📏 Calculating Distance: \(distanceMeters) meters") // ✅ Debug print
        return distanceMeters / 1000 // Convert meters to km
    }



    func formatTime(_ time: TimeInterval) -> String {
        let minutes = Int(time) / 60
        let seconds = Int(time) % 60
        return String(format: "%02d:%02d min", minutes, seconds)
    }

    func saveHike() {
        guard let token = TokenManager.shared.getToken() else { return }
        
        let url = URL(string: "https://hopla.onrender.com/userhikes/create")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        let body: [String: Any] = [
            "Title": "My Hike",
            "Description": "Beautiful hike in the forest",
            "StartedAt": ISO8601DateFormatter().string(from: Date()),
            "Distance": String(format: "%.2f", distance),
            "Duration": String(format: "%.2f", elapsedTime / 60),
            "Coordinates": coordinates.map { ["timestamp": $0.timestamp, "lat": $0.lat, "long": $0.long] }
        ]

        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body, options: [])
        } catch {
            print("❌ JSON encoding failed:", error)
            return
        }

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error saving hike:", error.localizedDescription)
                return
            }

            if let httpResponse = response as? HTTPURLResponse {
                print("📡 Hike saved successfully! Status:", httpResponse.statusCode)
            }
        }.resume()
    }
}


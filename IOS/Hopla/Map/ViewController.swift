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
    // Core Location manager to request and receive location updates
    var locationManager = CLLocationManager()
    // Timer to track elapsed time during a hike
    var timer: Timer?
    // Accumulated time since tracking started (in seconds)
    var elapsedTime: TimeInterval = 0
    // Accumulated distance traveled (in kilometers)
    var distance: Double = 0.0
    // Array of recorded coordinates during the hike
    var coordinates: [Coordinate] = []
    // Flag to indicate whether tracking is active
    var isTracking = false
    // URLSession instance for network requests
    var session: URLSession = .shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Configure location manager delegate and accuracy
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        // Check and request location permissions once view appears
        checkLocationAuthorization()
    }
    
    /// Checks current location authorization status and requests permission if needed
    func checkLocationAuthorization() {
        let status = locationManager.authorizationStatus
        print("Current Authorization Status: \(status.rawValue)") // Debug print
        
        switch status {
        case .notDetermined:
            // First time request: ask for "When In Use" permission
            print("Permission not asked yet. Requesting now...")
            locationManager.requestWhenInUseAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            // Permission already granted: start receiving updates
            print("Location access granted!")
            locationManager.startUpdatingLocation()
        case .restricted, .denied:
            // Permission denied or restricted: inform user to enable in Settings
            print("Location access denied. Please enable it in settings.")
        @unknown default:
            // Handle any future cases gracefully
            print("Unknown location authorization status.")
        }
    }
    
    /// Called if location manager encounters an error
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to get location: \(error.localizedDescription)")
    }
    
    // MARK: – Start and Stop Tracking
    
    /// Initializes and starts the timer and location updates
    func startTrackingHike() {
        // Reset counters and data
        elapsedTime = 0
        distance = 0.0
        coordinates = []
        
        // Schedule a timer to increment elapsedTime every second
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            self.elapsedTime += 1
        }
        
        // Begin receiving location updates
        locationManager.startUpdatingLocation()
        isTracking = true
        print("Hike started!")
    }
    
    /// Stops the timer and location updates, then saves the hike data
    func stopTrackingHike() {
        timer?.invalidate()           // Stop and clear timer
        timer = nil
        locationManager.stopUpdatingLocation() // Stop location updates
        isTracking = false
        // Log final stats
        print("Hike stopped! Distance: \(distance) km, Time: \(formatTime(elapsedTime))")
        
        // Persist hike to backend
        saveHike()
    }
    
    /// Delegate callback for updated locations
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        // Take the most recent location update
        guard let location = locations.last else { return }
        
        // Wrap into our Coordinate model with timestamp
        let newCoordinate = Coordinate(
            lat: location.coordinate.latitude,
            long: location.coordinate.longitude,
            timestamp: Date().timeIntervalSince1970
        )
        
        // If tracking, add distance from last point
        if isTracking, let lastCoordinate = coordinates.last {
            distance += calculateDistance(from: lastCoordinate, to: newCoordinate)
        }
        
        // Append this point for later use or saving
        coordinates.append(newCoordinate)
        print("Updated location: \(location.coordinate.latitude), \(location.coordinate.longitude)")
    }
    
    /// Calculates the distance in kilometers between two coordinates
    func calculateDistance(from start: Coordinate, to end: Coordinate) -> Double {
        let startLocation = CLLocation(latitude: start.lat, longitude: start.long)
        let endLocation = CLLocation(latitude: end.lat, longitude: end.long)
        
        let distanceMeters = startLocation.distance(from: endLocation)
        print("Calculating Distance: \(distanceMeters) meters") // Debug print
        return distanceMeters / 1000 // Convert meters to kilometers
    }
    
    /// Formats elapsed time (in seconds) to "MM:SS min"
    func formatTime(_ time: TimeInterval) -> String {
        let minutes = Int(time) / 60
        let seconds = Int(time) % 60
        return String(format: "%02d:%02d min", minutes, seconds)
    }
    
    /// Sends the completed hike data to the backend API
    func saveHike() {
        // Ensure we have an authentication token
        guard let token = TokenManager.shared.getToken() else { return }
        
        // API endpoint for creating a new hike record
        let url = URL(string: "https://hopla.onrender.com/userhikes/create")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        // Set headers for JSON body and bearer token
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        // Build JSON payload
        let body: [String: Any] = [
            "Title": "My Hike",
            "Description": "Beautiful hike in the forest",
            "StartedAt": ISO8601DateFormatter().string(from: Date()),
            "Distance": String(format: "%.2f", distance),
            "Duration": String(format: "%.2f", elapsedTime / 60),
            "Coordinates": coordinates.map { ["timestamp": $0.timestamp, "lat": $0.lat, "long": $0.long] }
        ]
        
        do {
            // Encode dictionary to JSON data
            request.httpBody = try JSONSerialization.data(withJSONObject: body, options: [])
        } catch {
            print("JSON encoding failed:", error)
            return
        }
        
        // Perform network request
        session.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error saving hike:", error.localizedDescription)
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                print("Hike saved successfully! Status:", httpResponse.statusCode)
            }
        }.resume()
    }
}

//
//  LocationManager.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 03/03/2025.
//
import CoreLocation

// Coordinate struct
struct Coordinate: Codable, Identifiable {
    var id = UUID()
    let lat: Double
    let long: Double
    let timestamp: TimeInterval
}

// LocationManager class
class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private var locationManager = CLLocationManager()
    @Published var userLocation: CLLocation? = nil
    @Published var coordinates: [Coordinate] = []
    @Published var latitude: Double?
    @Published var longitude: Double?
    @Published var distance: Double = 0.0
    @Published var isTracking: Bool = false
    @Published var elapsedTime: TimeInterval = 0.0
    
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
    }
    
    func calculateDistance(from start: Coordinate, to end: Coordinate) -> Double {
        let startLocation = CLLocation(latitude: start.lat, longitude: start.long)
        let endLocation = CLLocation(latitude: end.lat, longitude: end.long)
        let distanceMeters = startLocation.distance(from: endLocation)
        print("Calculating Distance: \(distanceMeters) meters") // Debug print
        return distanceMeters / 1000 // Convert meters to km
    }
    
    // Start tracking distance
    func startTracking() {
        isTracking = true
        coordinates = []
        distance = 0.0
        locationManager.distanceFilter = 5 // Update when moving at least 5 meters
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
        print("Location tracking started!")
    }
   
    // Stop tracking distance
    func stopTracking() {
        isTracking = false
        locationManager.stopUpdatingLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else {
            print("No locations received!")
            return
        }
        
        let newCoordinate = Coordinate(lat: location.coordinate.latitude, long: location.coordinate.longitude, timestamp: Date().timeIntervalSince1970)
        
        print("New Location Received: \(newCoordinate.lat), \(newCoordinate.long)") // Debug GPS updates
        
        DispatchQueue.main.async {
            self.userLocation = location // Update user location
            print("User Location Updated:", self.userLocation?.coordinate.latitude ??
                  "No Latitude", self.userLocation?.coordinate.longitude ?? "No Longitude") // Debug UI behavior
        }
        
        if isTracking, let lastCoordinate = coordinates.last {
            let calculatedDistance = calculateDistance(from: lastCoordinate, to: newCoordinate)
            
            print("Previous Location: \(lastCoordinate.lat), \(lastCoordinate.long)")
            print("Calculated Distance: \(calculatedDistance) meters")
            
            if calculatedDistance > 0.005 { // Calculate if more than 5 meter movement
                DispatchQueue.main.async {
                    self.distance += calculatedDistance
                }
                print("Distance Updated: \(self.distance) km")
            } else {
                print("Ignored small movement: \(calculatedDistance) km")
            }
        }
        
        DispatchQueue.main.async {
            self.coordinates.append(newCoordinate)
        }
    }
    
    // To check location authorization
    func checkLocationAuthorization() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            
            if #available(iOS 14.0, *) {
                switch self.locationManager.authorizationStatus {
                case .authorizedWhenInUse, .authorizedAlways:
                    self.locationManager.startUpdatingLocation()
                    print("Location access granted!")
                case .denied, .restricted:
                    print("Location access denied.")
                    NotificationCenter.default.post(name: .locationAccessDenied, object: nil)
                case .notDetermined:
                    print("Location permission not asked yet. Requesting now...")
                    self.locationManager.requestWhenInUseAuthorization()
                @unknown default:
                    print("Unknown location authorization status.")
                }
            } else {
                switch CLLocationManager.authorizationStatus() {
                case .authorizedWhenInUse, .authorizedAlways:
                    self.locationManager.startUpdatingLocation()
                    print("Location access granted!")
                case .denied, .restricted:
                    print("Location access denied.")
                    NotificationCenter.default.post(name: .locationAccessDenied, object: nil)
                case .notDetermined:
                    print("Location permission not asked yet. Requesting now...")
                    self.locationManager.requestWhenInUseAuthorization()
                @unknown default:
                    print("Unknown location authorization status.")
                }
            }
        }
    }
    
    // Cheack authorization again
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        checkLocationAuthorization()
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to get location: \(error.localizedDescription)")
    }
    // Update location of user
    func startUpdatingLocation(completion: @escaping (Coordinate) -> Void) {
        locationManager.startUpdatingLocation()
        
        NotificationCenter.default.addObserver(forName: .didUpdateLocation, object: nil, queue: .main) { notification in
            if let location = notification.object as? CLLocation {
                let coordinate = Coordinate(lat: location.coordinate.latitude, long: location.coordinate.longitude, timestamp: Date().timeIntervalSince1970)
                completion(coordinate) // Send new location update to tracking system
            }
        }
    }
    
    func requestLocationUpdate() {
        locationManager.requestLocation() // Force a fresh location update
        print("Requesting new location update...")
    }
    
}

extension Notification.Name {
    static let didUpdateLocation = Notification.Name("didUpdateLocation")
    static let locationAccessDenied = Notification.Name("locationAccessDenied")
}

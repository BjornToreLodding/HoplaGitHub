//
//  NewHike.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI
import GoogleMaps
import GooglePlaces

struct NewHike: View {
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject private var locationManager = LocationManager()
    

    @State private var isTracking = false
    @State private var elapsedTime: TimeInterval = 0
    @State private var distance: Double = 0.0
    @State private var timer: Timer?

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                MapView(locationManager: locationManager) // ✅ Pass location manager to track hike
                    .frame(maxHeight: .infinity)
                    .edgesIgnoringSafeArea(.top)

                // Time and Distance Tracking
                HStack {
                    Text(formatTime(elapsedTime))
                        .frame(maxWidth: .infinity, alignment: .center)

                    Button(action: {
                        isTracking.toggle()
                        if isTracking {
                            startHikeTracking()
                        } else {
                            stopHikeTracking()
                        }
                    }) {
                        Text(isTracking ? "Stop" : "Start")
                            .foregroundColor(.white)
                            .frame(width: 70, height: 70)
                            .background(AdaptiveColor(light: .lightGreen, dark: .darkGreen).color(for: colorScheme))
                            .clipShape(Circle())
                            .overlay(Circle().stroke(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme), lineWidth: 4))
                    }
                    .frame(maxWidth: .infinity, alignment: .center)

                    Text(String(format: "%.4f km", locationManager.distance))
                        .frame(maxWidth: .infinity, alignment: .center)
                }
                .foregroundColor(.white)
                .frame(height: 75)
                .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
            }
        }
    }
    
    private func formatTime(_ time: TimeInterval) -> String {
        let minutes = Int(time) / 60
        let seconds = Int(time) % 60
        return String(format: "%02d:%02d min", minutes, seconds)
    }

    
    private func startHikeTracking() {
        elapsedTime = 0
        distance = 0.0 // ✅ Reset distance when starting
        locationManager.coordinates = [] // ✅ Reset coordinates
        locationManager.startTracking()

        print("🚀 Starting hike tracking!")

        timer = Timer.scheduledTimer(withTimeInterval: 3, repeats: true) { _ in
            locationManager.requestLocationUpdate()
            self.elapsedTime += 3
        }

        locationManager.startTracking()
    }




    private func stopHikeTracking() {
        timer?.invalidate()
        timer = nil
        locationManager.stopTracking() // ✅ Stop location updates
    }
    
    private func saveHike() {
        guard let token = TokenManager.shared.getToken() else { return }
        
        let url = URL(string: "https://hopla.onrender.com/userhikes/create")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        let body: [String: Any] = [
            "Title": "My Hike",
            "Description": "A beautiful scenic route",
            "StartedAt": ISO8601DateFormatter().string(from: Date()),
            "Distance": String(format: "%.2f", distance),
            "Duration": String(format: "%.2f", elapsedTime / 60),
            "Coordinates": locationManager.coordinates.map { ["timestamp": $0.timestamp, "lat": $0.lat, "long": $0.long] } // ✅ Get dynamic coordinates
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


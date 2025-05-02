//
//  NewHike.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//
import SwiftUI
import GoogleMaps
import GooglePlaces

struct CreateHikeResponse: Decodable {
    let id: String
}

// New hike struct
struct NewHike: View {
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject private var locationManager = LocationManager()
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var myHikeVM: MyHikeViewModel
    @State private var showSaveConfirmation = false
    @State private var isTracking = false
    @State private var elapsedTime: TimeInterval = 0
    @State private var distance: Double = 0.0
    @State private var timer: Timer?
    @State private var title: String = ""
    @State private var description: String = ""
    @State private var stars: Int = 3
    @State private var filters: String = ""
    @State private var isPrivate: Bool = false
    @State private var selectedImage: UIImage? = nil
    
    // For saving a new hike
    @State private var showPopup = false
    @State private var showDetailForm = false
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                MapView(locationManager: locationManager) // Pass location manager to track hike
                    .frame(maxHeight: .infinity)
                    .edgesIgnoringSafeArea(.top)
                
                // Time and Distance Tracking
                HStack {
                    Text("Time: \(String(format: "%02d:%02d", Int(locationManager.elapsedTime) / 60, Int(locationManager.elapsedTime) % 60))")
                        .padding()
                    Button(action: {
                        isTracking.toggle()
                        if isTracking {
                            startHikeTracking()
                        } else {
                            stopHikeTracking()
                            showPopup = true // Set state variable instead
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
                    .alert("Save or Fill in Details", isPresented: $showPopup) {
                        Button("Save") {
                            saveHike {
                                DispatchQueue.main.async {
                                    locationManager.elapsedTime = 0 // Reset elapsed time
                                    locationManager.distance = 0 // Reset distance
                                }
                                presentationMode.wrappedValue.dismiss() // Only dismiss when saving
                            }
                        }
                        Button("Fill in Details") {
                            showDetailForm = true
                        }
                    }
                    .sheet(isPresented: $showDetailForm) {
                        FillHikeDetailsView(
                            locationManager: locationManager,
                            showSaveConfirmation: $showSaveConfirmation
                        )
                    }
                    .alert("Hike saved to My Activity", isPresented: $showSaveConfirmation) {
                        Button("OK", role: .cancel) {
                            presentationMode.wrappedValue.dismiss()
                        }
                    }
                    Text(String(format: "%.3f km", locationManager.distance))
                        .frame(maxWidth: .infinity, alignment: .center)
                }
                .foregroundColor(.white)
                .frame(height: 75)
                .background(AdaptiveColor(light: .lightBrown, dark: .darkBrown).color(for: colorScheme))
            }
        }
    }
    
    // Format time in minutes:Seconds
    private func formatTime(_ time: TimeInterval) -> String {
        let minutes = Int(time) / 60
        let seconds = Int(time) % 60
        let formattedDuration = String(format: "%02d:%02d", minutes, seconds) // Correct MM:SS format
        
        print("Correctly Formatted Duration:", formattedDuration) // Debugging output
        return formattedDuration
    }
    
    // Start tracking hike, start timer and coordinates
    private func startHikeTracking() {
        elapsedTime = 0
        distance = 0.0 // Reset distance when starting
        locationManager.coordinates = [] // Reset coordinates
        locationManager.startTracking()
        
        print("Starting hike tracking!")
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            DispatchQueue.main.async {
                locationManager.elapsedTime += 1 // Updates every second
                print("Timer Updated Elapsed Time:", locationManager.elapsedTime) // Debugging duration updates
            }
        }
        locationManager.startTracking()
    }
    
    // Stop timer and stop tracking
    private func stopHikeTracking() {
        timer?.invalidate()
        timer = nil
        locationManager.stopTracking()
        showPopup = true // Show pop-up when stopping
    }
    
    // Save hike
    private func saveHike(completion: @escaping () -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found.")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/userhikes/create")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        // Build your multipart body
        var body = Data()
        let lineBreak = "\r\n"
        
        func appendField(name: String, value: String) {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"\(name)\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append("\(value)\(lineBreak)".data(using: .utf8)!)
        }
        
        appendField(name: "Title", value: title.isEmpty ? "Unnamed Hike" : title)
        appendField(name: "Description", value: description.isEmpty ? "No description provided." : description)
        appendField(name: "StartedAt", value: ISO8601DateFormatter().string(from: Date()))
        appendField(name: "Distance", value: locationManager.distance > 0 ? String(format: "%.2f", locationManager.distance) : "0.00")
        appendField(name: "Duration", value: "\(Int(locationManager.elapsedTime))")
        
        // Append JSON field (and other fields)
        
        appendField(name: "HorseId", value: "") // (send an empty string or a valid horse id)
        appendField(name: "TrailId", value: "")
        appendField(name: "Stars", value: "\(stars)")
        appendField(name: "Filters", value: filters.isEmpty ? "None" : filters)
        appendField(name: "IsPrivate", value: isPrivate ? "true" : "false")
        
        // If there is an image selected, append it.
        if let image = selectedImage, let imageData = image.jpegData(compressionQuality: 0.8) {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"Image\"; filename=\"new_hike.jpg\"\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Type: image/jpeg\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append(imageData)
            body.append("\(lineBreak)".data(using: .utf8)!)
        }
        
        body.append("--\(boundary)--\(lineBreak)".data(using: .utf8)!)
        
        request.httpBody = body
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error saving hike:", error)
                return
            }
            guard let data = data else { return }
            
            do {
                let resp = try JSONDecoder().decode(CreateHikeResponse.self, from: data)
                print("Created hike id:", resp.id)
                DispatchQueue.main.async {
                    // Instead of inserting a half‑baked MyHike, just reload the full list
                    self.myHikeVM.reloadHikes()
                    completion()
                    self.showSaveConfirmation = true
                }
            } catch {
                print("Decoding create response:", error)
            }
        }
        .resume()
    }
}

// The form to fill in details about hike
struct FillHikeDetailsView: View {
    @EnvironmentObject var myHikeVM: MyHikeViewModel
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    @ObservedObject var locationManager: LocationManager
    @State private var selectedImage: UIImage?
    @State private var showImagePicker = false
    @Binding var showSaveConfirmation: Bool
    @State private var title = ""
    @State private var description = ""
    @State private var stars: Int = 0
    @State private var filters: String = ""
    @State private var isPrivate = false
    
    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text("Hike Details")) {
                    TextField("Title", text: $title)
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    TextField("Description", text: $description)
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    Stepper("Stars: \(stars)", value: $stars, in: 1...5)
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    TextField("Filters", text: $filters)
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                    Toggle("Private Hike?", isOn: $isPrivate)
                        .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                }
                .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
                Button("Save Hike") {
                    saveHike {
                        DispatchQueue.main.async {
                            locationManager.elapsedTime = 0 // Reset elapsed time
                            locationManager.distance = 0 // Reset distance
                        }
                        showSaveConfirmation = true
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                .foregroundStyle(AdaptiveColor(light: .textLightBackground, dark: .textDarkBackground).color(for: colorScheme))
            }
            .navigationTitle("Fill in Details")
            .background(AdaptiveColor(light: .lightPostBackground, dark: .darkPostBackground).color(for: colorScheme))
        }
    }
    
    // Save hike
    private func saveHike(completion: @escaping () -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found.")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/userhikes/create")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        // Debugging Duration Before Sending
        print("Title being sent:", title)
        print("Duration before sending:", locationManager.elapsedTime)
        print("Formatted Duration:", String(format: "%.2f", locationManager.elapsedTime / 60))
        
        // Prepare coordinates JSON string
        let coordinatesArray = locationManager.coordinates.map {
            [
                "timestamp": Int($0.timestamp),
                "lat": $0.lat,
                "long": $0.long
            ]
        }
        
        guard let coordinatesData = try? JSONSerialization.data(withJSONObject: coordinatesArray, options: []),
              let coordinatesString = String(data: coordinatesData, encoding: .utf8) else {
            print("Failed to serialize coordinates")
            return
        }
        
        // Prepare other fields
        var body = Data()
        
        func appendField(name: String, value: String) {
            body.append("--\(boundary)\r\n".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"\(name)\"\r\n\r\n".data(using: .utf8)!)
            body.append("\(value)\r\n".data(using: .utf8)!)
        }
        
        appendField(name: "Title", value: title.isEmpty ? "Unnamed Hike" : title)
        appendField(name: "Description", value: description.isEmpty ? "No description provided." : description)
        appendField(name: "StartedAt", value: ISO8601DateFormatter().string(from: Date()))
        appendField(name: "Distance", value: locationManager.distance > 0 ? String(format: "%.2f", locationManager.distance) : "0.00")
        appendField(name: "Duration", value: "\(Int(locationManager.elapsedTime))") // Send raw seconds as an integer
        
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"Coordinates\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: application/json\r\n\r\n".data(using: .utf8)!) // Explicit JSON format
        body.append(coordinatesData) // Direct JSON data
        body.append("\r\n".data(using: .utf8)!)
        
        appendField(name: "HorseId", value: "") // send empty string instead of NSNull
        appendField(name: "TrailId", value: "")
        appendField(name: "Stars", value: "\(stars)")
        appendField(name: "Filters", value: filters.isEmpty ? "None" : filters)
        appendField(name: "IsPrivate", value: isPrivate ? "true" : "false")
        
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        
        if let rawBody = String(data: body, encoding: .utf8) {
            print("Final Request Body:\n", rawBody) // Debugging raw request payload
        }
        
        request.httpBody = body
        // Send request
        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                print("Error saving hike:", error)
                return
            }
            guard let data = data else { return }
            
            // DEBUG
            if let jsonStr = String(data: data, encoding: .utf8) {
                print("Raw response JSON:\n", jsonStr)
            }
            do {
                let resp = try JSONDecoder().decode(CreateHikeResponse.self, from: data)
                print("Created hike id:", resp.id)
                DispatchQueue.main.async {
                    // reload the full hikes list
                    self.myHikeVM.reloadHikes()
                    completion()
                    self.presentationMode.wrappedValue.dismiss()
                }
            } catch {
                print("Decoding create response:", error)
            }
        }
        .resume()
    }
}

//
//  MyHikesDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 07/04/2025.
//

import SwiftUI

struct CoordinateMyHikes: Codable {
    let lat: Double
    let lng: Double
    
    private enum CodingKeys: String, CodingKey {
        case lat = "latMean"
        case lng = "longMean"
    }
}


struct TrailResponse: Codable {
    let id: String
    let distance: Double
    let allCoords: [CoordinateMyHikes]
}

struct FilterPicker: View {
    @Binding var selectedFilters: [String]
    
    let availableFilters = [
        "Grus", "3", "true" // Example filter options
    ]
    
    var body: some View {
        ForEach(availableFilters, id: \.self) { filter in
            Button(action: {
                if selectedFilters.contains(filter) {
                    selectedFilters.removeAll { $0 == filter }
                } else {
                    selectedFilters.append(filter)
                }
            }) {
                HStack {
                    Text(filter)
                    if selectedFilters.contains(filter) {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(.green)
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.2))
                .cornerRadius(8)
            }
        }
    }
}



struct MyHikesDetails: View {
    @Binding var myHikes: [MyHike]
    @EnvironmentObject var viewModel: MyHikeViewModel
    @State private var isEditing = false
    @State private var updatedTrailname: String
    @State private var updatedDescription: String
    @State private var updatedTitle: String
    @State var hike: MyHike
    @State private var coordinates: [CoordinateMyHikes] = []
    @State private var trailResponse: TrailResponse? = nil
    @State private var showImagePicker = false
    @State private var selectedImage: UIImage? = nil
    @State private var formVisible = false
    @State private var newTitle = ""
    @State private var selectedFilters: [String] = []
    @State private var selectedHorseId: String? = nil
    @State private var showHorsePicker = false
    
    // Add a HorseViewModel instance to get horse details.
    @StateObject private var horseVM = HorseViewModel()
    
    init(hike: MyHike, myHikes: Binding<[MyHike]>) {
        self.hike = hike
        _myHikes = myHikes
        _updatedTrailname = State(initialValue: hike.trailName)
        _updatedDescription = State(initialValue: hike.comment)
        _updatedTitle = State(initialValue: hike.title)
    }
    
    // A computed property that returns the name of the selected horse.
    var selectedHorseName: String {
        if let horseId = selectedHorseId, let horse = horseVM.horses.first(where: { $0.id == horseId }) {
            return horse.name
        }
        return "None"
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                // ✏️ Edit Button
                HStack {
                    Text("Hike Details").font(.title).bold()
                    Spacer()
                    Button(action: { isEditing.toggle() }) {
                        Image(systemName: "pencil")
                            .resizable()
                            .frame(width: 20, height: 20)
                    }
                    .buttonStyle(BorderlessButtonStyle())
                    // Upgrade button
                    if hike.trailButton { // Ensure that we show the upgrade button only if it's not already a trail
                        Button("Upgrade to Trail") {
                            formVisible.toggle()
                        }
                        .foregroundColor(.green)
                        .padding()
                        .background(Color.green.opacity(0.2))
                        .cornerRadius(8)
                    }
                }
                if formVisible {
                    upgradeForm
                }
                
                if isEditing {
                    // 🔄 Edit Form
                    TextField("Trail name", text: $updatedTrailname)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    TextField("Title", text: $updatedTitle)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    TextField("Description", text: $updatedDescription)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    // Button to select a horse
                    Button("Select Horse") {
                        showHorsePicker.toggle()
                    }
                    
                    // Display the name of the selected horse.
                    Text("Selected Horse: \(selectedHorseName)")
                        .padding(.vertical, 5)
                    
                    
                    // Image picker button
                    Button("Select Image") {
                        showImagePicker.toggle()
                    }
                    
                    if let image = selectedImage {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 200)
                    }
                    
                    HStack {
                        Button("Cancel") {
                            isEditing = false
                            updatedTrailname = hike.trailName
                            updatedDescription = hike.comment
                            updatedTitle = hike.title
                        }
                        .foregroundColor(.red)
                        
                        Button("Save") {
                            updateHike()  // Update on the server.
                            // Immediately update the shared view model.
                            if let index = viewModel.myHikes.firstIndex(where: { $0.id == hike.id }) {
                                viewModel.myHikes[index].trailName = updatedTrailname
                                viewModel.myHikes[index].comment = updatedDescription
                                viewModel.myHikes[index].title = updatedTitle
                                // Optionally update horse name if a new horse was selected.
                                if let newHorseId = selectedHorseId,
                                   let horse = horseVM.horses.first(where: { $0.id == newHorseId }) {
                                    viewModel.myHikes[index].horseName = horse.name
                                }
                            }
                            isEditing = false
                        }
                        .foregroundColor(.blue)
                    }
                } else {
                    if let url = URL(string: hike.pictureUrl ?? "") {
                        AsyncImage(url: url) { image in
                            image.resizable().scaledToFit().frame(height: 200)
                        } placeholder: {
                            ProgressView()
                        }
                    }
                    
                    Text("Trail name: \(hike.trailName)").font(.title).bold()
                    Text("Title: \(hike.title)")
                    Text("Comment: \(hike.comment)")
                    if let horse = hike.horseName, !horse.isEmpty {
                        Text("Horse: \(horse)")
                    }
                    
                }
                
                Text("Distance: \(String(format: "%.2f km", hike.length))")
                Text("Duration: \(String(format: "%02d:%02d", Int(hike.duration) / 60, Int(hike.duration) % 60))")
                
                let hasCoordinates = !coordinates.isEmpty
                let hasTrailCoords = trailResponse?.allCoords.isEmpty == false
                
                if hasCoordinates || hasTrailCoords {
                    MapWithRouteView(coordinates: coordinates, trailButton: hike.trailButton, trailResponse: trailResponse)
                        .frame(height: 300)
                        .cornerRadius(12)
                } else {
                    Text("KART FUNKER IKKE")
                }
            }
            .padding()
            .onAppear {
                viewModel.fetchMyHikes()  // or fetch updated details if needed
                fetchUpdatedHikeDetails()
                fetchCoordinates()
                horseVM.fetchHorses()
            }
            .sheet(isPresented: $showImagePicker) {
                ImagePicker(sourceType: .photoLibrary, selectedImage: $selectedImage, showImagePicker: $showImagePicker)
            }
        }
        .sheet(isPresented: $showHorsePicker) {
            HorseSelectionView { chosenHorseId in
                self.selectedHorseId = chosenHorseId
                showHorsePicker = false
            }
        }
        .navigationTitle("Hike Details")
        
    }
    
    var upgradeForm: some View {
        VStack {
            TextField("Title", text: $newTitle)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            
            // Image Picker Button
            Button("Select Image") {
                showImagePicker.toggle()
            }
            
            if let image = selectedImage {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 200)
            }
            
            // Filters
            VStack(alignment: .leading) {
                Text("Select Filters").font(.headline)
                FilterPicker(selectedFilters: $selectedFilters)
            }
            
            Button("Submit") {
                upgradeHikeToTrail()
                formVisible.toggle() // Close form after submission
            }
            .padding()
            .background(Color.blue)
            .cornerRadius(8)
            .foregroundColor(.white)
        }
        .padding()
        .background(Color.white)
        .cornerRadius(10)
        .shadow(radius: 5)
    }
    
    struct HorseSelectionView: View {
        @ObservedObject var horseVM = HorseViewModel()
        var onSelect: (String) -> Void
        
        var body: some View {
            NavigationView {
                List(horseVM.horses) { horse in
                    Button(action: {
                        if let horseId = horse.id {
                            onSelect(horseId)
                        }
                    }) {
                        Text(horse.name)
                    }
                }
                .navigationTitle("Select a Horse")
                .onAppear {
                    horseVM.fetchHorses()
                }
            }
        }
    }
    
    
    private func updateHike() {
        // 1. Check for a token and build the URL.
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found.")
            return
        }
        guard let url = URL(string: "https://hopla.onrender.com/userhikes/\(hike.id)") else {
            print("❌ Invalid URL")
            return
        }
        
        // 2. Configure the request.
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let boundary = "Boundary-\(UUID().uuidString)"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        // 3. Build the multipart/form-data body.
        var body = Data()
        let lineBreak = "\r\n"
        
        // Add text parameters.
        let parameters: [String: String] = [
            "Title": updatedTitle,
            "Description": updatedDescription, // Or "Comment", depending on what your server expects.
            "TrailName": updatedTrailname
        ]
        
        // Optionally add HorseId if selected.
        if let horseId = selectedHorseId, !horseId.isEmpty {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"HorseId\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append("\(horseId)\(lineBreak)".data(using: .utf8)!)
        }
        
        // Add the rest of the text parameters.
        for (key, value) in parameters {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"\(key)\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append("\(value)\(lineBreak)".data(using: .utf8)!)
        }
        
        // Add the image file if one has been selected.
        if let image = selectedImage, let imageData = image.jpegData(compressionQuality: 0.8) {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"Image\"; filename=\"updated_hike.jpg\"\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Type: image/jpeg\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append(imageData)
            body.append("\(lineBreak)".data(using: .utf8)!)
        }
        
        // End the multipart/form-data body.
        body.append("--\(boundary)--\(lineBreak)".data(using: .utf8)!)
        
        request.httpBody = body
        
        // 4. Send the request.
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error updating hike: \(error.localizedDescription)")
                return
            }
            
            if let data = data {
                do {
                    // Try to decode the updated hike from the response.
                    let updatedHike = try JSONDecoder().decode(MyHike.self, from: data)
                    DispatchQueue.main.async {
                        // Update the local hike and the shared view model.
                        self.hike = updatedHike
                        if let index = viewModel.myHikes.firstIndex(where: { $0.id == self.hike.id }) {
                            viewModel.myHikes[index] = updatedHike
                        }
                        print("✅ Hike updated successfully with new image: \(updatedHike.pictureUrl ?? "No URL")")
                    }
                } catch {
                    // If decoding fails, print the error.
                    print("❌ Failed to decode updated hike:", error.localizedDescription)
                }
            }
        }.resume()
    }

    
    
    private func fetchUpdatedHikeDetails() {
        guard let url = URL(string: "https://hopla.onrender.com/userhikes/\(hike.id)") else {
            print("❌ Invalid URL")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error fetching updated hike:", error.localizedDescription)
                return
            }
            
            if let data = data {
                do {
                    let updatedHike = try JSONDecoder().decode(MyHike.self, from: data)
                    DispatchQueue.main.async {
                        self.hike = updatedHike
                        print("✅ Successfully fetched updated hike: \(updatedHike)")
                    }
                } catch {
                    print("❌ Failed to decode updated hike:", error.localizedDescription)
                }
            }
        }
        .resume()
    }
    
    private func fetchCoordinates() {
        let urlString: String
        if hike.trailButton {
            // Use the "userhikes/coordinates" endpoint
            urlString = "https://hopla.onrender.com/userhikes/coordinates/\(hike.id)"
        } else {
            // Use the "trails/prepare" endpoint
            urlString = "https://hopla.onrender.com/trails/prepare?trailId=\(hike.id)"
        }
        
        guard let url = URL(string: urlString) else {
            print("❌ Invalid URL: \(urlString)")
            return
        }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("❌ Error fetching coordinates:", error.localizedDescription)
                return
            }
            
            if let data = data {
                do {
                    if hike.trailButton {
                        // Decode coordinates for userhikes (lat, lng)
                        let fetchedCoordinates = try JSONDecoder().decode([CoordinateMyHikes].self, from: data)
                        DispatchQueue.main.async {
                            self.coordinates = fetchedCoordinates
                            print("✅ Coordinates fetched for userhikes:", fetchedCoordinates)
                        }
                    } else {
                        // Decode coordinates for trails/prepare (id, distance, allCoords)
                        let response = try JSONDecoder().decode(TrailResponse.self, from: data)
                        DispatchQueue.main.async {
                            self.trailResponse = response
                            self.coordinates = response.allCoords
                            print("✅ TrailResponse fetched:", response)
                        }
                    }
                } catch {
                    print("❌ Failed to decode coordinates:", error.localizedDescription)
                }
            }
        }
        .resume()
    }
    
    
    private func uploadImage(image: UIImage, completion: @escaping (String?) -> Void) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found.")
            completion(nil)
            return
        }
        
        guard let url = URL(string: "https://hopla.onrender.com/upload-image") else {
            print("❌ Invalid image upload URL.")
            completion(nil)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("multipart/form-data", forHTTPHeaderField: "Content-Type")
        
        // Prepare image data
        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            print("❌ Failed to convert image to data.")
            completion(nil)
            return
        }
        
        let boundary = "Boundary-\(UUID().uuidString)"
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        
        // Start boundary
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        
        // Add the content disposition header for the file
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n".data(using: .utf8)!)
        
        // Set content type
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        
        // Append the image data
        body.append(imageData) // imageData should already be of type Data
        
        // End boundary
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        
        
        request.httpBody = body
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error uploading image:", error.localizedDescription)
                completion(nil)
                return
            }
            
            if let data = data {
                do {
                    // Assuming the server responds with a JSON containing the image URL
                    let jsonResponse = try JSONSerialization.jsonObject(with: data, options: [])
                    if let responseDict = jsonResponse as? [String: Any], let imageUrl = responseDict["url"] as? String {
                        completion(imageUrl)
                    } else {
                        completion(nil)
                    }
                } catch {
                    print("❌ Failed to decode image upload response:", error.localizedDescription)
                    completion(nil)
                }
            }
        }.resume()
    }
    
    private func sendPutRequest(formData: [String: Any]) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found.")
            return
        }
        
        guard let url = URL(string: "https://hopla.onrender.com/userhikes/\(hike.id)") else {
            print("❌ Invalid URL")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        guard let jsonBody = try? JSONSerialization.data(withJSONObject: formData, options: []) else {
            print("❌ Failed to encode JSON")
            return
        }
        
        request.httpBody = jsonBody
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error updating hike:", error.localizedDescription)
                return
            }
            
            if let data = data {
                do {
                    let jsonResponse = try JSONSerialization.jsonObject(with: data, options: [])
                    print("📡 Server Response:", jsonResponse)
                } catch {
                    print("❌ Failed to decode response:", error.localizedDescription)
                }
            }
        }.resume()
    }
    
    private func upgradeHikeToTrail() {
        // Prepare the JSON data for the request
        let dataJson: [String: Any] = [
            "UserHikeId": hike.id,  // Use the hike's ID for UserHikeId
            "name": updatedTrailname,
            "filters": [
                ["filterDefinitionId": "3a6e859e-d3fc-40dc-bc2c-4ad81263f8f2", "value": "Grus"],
                ["filterDefinitionId": "edc35fcf-2d98-4b6c-9617-8800f5a65b49", "value": "3"],
                ["filterDefinitionId": "b558af34-ecfc-4204-bba3-d6c1b4161f84", "value": "true"]
            ]
        ]
        
        // Upload the image if selected
        if let selectedImage = selectedImage {
            uploadImage(image: selectedImage) { imageUrl in
                if let imageUrl = imageUrl {
                    var formData: [String: Any] = dataJson
                    formData["imageUrl"] = imageUrl // Attach the image URL
                    
                    // Send the POST request
                    sendPostRequest(formData: formData)
                } else {
                    print("❌ Image upload failed.")
                }
            }
        } else {
            // Send the POST request without the image
            sendPostRequest(formData: dataJson)
        }
    }
    
    private func sendPostRequest(formData: [String: Any]) {
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token found.")
            return
        }
        
        guard let url = URL(string: "https://hopla.onrender.com/trails/create/\(hike.id)") else {
            print("❌ Invalid URL")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        guard let jsonBody = try? JSONSerialization.data(withJSONObject: formData, options: []) else {
            print("❌ Failed to encode JSON")
            return
        }
        
        request.httpBody = jsonBody
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error creating trail:", error.localizedDescription)
                return
            }
            
            if let data = data {
                do {
                    let jsonResponse = try JSONSerialization.jsonObject(with: data, options: [])
                    print("📡 Server response:", jsonResponse)
                } catch {
                    print("❌ Failed to decode response:", error.localizedDescription)
                }
            }
        }.resume()
    }
    /*
     func fetchTrailFilters() {
     guard let url = URL(string: "https://hopla.onrender.com/trailfilters/all") else { return }
     URLSession.shared.dataTask(with: url) { data, response, error in
     if let data = data {
     do {
     let filters = try JSONDecoder().decode([TrailFilter].self, from: data)
     DispatchQueue.main.async {
     self.trailFilters = filters
     }
     } catch {
     print("Decoding error: \(error)")
     }
     }
     }.resume()
     }
     */
}


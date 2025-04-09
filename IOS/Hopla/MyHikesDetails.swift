//
//  MyHikesDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 07/04/2025.
//

import SwiftUI

struct MyHikesDetails: View {
    @Binding var myHikes: [MyHike]
    @StateObject private var viewModel = MyHikeViewModel()
    @State private var isEditing = false // ✅ Track edit mode
    @State private var updatedTrailname: String
    @State private var updatedDescription: String
    @State private var updatedTitle: String
    @State var hike: MyHike
    
    init(hike: MyHike, myHikes: Binding<[MyHike]>) {
        self.hike = hike
        _myHikes = myHikes
        _updatedTrailname = State(initialValue: hike.trailName)
        _updatedDescription = State(initialValue: hike.comment)
        _updatedTitle = State(initialValue: hike.title)
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
                }
                
                if isEditing {
                    // 🔄 Edit Form
                    TextField("Trail name", text: $updatedTrailname)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    TextField("Title", text: $updatedTitle)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    TextField("Description", text: $updatedDescription)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    
                    HStack {
                        Button("Cancel") {
                            isEditing = false
                            updatedTrailname = hike.trailName
                            updatedDescription = hike.comment
                            updatedTitle = hike.title
                        }
                        .foregroundColor(.red)
                        
                        Button("Save") {
                            updateHike()
                            DispatchQueue.main.async {
                                isEditing = false
                                hike.trailName = updatedTrailname
                                hike.comment = updatedDescription
                                hike.title = updatedTitle
                                
                                if let index = myHikes.firstIndex(where: { $0.id == hike.id }) {
                                    myHikes[index].trailName = updatedTrailname
                                    myHikes[index].comment = updatedDescription
                                    myHikes[index].title = updatedTitle
                                }
                                
                                viewModel.fetchMyHikes() // ✅ Fetch updated hikes immediately
                            }
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
                }
                
                Text("Distance: \(String(format: "%.2f km", hike.length))")
                Text("Duration: \(String(format: "%02d:%02d", Int(hike.duration) / 60, Int(hike.duration) % 60))")
            }
            .padding()
            .onAppear {
                viewModel.fetchMyHikes()
                fetchUpdatedHikeDetails()
                print("📡 New API Response:", viewModel.rawApiResponse) // ✅ Use instance, not type
            }
        }
        .navigationTitle("Hike Details")
    }
    
    // ✅ PUT Request to Update Hike
    private func updateHike() {
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
        
        let formData: [String: Any] = [
            "trailName": updatedTrailname,
            "comment": updatedDescription,
            "title": updatedTitle
        ]
        
        guard let jsonBody = try? JSONSerialization.data(withJSONObject: formData, options: []) else {
            print("❌ Failed to encode JSON")
            return
        }
        
        request.httpBody = jsonBody
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        print("📡 Sending PUT request to:", url.absoluteString)
        print("📡 Request Body:", formData)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Error updating hike:", error.localizedDescription)
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                print("📡 Response Status Code:", httpResponse.statusCode)
            }
            
            if let data = data {
                do {
                    let jsonResponse = try JSONSerialization.jsonObject(with: data, options: [])
                    print("📡 Server Response:", jsonResponse)
                } catch {
                    print("❌ Failed to decode response:", error.localizedDescription)
                }
            }
            
            DispatchQueue.main.async {
                isEditing = false
                hike.trailName = updatedTrailname
                hike.comment = updatedDescription
                hike.title = updatedTitle
                
                if let index = myHikes.firstIndex(where: { $0.id == hike.id }) {
                    myHikes[index].trailName = updatedTrailname
                    myHikes[index].comment = updatedDescription
                    myHikes[index].title = updatedTitle
                }
                
                // 🔥 Immediately refresh hike data to confirm the server has stored changes
                fetchUpdatedHikeDetails()
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
        }.resume()
    }
}

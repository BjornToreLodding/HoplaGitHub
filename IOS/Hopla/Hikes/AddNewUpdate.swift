//
//  AddNewUpdateView.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 11/04/2025.
//

import SwiftUI

struct AddNewUpdateView: View {
    let trailId: String  // Pass the trailId from HikesDetails
    @Environment(\.presentationMode) var presentationMode
    @State private var message: String = ""
    @State private var selectedCondition: String = "Møkkette"
    @State private var image: UIImage?
    @State private var isUploading = false
    @State private var showImagePicker = false
    
    let conditions = ["Ukjent", "Bra", "Vått", "Møkkette", "Farlig", "Blokkert"]
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Create a New Update")
                .font(.title2)
                .bold()
            
            TextField("Message", text: $message)
                .textFieldStyle(RoundedBorderTextFieldStyle())
            
            SwiftUI.Picker("Condition", selection: $selectedCondition) {
                ForEach(conditions, id: \.self) { condition in
                    Text(condition)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
            
            Button("Pick Image") {
                showImagePicker = true
            }
            
            if let image = image {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 200)
            }
            
            Button(action: submitUpdate) {
                if isUploading {
                    ProgressView()
                } else {
                    Text("Submit Update")
                        .bold()
                }
            }
            .padding()
            .background(Color.green)
            .foregroundColor(.white)
            .cornerRadius(10)
            
            Spacer()
        }
        .padding()
        .sheet(isPresented: $showImagePicker) {
            // Replace this with your own ImagePicker implementation.
            ImagePicker(sourceType: .photoLibrary, selectedImage: $image, showImagePicker: $showImagePicker)
        }
    }
    
    func submitUpdate() {
        guard !message.isEmpty else { return }
        guard let token = TokenManager.shared.getToken() else {
            print("❌ No token")
            return
        }
        isUploading = true

        guard let url = URL(string: "https://hopla.onrender.com/trails/review") else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)",
                         forHTTPHeaderField: "Content-Type")

        var body = Data()
        let lineBreak = "\r\n"

        // 1) Image part (if any)
        if let imageData = image?.jpegData(compressionQuality: 0.8) {
            body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"Image\"; filename=\"update.jpg\"\(lineBreak)".data(using: .utf8)!)
            body.append("Content-Type: image/jpeg\(lineBreak)\(lineBreak)".data(using: .utf8)!)
            body.append(imageData)
            body.append("\(lineBreak)".data(using: .utf8)!)
        }

        // 2) TrailId
        body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"TrailId\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
        body.append("\(trailId)\(lineBreak)".data(using: .utf8)!)

        // 3) Message
        body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"Message\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
        body.append("\(message)\(lineBreak)".data(using: .utf8)!)

        // 4) Condition
        let conditionIndex = conditions.firstIndex(of: selectedCondition) ?? 0
        body.append("--\(boundary)\(lineBreak)".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"Condition\"\(lineBreak)\(lineBreak)".data(using: .utf8)!)
        body.append("\(conditionIndex)\(lineBreak)".data(using: .utf8)!)

        // Close
        body.append("--\(boundary)--\(lineBreak)".data(using: .utf8)!)
        request.httpBody = body

        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                isUploading = false
                if let error = error {
                    print("❌ Failed to upload:", error)
                    return
                }
                guard let http = response as? HTTPURLResponse else {
                    print("❌ No HTTP response")
                    return
                }
                print("📡 Status code:", http.statusCode)
                if (200...299).contains(http.statusCode) {
                    presentationMode.wrappedValue.dismiss()
                } else {
                    if let data = data,
                       let resp = String(data: data, encoding: .utf8) {
                        print("Server error:", resp)
                    }
                }
            }
        }.resume()
    }
}

//
//  AddNewUpdate.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 11/04/2025.
//

import SwiftUI

struct AddNewUpdateView: View {
    let trailId: String
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
                ForEach(conditions, id: \.self) {
                    Text($0)
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
            ImagePicker(sourceType: .photoLibrary, selectedImage: $image, showImagePicker: $showImagePicker)
        }
    }

    func submitUpdate() {
        guard !message.isEmpty else { return }
        isUploading = true

        var request = URLRequest(url: URL(string: "https://hopla.onrender.com/trails/review")!)
        request.httpMethod = "POST"

        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        let imageData = image?.jpegData(compressionQuality: 0.8)

        var body = Data()

        if let imageData = imageData {
            body.append("--\(boundary)\r\n".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"Image\"; filename=\"image.jpg\"\r\n".data(using: .utf8)!)
            body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
            body.append(imageData)
            body.append("\r\n".data(using: .utf8)!)
        }

        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"trailId\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(trailId)\r\n".data(using: .utf8)!)

        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"message\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(message)\r\n".data(using: .utf8)!)

        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"condition\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(selectedCondition)\r\n".data(using: .utf8)!)

        body.append("--\(boundary)--\r\n".data(using: .utf8)!)

        request.httpBody = body

        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                isUploading = false
                
                if let error = error {
                    print("Failed to upload update: \(error.localizedDescription)")
                    return
                }
                
                if let httpResponse = response as? HTTPURLResponse {
                    print("HTTP status code: \(httpResponse.statusCode)")
                    print("Headers:", httpResponse.allHeaderFields)
                }


                if let data = data, let responseString = String(data: data, encoding: .utf8) {
                    print("Server response:", responseString)
                }

                if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 {
                    presentationMode.wrappedValue.dismiss()
                } else {
                    print("Upload failed with non-200 response")
                }
            }
        }.resume()
    }
}

//
//  HorseDetails.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 05/03/2025.
//

import SwiftUI
import Combine
import KeychainAccess
import KeychainSwift


class HorseDetailsViewModel: ObservableObject {
    @Published var horse: Horse?

    func fetchHorseDetails(horseId: String) {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let urlString = "https://hopla.onrender.com/horses/\(horseId)"
        guard let url = URL(string: urlString) else {
            print("Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }

            guard let httpResponse = response as? HTTPURLResponse, let data = data else {
                print("Invalid response")
                return
            }

            if httpResponse.statusCode == 200 {
                print("Received data: \(String(data: data, encoding: .utf8) ?? "No data")") // Log the raw response
                do {
                    let horse = try JSONDecoder().decode(Horse.self, from: data)
                    DispatchQueue.main.async {
                        self.horse = horse
                        self.horse?.id = horseId
                    }
                } catch let DecodingError.dataCorrupted(context) {
                    print("Data corrupted:", context)
                } catch let DecodingError.keyNotFound(key, context) {
                    print("Key '\(key)' not found:", context.debugDescription)
                    print("Coding path:", context.codingPath)
                } catch let DecodingError.valueNotFound(value, context) {
                    print("Value '\(value)' not found:", context.debugDescription)
                    print("Coding path:", context.codingPath)
                } catch let DecodingError.typeMismatch(type, context) {
                    print("Type '\(type)' mismatch:", context.debugDescription)
                    print("Coding path:", context.codingPath)
                } catch {
                    print("Error decoding horse details:", error.localizedDescription)
                }
            } else {
                print("Failed to retrieve horse details. Status Code:", httpResponse.statusCode)
            }
        }.resume()
    }


}



struct HorseDetails: View {
    @ObservedObject var vm = HorseDetailsViewModel()
    var horseId: String

    var body: some View {
        VStack(spacing: 20) {
            if let horse = vm.horse {
                Text(horse.name)
                    .font(.largeTitle)
                    .fontWeight(.bold)

                if let breed = horse.breed {
                    Text("Breed: \(breed)")
                        .font(.title2)
                }

                if let age = horse.age {
                    Text("Age: \(age) years old")
                        .font(.title2)
                }

                if let urlString = horse.horsePictureUrl, let url = URL(string: urlString) {
                    AsyncImage(url: url) { image in
                        image.resizable()
                            .scaledToFill()
                            .frame(width: 150, height: 150)
                            .clipShape(Circle())
                    } placeholder: {
                        ProgressView()
                            .frame(width: 150, height: 150)
                    }
                }
            } else {
                ProgressView()
            }
        }
        .onAppear {
            vm.fetchHorseDetails(horseId: horseId)
        }
    }
}


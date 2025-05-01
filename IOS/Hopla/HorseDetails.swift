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
    @Environment(\.dismiss) private var dismiss
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var vm = HorseDetailsViewModel()
    var horseId: String
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Custom header with back arrow and horse name
                HeaderViewHorseDetails(
                    name: vm.horse?.name ?? "",
                    colorScheme: colorScheme,
                    onBack: { dismiss() }
                )
                .frame(height: 40)
                
                ScrollView {
                    VStack(spacing: 20) {
                        Spacer()
                        
                        if let horse = vm.horse {
                            // Circular image
                            if let urlString = horse.horsePictureUrl,
                               let url = URL(string: urlString) {
                                AsyncImage(url: url) { phase in
                                    switch phase {
                                    case .success(let image):
                                        image.resizable().scaledToFill()
                                    case .failure:
                                        Image(systemName: "photo").resizable()
                                    case .empty:
                                        ProgressView()
                                    @unknown default:
                                        EmptyView()
                                    }
                                }
                                .frame(width: 200, height: 200)
                                .clipShape(Circle())
                                .shadow(radius: 5)
                            } else {
                                Image(systemName: "photo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 200, height: 200)
                                    .clipShape(Circle())
                                    .foregroundColor(.gray)
                            }
                            
                            let breedText = horse.breed ?? "Unknown"
                            Text("Breed: \(breedText)")
                                .font(.title2)
                                .foregroundStyle(
                                    AdaptiveColor(light: .textLightBackground,
                                                  dark:  .textDarkBackground)
                                    .color(for: colorScheme)
                                )
                            
                            
                            
                            Text("Age: \(horse.age ?? 0) years old")
                                .font(.title3)
                                .foregroundStyle(
                                    AdaptiveColor(light: .textLightBackground,
                                                  dark: .textDarkBackground)
                                    .color(for: colorScheme)
                                )
                        } else {
                            ProgressView()
                                .padding()
                        }
                    }
                    .padding()
                }
                .frame(maxWidth: .infinity)
                .background(
                    AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                        .color(for: colorScheme)
                        .ignoresSafeArea()
                )
            }
            .onAppear { vm.fetchHorseDetails(horseId: horseId) }
            .navigationBarBackButtonHidden(true)
        }
    }
    
    // MARK: - Header
    struct HeaderViewHorseDetails: View {
        let name: String
        let colorScheme: ColorScheme
        let onBack: () -> Void
        
        var body: some View {
            ZStack {
                AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                    .color(for: colorScheme)
                    .frame(maxWidth: .infinity)
                
                // Centered Title
                Text(name)
                    .font(.custom("ArialNova", size: 20))
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                
                // Back button aligned leading
                HStack {
                    Button(action: onBack) {
                        Image(systemName: "arrow.left")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 24, height: 24)
                            .foregroundStyle(
                                AdaptiveColor(light: .lightModeTextOnGreen,
                                              dark: .darkModeTextOnGreen)
                                .color(for: colorScheme)
                            )
                    }
                    Spacer()
                }
                .padding(.horizontal, 16)
            }
        }
    }
}

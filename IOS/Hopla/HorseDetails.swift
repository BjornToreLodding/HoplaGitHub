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

/// ViewModel responsible for fetching and storing detailed horse data
class HorseDetailsViewModel: ObservableObject {
    @Published var horse: Horse?  // Holds the fetched Horse object
    
    /// Fetches horse details from the backend by ID
    func fetchHorseDetails(horseId: String) {
        // Retrieve auth token, bail out if missing
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        // Build URL for the horse details endpoint
        let urlString = "https://hopla.onrender.com/horses/\(horseId)"
        guard let url = URL(string: urlString) else {
            print("Invalid URL")
            return
        }
        
        // Prepare GET request with Bearer authorization header
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        // Perform the network call
        URLSession.shared.dataTask(with: request) { data, response, error in
            // Handle low-level errors
            if let error = error {
                print("Request error:", error.localizedDescription)
                return
            }
            
            // Verify valid HTTP response and non-nil data
            guard let httpResponse = response as? HTTPURLResponse, let data = data else {
                print("Invalid response")
                return
            }
            
            if httpResponse.statusCode == 200 {
                // Log raw JSON for debugging
                print("Received data: \(String(data: data, encoding: .utf8) ?? "No data")")
                do {
                    // Decode JSON into Horse model
                    let horse = try JSONDecoder().decode(Horse.self, from: data)
                    DispatchQueue.main.async {
                        self.horse = horse
                        self.horse?.id = horseId  // Ensure the ID is set
                    }
                }
                // Detailed decoding error handling
                catch let DecodingError.dataCorrupted(context) {
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
                // Non-200 status code handling
                print("Failed to retrieve horse details. Status Code:", httpResponse.statusCode)
            }
        }
        .resume()  // Start the network task
    }
}

/// SwiftUI view displaying horse details
struct HorseDetails: View {
    @Environment(\.dismiss) private var dismiss             // For programmatic back navigation
    @Environment(\.colorScheme) var colorScheme             // To adapt colors for light/dark
    @ObservedObject var vm = HorseDetailsViewModel()        // ViewModel instance
    var horseId: String                                     // ID to fetch details for
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Custom header with back arrow and horse’s name
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
                            // Display horse image if URL exists, else placeholder
                            if let urlString = horse.horsePictureUrl,
                               let url = URL(string: urlString) {
                                AsyncImage(url: url) { phase in
                                    switch phase {
                                    case .success(let image):
                                        image
                                            .resizable()
                                            .scaledToFill()
                                    case .failure:
                                        Image(systemName: "photo")
                                            .resizable()
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
                                // Placeholder image
                                Image(systemName: "photo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 200, height: 200)
                                    .clipShape(Circle())
                                    .foregroundColor(.gray)
                            }
                            
                            // Display breed with adaptive text color
                            let breedText = horse.breed ?? "Unknown"
                            Text("Breed: \(breedText)")
                                .font(.title2)
                                .foregroundStyle(
                                    AdaptiveColor(light: .textLightBackground,
                                                  dark:  .textDarkBackground)
                                    .color(for: colorScheme)
                                )
                            
                            // Display age with adaptive text color
                            Text("Age: \(horse.age ?? 0) years old")
                                .font(.title3)
                                .foregroundStyle(
                                    AdaptiveColor(light: .textLightBackground,
                                                  dark: .textDarkBackground)
                                    .color(for: colorScheme)
                                )
                        } else {
                            // Loading indicator while fetching
                            ProgressView()
                                .padding()
                        }
                    }
                    .padding()
                }
                .frame(maxWidth: .infinity)
                .background(
                    // Background adapts to light/dark mode
                    AdaptiveColor(light: .mainLightBackground,
                                  dark: .mainDarkBackground)
                    .color(for: colorScheme)
                    .ignoresSafeArea()
                )
            }
            .onAppear { vm.fetchHorseDetails(horseId: horseId) }  // Fetch data on view load
            .navigationBarBackButtonHidden(true)                 // Hide default back button
        }
    }
    
    // MARK: - Header View
    
    /// Reusable header with title and custom back button
    struct HeaderViewHorseDetails: View {
        let name: String
        let colorScheme: ColorScheme
        let onBack: () -> Void
        
        var body: some View {
            ZStack {
                // Header background
                AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                    .color(for: colorScheme)
                    .frame(maxWidth: .infinity)
                
                // Centered horse name title
                Text(name)
                    .font(.custom("ArialNova", size: 20))
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                
                // Leading back button
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

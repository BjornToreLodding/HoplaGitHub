//
//  MyHorses.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/02/2025.
//

import SwiftUI
import Combine
import KeychainAccess
import KeychainSwift
import Foundation


// MARK: - Horse ViewModel
class HorseViewModel: ObservableObject {
    @Published var horses: [Horse] = []
    private var cancellables = Set<AnyCancellable>()
    
    func fetchHorses() {
        guard let token = TokenManager.shared.getToken() else {
            print("No token found")
            return
        }
        
        let url = URL(string: "https://hopla.onrender.com/horses/userhorses/")!
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
                do {
                    let horses = try JSONDecoder().decode([Horse].self, from: data)
                    DispatchQueue.main.async {
                        self.horses = horses
                    }
                } catch {
                    print("Error decoding horses:", error.localizedDescription)
                }
            } else {
                print("Failed to retrieve horses. Status Code:", httpResponse.statusCode)
            }
        }.resume()
    }
    
    
    func addHorse(name: String, image: UIImage?) {
        let newHorse = Horse(
            id: UUID().uuidString, // Generate a unique ID
            name: name,
            horsePictureUrl: nil // For now, we're not uploading the image to a server
        )
        horses.append(newHorse) // Add the new horse to the list
    }
}


// MARK: - Horse Model
struct Horse: Identifiable, Decodable {
    let id: String
    let name: String
    let horsePictureUrl: String?
}


// MARK: - My Horses View
struct MyHorses: View {
    @StateObject private var vm = HorseViewModel()
    @State private var showAddHorseSheet = false
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Text("My horses")
                    .font(.title)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, alignment: .center) // Aligns text to the right
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)
                NavigationView {
                    ZStack {
                        // Set the background color for the whole screen based on color scheme
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                            .color(for: colorScheme)
                            .edgesIgnoringSafeArea(.all) // Ensure the background fills the screen
                        
                        ScrollView {
                            VStack(spacing: 10) {
                                ForEach(vm.horses) { horse in
                                    NavigationLink(destination: HorseDetails(horse: horse)) {
                                        HStack {
                                            if let urlString = horse.horsePictureUrl, let url = URL(string: urlString) {
                                                AsyncImage(url: url) { image in
                                                    image.resizable()
                                                        .scaledToFill() // Ensures all images fill the same frame
                                                        .frame(width: 80, height: 80) // Uniform size for all images
                                                        .clipShape(Circle())
                                                } placeholder: {
                                                    ProgressView()
                                                        .frame(width: 80, height: 80) // Ensures placeholder also has the same size
                                                }
                                            } else {
                                                // Placeholder if no image URL is available
                                                Circle()
                                                    .fill(Color.gray.opacity(0.5))
                                                    .frame(width: 80, height: 80)
                                            }
                                            
                                            Text(horse.name)
                                                .font(.headline)
                                        }
                                        .padding()
                                        .frame(maxWidth: .infinity, minHeight: 100, alignment: .leading) // Ensures a consistent row height
                                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                        
                                    }
                                    .buttonStyle(PlainButtonStyle()) // Removes default navigation link styling
                                }
                            }
                        }
                    }
                    .edgesIgnoringSafeArea(.top) // Ensures it can be placed above navigation elements
                    
                    .toolbar {
                        ToolbarItem(placement: .bottomBar) {
                            Button(action: { showAddHorseSheet = true }) {
                                Image(systemName: "plus")
                                    .resizable()
                                    .scaledToFill() // Ensures the image fills
                                    .frame(width: 30, height: 30) // Adjust the icon size
                                    .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                                    .padding(20) // Ensures touch target is bigger
                                    .background(
                                        Circle()
                                            .fill(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                            .frame(width: 60, height: 60) // Adjusts the button size
                                            .shadow(radius: 3)
                                    )
                            }
                            .padding(.leading, 320)
                            .padding(.bottom, 30)
                        }
                    }
                    .sheet(isPresented: $showAddHorseSheet) {
                        AddHorseView(vm: vm)
                    }
                }
                .navigationBarBackButtonHidden(true) // Hides the default back button
            }
            .onAppear {
                vm.fetchHorses()
            }
            
            // MARK: - Custom Back Button
            VStack {
                HStack {
                    Button(action: {
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "arrow.left")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 30, height: 30)
                            .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    }
                    .position(x: 25, y: 20)
                    
                    Spacer()
                }
                Spacer()
            }
        }
    }
}


// MARK: - Add Horse View
struct AddHorseView: View {
    @ObservedObject var vm: HorseViewModel
    @State private var horseName = ""
    @State private var selectedImage: UIImage?
    @State private var horseBreed = ""
    @State private var horseAge = ""
    @State private var showImagePicker = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            Form {
                // Section for horse details
                Section(header: Text("Horse Details")) {
                    TextField("Enter horse name", text: $horseName)
                }
                
                // Section for image selection
                Section(header: Text("Image Selection")) {
                    Button(action: {
                        showImagePicker = true
                    }) {
                        Text("Select Image")
                    }
                    
                    if let image = selectedImage {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 100)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                }
                
                // Section for adding the horse
                Section {
                    Button(action: {
                        vm.addHorse(name: horseName, image: selectedImage) // Calls the ViewModel's addHorse method
                        horseName = "" // Reset the input fields
                        selectedImage = nil
                        presentationMode.wrappedValue.dismiss() // Close the sheet
                    }) {
                        Text("Add Horse")
                            .frame(maxWidth: .infinity, alignment: .center)
                    }
                    .disabled(horseName.isEmpty || selectedImage == nil) // Ensure name and image are provided
                    
                }
            }
            .navigationTitle("Add a Horse")
            .toolbar {
                ToolbarItem(placement: .bottomBar) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .sheet(isPresented: $showImagePicker) {
                ImagePicker(sourceType: .photoLibrary, selectedImage: $selectedImage)
            }
        }
    }
}


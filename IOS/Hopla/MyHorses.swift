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
            
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200, let data = data else {
                print("Invalid response or status code")
                return
            }
            
            do {
                let horses = try JSONDecoder().decode([Horse].self, from: data)
                DispatchQueue.main.async {
                    self.horses = horses
                }
            } catch {
                print("Error decoding horse details:", error.localizedDescription)
            }
        }.resume()
    }
    
    func addHorse(name: String, breed: String?, age: Int?, horsePictureURL: UIImage?, dob: String?) {
        var imageUrlString: String? = nil
        
        // Convert UIImage to URL string (Assuming you have logic to store it)
        if let image = horsePictureURL {
            // Implement logic to upload/store image and retrieve URL
            // For now, just set imageUrlString to a placeholder or nil
            imageUrlString = "https://example.com/sample.jpg"
        }
        
        let newHorse = Horse(
            name: name,
            breed: breed,
            age: age,
            horsePictureUrl: imageUrlString,
            dob: dob
        )
        
        horses.append(newHorse)
    }
}



// MARK: - Horse Model
struct Horse: Identifiable, Decodable {
    var id: String
    var name: String
    var breed: String?
    var age: Int?
    var horsePictureUrl: String?
    var dob: String?

    // Regular initializer
    init(id: String = UUID().uuidString, name: String, breed: String? = nil, age: Int? = nil, horsePictureUrl: String? = nil, dob: String? = nil) {
        self.id = id
        self.name = name
        self.breed = breed
        self.age = age
        self.horsePictureUrl = horsePictureUrl
        self.dob = dob
    }

    // Custom decoder for 'dob' if necessary
    private enum CodingKeys: String, CodingKey {
        case id, name, breed, age, horsePictureUrl, dob
    }

    private enum DOBKeys: String, CodingKey {
        case year, month, day
    }

    // Custom decoding to handle DOB format
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(String.self, forKey: .id) ?? UUID().uuidString
        name = try container.decode(String.self, forKey: .name)
        breed = try container.decodeIfPresent(String.self, forKey: .breed)
        age = try container.decodeIfPresent(Int.self, forKey: .age)
        horsePictureUrl = try container.decodeIfPresent(String.self, forKey: .horsePictureUrl)

        if let dobContainer = try? container.nestedContainer(keyedBy: DOBKeys.self, forKey: .dob) {
            let year = try dobContainer.decode(Int.self, forKey: .year)
            let month = try dobContainer.decode(Int.self, forKey: .month)
            let day = try dobContainer.decode(Int.self, forKey: .day)
            dob = "\(year)-\(String(format: "%02d", month))-\(String(format: "%02d", day))"
        } else {
            dob = nil
        }
    }
}




struct MyHorses: View {
    @StateObject private var vm = HorseViewModel()
    @State private var showAddHorseSheet = false
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                HeaderView(colorScheme: colorScheme)
                NavigationStack {
                    
                    ZStack {
                        AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                            .color(for: colorScheme)
                            .edgesIgnoringSafeArea(.all)
                        
                        HorseListView(vm: vm, colorScheme: colorScheme)
                    }
                    .toolbar {  // Move toolbar directly inside NavigationView
                        ToolbarItem(placement: .bottomBar) {
                            AddHorseButton(showAddHorseSheet: $showAddHorseSheet, colorScheme: colorScheme)
                        }
                    }
                     
                }
                .sheet(isPresented: $showAddHorseSheet) {
                    AddHorseView(vm: vm)
                }
                .navigationBarBackButtonHidden(true)
            }
            .onAppear {
                vm.fetchHorses()
            }
            CustomBackButton(colorScheme: colorScheme)
        }
    }
}

// MARK: - Header
struct HeaderView: View {
    var colorScheme: ColorScheme
    
    var body: some View {
        Text("My horses")
            .font(.title)
            .fontWeight(.bold)
            .frame(maxWidth: .infinity)
            .frame(height: 40)
            .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
            .foregroundColor(.white)
    }
}

// MARK: - Horse List
struct HorseListView: View {
    @ObservedObject var vm: HorseViewModel
    var colorScheme: ColorScheme
    
    var body: some View {
        ScrollView {
            VStack(spacing: 10) {
                ForEach(vm.horses) { horse in
                    NavigationLink(destination: HorseDetails(horseId: horse.id)) {
                        HorseRowView(horse: horse, colorScheme: colorScheme)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
    }
}


// MARK: - Horse Row
struct HorseRowView: View {
    var horse: Horse
    var colorScheme: ColorScheme
    
    var body: some View {
        HStack {
            if let urlString = horse.horsePictureUrl, let url = URL(string: urlString) {
                AsyncImage(url: url) { image in
                    image.resizable()
                        .scaledToFill()
                        .frame(width: 80, height: 80)
                        .clipShape(Circle())
                } placeholder: {
                    ProgressView()
                        .frame(width: 80, height: 80)
                }
            } else {
                Circle()
                    .fill(Color.gray.opacity(0.5))
                    .frame(width: 80, height: 80)
            }
            
            Text(horse.name)
                .font(.headline)
        }
        .padding()
        .frame(maxWidth: .infinity, minHeight: 100, alignment: .leading)
        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
    }
}

// MARK: - Add Horse Button
struct AddHorseButton: View {
    @Binding var showAddHorseSheet: Bool
    var colorScheme: ColorScheme
    
    var body: some View {
        Button(action: { showAddHorseSheet = true }) {
            Image(systemName: "plus")
                .resizable()
                .scaledToFill()
                .frame(width: 30, height: 30)
                .foregroundColor(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                .padding(20)
                .background(
                    Circle()
                        .fill(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                        .frame(width: 60, height: 60)
                        .shadow(radius: 3)
                )
        }
    }
}

// MARK: - Custom Back Button
struct CustomBackButton: View {
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.dismiss) private var dismiss
    
    var colorScheme: ColorScheme
    
    var body: some View {
        VStack {
            HStack {
                Button(action: {
                    dismiss()
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
                Section(header: Text("Horse Details")) {
                    TextField("Enter horse name", text: $horseName)
                    TextField("Enter breed", text: $horseBreed)
                    TextField("Enter age", text: $horseAge)
                        .keyboardType(.numberPad)
                }
                
                Section(header: Text("Image Selection")) {
                    Button(action: { showImagePicker = true }) {
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
                
                Section {
                    Button(action: {
                        if let ageInt = Int(horseAge) {
                            vm.addHorse(
                                name: horseName,
                                breed: horseBreed,
                                age: ageInt,
                                horsePictureURL: selectedImage, // Corrected parameter name
                                dob: nil // Provide a default value or user input
                            )
                            presentationMode.wrappedValue.dismiss()
                        }
                    }) {
                        Text("Add Horse")
                            .frame(maxWidth: .infinity, alignment: .center)
                    }
                    .disabled(horseName.isEmpty || horseBreed.isEmpty || horseAge.isEmpty || selectedImage == nil)
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



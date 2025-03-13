//
//  MyHorses.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/02/2025.
//

import SwiftUI

// MARK: - Horse ViewModel
class HorseViewModel: ObservableObject {
    @Published var horses: [Horse] = [
        Horse(name: "Horse1", image: UIImage(named: "HorseImage")),
        Horse(name: "Horse2", image: UIImage(named: "HorseImage2")),
        Horse(name: "Horse3", image: UIImage(named: "HorseImage3"))
    ]
    
    func addHorse(name: String, image: UIImage?) {
        let newHorse = Horse(name: name, image: image)
        horses.append(newHorse)
        horses.sort { $0.name < $1.name }  // Sort alphabetically
    }
}

// MARK: - Horse Model
struct Horse: Identifiable {
    let id = UUID()
    let name: String
    let image: UIImage?
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
                                            if let image = horse.image {
                                                Image(uiImage: image)
                                                    .resizable()
                                                    .scaledToFit()
                                                    .frame(width: 80, height: 80)
                                                    .clipShape(Circle())
                                            }
                                            Text(horse.name)
                                                .font(.headline)
                                                .foregroundColor(AdaptiveColor.text.color(for: colorScheme))
                                        }
                                        .frame(width: 380, height: 120)
                                        .background(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
                                        .padding(5)
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
                    .position(x: 25, y: 20) // Adjust for exact placement
                    
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
    @State private var showImagePicker = false
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Horse Details")) {
                    TextField("Enter horse name", text: $horseName)
                    
                    Button("Select Image") {
                        showImagePicker = true
                    }
                    
                    if let image = selectedImage {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFit()
                            .frame(height: 100)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    
                    Button("Add") {
                        vm.addHorse(name: horseName, image: selectedImage)
                        horseName = ""
                        selectedImage = nil
                    }
                    .disabled(horseName.isEmpty || selectedImage == nil)
                    .frame(maxWidth: .infinity, alignment: .center)
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

//
//  MyHorses.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/02/2025.
//

/**
 import SwiftUI
 
 // MARK: - Horse Model
 struct Horse: Identifiable {
 let id = UUID()
 let name: String
 let imageName: String
 }
 
 struct MyHorses: View {
 
 
 
 @Environment(\.colorScheme) var colorScheme
 
 @State private var horses: [Horse] = [
 Horse(name: "Horse1", imageName: "HorseImage"),
 Horse(name: "Horse2", imageName: "HorseImage2"),
 Horse(name: "Horse3", imageName: "HorseImage3"),
 Horse(name: "Horse4", imageName: "HorseImage"),
 Horse(name: "Horse5", imageName: "HorseImage2"),
 Horse(name: "Horse6", imageName: "HorseImage3")
 ]
 
 var body: some View {
 VStack {
 NavigationView {
 VStack {
 ScrollView {
 VStack(spacing: 10) {
 ForEach($horses) { $horse in
 HorseCard(horse: $horse)
 }
 }
 .padding(.horizontal)
 }
 }
 .background(colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
 
 }
 }
 }
 
 private func horseBinding(for horse: Horse) -> Binding<Horse>? {
 guard let index = horses.firstIndex(where: { $0.id == horse.id }) else {
 return nil
 }
 return $horses[index]
 }
 
 
 }
 
 // MARK: - Horse Card
 
 struct HorseCard: View {
 @Environment(\.colorScheme) var colorScheme
 @Binding var horse: Horse
 
 var body: some View {
 NavigationLink(destination: HorseDetails(horse: horse)) {
 VStack {
 ZStack(alignment: .leading) {
 
 Rectangle()
 .fill(AdaptiveColor(light: .white, dark: .black).color(for: colorScheme))
 .frame(width: 380, height: 120)
 
 HStack {
 Image(horse.imageName)
 .resizable()
 .scaledToFill()
 .frame(width: 100, height: 100)
 .clipShape(Circle())
 
 Text(horse.name)
 .padding(.leading, 10)
 .foregroundStyle(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
 }
 }
 }
 .shadow(radius: 3)
 }
 .buttonStyle(PlainButtonStyle()) // Removes default navigation link styling
 }
 }
 */

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
        VStack {
            /*
            Text("")
                .frame(maxWidth: .infinity)
                .frame(height: 0)
                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
            */
            NavigationView {
                ZStack {
                    // Set the background color for the whole screen based on color scheme
                    AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground)
                        .color(for: colorScheme)
                        .edgesIgnoringSafeArea(.all) // Ensure the background fills the screen

                    ScrollView {
                        VStack {
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
                    
                    // Custom Back Button
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
                            .padding(.bottom, 150) // Adjust the vertical position
                            .padding(.leading, 20) // Adjust the horizontal position
                            Spacer()
                        }
                        Spacer()
                    }
                }
                .navigationBarBackButtonHidden(true) // Hides the default back button
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

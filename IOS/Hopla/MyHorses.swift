//
//  MyHorses.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/02/2025.
//

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

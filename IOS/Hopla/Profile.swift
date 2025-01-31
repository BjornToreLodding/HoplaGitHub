//
//  Profile.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 28/01/2025.
//

import SwiftUI

struct Profile: View {
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        ZStack {
            AdaptiveColor.background.color(for: colorScheme)
                .ignoresSafeArea()
            
            ScrollView {
                VStack {
                    ZStack {
                        Circle()
                            .frame(width: 200, height: 200)
                            .foregroundColor(.white)
                            .padding(.top, 10)
                        
                        Image("Profile")
                            .resizable()
                            .scaledToFill()
                            .frame(width: 180, height: 180)
                            .clipShape(Circle())
                            .padding(.top, 10)
                    }
                    
                    Text("Change profile picture")
                        .underline()
                        .padding(.top, 10)
                    
                    HStack {
                        Button("My hikes") {}
                            .frame(width: 120, height: 50)
                            .background(Color.white)
                            .foregroundStyle(Color.black)
                        
                        Button("Friends") {}
                            .frame(width: 120, height: 50)
                            .background(Color.white)
                            .foregroundStyle(Color.black)
                        
                        Button("Following") {}
                            .frame(width: 120, height: 50)
                            .background(Color.white)
                            .foregroundStyle(Color.black)
                    }
                    .padding(.top, 20)
                    
                    ZStack {
                        Rectangle()
                            .frame(width: 380, height: 280)
                            .foregroundColor(.white)
                            .padding(.top, 20)
                        
                        VStack {
                            Text("Username")
                                .frame(width: 360, height: 40)
                                .background(Color.darkBeige)
                            
                            Text("Bob")
                                .frame(width: 360, height: 40)
                                .background(Color.lightBeige)
                            
                            Text("E-mail")
                                .frame(width: 360, height: 40)
                                .background(Color.darkBeige)
                            
                            Text("bob@mail.no")
                                .frame(width: 360, height: 40)
                                .background(Color.lightBeige)
                            
                            Text("Change password")
                                .frame(width: 360, height: 40)
                                .background(Color.darkBeige)
                        }
                    }
                    
                    // Button for Settings
                    NavigationLink(destination: SettingsView()) {
                        HStack {
                            Image(systemName: "gearshape")
                                .font(.system(size: 20))
                            Text("Settings")
                        }
                        .padding()
                        .frame(width: 200)
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(10)
                    }
                    .padding(.top, 20)
                    
                    Spacer()
                }
            }
        }
        .navigationTitle("Profile")
    }
}


struct SettingsView: View {
    var body: some View {
        VStack {
            Text("Settings")
                .font(.largeTitle)
                .padding()
            
            Text("Settings content goes here...")
            
            Spacer()
        }
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
    }
}


#Preview("English") {
    ContentView()
}

#Preview("Norsk") {
    ContentView()
        .environment(\.locale, Locale(identifier: "nb_NO"))
}

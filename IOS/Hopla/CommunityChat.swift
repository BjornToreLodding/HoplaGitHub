//
//  CommunityChat.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/02/2025.
//

import SwiftUI

struct CommunityChat: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    
    let stable: Stable
    @State private var messages: [Message] = [
        Message(id: UUID(), sender: "Alice", text: "Hello everyone!", time: "10:30", date: "27/02/2025"),
        Message(id: UUID(), sender: "Bob", text: "Hey Alice!", time: "10:32", date: "27/02/2025")
    ]
    @State private var newMessage: String = ""
    @State private var scrollToBottom = UUID() // Track last message for scrolling
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) { // Removes white space
                // Group Name Header
                
                Text(stable.stableName)
                    .font(.title)
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity, alignment: .center) // Aligns text to the right
                    .frame(height: 40)
                    .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
                    .foregroundColor(.white)

                
                
                // Messages Section
                ZStack {
                    (colorScheme == .dark ? Color.mainDarkBackground : Color.mainLightBackground)
                        .edgesIgnoringSafeArea(.all)
                    
                    NavigationView {
                        ScrollViewReader { scrollView in
                            ScrollView {
                                VStack(alignment: .leading, spacing: 10) {
                                    ForEach(messages, id: \.id) { message in
                                        VStack {
                                            // Show Date in Center
                                            if shouldShowDate(for: message) {
                                                Text(message.date)
                                                    .font(.footnote)
                                                    .foregroundColor(.gray)
                                                    .frame(maxWidth: .infinity, alignment: .center)
                                                    .padding(.vertical, 5)
                                            }
                                            
                                            HStack {
                                                if message.sender == "Me" {
                                                    Spacer()
                                                    VStack(alignment: .trailing) {
                                                        Text("\(message.time): \(message.sender)")
                                                            .font(.caption2)
                                                            .foregroundColor(.gray)
                                                        
                                                        Text(message.text)
                                                            .padding()
                                                            .background(Color.green.opacity(0.8))
                                                            .cornerRadius(10)
                                                            .foregroundColor(.white)
                                                    }
                                                } else {
                                                    VStack(alignment: .leading) {
                                                        Text("\(message.time): \(message.sender)")
                                                            .font(.caption2)
                                                            .foregroundColor(.gray)
                                                        
                                                        Text(message.text)
                                                            .padding()
                                                            .background(Color.gray.opacity(0.3))
                                                            .cornerRadius(10)
                                                            .foregroundColor(.black)
                                                    }
                                                    Spacer()
                                                }
                                            }
                                        }
                                        .id(message.id) // Assign ID for scrolling
                                    }
                                }
                                .padding()
                            }
                            // Background
                            .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                            .onChange(of: messages.count) { _ in
                                if let lastMessage = messages.last {
                                    DispatchQueue.main.async {
                                        scrollView.scrollTo(lastMessage.id, anchor: .bottom)
                                    }
                                }
                            }
                        }
                    }
                    .navigationBarBackButtonHidden(true) // Hides the default back button
                }
                .edgesIgnoringSafeArea(.top)
                
                // Message Input Field
                HStack {
                    TextField("Type a message...", text: $newMessage)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(8)
                    
                    Button(action: sendMessage) {
                        Image(systemName: "paperplane.fill")
                            .foregroundColor(newMessage.isEmpty ? .gray : .green)
                            .padding()
                    }
                    .disabled(newMessage.isEmpty)
                }
                .background(AdaptiveColor(light: .lighterGreen, dark: .darkGreen).color(for: colorScheme))
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
                    }
                    .padding()
                    .foregroundStyle(AdaptiveColor(light: .black, dark: .white).color(for: colorScheme))
                    .position(x: 25, y: 20) // Adjust for exact placement

                    Spacer()
                }
                Spacer()
            }
        }
    }

    
    // Show date if the messages are sent on a different day
    private func shouldShowDate(for message: Message) -> Bool {
        if let index = messages.firstIndex(where: { $0.id == message.id }), index > 0 {
            return messages[index - 1].date != message.date
        }
        return true
    }
    
    // Send message function
    private func sendMessage() {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        let currentTime = formatter.string(from: Date())
        
        formatter.dateFormat = "dd/MM/yyyy"
        let currentDate = formatter.string(from: Date())
        
        let newMsg = Message(id: UUID(), sender: "Me", text: newMessage, time: currentTime, date: currentDate)
        messages.append(newMsg)
        newMessage = ""
    }
}

//MARK: - Message Model
struct Message: Identifiable {
    let id: UUID
    let sender: String
    let text: String
    let time: String
    let date: String
}

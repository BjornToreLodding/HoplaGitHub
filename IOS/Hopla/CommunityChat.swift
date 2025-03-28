//
//  CommunityChat.swift
//  Hopla
//
//  Created by Ane Marie Johnsen on 27/02/2025.
//

import SwiftUI

struct ServerMessage: Decodable {
    let content: String
    let timestamp: String
    let senderId: String
    let senderAlias: String
}

private func formatTime(from isoString: String) -> String {
    let formatter = ISO8601DateFormatter()
    formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds] // Handles timestamps with milliseconds
    
    if let date = formatter.date(from: isoString) {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "HH:mm"
        return timeFormatter.string(from: date)
    }
    return "Unknown Time"
}

private func formatDate(from isoString: String) -> String {
    let formatter = ISO8601DateFormatter()
    formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds] // Consistent with `formatTime`
    
    if let date = formatter.date(from: isoString) {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"
        return dateFormatter.string(from: date)
    }
    return "Unknown Date"
}

struct CommunityChat: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    
    let stable: Stable
    @State private var messages: [Message] = [] // Start with an empty array for messages
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
                                    if messages.isEmpty {
                                        Text("No messages yet.")
                                            .foregroundColor(.gray)
                                            .italic()
                                    } else {
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
            .onAppear {
                fetchMessages() // Fetch messages when the view appears
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
    
    private func fetchMessages() {
        let baseUrl = "https://hopla.onrender.com/stablemessages"
        guard let url = URL(string: "\(baseUrl)/\(stable.stableId)?pagesize=10&pagenumber=1") else {
            print("Invalid URL")
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error fetching messages:", error.localizedDescription)
                return
            }

            guard let data = data, !data.isEmpty else {
                print("No data received or response body is empty")
                return
            }

            do {
                let decodedResponse = try JSONDecoder().decode([ServerMessage].self, from: data)

                let fetchedMessages = decodedResponse.compactMap { serverMessage -> Message? in
                    let formatter = ISO8601DateFormatter()
                    formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]

                    guard let date = formatter.date(from: serverMessage.timestamp) else {
                        print("Invalid timestamp:", serverMessage.timestamp)
                        return nil
                    }

                    return Message(
                        id: UUID(),
                        sender: serverMessage.senderAlias,  // Show sender alias instead of sender ID
                        text: serverMessage.content,
                        time: formatTime(from: serverMessage.timestamp),
                        date: formatDate(from: serverMessage.timestamp),
                        dateObject: date // Store the actual Date object
                    )
                }

                DispatchQueue.main.async {
                    self.messages = fetchedMessages.sorted { $0.dateObject < $1.dateObject }
                    print("Messages updated in UI.")
                }
            } catch {
                print("Decoding error:", error.localizedDescription)
            }
        }.resume()
    }

    
    
    
    // Send message function
    private func sendMessage() {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        let currentTime = formatter.string(from: Date())
        
        formatter.dateFormat = "dd/MM/yyyy"
        let currentDate = formatter.string(from: Date())
        
        // Prepare the new message locally
        let newMsg = Message(id: UUID(), sender: "Me", text: newMessage, time: currentTime, date: currentDate, dateObject: Date())
        messages.append(newMsg)
        messages.sort { $0.time < $1.time } // Keep messages in chronological order

        
        // Clear the input field
        newMessage = ""
        
        // Prepare JSON body for POST request
        let messagePayload: [String: Any] = [
            "StableId": stable.stableId,
            "Content": newMsg.text
        ]
        
        guard let url = URL(string: "https://hopla.onrender.com/stablemessages") else {
            print("Invalid URL")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(TokenManager.shared.getToken() ?? "")", forHTTPHeaderField: "Authorization")
        
        // Encode the JSON payload
        do {
            let requestBody = try JSONSerialization.data(withJSONObject: messagePayload, options: [])
            request.httpBody = requestBody
        } catch {
            print("Error encoding message payload:", error.localizedDescription)
            return
        }
        
        // Send the request
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error sending message:", error.localizedDescription)
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                print("HTTP Status Code:", httpResponse.statusCode)
                if !(200...299).contains(httpResponse.statusCode) {
                    print("Failed to send message.")
                    return
                }
            }
            
            // Handle response if necessary
        }.resume()
    }
}


//MARK: - Message Model
struct Message: Identifiable {
    let id: UUID
    let sender: String
    let text: String
    let time: String
    let date: String
    let dateObject: Date // New field to store actual Date object
}


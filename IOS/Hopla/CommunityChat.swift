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

// MARK: - Custom Back Button
struct CustomBackButtonCommunityChat: View {
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
                        .foregroundStyle(AdaptiveColor(light: .lightModeTextOnGreen, dark: .darkModeTextOnGreen).color(for: colorScheme))
                }
                .position(x: 15, y: 5)
                Spacer()
            }
            Spacer()
        }
    }
}

struct CommunityChat: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.dismiss) private var dismiss
    
    let stable: Stable
    var scrollToBottom: Bool
    
    @State private var messages: [Message] = []
    @State private var newMessage: String = ""
    @State private var shouldScroll = false  // Track when to scroll
    
    var body: some View {
            NavigationStack {
                ZStack(alignment: .topLeading) {
                    VStack(spacing: 0) {
                        ScrollViewReader { scrollViewProxy in
                            VStack(spacing: 0) {
                                // MARK: — Group Name Header
                                Text(stable.stableName)
                                    .font(.custom("ArialNova", size: 20))
                                    .fontWeight(.bold)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 40)
                                    .background(
                                        AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                                            .color(for: colorScheme)
                                    )
                                    .foregroundColor(.white)

                                // MARK: — Messages Section
                                ScrollView {
                                    VStack(alignment: .leading, spacing: 10) {
                                        ForEach(messages, id: \.id) { message in
                                            VStack {
                                                if shouldShowDate(for: message) {
                                                    Text(message.date)
                                                        .font(.footnote)
                                                        .foregroundStyle(
                                                            AdaptiveColor(light: .textLightBackground,
                                                                          dark:  .textDarkBackground)
                                                                .color(for: colorScheme)
                                                        )
                                                        .frame(maxWidth: .infinity)
                                                        .padding(.vertical, 5)
                                                }

                                                HStack {
                                                    if message.sender == "Me" {
                                                        Spacer()
                                                        VStack(alignment: .trailing) {
                                                            Text("\(message.time): Me")
                                                                .font(.caption2)
                                                                .foregroundStyle(
                                                                    AdaptiveColor(light: .textLightBackground,
                                                                                  dark:  .textDarkBackground)
                                                                        .color(for: colorScheme)
                                                                )
                                                            Text(message.text)
                                                                .padding()
                                                                .background(
                                                                    AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                                                                        .color(for: colorScheme)
                                                                        .opacity(0.8)
                                                                )
                                                                .cornerRadius(10)
                                                                .foregroundColor(.white)
                                                        }
                                                    } else {
                                                        VStack(alignment: .leading) {
                                                            Text("\(message.time): \(message.sender)")
                                                                .font(.caption2)
                                                                .foregroundStyle(
                                                                    AdaptiveColor(light: .textLightBackground,
                                                                                  dark:  .textDarkBackground)
                                                                        .color(for: colorScheme)
                                                                )
                                                            Text(message.text)
                                                                .padding()
                                                                .background(
                                                                    AdaptiveColor(light: .lightPostBackground,
                                                                                  dark:  .darkPostBackground)
                                                                        .color(for: colorScheme)
                                                                )
                                                                .cornerRadius(10)
                                                                .foregroundStyle(
                                                                    AdaptiveColor(light: .textLightBackground,
                                                                                  dark:  .textDarkBackground)
                                                                        .color(for: colorScheme)
                                                                )
                                                        }
                                                        Spacer()
                                                    }
                                                }
                                            }
                                            .id(message.id)
                                        }
                                    }
                                    .padding()
                                }

                                // MARK: — Input Field
                                HStack {
                                    TextField("Type a message…", text: $newMessage)
                                        .textFieldStyle(RoundedBorderTextFieldStyle())
                                        .padding(8)

                                    Button {
                                        sendMessage(scrollViewProxy: scrollViewProxy)
                                    } label: {
                                        Image(systemName: "paperplane.fill")
                                            .foregroundColor(newMessage.isEmpty ? .white : .green)
                                            .padding()
                                    }
                                    .disabled(newMessage.isEmpty)
                                }
                                .background(
                                    AdaptiveColor(light: .lightGreen, dark: .darkGreen)
                                        .color(for: colorScheme)
                                )
                            }
                            .onAppear {
                                fetchMessages(scrollViewProxy: scrollViewProxy)
                            }
                            .onChange(of: messages.count) { _ in
                                DispatchQueue.main.async {
                                    if let last = messages.last {
                                        scrollViewProxy.scrollTo(last.id, anchor: .bottom)
                                    }
                                }
                            }
                        }
                    }

                    // MARK: — Custom Back Button Overlay
                    CustomBackButtonCommunityChat(colorScheme: colorScheme)
                        .padding(.leading, 16)
                        .padding(.top, 16)
                }
                .background(AdaptiveColor(light: .mainLightBackground, dark: .mainDarkBackground).color(for: colorScheme))
                .navigationBarBackButtonHidden(true)
            }
        }
    
    
    // Show date if the messages are sent on a different day
    private func shouldShowDate(for message: Message) -> Bool {
        if let index = messages.firstIndex(where: { $0.id == message.id }), index > 0 {
            return messages[index - 1].date != message.date
        }
        return true
    }
    
    private func fetchMessages(scrollViewProxy: ScrollViewProxy) {
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
                
                guard let currentUserId = TokenManager.shared.getUserId() else {
                    print("Could not retrieve user ID")
                    return
                }
                
                let fetchedMessages = decodedResponse.compactMap { serverMessage -> Message? in
                    let formatter = ISO8601DateFormatter()
                    formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
                    
                    guard let date = formatter.date(from: serverMessage.timestamp) else {
                        print("Invalid timestamp:", serverMessage.timestamp)
                        return nil
                    }
                    
                    let isCurrentUser = serverMessage.senderId == currentUserId
                    let senderDisplayName = isCurrentUser ? "Me" : serverMessage.senderAlias
                    
                    return Message(
                        id: UUID(),
                        sender: senderDisplayName,
                        text: serverMessage.content,
                        time: formatTime(from: serverMessage.timestamp),
                        date: formatDate(from: serverMessage.timestamp),
                        dateObject: date
                    )
                }
                
                DispatchQueue.main.async {
                    self.messages = fetchedMessages.sorted { $0.dateObject < $1.dateObject }
                    print("Messages updated in UI.")
                    
                    // 🛠 Delay to ensure UI updates before scrolling
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                        if let lastMessage = self.messages.last {
                            scrollViewProxy.scrollTo(lastMessage.id, anchor: .bottom)
                        }
                    }
                }
            } catch {
                print("Decoding error:", error.localizedDescription)
            }
        }.resume()
    }
    
    
    
    // Send message function
    private func sendMessage(scrollViewProxy: ScrollViewProxy) {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        let currentTime = formatter.string(from: Date())
        
        formatter.dateFormat = "dd/MM/yyyy"
        let currentDate = formatter.string(from: Date())
        
        // Prepare the new message locally
        let newMsg = Message(id: UUID(), sender: "Me", text: newMessage, time: currentTime, date: currentDate, dateObject: Date())
        messages.append(newMsg)
        
        // Correct sorting by `dateObject`
        messages.sort { $0.dateObject < $1.dateObject }
        
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
                //print("HTTP Status Code:", httpResponse.statusCode)
                if !(200...299).contains(httpResponse.statusCode) {
                    print("Failed to send message.")
                    return
                }
            }
            
            // Scroll to the bottom of the chat after sending
            DispatchQueue.main.async {
                if let lastMessage = messages.last {
                    scrollViewProxy.scrollTo(lastMessage.id, anchor: .bottom)
                }
            }
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


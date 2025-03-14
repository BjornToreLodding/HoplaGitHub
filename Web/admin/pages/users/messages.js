export async function render(container) {
    container.innerHTML = `
        <h2>Meldinger</h2>
        <div id="message-container">
            <div id="conversations">
                <h3>Dine samtaler</h3>
                <ul id="conversation-list"></ul>
            </div>
            <div id="chat">
                <h3 id="chat-title">Velg en samtale</h3>
                <div id="chat-messages"></div>
                <div id="chat-input-container" class="hidden">
                    <input type="text" id="message-input" placeholder="Skriv en melding...">
                    <button id="send-message">Send</button>
                </div>
            </div>
        </div>
    `;

    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.name) {
        container.innerHTML = "<p>Du må være logget inn for å se meldinger.</p>";
        return;
    }

    const userId = localStorage.getItem("authToken") ? userInfo.id : null;
    if (!userId) {
        console.error("Ingen bruker-ID funnet.");
        return;
    }

    console.log("🔍 Henter samtaler for bruker:", userId);
    await loadConversations(userId);
}

async function loadConversations(userId) {
    const conversationList = document.getElementById("conversation-list");
    conversationList.innerHTML = "<p>Laster samtaler...</p>";

    try {
        //const response = await fetch(`https://localhost:7128/messages/${userId}`);
        const response = await fetch(`https://localhost:7128/messages/12345678-0000-0000-0001-123456780001`);
        if (!response.ok) throw new Error(`Feil ved henting av meldinger: ${response.status}`);

        const conversations = await response.json();
        console.log("✅ Samtaler mottatt:", conversations);

        conversationList.innerHTML = ""; // Tøm listen

        conversations.forEach(conv => {
            const partnerId = conv.SenderId === userId ? conv.ReceiverId : conv.SenderId;
            const partnerName = conv.SenderId === userId ? conv.ReceiverName : conv.SenderName;
            const partnerAlias = conv.SenderId === userId ? conv.ReceiverAlias : conv.SenderAlias;
            const partnerPicture = conv.SenderId === userId ? conv.ReceiverPicture : conv.SenderPicture;
            const lastMessage = conv.Content;

            const listItem = document.createElement("li");
            listItem.classList.add("conversation-item");
            listItem.innerHTML = `
                <img src="${partnerPicture || 'default-profile.png'}" class="profile-pic" alt="${partnerName}">
                <div class="conversation-info">
                    <strong>${partnerAlias} (${partnerName})</strong>
                    <p>${lastMessage}</p>
                </div>
            `;

            listItem.addEventListener("click", () => loadChat(userId, partnerId, partnerName));
            conversationList.appendChild(listItem);
        });

    } catch (error) {
        console.error("❌ Feil ved henting av samtaler:", error);
        conversationList.innerHTML = "<p>Kunne ikke laste samtaler.</p>";
    }
}

async function loadChat(userId, partnerId, partnerName) {
    const chatTitle = document.getElementById("chat-title");
    const chatMessages = document.getElementById("chat-messages");
    const chatInputContainer = document.getElementById("chat-input-container");
    
    chatTitle.textContent = `Samtale med ${partnerName}`;
    chatMessages.innerHTML = "<p>Laster meldinger...</p>";
    chatInputContainer.classList.remove("hidden");

    try {
        //const response = await fetch(`https://localhost:7128/messages/${userId}?id=${partnerId}`);
        const response = await fetch(`https://localhost:7128/messages/12345678-0000-0000-0001-123456780001?id=12345678-0000-0000-0001-123456780002`);
        if (!response.ok) throw new Error(`Feil ved henting av meldinger: ${response.status}`);

        const messages = await response.json();
        console.log("✅ Meldinger mottatt:", messages);

        chatMessages.innerHTML = ""; // Tøm chat-loggen

        messages.forEach(msg => {
            const messageItem = document.createElement("div");
            messageItem.classList.add("chat-message", msg.SenderId === userId ? "sent" : "received");
            messageItem.innerHTML = `
                <p><strong>${msg.SenderAlias}:</strong> ${msg.Content}</p>
                <small>${new Date(msg.Timestamp).toLocaleString()}</small>
            `;
            chatMessages.appendChild(messageItem);
        });

    } catch (error) {
        console.error("❌ Feil ved henting av meldinger:", error);
        chatMessages.innerHTML = "<p>Kunne ikke laste meldinger.</p>";
    }
}

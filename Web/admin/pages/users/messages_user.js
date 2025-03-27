export async function render(container) {
    // Hent innlogget bruker og samtalepartner fra sessionStorage
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    const userId = userInfo ? userInfo.id : null;
    const userPicture = userInfo ? userInfo.profilePictureURL : "default-profile.png";
    const partnerId = sessionStorage.getItem("chatPartnerId");
    const partnerName = sessionStorage.getItem("chatPartnerName");

    if (!userId || !partnerId) {
        container.innerHTML = "<p>Feil: Mangler bruker-ID eller samtalepartner.</p>";
        return;
    }

    container.innerHTML = `
        <div id="chat-header">
            <h2>Meldinger med ${partnerName}</h2>
            <img id="partner-avatar" src="default-profile.png" alt="${partnerName}">
        </div>
        <div id="chat-container">
            <div id="chat-messages"></div>
        </div>
    `;

    console.log(`🔍 Henter meldinger mellom ${userId} og ${partnerId}`);

    try {
        const response = await fetch(`https://hopla.onrender.com/messages/${userId}?id=${partnerId}`);
        if (!response.ok) throw new Error(`Feil ved henting av meldinger: ${response.status}`);

        const messages = await response.json();
        console.log("✅ Meldinger mottatt:", messages);

        const chatMessages = document.getElementById("chat-messages");
        const partnerAvatar = document.getElementById("partner-avatar");

        if (messages.length > 0) {
            const partnerPicture = messages[0].senderId === userId ? messages[0].receiverPicture : messages[0].senderPicture;
            partnerAvatar.src = partnerPicture || "default-profile.png";
        }

        chatMessages.innerHTML = ""; // Tøm chat-loggen

        messages.forEach(message => {
            const isSender = message.senderId === userId;
            const messageItem = document.createElement("div");
            messageItem.classList.add("chat-message", isSender ? "sent" : "received");

            const senderPicture = isSender ? userPicture : message.senderPicture;

            messageItem.innerHTML = `
                <div class="message-bubble">
                    <img src="${senderPicture || 'default-profile.png'}" class="profile-pic message-pic">
                    <div class="message-text">
                        <p><strong>${isSender ? "Deg" : message.senderName}:</strong> ${message.content}</p>
                        <small>${new Date(message.timestamp).toLocaleString()}</small>
                    </div>
                </div>
            `;
            chatMessages.appendChild(messageItem);
        });

    } catch (error) {
        console.error("❌ Feil ved henting av meldinger:", error);
        container.innerHTML += "<p>Kunne ikke laste meldinger.</p>";
    }
}

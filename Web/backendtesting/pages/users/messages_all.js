export async function render(container) {
    container.innerHTML = `
        <h2>Dine samtaler</h2>
        <ul id="messages-list"></ul>
    `;

    const userInfo = JSON.parse(localStorage.getItem("userInfo"));
    if (!userInfo || !userInfo.id) {
        container.innerHTML = "<p>Du må være logget inn for å se meldinger.</p>";
        return;
    }

    const userId = userInfo.id;
    console.log("🔍 Henter meldinger for bruker:", userId);
    
    try {
        const response = await fetch(`https://localhost:7128/messages/${userId}`);
        if (!response.ok) throw new Error(`Feil ved henting av meldinger: ${response.status}`);

        const messages = await response.json();
        console.log("✅ Meldinger mottatt:", messages);

        const messagesList = document.getElementById("messages-list");
        messagesList.innerHTML = ""; // Tøm listen før vi fyller inn

        messages.forEach(message => {
            const isSender = message.senderId === userId;
            const partnerId = isSender ? message.receiverId : message.senderId;
            const partnerName = isSender ? message.receiverName : message.senderName;
            const partnerPicture = isSender ? message.receiverPicture : message.senderPicture;
            const messageContent = message.content;
            const timestamp = new Date(message.timestamp).toLocaleString();

            // Opprett listeelement for hver melding
            const listItem = document.createElement("li");
            listItem.classList.add("message-item");
            listItem.innerHTML = `
                <img src="${partnerPicture || 'default-profile.png'}" class="profile-pic" alt="${partnerName}">
                <div class="message-info">
                    <strong>${partnerName}</strong> <small>${timestamp}</small>
                    <p>${messageContent}</p>
                </div>
            `;

            // Når en melding klikkes, åpne `messages_user.js`
            listItem.addEventListener("click", () => {
                loadContent('users', 'messages_user');
                sessionStorage.setItem("chatPartnerId", partnerId);
                sessionStorage.setItem("chatPartnerName", partnerName);
            });

            messagesList.appendChild(listItem);
        });

    } catch (error) {
        console.error("❌ Feil ved henting av meldinger:", error);
        container.innerHTML += "<p>Kunne ikke laste meldinger.</p>";
    }
}

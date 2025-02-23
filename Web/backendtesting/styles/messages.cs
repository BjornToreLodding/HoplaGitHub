/* Stil for samtalelisten */
#messages-list {
    list-style-type: none;
    padding: 0;
}

.message-item {
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ddd;
    cursor: pointer;
    transition: background 0.3s;
}

.message-item:hover {
    background: #f5f5f5;
}

.message-item img.profile-pic {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.message-info {
    flex-grow: 1;
}

.message-info strong {
    display: block;
}

/* Stil for chat */
#chat-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
}

#partner-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
}

#chat-container {
    border: 1px solid #ddd;
    padding: 10px;
    height: 400px;
    overflow-y: auto;
    background: #f9f9f9;
}

.chat-message {
    padding: 5px;
    margin: 5px 0;
}

.sent {
    text-align: right;
    background: #d1e7dd;
    padding: 10px;
    border-radius: 10px;
    max-width: 60%;
    margin-left: auto;
}

.received {
    text-align: left;
    background: #f8d7da;
    padding: 10px;
    border-radius: 10px;
    max-width: 60%;
    margin-right: auto;
}

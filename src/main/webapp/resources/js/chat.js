let socket;
let typingIndicator;

function connect() {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    const contextPath = getApplicationContext();
    const path = `${contextPath}/chat`;
    const wsUrl = `${protocol}//${host}${path}`;

    try {
        socket = new WebSocket(wsUrl);

        socket.onmessage = function (event) {
            hideTypingIndicator(); // Hide typing indicator when message is received
            addMessage(event.data, "bot"); // Display bot response
        };

        socket.onopen = function () {
            console.log("Connected to WebSocket");
        };

        socket.onclose = function () {
            console.log("Disconnected from WebSocket");
        };

        socket.onerror = function (error) {
            console.error("WebSocket error:", error);
        };
    } catch (e) {
        console.error("WebSocket connection failed:", error);
        showErrorBubble("Unable to connect to the chatbot. Please try again later.");
    }
}

function sendMessage() {
    const input = document.getElementById("message-input");
    const message = input.value.trim();

    if (message) {
        addMessage(message, "user"); // Display user message
        showTypingIndicator(); // Show typing indicator before sending message
        socket.send(message); // Send message to server
        input.value = ""; // Clear input field
    }
}

function addMessage(text, type) {
    const chatWindow = document.getElementById("chat-window");
    const messageElement = document.createElement("div");

    messageElement.classList.add("message-bubble", type);
    if (type === "bot") {
        messageElement.innerHTML = `<div class="markdown-content">${marked.parse(text)}</div>`;
    } else {
        messageElement.textContent = text;
    }
    chatWindow.appendChild(messageElement);
    chatWindow.scrollTop = chatWindow.scrollHeight;
}

function showTypingIndicator() {
    if (!typingIndicator) {
        const chatWindow = document.getElementById("chat-window");
        typingIndicator = document.createElement("div");
        typingIndicator.classList.add("message", "bot", "typing");
        typingIndicator.innerHTML = `
            <div class="typing-indicator">
                <span></span><span></span><span></span>
            </div>
        `;
        chatWindow.appendChild(typingIndicator);

        chatWindow.scrollTop = chatWindow.scrollHeight;
    }
}

function hideTypingIndicator() {
    if (typingIndicator) {
        typingIndicator.parentNode.removeChild(typingIndicator);
        typingIndicator = null;
    }
}

function getApplicationContext() {
    const pathname = window.location.pathname;
    const context = pathname.split("/")[1];
    return context ? `/${context}` : "";
}

function showErrorBubble(message) {
    const errorBubble = document.getElementById("error-bubble");
    errorBubble.textContent = message;
    errorBubble.style.display = "flex";
    setTimeout(() => {
        errorBubble.style.display = "none";
    }, 5000);
}

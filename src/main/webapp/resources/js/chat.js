let socket;

function connect() {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    const contextPath = getApplicationContext();
    const path = `${contextPath}/chat`;
    const wsUrl = `${protocol}//${host}${path}`;

    socket = new WebSocket(wsUrl);

    socket.onmessage = function (event) {
        addMessage(event.data, "bot"); // Bot response
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
}

function sendMessage() {
    const input = document.getElementById("message-input");
    const message = input.value.trim();

    if (message) {
        addMessage(message, "user"); // User message
        socket.send(message); // Send to server
        input.value = ""; // Clear input
    }
}

function addMessage(text, type) {
    const chatWindow = document.getElementById("chat-window");
    const messageElement = document.createElement("p");
    messageElement.classList.add(type); // Add "user" or "bot" class
    messageElement.textContent = text;
    chatWindow.appendChild(messageElement);

    // Scroll to the latest message
    chatWindow.scrollTop = chatWindow.scrollHeight;
}

function getApplicationContext() {
    const pathname = window.location.pathname;
    const context = pathname.split("/")[1];
    return context ? `/${context}` : "";
}

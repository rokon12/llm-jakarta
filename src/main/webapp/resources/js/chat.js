let socket;
let typingIndicator;
let currentStreamingMessage = null; // Track the current bot message bubble
let markdownBuffer = ""; // Buffer to hold Markdown fragments during streaming

function connect() {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    const contextPath = getApplicationContext();
    const path = `${contextPath}/chat`;
    const wsUrl = `${protocol}//${host}${path}`;

    try {
        socket = new WebSocket(wsUrl);

        socket.onmessage = function (event) {
            const data = event.data;

            if (data === "[END]") {
                // Finalize the current bot message when streaming ends
                finalizeStreamingMessage();
                hideTypingIndicator();
            } else {
                // Create a new bot bubble if none exists for this response
                if (!currentStreamingMessage) {
                    createNewBotBubble();
                }
                // Append streaming data to the buffer and show raw content temporarily
                appendToStreamingBuffer(data);
            }
        };

        socket.onopen = function () {
            console.log("Connected to WebSocket");
        };

        socket.onclose = function () {
            console.log("Disconnected from WebSocket");
            hideTypingIndicator();
        };

        socket.onerror = function (error) {
            console.error("WebSocket error:", error);
            hideTypingIndicator();
            showErrorBubble("Unable to connect to the chatbot. Please try again later.");
        };
    } catch (e) {
        console.error("WebSocket connection failed:", e);
        showErrorBubble("Unable to connect to the chatbot. Please try again later.");
    }
}

function sendMessage() {
    const input = document.getElementById("message-input");
    const message = input.value.trim();

    if (message) {
        addMessage(message, "user");
        showTypingIndicator();
        socket.send(message);
        input.value = "";
    }
}

function createNewBotBubble() {
    const chatWindow = document.getElementById("chat-window");
    currentStreamingMessage = document.createElement("div");
    currentStreamingMessage.classList.add("message-bubble", "bot");
    chatWindow.appendChild(currentStreamingMessage);

    // Auto-scroll to show the latest message
    chatWindow.scrollTop = chatWindow.scrollHeight;
}

function appendToStreamingBuffer(textFragment) {
    if (currentStreamingMessage) {
        // Append the fragment to the buffer
        markdownBuffer += textFragment;

        // Show raw content temporarily
        currentStreamingMessage.innerHTML = markdownBuffer.endsWith(" ")
            ? markdownBuffer
            : markdownBuffer + " ";
    }
}

function finalizeStreamingMessage() {
    if (currentStreamingMessage) {
        // Render the complete Markdown content
        currentStreamingMessage.innerHTML = `<div class="markdown-content">${marked.parse(markdownBuffer)}</div>`;
        currentStreamingMessage = null; // Reset for the next bot message
        markdownBuffer = ""; // Clear the buffer
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
        typingIndicator.classList.add("message-bubble", "bot", "typing");
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

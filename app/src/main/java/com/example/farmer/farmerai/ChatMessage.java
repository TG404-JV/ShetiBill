package com.example.farmer.farmerai;

public class ChatMessage {

    private String message;  // mesage
    private boolean isUserMessage;

    public ChatMessage(String message, boolean isUserMessage) {
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }

    public void setUserMessage(boolean userMessage) {
        isUserMessage = userMessage;
    }
}


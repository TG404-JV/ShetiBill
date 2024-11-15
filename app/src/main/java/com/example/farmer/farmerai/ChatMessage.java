package com.example.farmer.farmerai;

public class ChatMessage {

    private String message;  // Message content
    private boolean isUserMessage;  // Indicates if the message is from the user
    private boolean isLoading;  // Indicates if the message is in a loading state

    // Constructor for user and bot messages (without loading state)
    public ChatMessage(String message, boolean isUserMessage) {
        this.message = message;
        this.isUserMessage = isUserMessage;
        this.isLoading = false;  // Default to not loading
    }

    // Constructor for loading state
    public ChatMessage(boolean isLoading) {
        this.message = null;  // No message yet
        this.isUserMessage = false;  // Bot messages show loading
        this.isLoading = isLoading;
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

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}

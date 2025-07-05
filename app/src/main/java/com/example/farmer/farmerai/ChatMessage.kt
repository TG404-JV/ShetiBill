package com.example.farmer.farmerai

class ChatMessage {
    @JvmField
    var message: String? // Message content
    @JvmField
    var isUserMessage: Boolean // Indicates if the message is from the user
    @JvmField
    var isLoading: Boolean // Indicates if the message is in a loading state

    // Constructor for user and bot messages (without loading state)
    constructor(message: String?, isUserMessage: Boolean) {
        this.message = message
        this.isUserMessage = isUserMessage
        this.isLoading = false // Default to not loading
    }

    // Constructor for loading state
    constructor(isLoading: Boolean) {
        this.message = null // No message yet
        this.isUserMessage = false // Bot messages show loading
        this.isLoading = isLoading
    }
}

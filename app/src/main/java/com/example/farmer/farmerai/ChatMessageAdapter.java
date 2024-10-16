package com.example.farmer.farmerai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_BOT_MESSAGE = 2;

    private List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine if the message is from the user or bot
        ChatMessage message = messages.get(position);
        if (message.isUserMessage()) {
            return VIEW_TYPE_USER_MESSAGE;
        } else {
            return VIEW_TYPE_BOT_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);
            return new ChatMessageAdapter.UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_message_item, parent, false);
            return new ChatMessageAdapter.BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof ChatMessageAdapter.UserMessageViewHolder) {
            ((ChatMessageAdapter.UserMessageViewHolder) holder).messageTextView.setText(message.getMessage());
        } else if (holder instanceof ChatMessageAdapter.BotMessageViewHolder) {
            ((ChatMessageAdapter.BotMessageViewHolder) holder).messageTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder for user messages
    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

    // ViewHolder for bot messages
    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
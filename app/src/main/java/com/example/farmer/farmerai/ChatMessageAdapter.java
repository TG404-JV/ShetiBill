package com.example.farmer.farmerai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_BOT_MESSAGE = 2;

    private List<ChatMessage> messages;

    // Listener interface for handling click events
    public interface OnMessageClickListener {
        void onMessageClick(ChatMessage message);
    }

    private OnMessageClickListener clickListener;

    public ChatMessageAdapter(List<ChatMessage> messages, OnMessageClickListener clickListener) {
        this.messages = messages;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.isUserMessage() ? VIEW_TYPE_USER_MESSAGE : VIEW_TYPE_BOT_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_message_item, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.messageTextView.setText(message.getMessage());
            holder.itemView.setOnLongClickListener(v -> {
                clickListener.onMessageClick(message);
                return true;
            });
        } else if (holder instanceof BotMessageViewHolder) {
            BotMessageViewHolder botHolder = (BotMessageViewHolder) holder;

            if (message.isLoading()) {
                // Show shimmer effect
                botHolder.messageTextView.setVisibility(View.GONE);
                botHolder.shimmerContainer.setVisibility(View.VISIBLE);
                botHolder.shimmerContainer.startShimmer();
            } else {
                // Show bot message
                botHolder.shimmerContainer.stopShimmer();
                botHolder.shimmerContainer.setVisibility(View.GONE);
                botHolder.messageTextView.setVisibility(View.VISIBLE);
                botHolder.messageTextView.setText(message.getMessage());
            }

            holder.itemView.setOnLongClickListener(v -> {
                clickListener.onMessageClick(message);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ShimmerFrameLayout shimmerContainer;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            shimmerContainer = itemView.findViewById(R.id.shimmerContainer);
        }
    }
}

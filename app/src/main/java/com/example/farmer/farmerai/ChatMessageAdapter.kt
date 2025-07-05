package com.example.farmer.farmerai

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.facebook.shimmer.ShimmerFrameLayout

class ChatMessageAdapter(
    private val messages: MutableList<ChatMessage>,
    private val clickListener: OnMessageClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    // Listener interface for handling click events
    fun interface OnMessageClickListener {
        fun onMessageClick(message: ChatMessage?)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages.get(position)
        return if (message.isUserMessage) VIEW_TYPE_USER_MESSAGE else VIEW_TYPE_BOT_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            val view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_message_item, parent, false)
            return UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bot_message_item, parent, false)
            return BotMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages.get(position)

        if (holder is UserMessageViewHolder) {
            val userHolder = holder
            userHolder.messageTextView.setText(message.message)
            holder.itemView.setOnLongClickListener(OnLongClickListener { v: View? ->
                clickListener.onMessageClick(message)
                true
            })
        } else if (holder is BotMessageViewHolder) {
            val botHolder = holder

            if (message.isLoading) {
                // Show shimmer effect
                botHolder.messageTextView.setVisibility(View.GONE)
                botHolder.shimmerContainer.setVisibility(View.VISIBLE)
                botHolder.shimmerContainer.startShimmer()
            } else {
                // Show bot message
                botHolder.shimmerContainer.stopShimmer()
                botHolder.shimmerContainer.setVisibility(View.GONE)
                botHolder.messageTextView.setVisibility(View.VISIBLE)
                botHolder.messageTextView.setText(message.message)
            }

            holder.itemView.setOnLongClickListener(OnLongClickListener { v: View? ->
                clickListener.onMessageClick(message)
                true
            })
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageTextView: TextView

        init {
            messageTextView = itemView.findViewById<TextView>(R.id.messageTextView)
        }
    }

    class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageTextView: TextView
        var shimmerContainer: ShimmerFrameLayout

        init {
            messageTextView = itemView.findViewById<TextView>(R.id.messageTextView)
            shimmerContainer = itemView.findViewById<ShimmerFrameLayout>(R.id.shimmerContainer)
        }
    }

    companion object {
        private const val VIEW_TYPE_USER_MESSAGE = 1
        private const val VIEW_TYPE_BOT_MESSAGE = 2
    }
}

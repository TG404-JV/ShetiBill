package com.example.farmer.farmerai

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.farmerai.ChatMessageAdapter.OnMessageClickListener
import com.example.farmer.farmerai.secureAi.SecurityKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.ServerException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FragmentFarmerAi : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var textToSpeech: TextToSpeech? = null
    private var chatAdapter: ChatMessageAdapter? = null
    private var chatMessages: MutableList<ChatMessage?>? = null

    private var messageEditText: EditText? = null
    private var sendButton: ImageButton? = null

    var Message: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_farmer_ai, container, false)

        // Initialize views
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMessages)
        messageEditText = view.findViewById<EditText>(R.id.messageEditText)
        sendButton = view.findViewById<ImageButton>(R.id.sendButton)

        // Set up RecyclerView
        chatMessages = ArrayList<ChatMessage?>()
        chatAdapter =
            ChatMessageAdapter(chatMessages as MutableList<ChatMessage>, { message: ChatMessage? ->
                this.showUserMessageDialog(
                    message!!
                )
            }) // Pass method reference
        recyclerView!!.setLayoutManager(LinearLayoutManager(getContext()))
        recyclerView!!.setAdapter(chatAdapter)

        // Send button click listener
        sendButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            val userMessage = messageEditText!!.getText().toString().trim { it <= ' ' }
            if (!userMessage.isEmpty()) {
                addUserMessage(userMessage)
                messageEditText!!.setText("")
                fetchBotResponse(userMessage)
            } else {
                Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    private fun addUserMessage(message: String?) {
        val userMessage = ChatMessage(message, true)
        chatMessages!!.add(userMessage)
        chatAdapter!!.notifyItemInserted(chatMessages!!.size - 1)
        recyclerView!!.scrollToPosition(chatMessages!!.size - 1)
    }

    private fun fetchBotResponse(userMessage: String?) {
        // Add a loading message to the chat
        var userMessage = userMessage
        val loadingMessage = ChatMessage(true) // Loading state
        chatMessages!!.add(loadingMessage)
        chatAdapter!!.notifyItemInserted(chatMessages!!.size - 1)
        recyclerView!!.scrollToPosition(chatMessages!!.size - 1)

        // Prepend developer message (if needed)
        userMessage = SecurityKey.DEVELOPER_MESSAGE + userMessage

        // Set up the generative model with the provided API key
        val executor: Executor = Executors.newSingleThreadExecutor()
        val gm = GenerativeModel(SecurityKey.AIMODEL, SecurityKey.APIKEY)
        val model = GenerativeModelFutures.from(gm)

        // Build the content with the full query
        val content = Content.Builder()
            .text(userMessage)
            .build()

        // Asynchronously fetch the response from the generative model
        val response: ListenableFuture<GenerateContentResponse?> = model.generateContent(content) as ListenableFuture<GenerateContentResponse?>

        // Handle the response or failure
        Futures.addCallback<GenerateContentResponse?>(
            response,
            object : FutureCallback<GenerateContentResponse?> {
                override fun onSuccess(result: GenerateContentResponse?) {
                    // Log the generated text
                    val generatedText = result?.text

                    // Clean up the response by removing unwanted characters or text
                    val output = generatedText!!.replace("*", "")

                    // Create a bot message with the generated text
                    val botMessage = ChatMessage(output, false)

                    // Run the UI update on the main thread, ensuring activity is still available
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(Runnable {
                            // Replace the loading message with the bot response
                            chatMessages!!.set(
                                chatMessages!!.size - 1,
                                botMessage
                            ) // Replace the loading message
                            chatAdapter!!.notifyItemChanged(chatMessages!!.size - 1) // Notify adapter
                        })
                    }
                }



                override fun onFailure(t: Throwable) {
                    // Log the error
                    Log.e("BotResponseError", "Error generating response", t)

                    if (t is ServerException) {
                        // Handle API-specific errors
                        Log.e("BotResponseError", "ServerException: " + t.message)
                    } else {
                        // Handle other errors (e.g., network issues)
                        Log.e("BotResponseError", "Error: " + t.message)
                    }

                    // Handle failure case on the main thread, ensuring activity is still available
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(Runnable {
                            // Replace loading message with an error message
                            val errorMessage = ChatMessage(
                                "Sorry, I couldn't generate a response. Please try again.",
                                false
                            )
                            chatMessages!!.set(
                                chatMessages!!.size - 1,
                                errorMessage
                            ) // Replace loading message
                            chatAdapter!!.notifyItemChanged(chatMessages!!.size - 1) // Notify adapter
                        })
                    }
                }
            },
            executor
        )
    }

    // Show a dialog when user message is clicked
    private fun showUserMessageDialog(message: ChatMessage) {
        // Inflate the custom dialog layout
        val inflater = LayoutInflater.from(getContext())
        val customView = inflater.inflate(R.layout.rounded_circle_dialogue, null)

        // Find the buttons in the custom layout
        val btnShare = customView.findViewById<FloatingActionButton>(R.id.btnShare)
        val btnDelete = customView.findViewById<FloatingActionButton>(R.id.btnDelete)
        val btnCopy = customView.findViewById<FloatingActionButton>(R.id.btnCopy)
        val btnRead = customView.findViewById<FloatingActionButton>(R.id.btnRead)

        // Set up AlertDialog with the custom view
        val builder = AlertDialog.Builder(getContext())
        builder.setView(customView)

        val dialog = builder.create()
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set up the share button
        btnShare.setOnClickListener(View.OnClickListener { v: View? ->
            // Code to share the message (e.g., using Intent)
            shareMessage(message.message)
            dialog.dismiss()
        })

        // Set up the delete button
        btnDelete.setOnClickListener(View.OnClickListener { v: View? ->
            // Code to delete the message
            chatMessages!!.removeIf { chatMessage: ChatMessage? -> chatMessage!!.message == message.message }
            chatAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        })

        // Set up the copy button
        btnCopy.setOnClickListener(View.OnClickListener { v: View? ->
            // Code to copy the message to clipboard
            copyToClipboard(message.message)
            dialog.dismiss()
        })

        // Set up the read button to read the message aloud
        btnRead.setOnClickListener(View.OnClickListener { v: View? ->
            // Code to read the message aloud (you can use TextToSpeech)
            Message = message.message
            readMessageAloud(Message)
            dialog.dismiss()
        })

        // Show the dialog
        dialog.show()
    }

    // Method to share the message
    private fun shareMessage(message: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    // Method to copy the message to clipboard
    private fun copyToClipboard(message: String?) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("message", message)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(getContext(), "Message copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    // Method to read the message aloud (TextToSpeech example)
    private fun readMessageAloud(message: String?) {
        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(getContext(), OnInitListener { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = textToSpeech!!.setLanguage(Locale.US)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getContext(), "Language not supported", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    getContext(),
                    "Text-to-speech initialization failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

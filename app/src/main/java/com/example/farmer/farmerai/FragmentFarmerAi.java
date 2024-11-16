package com.example.farmer.farmerai;

import static com.example.farmer.farmerai.secureAi.SecurityKey.AIMODEL;
import static com.example.farmer.farmerai.secureAi.SecurityKey.APIKEY;
import static com.example.farmer.farmerai.secureAi.SecurityKey.DEVELOPER_MESSAGE;
import static com.example.farmer.farmerai.secureAi.SecurityKey.QUESTION_REFFERENCE;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentFarmerAi extends Fragment {


    private RecyclerView recyclerView;
    TextToSpeech   textToSpeech;
    private ChatMessageAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private EditText messageEditText;
    private ImageButton sendButton;

     String Message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_ai, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        messageEditText = view.findViewById(R.id.searchEditText);
        sendButton = view.findViewById(R.id.searchButton);


        // Set up RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatMessageAdapter(chatMessages, this::showUserMessageDialog); // Pass method reference
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString().trim();


            addUserMessage(userMessage);
            messageEditText.setText("");


                fetchBotResponse(userMessage);

        });


        return view;
    }



    private void addUserMessage(String message) {
        ChatMessage userMessage = new ChatMessage(message, true);
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }



    private void fetchBotResponse(String userMessage) {
        // Add a loading message to the chat
        ChatMessage loadingMessage = new ChatMessage(true);  // Loading state
        chatMessages.add(loadingMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);

        userMessage = DEVELOPER_MESSAGE + QUESTION_REFFERENCE + userMessage;
        // Set up the generative model with the provided API key
        Executor executor = Executors.newSingleThreadExecutor();
        GenerativeModel gm = new GenerativeModel(AIMODEL, APIKEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Build the content with the full query
        Content content = new Content.Builder()
                .addText(userMessage)
                .build();

        // Asynchronously fetch the response from the generative model
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Handle the response or failure
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Log the generated text
                String generatedText = result.getText();

                // Clean up the response by removing unwanted characters or text
                String output = generatedText.replace("*", "");
                ChatMessage botMessage = new ChatMessage(output, false);


                // Run the UI update on the main thread
                requireActivity().runOnUiThread(() -> {
                    // Replace loading message with bot response
                        chatMessages.set(chatMessages.size() - 1, botMessage);  // Replace the loading message
                        chatAdapter.notifyItemChanged(chatMessages.size() - 1);  // Notify adapter

                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                // Log error
                Log.e("BotResponseError", "Error generating response", t);

                // Handle failure case
                requireActivity().runOnUiThread(() -> {
                    // Replace loading message with an error message
                    ChatMessage errorMessage = new ChatMessage("Sorry, I couldn't generate a response. Please try again.", false);
                    chatMessages.set(chatMessages.size() - 1, errorMessage);  // Replace loading message
                    chatAdapter.notifyItemChanged(chatMessages.size() - 1);  // Notify adapter
                });
            }
        }, executor);
    }

    // Show a dialog when user message is clicked
// Show a dialog when user message is clicked
// Show a dialog when user message is clicked
    private void showUserMessageDialog(ChatMessage message) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.rounded_circle_dialogue, null);

        // Find the buttons in the custom layout
        FloatingActionButton btnShare = customView.findViewById(R.id.btnShare);
        FloatingActionButton btnDelete = customView.findViewById(R.id.btnDelete);
        FloatingActionButton btnCopy = customView.findViewById(R.id.btnCopy);
        FloatingActionButton btnRead = customView.findViewById(R.id.btnRead);

        // Set up AlertDialog with the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));



        // Set up the share button
        btnShare.setOnClickListener(v -> {
            // Code to share the message (e.g., using Intent)
            shareMessage(message.getMessage());
            dialog.dismiss();

        });

        // Set up the delete button
        btnDelete.setOnClickListener(v -> {
            // Code to delete the message
            chatMessages.removeIf(chatMessage -> chatMessage.getMessage().equals(message.getMessage()));
            chatAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        // Set up the copy button
        btnCopy.setOnClickListener(v -> {
            // Code to copy the message to clipboard
            copyToClipboard(message.getMessage());
            dialog.dismiss();

        });

        // Set up the read button to read the message aloud
        btnRead.setOnClickListener(v -> {
            // Code to read the message aloud (you can use TextToSpeech)
            Message=message.getMessage();
            readMessageAloud(Message);
            dialog.dismiss();

        });

        // Show the dialog
        dialog.show();
    }

    // Method to share the message
    private void shareMessage(String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Method to copy the message to clipboard
    private void copyToClipboard(String message) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", message);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Message copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    // Method to read the message aloud (TextToSpeech example)
    private void readMessageAloud(String message) {
        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getContext(), "Language not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Text-to-speech initialization failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

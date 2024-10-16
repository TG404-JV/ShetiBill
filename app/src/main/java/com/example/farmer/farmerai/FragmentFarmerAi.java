package com.example.farmer.farmerai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FragmentFarmerAi extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking images

    private RecyclerView recyclerView;
    private ChatMessageAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton attachmentButton;
    ShimmerFrameLayout shimmerFrameLayout;

    private ImageView selectedImageView;

    private Bitmap selectedImage; // Store the selected image



    private static final String API_KEY = "AIzaSyCG34Q8Y7USJbk3BVYluNy217GqeU67MSw"; // Replace with your Gemini API key

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_ai, container, false);



        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        attachmentButton = view.findViewById(R.id.attachmentButton);
        selectedImageView = view.findViewById(R.id.selectedImageView); // Ensure you have an ImageView for the selected image in your layout

        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);

        // Set up RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatMessageAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        // Load user data

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString().trim();

            shimmerFrameLayout.setVisibility(View.VISIBLE);

            // Check if user message is empty and selectedImage is null
            if (TextUtils.isEmpty(userMessage) && selectedImage == null) {
                Toast.makeText(getContext(), "Please enter a message or select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add user message to the list
            addUserMessage(userMessage);

            // Clear the input field
            messageEditText.setText("");

            // Check if an image is selected
            if (selectedImage != null) {
                // Generate text using the selected image
                generateTextFromImage(userMessage, selectedImage);
            } else {
                // Fetch bot response from Gemini AI based on user message
                fetchBotResponse(userMessage);
            }
        });

        // Attachment button click listener
        attachmentButton.setOnClickListener(v -> openImagePicker());

        return view;
    }

    // Load user data from Firebase

    // Load user profile image from Firebase Storage

    // Open the image picker
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                selectedImageView.setImageBitmap(selectedImage);
                selectedImageView.setVisibility(View.VISIBLE); // Show selected image
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to add a user message to the RecyclerView
    private void addUserMessage(String message) {
        ChatMessage userMessage = new ChatMessage(message, true);
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    // Method to add a bot message to the RecyclerView
    private void addBotMessage(String message) {
        ChatMessage botMessage = new ChatMessage(message, false);
        chatMessages.add(botMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    // Method to generate text from the selected image
    private void generateTextFromImage(String prompt, Bitmap image) {
        // Initialize an executor for background tasks
        Executor executor = Executors.newSingleThreadExecutor();

        // Initialize Gemini API model for image processing
        GenerativeModel gm = new GenerativeModel("gemini-pro-vision", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Create content with the user's prompt and selected image
        Content content = new Content.Builder()
                .addText(prompt)
                .addImage(image) // Assuming addImage accepts Bitmap
                .build();

        // Fetch AI response
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Extract generated text from the AI response
                String generatedText = result.getText();

                // Ensure UI updates are done on the main thread
                requireActivity().runOnUiThread(() -> {
                    // Add the bot's response to the RecyclerView
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    addBotMessage(generatedText);
                    selectedImageView.setVisibility(View.GONE); // Hide the image after processing
                    selectedImage = null; // Clear the selected image
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
                // Ensure UI updates are done on the main thread
                requireActivity().runOnUiThread(() -> addBotMessage("Sorry, I couldn't generate a response. Please try again."));
            }
        }, executor);
    }

    // Method to fetch a response from Gemini AI if no image is selected
    private void fetchBotResponse(String userMessage) {
        // Initialize an executor for background tasks
        Executor executor = Executors.newSingleThreadExecutor();

        // Initialize Gemini API model
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Create content with the user's prompt
        Content content = new Content.Builder()
                .addText(userMessage)
                .build();


        // Fetch AI response
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // Extract generated text from the AI response
                String generatedText = result.getText();
                String output = generatedText.replace("*", "");


                // Ensure UI updates are done on the main thread
                requireActivity().runOnUiThread(() -> {
                    // Add the bot's response to the RecyclerView
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    addBotMessage(output);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
                // Ensure UI updates are done on the main thread
                requireActivity().runOnUiThread(() -> addBotMessage("Sorry, I couldn't generate a response. Please try again."));
            }
        }, executor);
    }
}
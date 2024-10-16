package com.example.farmer.mainacthandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farmer.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseHelper {
    private static FirebaseAuth mAuth;
    private static DatabaseReference mDatabase;
    private static StorageReference mStorage;
    private static final String PROFILE_IMAGE_NAME = "profile_image.jpg"; // Local filename

    public static void initializeFirebase(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mStorage = FirebaseStorage.getInstance().getReference("profile_images");
    }

    public static void setupHeaderView(Context context, NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        ImageView userImageView = headerView.findViewById(R.id.pro);
        TextView userNameTextView = headerView.findViewById(R.id.userName);
        TextView userEmailTextView = headerView.findViewById(R.id.userEmail);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();
            userEmailTextView.setText(userEmail);

            // Load user data from Firebase
            mDatabase.child(userId).get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String userName = firstName + " " + lastName;
                    userNameTextView.setText(userName);

                    // Load profile image
                    loadProfileImage(userId, userImageView, context);
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private static void loadProfileImage(String userId, ImageView imageView, Context context) {
        File localFile = new File(context.getFilesDir(), PROFILE_IMAGE_NAME);
        if (localFile.exists()) {
            // Load image from local storage using Glide
            Glide.with(context)
                    .load(localFile)
                    .circleCrop()  // Apply circleCrop for rounded profile images
                    .placeholder(R.drawable.ic_farmer_profile_img) // Placeholder image
                    .into(imageView);
        } else {
            // Download from Firebase if not available locally
            StorageReference profileImageRef = mStorage.child(userId + ".jpg");
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                new Thread(() -> {
                    try {
                        Bitmap bitmap = downloadImage(uri);
                        if (bitmap != null) {
                            saveImageLocally(context, bitmap); // Save image locally
                            imageView.post(() -> {
                                Glide.with(context)
                                        .load(bitmap)
                                        .circleCrop()
                                        .into(imageView);
                            });
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseHelper", "Error downloading image", e);
                        imageView.post(() -> imageView.setImageResource(R.drawable.ic_farmer_profile_img));
                    }
                }).start();
            }).addOnFailureListener(e ->
                    imageView.setImageResource(R.drawable.ic_farmer_profile_img)
            );
        }
    }

    // Download image bitmap from URL
    private static Bitmap downloadImage(Uri uri) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    // Save the bitmap image locally
    private static void saveImageLocally(Context context, Bitmap bitmap) {
        try (FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), PROFILE_IMAGE_NAME))) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos); // Compress and save as JPEG
        } catch (Exception e) {
            Log.e("FirebaseHelper", "Error saving image locally", e);
        }
    }

    // Get the user profile image URL
    public static void getUserProfileImageUrl(String userId, OnImageUrlRetrievedListener listener) {
        StorageReference profileImageRef = mStorage.child(userId + ".jpg");
        profileImageRef.getDownloadUrl().addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> {
                    Log.e("FirebaseHelper", "Failed to retrieve image URL", e);
                    listener.onFailure(e);
                });
    }

    // Interface for retrieving the image URL
    public interface OnImageUrlRetrievedListener {
        void onSuccess(Uri uri);
        void onFailure(Exception e);
    }
}

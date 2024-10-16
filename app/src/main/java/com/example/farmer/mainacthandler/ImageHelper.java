package com.example.farmer.mainacthandler;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.farmer.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class ImageHelper {

    public static final int PICK_IMAGE_REQUEST = 1;

    public static void handleImageResult(Context context, Uri imageUri, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        StorageReference imageRef = storageRef.child(imageUri.getLastPathSegment());

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .circleCrop()
                        .into(imageView);
            });
        }).addOnFailureListener(e ->
                imageView.setImageResource(R.drawable.ic_farmer_profile_img)
        );
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }
}


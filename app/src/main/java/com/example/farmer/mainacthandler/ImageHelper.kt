package com.example.farmer.mainacthandler

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.IOException


object ImageHelper {
    const val PICK_IMAGE_REQUEST: Int = 1

    fun handleImageResult(context: Context, imageUri: Uri, imageView: ImageView) {
        val storageRef = FirebaseStorage.getInstance().getReference("profile_images")
        val imageRef = storageRef.child(imageUri.getLastPathSegment()!!)

        imageRef.putFile(imageUri)
            .addOnSuccessListener(OnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                imageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                    Glide.with(context)
                        .load(uri)
                        .circleCrop()
                        .into(imageView)
                })
            })
            .addOnFailureListener(OnFailureListener { e: Exception? -> imageView.setImageResource(R.drawable.ic_farmer_profile_img) }
            )
    }

    @Throws(IOException::class)
    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
    }
}


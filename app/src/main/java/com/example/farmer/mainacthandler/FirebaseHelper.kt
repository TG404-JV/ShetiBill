package com.example.farmer.mainacthandler

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object FirebaseHelper {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var mStorage: StorageReference? = null
    private const val PROFILE_IMAGE_NAME = "profile_image.jpg" // Local filename

    fun initializeFirebase(context: Context?) {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")
        mStorage = FirebaseStorage.getInstance().getReference("profile_images")
    }

    fun setupHeaderView(context: Context, navigationView: NavigationView) {
        val headerView = navigationView.getHeaderView(0)
        val userImageView = headerView.findViewById<ImageView>(R.id.profileImage)
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.userEmail)

        val user = mAuth!!.currentUser
        if (user != null) {
            val userId = user.uid
            val userEmail = user.email
            userEmailTextView.text = userEmail

            // Load user data from Firebase
            mDatabase!!.child(userId).get()
                .addOnSuccessListener(OnSuccessListener { dataSnapshot: DataSnapshot? ->
                    if (dataSnapshot!!.exists()) {
                        val firstName =
                            dataSnapshot.child("firstName").getValue<String?>(String::class.java)
                        val lastName =
                            dataSnapshot.child("lastName").getValue<String?>(String::class.java)
                        val userName = "$firstName $lastName"
                        userNameTextView.text = userName

                        loadProfileImage(userId, userImageView, context)
                    }
                }).addOnFailureListener { e: Exception? ->
                    Toast.makeText(
                        context,
                        "Failed to load user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun loadProfileImage(userId: String?, imageView: ImageView, context: Context) {
        val localFile = File(context.getFilesDir(), PROFILE_IMAGE_NAME)
        if (localFile.exists()) {
            // Load image from local storage using Glide
            Glide.with(context)
                .load(localFile)
                .circleCrop() // Apply circleCrop for rounded profile images
                .placeholder(R.drawable.ic_farmer_profile_img) // Placeholder image
                .into(imageView)
        } else {
            // Download from Firebase if not available locally
            val profileImageRef = mStorage!!.child("$userId.jpg")
            profileImageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                Thread {
                    try {
                        val bitmap = downloadImage(uri!!)
                        if (bitmap != null) {
                            saveImageLocally(context, bitmap) // Save image locally
                            imageView.post {
                                Glide.with(context)
                                    .load(bitmap)
                                    .circleCrop()
                                    .into(imageView)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FirebaseHelper", "Error downloading image", e)
                        imageView.post(Runnable { imageView.setImageResource(R.drawable.ic_farmer_profile_img) })
                    }
                }.start()
            }).addOnFailureListener { e: Exception? ->
                imageView.setImageResource(R.drawable.ic_farmer_profile_img)
            }
        }
    }

    // Download image bitmap from URL
    @Throws(Exception::class)
    private fun downloadImage(uri: Uri): Bitmap? {
        val connection = URL(uri.toString()).openConnection() as HttpURLConnection
        connection.setDoInput(true)
        connection.connect()
        val input = connection.getInputStream()
        return BitmapFactory.decodeStream(input)
    }

    // Save the bitmap image locally
    private fun saveImageLocally(context: Context, bitmap: Bitmap) {
        try {
            FileOutputStream(File(context.filesDir, PROFILE_IMAGE_NAME)).use { fos ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG, 90, fos
                ) // Compress and save as JPEG
            }
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Error saving image locally", e)
        }
    }

    // Get the user profile image URL
    fun getUserProfileImageUrl(userId: String?, listener: OnImageUrlRetrievedListener) {
        val profileImageRef = mStorage!!.child(userId + ".jpg")
        profileImageRef.getDownloadUrl()
            .addOnSuccessListener(OnSuccessListener { uri: Uri? -> listener.onSuccess(uri) })
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                Log.e("FirebaseHelper", "Failed to retrieve image URL", e)
                listener.onFailure(e)
            })
    }

    // Interface for retrieving the image URL
    interface OnImageUrlRetrievedListener {
        fun onSuccess(uri: Uri?)
        fun onFailure(e: Exception?)
    }
}

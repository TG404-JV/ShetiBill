package com.example.farmer.cropmarket;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UploadCropFragment extends Fragment {

    private RecyclerView cropRecyclerView;
    private CropAdapter cropAdapter;
    private List<Crop> cropList;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    // Firebase references
    private DatabaseReference cropReference;
    private StorageReference storageReference;

    private static final String TAG = "UploadCropFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_crop, container, false);

        // Initialize RecyclerView and Adapter
        cropRecyclerView = view.findViewById(R.id.cropRecyclerView);
        cropRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cropList = new ArrayList<>();
        cropAdapter = new CropAdapter(cropList, crop -> {
            // Handle crop click here (e.g., show details or allow editing)
            Toast.makeText(getContext(), "Clicked on: " + crop.getCropName(), Toast.LENGTH_SHORT).show();
        });
        cropRecyclerView.setAdapter(cropAdapter);

        // Initialize Firebase Database and Storage
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        cropReference = database.getReference("crops");
        storageReference = FirebaseStorage.getInstance().getReference("crop_images"); // Firebase Storage for images

        // Fetch crop details from Firebase
        fetchCropsFromFirebase();

        // Initialize Floating Action Button
        FloatingActionButton fabAddCrop = view.findViewById(R.id.fab_add_crop);
        fabAddCrop.setOnClickListener(v -> showAddCropDialog());

        return view;
    }

    private void showAddCropDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_crop);

        EditText dialogCropNameEditText = dialog.findViewById(R.id.cropNameEditText);
        EditText dialogCropQuantityEditText = dialog.findViewById(R.id.cropQuantityEditText);
        EditText dialogCropPriceEditText = dialog.findViewById(R.id.cropPriceEditText);
        EditText dialogLocationEditText = dialog.findViewById(R.id.locationTextView);
        EditText dialogContactEditText = dialog.findViewById(R.id.contact);
        Button dialogUploadCropButton = dialog.findViewById(R.id.addCropButton);
        ImageView cropImageView = dialog.findViewById(R.id.cropImageView);
        EditText sellerName = dialog.findViewById(R.id.SellerName);

        // Image picker when clicking on ImageView
        cropImageView.setOnClickListener(v -> openImagePicker());

        dialogUploadCropButton.setOnClickListener(v -> {
            String cropName = dialogCropNameEditText.getText().toString().trim();
            String cropQuantity = dialogCropQuantityEditText.getText().toString().trim();
            String cropPrice = dialogCropPriceEditText.getText().toString().trim();
            String location = dialogLocationEditText.getText().toString().trim();
            String contactNumber = dialogContactEditText.getText().toString().trim();
            String seller = sellerName.getText().toString().trim();

            // Validate input
            if (cropName.isEmpty() || cropQuantity.isEmpty() || cropPrice.isEmpty() || location.isEmpty() || contactNumber.isEmpty() || imageUri == null) {
                Toast.makeText(getContext(), "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Upload the crop image and details to Firebase
            uploadCropToFirebase(cropName, cropQuantity, cropPrice, location, contactNumber, seller);

            dialog.dismiss(); // Consider moving this inside the upload completion callback
        });

        dialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();  // Get the image URI from device storage
        }
    }

    private void uploadCropToFirebase(String cropName, String cropQuantity, String cropPrice, String location, String contactNumber,String name) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Retrieve the download URL of the uploaded image
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Create a new Crop object with the image URL
                    Crop newCrop = new Crop(cropName, cropQuantity, cropPrice, location, contactNumber,name, imageUrl);  // Updated constructor

                    // Upload the crop details along with the image URL to Firebase
                    String cropId = cropReference.push().getKey();
                    if (cropId != null) {
                        cropReference.child(cropId).setValue(newCrop)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Crop uploaded successfully!", Toast.LENGTH_SHORT).show();
                                    cropList.add(newCrop);
                                    cropAdapter.notifyItemInserted(cropList.size() - 1);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to upload crop", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Failed to upload crop: ", e);
                                });
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to upload image: ", e);
            });
        }
    }

    private void fetchCropsFromFirebase() {
        cropReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cropList.clear();
                for (DataSnapshot cropSnapshot : dataSnapshot.getChildren()) {
                    Crop crop = cropSnapshot.getValue(Crop.class);
                    if (crop != null) {
                        cropList.add(crop);
                    }
                }
                cropAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load crops", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load crops: ", databaseError.toException());
            }
        });
    }
}

package com.example.farmer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.farmer.cropmarket.CropMarket;
import com.example.farmer.emioptions.Loan_calculator;
import com.example.farmer.fertilizer.Fertilizer_Expendituer;
import com.example.farmer.govscheme.FragmentGovtScheme;
import com.example.farmer.graph.FragmentExpendGraph;
import com.example.farmer.home.Home;
import com.example.farmer.userprofile.UserProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;

    private DrawerLayout drawerLayout;
    private ImageButton menu;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private ImageView profileImageView;
    private TextView userNameTextView, userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase and permissions setup
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        checkPermissions();

        // Setup the navigation drawer
        setupNavigationDrawer();

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new Home());
        }

        // Setup the profile information (image, name, email)
        setupProfileInfo();
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        menu = findViewById(R.id.menu);
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.pro);
        userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmailTextView = navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set the onClick listener for the profile image view
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select an image
                openImagePicker();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupProfileInfo() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Fetch user's email (for now, as fallback)
            String userEmail = user.getEmail();
            userEmailTextView.setText(userEmail != null ? userEmail : "No Email");

            // Fetch farmer's information from Firebase Realtime Database using the user UID as the key
            String userId = user.getUid();
            DatabaseReference farmerRef = FirebaseDatabase.getInstance().getReference("Users").child(userId); // Assuming "farmers" is the node where the data is stored

            farmerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Fetch farmer's name from Firebase
                        String farmerName = dataSnapshot.child("name").getValue(String.class);
                        userNameTextView.setText(farmerName);
                    } else {
                        userNameTextView.setText("No Name"); // If no data exists
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors, like no connection or permission issues
                    Toast.makeText(MainActivity.this, "Failed to load farmer data.", Toast.LENGTH_SHORT).show();
                }
            });

            // Fetch and set profile image
            StorageReference profileImageRef = firebaseStorage.getReference().child("profile_images/" + user.getUid() + ".jpg");
            File localFile = new File(getFilesDir(), firebaseAuth.getUid());

            if (localFile.exists()) {
                Glide.with(this).load(localFile).circleCrop().into(profileImageView);
            } else {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this).load(uri).circleCrop().into(profileImageView);
                    saveImageLocally(uri);
                }).addOnFailureListener(e -> {
                    setDefaultProfileImage();
                });
            }
        } else {
            setDefaultProfileImage();
        }
    }

    private void setDefaultProfileImage() {
        profileImageView.setImageResource(R.drawable.ic_farmer_profile_img);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void saveImageLocally(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File localFile = new File(getFilesDir(), "profile_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(localFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            Toast.makeText(this, "Profile image saved locally", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image locally", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Set the selected image on the ImageView
            profileImageView.setImageURI(imageUri);

            // Upload the selected image to Firebase Storage
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Get the Firebase Storage reference for the user's profile image
        StorageReference profileImageRef = firebaseStorage.getReference().child("profile_images/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // After the image is uploaded successfully, get the image URL
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Set the profile image in the UI
                        Glide.with(this).load(uri).circleCrop().into(profileImageView);

                        // Save the image URL to Firebase Database
                        saveProfileImageUrl(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileImageUrl(String imageUrl) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.child("profileImageUrl").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Profile image URL saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to save image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new Home();
        } else if (id == R.id.nav_expenditure) {
            fragment = new Fertilizer_Expendituer();
        } else if (id == R.id.nav_market) {
            fragment = new CropMarket();
        } else if (id == R.id.loan_calculator) {
            fragment = new Loan_calculator();
        } else if (id == R.id.government_scheme) {
            fragment = new FragmentGovtScheme();
        } else if (id == R.id.nav_harvest) {
            fragment = new FragmentExpendGraph();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}

package com.example.farmer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar; // This is the correct import


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.farmer.cropmarket.CropMarket;
import com.example.farmer.emioptions.Loan_calculator;
import com.example.farmer.farmerai.FragmentFarmerAi;
import com.example.farmer.fertilizer.Fertilizer_Expendituer;
import com.example.farmer.govscheme.FragmentGovtScheme;
import com.example.farmer.graph.FragmentExpendGraph;
import com.example.farmer.home.Home;
import com.example.farmer.lang.LocaleHelper;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageButton menu;
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private ImageView profileImageView;
    private TextView userNameTextView, userEmailTextView;
    private Button loginLogoutButton; // Single button for login/logout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Firebase and permissions setup
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

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
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.profileImage);
        userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmailTextView = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        loginLogoutButton = navigationView.getHeaderView(0).findViewById(R.id.btn_login_logout); // Single button for login/logout

        // Update the login/logout button based on the user's login state
        updateLoginLogoutButton();



        // Button click listener
        loginLogoutButton.setOnClickListener(v -> {
            if (firebaseAuth.getCurrentUser() != null) {
                // User is logged in, log out
                firebaseAuth.signOut();
                updateLoginLogoutButton(); // Update button text to "Login"

                // Redirect to Login screen after logout
                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                // User is logged out, navigate to LoginActivity
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set the onClick listener for the profile image view
        profileImageView.setOnClickListener(v -> openImagePicker());

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_change_language) {
            // Handle language change
            /* Intent intent = new Intent(this, LanguageSelectionActivity.class);
            startActivity(intent);
            return true; */
            Intent LanguageChange=new Intent(MainActivity.this,LanguageSelection.class);
            startActivity(LanguageChange);
        } else if (item.getItemId() == R.id.action_about_app) {

            Toast.makeText(this, "About Selected", Toast.LENGTH_SHORT).show();  // Toast message

        } else if (item.getItemId() == R.id.action_privacy_policy) {
            // Handle privacy policy
            /* Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
            return true; */
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupProfileInfo() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            userEmailTextView.setText(userEmail != null ? userEmail : "No Email");

            String userId = user.getUid();
            DatabaseReference farmerRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            farmerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String farmerName = dataSnapshot.child("name").getValue(String.class);
                        userNameTextView.setText(farmerName);
                    } else {
                        userNameTextView.setText("No Name");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Failed to load farmer data.", Toast.LENGTH_SHORT).show();
                }
            });

            StorageReference profileImageRef = firebaseStorage.getReference().child("profile_images/" + user.getUid() + ".jpg");
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(MainActivity.this).load(uri).circleCrop().into(profileImageView);
            }).addOnFailureListener(e -> setDefaultProfileImage());
        } else {
            setDefaultProfileImage();
        }
    }

    private void setDefaultProfileImage() {
        profileImageView.setImageResource(R.drawable.ic_farmer_profile_img);
    }

    private void updateLoginLogoutButton() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            loginLogoutButton.setText(R.string.logout);
            loginLogoutButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Logout color
        } else {
            loginLogoutButton.setText(R.string.login);
            loginLogoutButton.setTextColor(getResources().getColor(android.R.color.holo_green_dark)); // Login color
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference profileImageRef = firebaseStorage.getReference().child("profile_images/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this).load(uri).circleCrop().into(profileImageView);
                        saveProfileImageUrl(uri.toString());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfileImageUrl(String imageUrl) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.child("profileImageUrl").setValue(imageUrl);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle fragment navigation
        Fragment fragment = null;

        if (item.getItemId() == R.id.nav_home) {
            fragment = new Home();
        } else if (item.getItemId() == R.id.nav_expenditure) {
            fragment = new Fertilizer_Expendituer();
        } else if (item.getItemId() == R.id.nav_market) {
            fragment = new CropMarket();
        } else if (item.getItemId() == R.id.loan_calculator) {
            fragment = new Loan_calculator();
        } else if (item.getItemId() == R.id.government_scheme) {
            fragment = new FragmentGovtScheme();
        } else if (item.getItemId() == R.id.nav_harvest) {
            fragment = new FragmentExpendGraph();
        } else if (item.getItemId() == R.id.ai_assistant) {
            fragment = new FragmentFarmerAi();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
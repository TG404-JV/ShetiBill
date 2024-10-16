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
import android.widget.SearchView;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.farmer.cropmarket.CropMarket;
import com.example.farmer.emioptions.Loan_calculator;
import com.example.farmer.farmerai.FragmentFarmerAi;
import com.example.farmer.fertilizer.Fertilizer_Expendituer;
import com.example.farmer.govscheme.FragmentGovtScheme;
import com.example.farmer.graph.FragmentExpendGraph;
import com.example.farmer.home.Home;
import com.example.farmer.mainacthandler.FirebaseHelper;
import com.example.farmer.mainacthandler.ImageHelper;
import com.example.farmer.mainacthandler.SearchHelper;
import com.example.farmer.userprofile.UserProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection

    private DrawerLayout drawerLayout;
    private ImageButton menu;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> dataList = new ArrayList<>(); // Sample data list for search
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private ImageView profileImageView; // ImageView in navheader (ID: pro)

    ImageView userlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase and permissions setup
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseHelper.initializeFirebase(this);
        checkPermissions();

        // Setup the navigation drawer
        setupNavigationDrawer();

        // Setup header view for user data
        FirebaseHelper.setupHeaderView(this, findViewById(R.id.navigation_view));

        // Initialize RecyclerView
        setupRecyclerView();

        // Search functionality
        setupSearchFunctionality();

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new Home());
        }

        // Load the profile image into the user logo
        loadProfileImage();
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        menu = findViewById(R.id.menu);
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.pro); // 'pro' ImageView in navheader
        userlogo = findViewById(R.id.logo);  // Assuming this is the ImageButton for user logo

        menu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Set click listener for profile image in navheader to upload a new profile picture
        profileImageView.setOnClickListener(v -> selectProfileImage());

        // Click listener for userlogo to load UserProfileFragment
        userlogo.setOnClickListener(v -> loadFragment(new UserProfileFragment()));

        // Setup login and logout buttons
        setupLoginLogout(navigationView);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView); // Ensure this ID matches the one in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize dummy data for testing search functionality
        SearchHelper.initializeDummyData(dataList);
    }

    private void setupSearchFunctionality() {
        ImageButton searchIcon = findViewById(R.id.search_icon);
        SearchView searchView = findViewById(R.id.search_view);

        searchIcon.setOnClickListener(v -> {
            if (searchView.getVisibility() == View.INVISIBLE) {
                searchView.setVisibility(View.VISIBLE);
            } else {
                searchView.setVisibility(View.INVISIBLE);
            }
        });

        // Handle search input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchHelper.performSearch(query, dataList, adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchHelper.performSearch(newText, dataList, adapter);
                return true;
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void loadProfileImage() {
        File localFile = new File(getFilesDir(), "profile_image.jpg");

        if (localFile.exists()) {
            // Load the local image into the profileImageView and userlogo
            Glide.with(this)
                    .load(localFile)
                    .centerCrop()
                    .circleCrop()
                    .into(profileImageView);

            Glide.with(this)
                    .load(localFile)
                    .centerCrop()
                    .circleCrop()
                    .into(userlogo);
        } else {
            // If the local image doesn't exist, fetch it from Firebase
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                StorageReference profileImageRef = firebaseStorage.getReference()
                        .child("profile_images/" + user.getUid() + ".jpg");

                // Attempt to fetch the profile image from Firebase Storage
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Load the image from Firebase into the profileImageView and userlogo
                    Glide.with(this)
                            .load(uri)
                            .centerCrop()
                            .circleCrop()
                            .into(profileImageView);

                    Glide.with(this)
                            .load(uri)
                            .centerCrop()
                            .circleCrop()
                            .into(userlogo);

                    // Optionally, save this image locally for future use
                    saveImageLocally(uri);
                }).addOnFailureListener(e -> {
                    // If fetching from Firebase fails, set a default image
                    setDefaultProfileImage();
                });
            } else {
                // User is not logged in, set a default image
                setDefaultProfileImage();
            }
        }
    }

    private void setDefaultProfileImage() {
        // Set the default image if no profile image is available
        profileImageView.setImageResource(R.drawable.ic_farmer_profile_img);
        userlogo.setImageResource(R.drawable.ic_farmer_profile_img); // Set the same default image for userlogo
    }

    private void selectProfileImage() {
        // Open the image picker when the user clicks the profile image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            // Upload the selected image to Firebase Storage
            StorageReference profileImageRef = firebaseStorage.getReference()
                    .child("profile_images/" + firebaseAuth.getCurrentUser().getUid() + ".jpg");

            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Load the new profile image into the ImageView
                        Glide.with(MainActivity.this)
                                .load(imageUri)
                                .circleCrop()
                                .into(profileImageView);

                        // Save the image to the local directory
                        saveImageLocally(imageUri);

                        Toast.makeText(MainActivity.this, "Profile image uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveImageLocally(Uri imageUri) {
        try {
            // Open an input stream from the image URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Create a file in the app's local directory
            File localFile = new File(getFilesDir(), "profile_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(localFile);

            // Write the input stream to the local file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams
            outputStream.close();
            inputStream.close();

            Toast.makeText(this, "Profile image saved locally", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image locally", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
        } else if (id == R.id.ai_assistant) {
            fragment = new FragmentFarmerAi();
        } else if (id==R.id.nav_harvest) {
            fragment=new FragmentExpendGraph();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment); // Ensure this ID matches your layout
        fragmentTransaction.commit();
    }

    private void setupLoginLogout(NavigationView navigationView) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is logged in, show user's email
            navigationView.getHeaderView(0).findViewById(R.id.userEmail).setVisibility(View.VISIBLE);
        } else {
            // User is logged out, set onClickListener for login
            navigationView.getHeaderView(0).findViewById(R.id.userEmail).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseAuth.signOut();
    }
}

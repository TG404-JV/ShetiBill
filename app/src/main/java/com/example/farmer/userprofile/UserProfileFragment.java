package com.example.farmer.userprofile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.farmer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView profileName, profileEmail, profiledob, profileAcres, profileContact;
    private Button btnUpdatePassword, btnUpdateProfile, btnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        // Bind Views
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profiledob = view.findViewById(R.id.profile_dob);
        profileAcres = view.findViewById(R.id.profile_acres);
        profileContact = view.findViewById(R.id.profile_contact);
        btnUpdatePassword = view.findViewById(R.id.btn_update_password);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Load user data
        loadUserData();

        // Set up button click listeners
        btnUpdatePassword.setOnClickListener(v -> onUpdatePasswordClick());
        btnUpdateProfile.setOnClickListener(v -> onUpdateProfileClick());
        btnLogout.setOnClickListener(v -> onLogoutClick());

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Set email directly from Authentication
            profileEmail.setText("Email: " + user.getEmail());

            // Check if data is available in SharedPreferences
            final String[] name = {sharedPreferences.getString("name", null)};
            final String[] dob = {sharedPreferences.getString("dob", null)};
            final String[] acres = {sharedPreferences.getString("acres", null)};
            final String[] contact = {sharedPreferences.getString("contact", null)};

            // If data is not in SharedPreferences, fetch from Firebase
            if (name[0] == null || dob[0] == null || acres[0] == null || contact[0] == null) {
                DatabaseReference userRef = firebaseDatabase.getReference("Users").child(user.getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            name[0] = snapshot.child("firstName").getValue(String.class);
                            dob[0] = snapshot.child("dob").getValue(String.class);
                            acres[0] = snapshot.child("farmSize").getValue(String.class);
                            contact[0] = snapshot.child("mobileNo").getValue(String.class);

                            // Update UI
                            profileName.setText(name[0]);
                            profiledob.setText("Birthdate: " + dob[0]);
                            profileAcres.setText("Acres Owned: " + acres[0]);
                            profileContact.setText("Contact: " + contact[0]);

                            // Store data in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", name[0]);
                            editor.putString("dob", dob[0]);
                            editor.putString("acres", acres[0]);
                            editor.putString("contact", contact[0]);
                            editor.apply();

                            // Fetch profile image from Firebase Storage
                            loadProfileImage(user.getUid());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors here
                    }
                });
            } else {
                // Use data from SharedPreferences
                profileName.setText(name[0]);
                profiledob.setText("Birthdate: " + dob[0]);
                profileAcres.setText("Acres Owned: " + acres[0]);
                profileContact.setText("Contact: " + contact[0]);

                // Fetch profile image from Firebase Storage
                loadProfileImage(user.getUid());
            }
        }
    }

    private void loadProfileImage(String userId) {
        StorageReference profileImageRef = firebaseStorage.getReference().child("profile_images/" + userId + ".jpg");
        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load image using Glide
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_farmer_profile_img)  // Placeholder image
                    .into(profileImage);
        }).addOnFailureListener(e -> {
            // Handle error, e.g., show a default image
            profileImage.setImageResource(R.drawable.ic_farmer_profile_img);
        });
    }

    // Handle password update
    private void onUpdatePasswordClick() {
        // Implement password update logic (e.g., navigate to a password update fragment or activity)
    }

    // Handle profile update
    private void onUpdateProfileClick() {
        // Implement profile update logic (e.g., navigate to a profile edit fragment or activity)
    }

    // Handle logout
    private void onLogoutClick() {
        firebaseAuth.signOut();
        // Redirect to login activity after logout
        // Example: startActivity(new Intent(getContext(), LoginActivity.class));
    }
}

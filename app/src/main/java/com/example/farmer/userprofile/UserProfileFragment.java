package com.example.farmer.userprofile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.LoginActivity;
import com.example.farmer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private TextView tvFarmerName, tvDob, tvMobileNumber, tvPaymentType, tvPaymentPerDay, tvPaymentPerKg, tvDayOfPayment;
    private CircleImageView profileImage;
    private Button btnProfileUpdate, btnLogin, btnLogout;
    private TextView ProfileName;

    // SharedPreferences for local storage
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize the views
        initializeViews(view);

        // Initialize Firebase Authentication and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users"); // Assuming user data is stored under "Users"

        // Initialize SharedPreferences to fetch local data
        sharedPreferences = getActivity().getSharedPreferences("FarmerDetails", Context.MODE_PRIVATE);

        // Fetch farmer data from local storage
        fetchFarmerDataFromLocalStorage();

        // Setup button listeners
        setupButtonListeners();


        // Check if user is logged in
        checkLoginStatus();



        // Setup button listeners
        setupButtonListeners();

        return view;
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        tvFarmerName = view.findViewById(R.id.tvFarmerName);
        tvDob = view.findViewById(R.id.tvDob);
        tvMobileNumber = view.findViewById(R.id.tvMobileNumber);
        tvPaymentType = view.findViewById(R.id.tvPaymentType);
        tvPaymentPerDay = view.findViewById(R.id.tvPaymentPerDay);
        tvPaymentPerKg = view.findViewById(R.id.tvPaymentPerKg);
        tvDayOfPayment = view.findViewById(R.id.tvDayOfPayment);
        btnProfileUpdate = view.findViewById(R.id.btnProfileUpdate);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogout = view.findViewById(R.id.btnLogout);
        ProfileName=view.findViewById(R.id.profile_name);
    }

    private void fetchFarmerDataFromLocalStorage() {
        // Fetch and set Farmer Name
        String farmerName = sharedPreferences.getString("farmerName", "N/A");
        Log.d("UserProfileFragment", "Farmer Name: " + farmerName);
        tvFarmerName.setText(farmerName);
        ProfileName.setText(farmerName);

        // Fetch and set Date of Birth
        String dob = sharedPreferences.getString("dob", "N/A");
        Log.d("UserProfileFragment", "DOB: " + dob);
        tvDob.setText(dob);

        // Fetch and set Mobile Number
        String mobileNumber = sharedPreferences.getString("mobileNumber", "N/A");
        Log.d("UserProfileFragment", "Mobile Number: " + mobileNumber);
        tvMobileNumber.setText(mobileNumber);

        // Fetch and set Payment Type
        String paymentType = sharedPreferences.getString("paymentType", "N/A");
        Log.d("UserProfileFragment", "Payment Type: " + paymentType);
        tvPaymentType.setText(paymentType);

        // Fetch and set Payment Per Day
        String paymentPerDay = sharedPreferences.getString("paymentPerDay", "N/A");
        Log.d("UserProfileFragment", "Payment Per Day: " + paymentPerDay);
        tvPaymentPerDay.setText(paymentPerDay);

        // Fetch and set Payment Per KG
        String paymentPerKg = sharedPreferences.getString("paymentPerKg", "N/A");
        Log.d("UserProfileFragment", "Payment Per KG: " + paymentPerKg);
        tvPaymentPerKg.setText(paymentPerKg);

        // Fetch and set Day of Payment
        String dayOfPayment = sharedPreferences.getString("dayOfPayment", "N/A");
        Log.d("UserProfileFragment", "Day of Payment: " + dayOfPayment);
        tvDayOfPayment.setText(dayOfPayment);

        // Fetch and set Profile Image URL (if available) from local storage
        String profileImageUrl = sharedPreferences.getString("profileImage", "");
        if (!profileImageUrl.isEmpty()) {
            Picasso.get().load(profileImageUrl).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.ic_farmer_profile_img); // Default image if no URL
        }
    }

    private void setupButtonListeners() {
        // Profile Update button: Navigate to an activity where user can update profile
        btnProfileUpdate.setOnClickListener(v -> {
            // Example: Navigate to a new activity to update profile
            startActivity(new Intent(getActivity(), UserProfileFragment.class));
        });

        // Login button: Navigate to the login screen
        btnLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
        });

        // Logout button: Log out from Firebase and clear SharedPreferences
        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase Authentication
            mAuth.signOut();

            // Clear all data from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Show a logout message
            showMessage("You have successfully logged out.");

            // Redirect to LoginActivity
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
            getActivity().finish(); // Optionally close the current activity
        });
    }


    private void checkLoginStatus() {
        if (mAuth.getCurrentUser() != null) {
            // User is logged in, show Logout button and hide Login button
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        } else {
            // User is not logged in, show Login button and hide Logout button
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }


    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}

package com.example.farmer.userprofile;

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

import com.example.farmer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private TextView tvFarmerName, tvDob, tvMobileNumber, tvPaymentType, tvPaymentPerDay, tvPaymentPerKg, tvDayOfPayment;
    private CircleImageView profileImage;
    private Button btnProfileUpdate, btnLogin, btnLogout;

    // Firebase database reference
    private DatabaseReference farmerRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize the views
        initializeViews(view);

        // Get the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String farmerId = currentUser.getUid(); // Get the Firebase user ID
            Log.d("UserProfileFragment", "Farmer ID: " + farmerId); // Log the farmer ID

            // Initialize Firebase reference
            farmerRef = FirebaseDatabase.getInstance().getReference("Farmers").child(farmerId);

            // Fetch farmer data from Firebase
            fetchFarmerData();
        } else {
            showMessage("User is not logged in.");
            // Optionally navigate back to login screen
        }

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
    }

    private void fetchFarmerData() {
        farmerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch and set Farmer Name
                    tvFarmerName.setText(dataSnapshot.child("farmerName").getValue(String.class));

                    // Fetch and set Date of Birth
                    tvDob.setText(dataSnapshot.child("dob").getValue(String.class));

                    // Fetch and set Mobile Number
                    tvMobileNumber.setText(dataSnapshot.child("mobileNumber").getValue(String.class));

                    // Fetch and set Payment Type
                    tvPaymentType.setText(dataSnapshot.child("paymentType").getValue(String.class));

                    // Fetch and set Payment Per Day
                    tvPaymentPerDay.setText(dataSnapshot.child("paymentPerDay").getValue(String.class));

                    // Fetch and set Payment Per KG
                    tvPaymentPerKg.setText(dataSnapshot.child("paymentPerKg").getValue(String.class));

                    // Fetch and set Day of Payment
                    tvDayOfPayment.setText(dataSnapshot.child("dayOfPayment").getValue(String.class));

                    // Optionally, load the profile image if available
                    // LoadProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                } else {
                    showMessage("No farmer data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    private void setupButtonListeners() {
        btnProfileUpdate.setOnClickListener(v -> {
            // Implement profile update functionality here
            showMessage("Profile Update button clicked.");
        });

        btnLogin.setOnClickListener(v -> {
            // Implement login functionality here
            showMessage("Login button clicked.");
        });

        btnLogout.setOnClickListener(v -> {
            // Implement logout functionality here
            showMessage("Logout button clicked.");
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}

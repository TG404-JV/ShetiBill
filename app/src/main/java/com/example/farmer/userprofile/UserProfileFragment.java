package com.example.farmer.userprofile;

import android.content.Context;
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

import com.example.farmer.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private TextView tvFarmerName, tvDob, tvMobileNumber, tvPaymentType, tvPaymentPerDay, tvPaymentPerKg, tvDayOfPayment;
    private CircleImageView profileImage;
    private Button btnProfileUpdate, btnLogin, btnLogout;

    // SharedPreferences for local storage
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize the views
        initializeViews(view);

        // Initialize SharedPreferences to fetch local data
        sharedPreferences = getActivity().getSharedPreferences("FarmerDetails", Context.MODE_PRIVATE);

        // Fetch farmer data from local storage
        fetchFarmerDataFromLocalStorage();

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

    private void fetchFarmerDataFromLocalStorage() {
        // Fetch and set Farmer Name
        String farmerName = sharedPreferences.getString("farmerName", "N/A");
        Log.d("UserProfileFragment", "Farmer Name: " + farmerName);
        tvFarmerName.setText(farmerName);

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
            // Optionally navigate to login screen after logout
            // Navigate to login screen using your preferred navigation method
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}

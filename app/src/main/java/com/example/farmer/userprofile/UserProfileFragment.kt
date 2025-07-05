package com.example.farmer.userprofile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.farmer.FarmerDetails
import com.example.farmer.LoginActivity
import com.example.farmer.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserProfileFragment : Fragment() {
    private var tvDob: TextView? = null
    private var tvMobileNumber: TextView? = null
    private var tvPaymentType: TextView? = null
    private var tvPaymentPerDay: TextView? = null
    private var tvPaymentPerKg: TextView? = null
    private var tvDayOfPayment: TextView? = null
    private var profileImage: CircleImageView? = null
    private var btnProfileUpdate: Button? = null
    private var btnLogout: Button? = null
    private var ProfileName: TextView? = null

    // SharedPreferences for local storage
    private var sharedPreferences: SharedPreferences? = null

    private var mAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        // Initialize the views
        initializeViews(view)

        // Initialize Firebase Authentication and Database Reference
        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Users") // Assuming user data is stored under "Users"

        // Initialize SharedPreferences to fetch local data
        sharedPreferences =
            requireActivity().getSharedPreferences("FarmerDetails", Context.MODE_PRIVATE)

        // Fetch farmer data from local storage
        fetchFarmerDataFromLocalStorage()

        val firebaseAuth: FirebaseAuth?
        val firebaseStorage: FirebaseStorage?

        // Firebase and permissions setup
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        val user = firebaseAuth.getCurrentUser()


        val profileImageRef =
            firebaseStorage.getReference().child("profile_images/" + user!!.getUid() + ".jpg")
        profileImageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
            Glide.with(
                requireActivity()
            ).load(uri).circleCrop().into(profileImage!!)
        }).addOnFailureListener(OnFailureListener { e: Exception? -> setDefaultProfileImage() })
        // Setup button listeners
        setupButtonListeners()


        // Check if user is logged in
        checkLoginStatus()


        // Setup button listeners
        setupButtonListeners()

        return view
    }

    private fun initializeViews(view: View) {
        profileImage = view.findViewById<CircleImageView>(R.id.profile_image)
        tvDob = view.findViewById<TextView>(R.id.tvDob)
        tvMobileNumber = view.findViewById<TextView>(R.id.tvMobileNumber)
        tvPaymentType = view.findViewById<TextView>(R.id.tvPaymentType)
        tvPaymentPerDay = view.findViewById<TextView>(R.id.tvPaymentPerDay)
        tvPaymentPerKg = view.findViewById<TextView>(R.id.tvPaymentPerKg)
        tvDayOfPayment = view.findViewById<TextView>(R.id.tvDayOfPayment)
        btnProfileUpdate = view.findViewById<Button>(R.id.btnProfileUpdate)
        btnLogout = view.findViewById<Button>(R.id.btnLogout)
        ProfileName = view.findViewById<TextView>(R.id.profile_name)
    }

    private fun fetchFarmerDataFromLocalStorage() {
        // Fetch and set Farmer Name
        val farmerName: String = sharedPreferences!!.getString("farmerName", "N/A")!!

        ProfileName!!.setText(farmerName)

        // Fetch and set Date of Birth
        val dob: String = sharedPreferences!!.getString("dob", "N/A")!!
        Log.d("UserProfileFragment", "DOB: " + dob)
        tvDob!!.setText(dob)

        // Fetch and set Mobile Number
        val mobileNumber: String = sharedPreferences!!.getString("mobileNumber", "N/A")!!
        Log.d("UserProfileFragment", "Mobile Number: " + mobileNumber)
        tvMobileNumber!!.setText(mobileNumber)

        // Fetch and set Payment Type
        val paymentType: String = sharedPreferences!!.getString("paymentType", "N/A")!!
        Log.d("UserProfileFragment", "Payment Type: " + paymentType)
        tvPaymentType!!.setText(paymentType)

        // Fetch and set Payment Per Day
        val paymentPerDay: String = sharedPreferences!!.getString("paymentPerDay", "N/A")!!
        Log.d("UserProfileFragment", "Payment Per Day: " + paymentPerDay)
        tvPaymentPerDay!!.setText(paymentPerDay)

        // Fetch and set Payment Per KG
        val paymentPerKg: String = sharedPreferences!!.getString("paymentPerKg", "N/A")!!
        Log.d("UserProfileFragment", "Payment Per KG: " + paymentPerKg)
        tvPaymentPerKg!!.setText(paymentPerKg)

        // Fetch and set Day of Payment
        val dayOfPayment: String = sharedPreferences!!.getString("dayOfPayment", "N/A")!!
        Log.d("UserProfileFragment", "Day of Payment: " + dayOfPayment)
        tvDayOfPayment!!.setText(dayOfPayment)

        // Fetch and set Profile Image URL (if available) from local storage
        val profileImageUrl: String = sharedPreferences!!.getString("profileImage", "")!!
        if (!profileImageUrl.isEmpty()) {
            Picasso.get().load(profileImageUrl).into(profileImage)
        } else {
            profileImage!!.setImageResource(R.drawable.ic_farmer_profile_img) // Default image if no URL
        }
    }

    private fun setupButtonListeners() {
        // Profile Update button: Navigate to an activity where user can update profile
        btnProfileUpdate!!.setOnClickListener(View.OnClickListener { v: View? ->
            // Example: Navigate to a new activity to update profile
            val ProfileUpdate = Intent(getActivity(), FarmerDetails::class.java)
            startActivity(ProfileUpdate)
        })


        // Logout button: Log out from Firebase and clear SharedPreferences
        btnLogout!!.setOnClickListener(View.OnClickListener { v: View? ->
            // Sign out from Firebase Authentication
            mAuth!!.signOut()

            // Clear all data from SharedPreferences
            val editor = sharedPreferences!!.edit()
            editor.clear()
            editor.apply()

            // Show a logout message
            showMessage("You have successfully logged out.")

            // Redirect to LoginActivity
            val loginIntent = Intent(getActivity(), LoginActivity::class.java)
            startActivity(loginIntent)
            requireActivity().finish() // Optionally close the current activity
        })
    }


    private fun checkLoginStatus() {
        if (mAuth!!.getCurrentUser() != null) {
            // User is logged in, show Logout button and hide Login button
            btnLogout!!.setVisibility(View.VISIBLE)
        } else {
            // User is not logged in, show Login button and hide Logout button
            btnLogout!!.setVisibility(View.GONE)
        }
    }


    private fun showMessage(message: String?) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setDefaultProfileImage() {
        profileImage!!.setImageResource(R.drawable.ic_farmer_profile_img)
    }
}

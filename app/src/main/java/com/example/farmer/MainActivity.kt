package com.example.farmer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.farmer.cropmarket.CropMarket
import com.example.farmer.databinding.ActivityMainBinding
import com.example.farmer.databinding.NavHeaderBinding
import com.example.farmer.emioptions.Loan_calculator
import com.example.farmer.farmerai.FragmentFarmerAi
import com.example.farmer.fertilizer.Fertilizer_Expendituer
import com.example.farmer.govscheme.FragmentGovtScheme
import com.example.farmer.graph.FragmentExpendGraph
import com.example.farmer.home.Home
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupNavigationDrawer()
        setupProfileInfo()

        // Launch correct fragment on first load
        if (savedInstanceState == null) {
            loadFragment(Home())
            binding.navigationView.setCheckedItem(R.id.nav_home)
        }

        // Check if a fragment needs to be loaded directly
        when (intent.getStringExtra("LOAD_FRAGMENT")) {
            "MARKET" -> loadFragment(CropMarket())
            "askAI" -> loadFragment(FragmentFarmerAi())
            "Fertilizer" -> loadFragment(Fertilizer_Expendituer())
        }

        setupImagePicker()
    }

    private fun setupNavigationDrawer() {
        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView

        // Bind header view
        headerBinding = NavHeaderBinding.bind(navigationView.getHeaderView(0))

        updateLoginLogoutButton()

        headerBinding.btnLoginLogout.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.signOut()
                updateLoginLogoutButton()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        headerBinding.profileImage.setOnClickListener {
            openImagePicker()
        }

        binding.menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            if (result.resultCode == RESULT_OK && data != null && data.data != null) {
                val imageUri = data.data!!
                headerBinding.profileImage.setImageURI(imageUri)
                uploadImageToFirebase(imageUri)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun setupProfileInfo() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            headerBinding.userEmail.text = user.email ?: "No Email"
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

            userRef.get().addOnSuccessListener { snapshot ->
                val farmerName = snapshot.child("name").getValue(String::class.java)
                headerBinding.userName.text = farmerName ?: "No Name"
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load farmer data.", Toast.LENGTH_SHORT).show()
            }

            val profileImageRef = firebaseStorage.reference.child("profile_images/${user.uid}.jpg")
            profileImageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).circleCrop().into(headerBinding.profileImage)
                }
                .addOnFailureListener {
                    setDefaultProfileImage()
                }
        } else {
            setDefaultProfileImage()
        }
    }

    private fun setDefaultProfileImage() {
        headerBinding.profileImage.setImageResource(R.drawable.ic_farmer_profile_img)
    }

    private fun updateLoginLogoutButton() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            headerBinding.btnLoginLogout.apply {
                text = getString(R.string.logout)
                setTextColor(resources.getColor(android.R.color.white, theme))
                setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, theme))
            }
        } else {
            headerBinding.btnLoginLogout.apply {
                text = getString(R.string.login)
                setTextColor(resources.getColor(android.R.color.white, theme))
                setBackgroundColor(resources.getColor(R.color.teal_700, theme))
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = firebaseAuth.currentUser ?: return
        val profileImageRef = firebaseStorage.reference.child("profile_images/${user.uid}.jpg")

        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).circleCrop().into(headerBinding.profileImage)
                    saveProfileImageUrl(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileImageUrl(url: String) {
        val user = firebaseAuth.currentUser ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
        userRef.child("profileImageUrl").setValue(url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_language -> {
                startActivity(Intent(this, LanguageSelection::class.java))
                return true
            }
            R.id.action_about_app -> {
                startActivity(Intent(this, ActiviyAboutUS::class.java))
                return true
            }
            R.id.action_privacy_policy -> {
                startActivity(Intent(this, ActivityPrivacyPolicy::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.nav_expenditure -> Fertilizer_Expendituer()
            R.id.nav_market -> CropMarket()
            R.id.loan_calculator -> Loan_calculator()
            R.id.government_scheme -> FragmentGovtScheme()
            R.id.nav_harvest -> FragmentExpendGraph()
            R.id.ai_assistant -> FragmentFarmerAi()
            R.id.Managment -> Home()
            else -> null
        }

        if (item.itemId == R.id.nav_home) {
            startActivity(Intent(this, HomePageActivity::class.java))
        } else if (fragment != null) {
            loadFragment(fragment)
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

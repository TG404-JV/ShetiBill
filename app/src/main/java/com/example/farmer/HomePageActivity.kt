package com.example.farmer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomePageActivity : AppCompatActivity() {
    private var CropManagementLayout: LinearLayout? = null
    private var MARKET: LinearLayout? = null
    private var askAI: LinearLayout? = null
    private var Fertilizer: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        CropManagementLayout = findViewById<LinearLayout>(R.id.cropManagement)
        MARKET = findViewById<LinearLayout>(R.id.Market)
        askAI = findViewById<LinearLayout>(R.id.askAI)
        Fertilizer = findViewById<LinearLayout>(R.id.Fertilizer)

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Keeps it hidden until user swipes
        )


        CropManagementLayout!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Create intent for MainActivity
                val intent = Intent(this@HomePageActivity, MainActivity::class.java)

                // Add extra to indicate we want to load the CropManagementFragment
                startActivity(intent)
            }
        })

        MARKET!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Create intent for MainActivity
                val intent = Intent(this@HomePageActivity, MainActivity::class.java)

                // Add extra to indicate we want to load the CropManagementFragment
                intent.putExtra("LOAD_FRAGMENT", "MARKET")
                startActivity(intent)
            }
        })

        askAI!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Create intent for MainActivity
                val intent = Intent(this@HomePageActivity, MainActivity::class.java)

                // Add extra to indicate we want to load the CropManagementFragment
                intent.putExtra("LOAD_FRAGMENT", "askAI")
                startActivity(intent)
            }
        })

        Fertilizer!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Create intent for MainActivity
                val intent = Intent(this@HomePageActivity, MainActivity::class.java)

                // Add extra to indicate we want to load the CropManagementFragment
                intent.putExtra("LOAD_FRAGMENT", "Fertilizer")
                startActivity(intent)
            }
        })
    }
}


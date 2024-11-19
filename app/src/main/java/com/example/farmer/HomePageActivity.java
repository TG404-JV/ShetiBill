package com.example.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.farmer.farmerai.FragmentFarmerAi;

public class HomePageActivity extends AppCompatActivity {

    private LinearLayout CropManagementLayout,MARKET,askAI,Fertilizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        CropManagementLayout = findViewById(R.id.cropManagement);
        MARKET = findViewById(R.id.Market);
        askAI = findViewById(R.id.askAI);
        Fertilizer = findViewById(R.id.Fertilizer);

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keeps it hidden until user swipes
        );


        CropManagementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for MainActivity
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                // Add extra to indicate we want to load the CropManagementFragment

                startActivity(intent);
            }
        });

        MARKET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for MainActivity
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                // Add extra to indicate we want to load the CropManagementFragment

                intent.putExtra("LOAD_FRAGMENT", "MARKET");
                startActivity(intent);
            }
        });

        askAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for MainActivity
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                // Add extra to indicate we want to load the CropManagementFragment

                intent.putExtra("LOAD_FRAGMENT", "askAI");
                startActivity(intent);
            }
        });

        Fertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for MainActivity
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                // Add extra to indicate we want to load the CropManagementFragment

                intent.putExtra("LOAD_FRAGMENT", "Fertilizer");
                startActivity(intent);
            }
        });





    }
}


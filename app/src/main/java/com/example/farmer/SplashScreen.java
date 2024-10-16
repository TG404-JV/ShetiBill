package com.example.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000; // Duration of the splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Find views
        ImageView splashLogo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.app_name);
        TextView appTagline = findViewById(R.id.app_tagline);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Load animations
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        Animation fadeInSlideUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        Animation fadeInProgressBar = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Start animations
        splashLogo.startAnimation(logoAnim);
        appName.startAnimation(fadeInSlideUp);
        appTagline.startAnimation(fadeInSlideUp);
        progressBar.startAnimation(fadeInProgressBar);

        // Transition to MainActivity after splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}

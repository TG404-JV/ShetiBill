package com.example.farmer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreen extends AppCompatActivity {

    private CircleImageView logoImageView;
    private TextView appNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logoImageView = findViewById(R.id.logo);
        appNameTextView = findViewById(R.id.app_name);

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keeps it hidden until user swipes
        );
        // Animate the logo and app name
        animateLogo();
        animateAppName();

        // Transition to the main activity after the animations are complete
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashScreen.this, LanguageSelection.class));
                finish();
            }
        };
        logoImageView.animate().scaleX(1f).scaleY(1f).setDuration(2000).setInterpolator(new AccelerateInterpolator()).setListener(listener);
    }

    private void animateLogo() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(logoImageView, View.SCALE_X, 0.5f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(logoImageView, View.SCALE_Y, 0.5f, 1f);
        scaleXAnimator.setDuration(2000);
        scaleYAnimator.setDuration(2000);
        scaleXAnimator.setInterpolator(new AccelerateInterpolator());
        scaleYAnimator.setInterpolator(new AccelerateInterpolator());
        scaleXAnimator.start();
        scaleYAnimator.start();
    }

    private void animateAppName() {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(appNameTextView, View.ALPHA, 0f, 1f);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setStartDelay(1000);
        alphaAnimator.start();
    }
}
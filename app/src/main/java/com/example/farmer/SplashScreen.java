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

public class SplashScreen extends AppCompatActivity {

    private ImageView logoImageView;
    private TextView appNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logoImageView = findViewById(R.id.logo);
        appNameTextView = findViewById(R.id.app_name);

        // Animate the logo and app name
        animateLogo();
        animateAppName();

        // Transition to the main activity after the animations are complete
        Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
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
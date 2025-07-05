package com.example.farmer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView

class SplashScreen : AppCompatActivity() {
    private var logoImageView: CircleImageView? = null
    private var appNameTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        logoImageView = findViewById(R.id.logo)
        appNameTextView = findViewById(R.id.app_name)

        // Hide the status bar and navigation bar
        window.decorView.setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Keeps it hidden until user swipes
        )
        // Animate the logo and app name
        animateLogo()
        animateAppName()

        // Transition to the main activity after the animations are complete
        val listener: Animator.AnimatorListener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                finish()
            }
        }
        logoImageView!!.animate().scaleX(1f).scaleY(1f).setDuration(2000)
            .setInterpolator(AccelerateInterpolator()).setListener(listener)
    }

    private fun animateLogo() {
        val scaleXAnimator = ObjectAnimator.ofFloat(logoImageView, View.SCALE_X, 0.5f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(logoImageView, View.SCALE_Y, 0.5f, 1f)
        scaleXAnimator.duration = 2000
        scaleYAnimator.duration = 2000
        scaleXAnimator.interpolator = AccelerateInterpolator()
        scaleYAnimator.interpolator = AccelerateInterpolator()
        scaleXAnimator.start()
        scaleYAnimator.start()
    }

    private fun animateAppName() {
        val alphaAnimator = ObjectAnimator.ofFloat(appNameTextView, View.ALPHA, 0f, 1f)
        alphaAnimator.duration = 2000
        alphaAnimator.startDelay = 1000
        alphaAnimator.start()
    }
}
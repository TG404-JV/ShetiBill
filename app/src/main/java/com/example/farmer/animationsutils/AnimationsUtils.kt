package com.example.farmer.animationsutils

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.View

object AnimationsUtils {

     fun collapseView(view: View) {
        // Get the current height of the view
        val initialHeight = view.getHeight()

        // Animate the view height to 0
        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.duration = 300 // Duration of the collapse animation
        animator.addUpdateListener(object : AnimatorUpdateListener {
            override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                // Set the new height value during animation
                val layoutParams = view.getLayoutParams()
                layoutParams.height = valueAnimator.getAnimatedValue() as Int
                view.setLayoutParams(layoutParams)
            }
        })
        animator.start()
    }

}
package com.example.farmer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.farmer.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : Fragment() {
    private var viewPager: ViewPager2? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)


        val bottomNavigationView = view.findViewById<BottomNavigationView?>(R.id.bottom_navigation)
        viewPager = view.findViewById<ViewPager2?>(R.id.view_pager)

        // Initialize your adapter
        val adapter = HomePagerAdapter(getChildFragmentManager(), lifecycle)
        viewPager!!.setAdapter(adapter)


        // Set up BottomNavigationView with ViewPager2
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem? ->
            // Animate the selected item
            animateSelectedItem(bottomNavigationView, item!!.getItemId())

            if (item.getItemId() == R.id.navigation_farm) {
                viewPager!!.setCurrentItem(0)
                 true
            } else if (item.getItemId() == R.id.navigation_fertilizer) {
                viewPager!!.setCurrentItem(1)
                 true
            } else if (item.getItemId() == R.id.navigation_weather) {
                viewPager!!.setCurrentItem(2)
                 true
            } else if (item.getItemId() == R.id.Account) {
                viewPager!!.setCurrentItem(3)
                 true
            }
            false
        })

        // Synchronize BottomNavigationView and ViewPager2
        viewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.setSelectedItemId(
                    bottomNavigationView.getMenu().getItem(position).getItemId()
                )
            }
        })

        return view
    }

    private fun animateSelectedItem(
        bottomNavigationView: BottomNavigationView,
        selectedItemId: Int,
    ) {
        // Reset translations for all items
        for (i in 0..<bottomNavigationView.getMenu().size()) {
            val itemView = bottomNavigationView.findViewById<View?>(
                bottomNavigationView.getMenu().getItem(i).getItemId()
            )
            if (itemView != null) {
                itemView.setTranslationY(0f) // Reset to original position
            }
        }

        // Animate the selected item
        val selectedItemView = bottomNavigationView.findViewById<View?>(selectedItemId)
        if (selectedItemView != null) {
            selectedItemView.animate().translationY(-10f).setDuration(200)
                .start() // Move upwards by 10dp
        }
    }
}
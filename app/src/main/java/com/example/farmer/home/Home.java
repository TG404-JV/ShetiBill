package com.example.farmer.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.viewpager2.widget.ViewPager2;

public class Home extends Fragment {

    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        viewPager = view.findViewById(R.id.view_pager);

        // Initialize your adapter
        HomePagerAdapter adapter = new HomePagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        // Set up BottomNavigationView with ViewPager2
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Animate the selected item
            animateSelectedItem(bottomNavigationView, item.getItemId());

            if (item.getItemId() == R.id.navigation_farm) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.navigation_fertilizer) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (item.getItemId() == R.id.navigation_weather) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (item.getItemId() == R.id.Account) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        // Synchronize BottomNavigationView and ViewPager2
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(position).getItemId());
            }
        });

        return view;
    }

    private void animateSelectedItem(BottomNavigationView bottomNavigationView, int selectedItemId) {
        // Reset translations for all items
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            View itemView = bottomNavigationView.findViewById(bottomNavigationView.getMenu().getItem(i).getItemId());
            if (itemView != null) {
                itemView.setTranslationY(0); // Reset to original position
            }
        }

        // Animate the selected item
        View selectedItemView = bottomNavigationView.findViewById(selectedItemId);
        if (selectedItemView != null) {
            selectedItemView.animate().translationY(-10).setDuration(200).start(); // Move upwards by 10dp
        }
    }
}
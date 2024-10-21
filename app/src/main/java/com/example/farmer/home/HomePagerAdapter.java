package com.example.farmer.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.lifecycle.Lifecycle;

import com.example.farmer.farmerai.FragmentFarmerAi;
import com.example.farmer.home.bottomtab.MainFarmExpenditureFragment;
import com.example.farmer.home.bottomtab.MainFertilizerExpenditureFragment;
import com.example.farmer.home.bottomtab.MainWeatherBroadcastFragment;
import com.example.farmer.userprofile.UserProfileFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new MainFertilizerExpenditureFragment();
            case 2:
                return new MainWeatherBroadcastFragment();
            case 3:
                return new UserProfileFragment();
            default:
                return new MainFarmExpenditureFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}
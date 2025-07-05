package com.example.farmer.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.farmer.home.bottomtab.MainFarmExpenditureFragment
import com.example.farmer.home.bottomtab.MainFertilizerExpenditureFragment
import com.example.farmer.home.bottomtab.MainWeatherBroadcastFragment
import com.example.farmer.userprofile.UserProfileFragment

class HomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> MainFertilizerExpenditureFragment()
            2 -> MainWeatherBroadcastFragment()
            3 -> UserProfileFragment()
            else -> MainFarmExpenditureFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4 // Number of tabs
    }
}
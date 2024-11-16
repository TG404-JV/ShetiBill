package com.example.farmer.cropmarket;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.farmer.R;
import com.google.android.material.tabs.TabLayout;

public class CropMarket extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop_market, container, false);
        viewPager = view.findViewById(R.id.buySellViewPager);
        tabLayout = view.findViewById(R.id.buySellTabLayout);

        // Set up ViewPager with FragmentPagerAdapter
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);




        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        CropPriceAdapter adapter = new CropPriceAdapter(getChildFragmentManager());
        adapter.addFragment(new UploadCropFragment(), "Upload Crop");
        adapter.addFragment(new AvailableCropsFragment(), "Available Crops");
        viewPager.setAdapter(adapter);
    }
}

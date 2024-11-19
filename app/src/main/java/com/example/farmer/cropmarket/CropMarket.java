package com.example.farmer.cropmarket;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.farmer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

public class CropMarket extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout CropDetails;
    private MaterialButton ViewAll;

    private boolean IsDetailsVisible=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop_market, container, false);
        viewPager = view.findViewById(R.id.buySellViewPager);
        tabLayout = view.findViewById(R.id.buySellTabLayout);
        CropDetails=view.findViewById(R.id.PriceDetails);
        ViewAll=view.findViewById(R.id.viewallBtn);

        // Set up ViewPager with FragmentPagerAdapter
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        ViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsDetailsVisible)
                {
                    CropDetails.setVisibility(View.GONE);
                    IsDetailsVisible=false;
                }
                else
                {
                    CropDetails.setVisibility(View.VISIBLE);
                    IsDetailsVisible=true;

                }
            }
        });





        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        CropPriceAdapter adapter = new CropPriceAdapter(getChildFragmentManager());
        adapter.addFragment(new UploadCropFragment(), "Upload Crop");
        adapter.addFragment(new AvailableCropsFragment(), "Available Crops");
        viewPager.setAdapter(adapter);
    }
}

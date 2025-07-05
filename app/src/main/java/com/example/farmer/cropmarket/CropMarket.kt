package com.example.farmer.cropmarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.farmer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout


class CropMarket : Fragment() {
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var CropDetails: LinearLayout? = null
    private var ViewAll: MaterialButton? = null

    private var IsDetailsVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crop_market, container, false)
        viewPager = view.findViewById<ViewPager>(R.id.buySellViewPager)
        tabLayout = view.findViewById<TabLayout>(R.id.buySellTabLayout)
        CropDetails = view.findViewById<LinearLayout>(R.id.PriceDetails)
        ViewAll = view.findViewById<MaterialButton>(R.id.viewallBtn)

        // Set up ViewPager with FragmentPagerAdapter
        setupViewPager(viewPager!!)
        tabLayout!!.setupWithViewPager(viewPager)

        ViewAll!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (IsDetailsVisible) {
                    CropDetails!!.setVisibility(View.GONE)
                    IsDetailsVisible = false
                } else {
                    CropDetails!!.setVisibility(View.VISIBLE)
                    IsDetailsVisible = true
                }
            }
        })





        return view
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = CropPriceAdapter(getChildFragmentManager())
        adapter.addFragment(UploadCropFragment(), "Upload Crop")
        adapter.addFragment(AvailableCropsFragment(), "Available Crops")
        viewPager.setAdapter(adapter)
    }
}

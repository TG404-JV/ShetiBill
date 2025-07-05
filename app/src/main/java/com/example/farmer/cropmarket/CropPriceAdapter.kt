package com.example.farmer.cropmarket

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CropPriceAdapter  // Constructor
    (fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragmentList: MutableList<Fragment> = ArrayList<Fragment>()
    private val fragmentTitleList: MutableList<String?> = ArrayList<String?>()

    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position)
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment?, title: String?) {
        fragmentList.add(fragment!!)
        fragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList.get(position)
    }
}

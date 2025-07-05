package com.example.farmer.mainacthandler

import com.example.farmer.MyAdapter
import java.util.Locale


object SearchHelper {
    fun initializeDummyData(dataList: MutableList<String?>) {
        dataList.add("Corn")
        dataList.add("Wheat")
        dataList.add("Rice")
        dataList.add("Sugarcane")
        dataList.add("Barley")
    }

    fun performSearch(query: String, dataList: MutableList<String>, adapter: MyAdapter?) {
        val filteredList: MutableList<String?> = ArrayList<String?>()

        for (item in dataList) {
            if (item.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
        }
    }
}


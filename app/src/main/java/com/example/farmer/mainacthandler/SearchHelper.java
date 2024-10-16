package com.example.farmer.mainacthandler;


import android.widget.Toast;

import com.example.farmer.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchHelper {

    public static void initializeDummyData(List<String> dataList) {
        dataList.add("Corn");
        dataList.add("Wheat");
        dataList.add("Rice");
        dataList.add("Sugarcane");
        dataList.add("Barley");
    }

    public static void performSearch(String query, List<String> dataList, MyAdapter adapter) {
        List<String> filteredList = new ArrayList<>();

        for (String item : dataList) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }


    }
}


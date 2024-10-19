package com.example.farmer.cropmarket;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AvailableCropsFragment extends Fragment {

    private static final String TAG = "AvailableCropsFragment"; // Tag for logging

    private RecyclerView cropRecyclerView;
    private CropAdapter cropAdapter;
    private List<Crop> cropList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_crops, container, false);
        initRecyclerView(view);
        fetchCrops();
        return view;
    }

    // Initialize RecyclerView and Adapter
    private void initRecyclerView(View view) {
        cropRecyclerView = view.findViewById(R.id.cropRecyclerView);
        cropRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cropList = new ArrayList<>();

        cropAdapter = new CropAdapter(cropList, crop -> {
            // Handle crop click here (e.g., show details or allow editing)
            Toast.makeText(getContext(), "Clicked on: " + crop.getCropName(), Toast.LENGTH_SHORT).show();
        }, crop -> {
            // Handle crop long click here
        });

        cropRecyclerView.setAdapter(cropAdapter);
    }

    // Fetch crops from Firebase
    private void fetchCrops() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("crops");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cropList.clear(); // Clear the existing list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Crop crop = snapshot.getValue(Crop.class);
                    if (crop != null) {
                        cropList.add(crop); // Add crop to the list
                    }
                }
                cropAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching crops: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load crops. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show delete confirmation dialog


    // Delete the selected crop


}

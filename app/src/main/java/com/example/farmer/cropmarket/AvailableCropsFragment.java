package com.example.farmer.cropmarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private RecyclerView cropRecyclerView;
    private CropAdapter cropAdapter;
    private List<Crop> cropList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_crops, container, false);

        // Initialize RecyclerView
        cropRecyclerView = view.findViewById(R.id.cropRecyclerView);
        cropRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cropList = new ArrayList<>();

        // Initialize CropAdapter
        cropAdapter = new CropAdapter(cropList, crop -> {
            // Handle crop click here
            // You can show crop details in a dialog or new fragment
        });
        cropRecyclerView.setAdapter(cropAdapter);

        // Fetch crops from Firebase
        fetchCrops();

        return view;
    }

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
                Log.e("FirebaseError", "Error fetching crops: " + databaseError.getMessage());
            }
        });
    }
}

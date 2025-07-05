package com.example.farmer.cropmarket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.cropmarket.CropAdapter.OnCropItemClickListener
import com.example.farmer.cropmarket.CropAdapter.OnCropItemLongClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AvailableCropsFragment : Fragment() {
    private var cropRecyclerView: RecyclerView? = null
    private var cropAdapter: CropAdapter? = null
    private var cropList: MutableList<Crop?>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_available_crops, container, false)
        initRecyclerView(view)
        fetchCrops()
        return view
    }

    // Initialize RecyclerView and Adapter
    private fun initRecyclerView(view: View) {
        cropRecyclerView = view.findViewById<RecyclerView>(R.id.cropRecyclerView)
        cropRecyclerView!!.setLayoutManager(LinearLayoutManager(getContext()))
        cropList = ArrayList<Crop?>()

        cropAdapter = CropAdapter(cropList as MutableList<Crop>, { crop: Crop? ->
            // Handle crop click here (e.g., show details or allow editing)
            Toast.makeText(getContext(), "Clicked on: " + crop!!.cropName, Toast.LENGTH_SHORT)
                .show()
        }, { crop: Crop? -> })

        cropRecyclerView!!.setAdapter(cropAdapter)
    }

    // Fetch crops from Firebase
    private fun fetchCrops() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("crops")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cropList!!.clear() // Clear the existing list to avoid duplicates
                for (snapshot in dataSnapshot.getChildren()) {
                    val crop = snapshot.getValue<Crop?>(Crop::class.java)
                    if (crop != null) {
                        cropList!!.add(crop) // Add crop to the list
                    }
                }
                cropAdapter!!.notifyDataSetChanged() // Notify adapter of data change
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error fetching crops: " + databaseError.getMessage())
                Toast.makeText(
                    getContext(),
                    "Failed to load crops. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    } // Show delete confirmation dialog
    // Delete the selected crop


    companion object {
        private const val TAG = "AvailableCropsFragment" // Tag for logging
    }
}

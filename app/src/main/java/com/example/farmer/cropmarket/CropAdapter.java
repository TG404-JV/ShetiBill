package com.example.farmer.cropmarket;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    private List<Crop> cropList;
    private OnCropItemClickListener onCropItemClickListener;

    // Constructor
    public CropAdapter(List<Crop> cropList, OnCropItemClickListener onCropItemClickListener) {
        this.cropList = cropList;
        this.onCropItemClickListener = onCropItemClickListener;
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_item, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = cropList.get(position);
        holder.cropNameTextView.setText(crop.getCropName());
        holder.cropQuantityTextView.setText(crop.getCropQuantity());
        holder.cropPriceTextView.setText(crop.getCropPrice());
        holder.cropSellerNameTextView.setText(crop.getSellerName()); // Use seller name instead
        holder.sellerLocationTextView.setText(crop.getLocation()); // Bind seller location

        // Load crop image with placeholder
        Picasso.get()
                .load(crop.getImageUrl())
                .placeholder(R.drawable.sample_crop_image) // Add a placeholder image resource
                .into(holder.cropImageView);

        holder.itemView.setOnClickListener(view -> onCropItemClickListener.onCropClick(crop));

        // Handle the call button click
        holder.callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + crop.getContactNumber()));
            holder.itemView.getContext().startActivity(callIntent);
        });
    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    public interface OnCropItemClickListener {
        void onCropClick(Crop crop);
    }

    public static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView cropNameTextView;
        TextView cropQuantityTextView;
        TextView cropPriceTextView;
        TextView cropSellerNameTextView;  // Field for seller name
        TextView sellerLocationTextView;   // Field for location
        ImageView cropImageView;            // ImageView for crop image
        ImageButton callButton;             // Call button

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            cropNameTextView = itemView.findViewById(R.id.cropNameTextView);
            cropQuantityTextView = itemView.findViewById(R.id.cropQuantityTextView);
            cropPriceTextView = itemView.findViewById(R.id.cropPriceTextView);
            cropSellerNameTextView = itemView.findViewById(R.id.cropSellerNameTextView); // Field for seller name
            sellerLocationTextView = itemView.findViewById(R.id.sellerLocationTextView); // Field for location
            cropImageView = itemView.findViewById(R.id.cropImageView); // ImageView for crop image
            callButton = itemView.findViewById(R.id.callButton); // ImageButton for call action
        }
    }
}

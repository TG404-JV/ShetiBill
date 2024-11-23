package com.example.farmer.fertilizer;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.farmer.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FertilizerExpenditureAdapter extends RecyclerView.Adapter<FertilizerExpenditureAdapter.ViewHolder> {

    private List<FertilizerExpenditure> expenditureList;
    private Context context;

    public FertilizerExpenditureAdapter(List<FertilizerExpenditure> expenditureList, Context context) {
        this.expenditureList = expenditureList;
        this.context = context;
    }

    @NonNull
    @Override
    public FertilizerExpenditureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fertilizer_expenditure_recyclerview, parent, false);
        return new FertilizerExpenditureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FertilizerExpenditureAdapter.ViewHolder holder, int position) {
        FertilizerExpenditure expenditure = expenditureList.get(position);

        // Bind data to views
        holder.tvItemName.setText(expenditure.getItemName());
        holder.tvPurchaseDate.setText(expenditure.getPurchaseDate());
        holder.tvPurchaseAmount.setText(expenditure.getPurchaseAmount());
        holder.tvPaymentMode.setText(expenditure.getPaymentMode());

        // Retrieve image
        final String imagePath = expenditure.getReceiptImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {
                // Load URI or file path using Glide
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_payment_mode_img) // Default placeholder
                        .into(holder.IVfertilizerName);
            } else {
                // Decode Base64 image if the path is encoded data
                try {
                    byte[] decodedBytes = Base64.decode(imagePath, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    holder.IVfertilizerName.setImageBitmap(bitmap);
                } catch (IllegalArgumentException e) {
                    holder.IVfertilizerName.setImageResource(R.drawable.ic_payment_mode_img); // Default image for errors
                    Toast.makeText(context, "Invalid image format", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            holder.IVfertilizerName.setImageResource(R.drawable.ic_payment_mode_img); // Default image
        }

        // View image in a popup on click
        holder.viewImg.setOnClickListener(v -> showImagePopup(imagePath));

        // Copy button functionality
        holder.copyBtn.setOnClickListener(v -> {
            String data = expenditure.getItemName() + " - " + expenditure.getPurchaseAmount() + " - " + expenditure.getPaymentMode();
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Fertilizer Data", data);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Data copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // Share button functionality
        holder.shareBtn.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Fertilizer Purchase\nItem: " + expenditure.getItemName() + "\nAmount: " + expenditure.getPurchaseAmount() + "\nPayment Mode: " + expenditure.getPaymentMode();
            context.startActivity(Intent.createChooser(shareIntent, "Share using"));
        });

        // Edit button functionality
        holder.editBtn.setOnClickListener(v -> {
            Toast.makeText(context, "Edit functionality", Toast.LENGTH_SHORT).show();
            // Add logic for editing the expenditure entry
        });

        holder.FertilizerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isCardVisible) {
                    // Show the card button with animation
                    holder.cardBtn.setVisibility(View.VISIBLE);
                    holder.cardBtn.animate()
                            .alpha(1f) // Fade in
                            .translationY(0) // Slide in (or set your preferred translation direction)
                            .setDuration(300) // Animation duration
                            .start();
                    holder.isCardVisible = false;
                } else {
                    // Hide the card button with animation
                    holder.cardBtn.animate()
                            .alpha(0f) // Fade out
                            .translationY(-100) // Slide out (you can adjust the value to your preference)
                            .setDuration(300) // Animation duration
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    holder.cardBtn.setVisibility(View.GONE);
                                }
                            })
                            .start();
                    holder.isCardVisible = true;
                }
            }
        });

        holder.FertilizerCollaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isCollapsVisible) {
                    // Show FertilizerDetails with animation
                    holder.FertilizerDetails.setVisibility(View.VISIBLE);
                    holder.FertilizerDetails.animate()
                            .alpha(1f) // Fade in
                            .translationY(0) // Slide in (you can adjust the direction or value)
                            .setDuration(300) // Animation duration
                            .start();
                    holder.isCollapsVisible = false;
                } else {
                    // Hide FertilizerDetails with animation
                    holder.FertilizerDetails.animate()
                            .alpha(0f) // Fade out
                            .translationY(-100) // Slide out (you can adjust the value to your preference)
                            .setDuration(300) // Animation duration
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    holder.FertilizerDetails.setVisibility(View.GONE);
                                }
                            })
                            .start();
                    holder.isCollapsVisible = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }

    // Method to show image popup
    private void showImagePopup(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(context, "No image available", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.image_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        PhotoView popupImageView = dialog.findViewById(R.id.popupImageView);

        if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {
            // Load URI or file path into the popup
            Glide.with(context)
                    .load(imagePath)
                    .into(popupImageView);
        } else {
            // Decode Base64 image for the popup
            try {
                byte[] decodedBytes = Base64.decode(imagePath, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                popupImageView.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                Toast.makeText(context, "Invalid image format", Toast.LENGTH_SHORT).show();
            }
        }

        // Share Button functionality - Share image directly
        dialog.findViewById(R.id.btnShare).setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) popupImageView.getDrawable()).getBitmap();
            String path = saveImageToExternalStorage(bitmap);  // Save image to external storage

            if (path != null) {
                File imageFile = new File(path);
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", imageFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);  // Share the image URI
                context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else {
                Toast.makeText(context, "Failed to save the image", Toast.LENGTH_SHORT).show();
            }
        });

        // Download Button functionality - Save image to file manager
        dialog.findViewById(R.id.btnDownload).setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) popupImageView.getDrawable()).getBitmap();
            String path = saveImageToExternalStorage(bitmap);  // Save image to external storage

            if (path != null) {
                Toast.makeText(context, "Image saved to " + path, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to save the image", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.closePopupButton).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Method to save image to external storage and return the file path
    private String saveImageToExternalStorage(Bitmap bitmap) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);  // Save the image as JPEG
            fos.flush();
            return file.getAbsolutePath();  // Return the path where the image is saved
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvPurchaseDate;
        TextView tvPurchaseAmount;
        TextView tvPaymentMode;
        ImageView IVfertilizerName;

        MaterialButton viewImg;
        LinearLayout cardBtn,FertilizerDetails;
        MaterialButton copyBtn, shareBtn, editBtn;

        RelativeLayout FertilizerCollaps;

        boolean isCardVisible=false;
        boolean isCollapsVisible=false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            tvPurchaseAmount = itemView.findViewById(R.id.tvPurchaseAmount);
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
            IVfertilizerName = itemView.findViewById(R.id.IVfertilizerName);
            cardBtn = itemView.findViewById(R.id.cardBtn);
            copyBtn = itemView.findViewById(R.id.CopyBtn);
            shareBtn = itemView.findViewById(R.id.ShareBtn);
            editBtn = itemView.findViewById(R.id.EditBtn);
            viewImg = itemView.findViewById(R.id.viewReceiptButton);
            FertilizerDetails=itemView.findViewById(R.id.FertilizerDetails);
            FertilizerCollaps=itemView.findViewById(R.id.FertilizerCollaps);
            cardBtn.setVisibility(View.GONE);
            FertilizerDetails.setVisibility(View.GONE);
        }
    }
}

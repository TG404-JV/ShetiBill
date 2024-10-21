package com.example.farmer.fertilizer;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;

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

        // Set up the click listener for card flip animation
        holder.itemView.setOnClickListener(v -> flipCard(holder));

        // Set up Copy button functionality
        holder.copyBtn.setOnClickListener(v -> {
            String data = expenditure.getItemName() + " - " + expenditure.getPurchaseAmount() + " - " + expenditure.getPaymentMode();
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Fertilizer Data", data);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Data copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // Set up Share button functionality
        holder.shareBtn.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Fertilizer Purchase\nItem: " + expenditure.getItemName() + "\nAmount: " + expenditure.getPurchaseAmount() + "\nPayment Mode: " + expenditure.getPaymentMode();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(shareIntent, "Share using"));
        });

        // Set up Edit button functionality (dummy logic)
        holder.editBtn.setOnClickListener(v -> {
            Toast.makeText(context, "Edit functionality", Toast.LENGTH_SHORT).show();
            // You can add logic to open a new activity or fragment to edit the expenditure entry
        });
    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }

    // Method to flip the card
    private void flipCard(FertilizerExpenditureAdapter.ViewHolder holder) {
        boolean isBackVisible = holder.cardBtn.getVisibility() == View.VISIBLE;

        if (isBackVisible) {
            // Flip back to front
            AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_out);
            AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_in);
            flipOut.setTarget(holder.itemView);
            flipIn.setTarget(holder.itemView);
            flipOut.start();
            holder.cardBtn.setVisibility(View.GONE);
            flipIn.start();
        } else {
            // Flip front to back
            AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_out);
            AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_in);
            flipOut.setTarget(holder.itemView);
            flipIn.setTarget(holder.itemView);
            flipOut.start();
            holder.cardBtn.setVisibility(View.VISIBLE);
            flipIn.start();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvPurchaseDate;
        TextView tvPurchaseAmount;
        TextView tvPaymentMode;
        RelativeLayout cardBtn;
        ImageButton copyBtn, shareBtn, editBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            tvPurchaseAmount = itemView.findViewById(R.id.tvPurchaseAmount);
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
            cardBtn = itemView.findViewById(R.id.cardBtn);  // Hidden buttons container
            copyBtn = itemView.findViewById(R.id.CopyBtn);
            shareBtn = itemView.findViewById(R.id.ShareBtn);
            editBtn = itemView.findViewById(R.id.EditBtn);

            // Initially hide the buttons
            cardBtn.setVisibility(View.GONE);
        }
    }
}
package com.example.farmer.home.bottomtab.labour;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LabourAdapter extends RecyclerView.Adapter<LabourAdapter.LabourViewHolder> {

    private final List<Labour> labourList;
    private final Context context;
    private final String userId; // Store the user ID

    // Constructor to include userId
    public LabourAdapter(List<Labour> labourList, Context context, String userId) {
        this.labourList = labourList;
        this.context = context;
        this.userId = userId; // Initialize userId
    }

    @NonNull
    @Override
    public LabourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_labour, parent, false);
        return new LabourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabourViewHolder holder, int position) {
        Labour labour = labourList.get(position);
        holder.labourNameTextView.setText(labour.getName());
        holder.workDateTextView.setText(labour.getDate());

        holder.LabourModificationMenu.setVisibility(View.GONE);

        holder.labourNameTextView.setOnClickListener(v -> {
            if (holder.LabourModificationMenu.getVisibility() == View.GONE) {
                holder.LabourModificationMenu.setVisibility(View.VISIBLE);
                holder.LabourModificationMenu.setAlpha(0f);
                holder.LabourModificationMenu.animate()
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            } else {
                holder.LabourModificationMenu.animate()
                        .alpha(0f)
                        .setDuration(400)
                        .withEndAction(() -> holder.LabourModificationMenu.setVisibility(View.GONE))
                        .start();
            }
        });

        WeightAdapter weightAdapter = new WeightAdapter(labour.getWeights(), weight -> {});

        holder.weightRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.weightRecyclerView.setAdapter(weightAdapter);

        holder.addWeightBtn.setOnClickListener(v -> showAddWeightDialog(labour, weightAdapter, holder));
        holder.subWeightBtn.setOnClickListener(v -> {
            if (!labour.getWeights().isEmpty()) {
                labour.getWeights().remove(labour.getWeights().size() - 1);
                weightAdapter.notifyDataSetChanged();
                updateTotalWeight(labour, holder);
                updateWeightsInFirebase(labour); // Save updated weights to Firebase
            }
        });

        holder.shareBtn.setOnClickListener(v -> shareLabourDetails(labour));
        holder.copyBtn.setOnClickListener(v -> copyLabourDetailsToClipboard(labour));
        holder.editBtn.setOnClickListener(v -> showEditDialog(labour, position));
        holder.deleteBtn.setOnClickListener(v -> {
            labourList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Labour details deleted", Toast.LENGTH_SHORT).show();
        });

        updateTotalWeight(labour, holder); // Update the total weight dynamically
        saveLabourToFirebase(labour); // Save or update labour data in Firebase
    }

    @Override
    public int getItemCount() {
        return labourList.size();
    }

    static class LabourViewHolder extends RecyclerView.ViewHolder {
        TextView labourNameTextView, workDateTextView, totalWeightTextView;
        RecyclerView weightRecyclerView;
        LinearLayout LabourModificationMenu;
        ImageButton addWeightBtn, subWeightBtn, shareBtn, copyBtn, editBtn, deleteBtn;

        LabourViewHolder(@NonNull View itemView) {
            super(itemView);
            labourNameTextView = itemView.findViewById(R.id.LabourName);
            workDateTextView = itemView.findViewById(R.id.WorkDate);
            totalWeightTextView = itemView.findViewById(R.id.totalWeight);
            weightRecyclerView = itemView.findViewById(R.id.AddWeightRecyclerView);
            addWeightBtn = itemView.findViewById(R.id.addWeightBtn);
            subWeightBtn = itemView.findViewById(R.id.subWeightBtn);
            shareBtn = itemView.findViewById(R.id.ShareBtn);
            copyBtn = itemView.findViewById(R.id.CopyBtn);
            editBtn = itemView.findViewById(R.id.EditBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            LabourModificationMenu = itemView.findViewById(R.id.menuTask);
        }
    }

    private void showAddWeightDialog(Labour labour, WeightAdapter weightAdapter, LabourViewHolder holder) {
        EditText weightInput = new EditText(context);
        weightInput.setHint("Enter weight");

        new AlertDialog.Builder(context)
                .setTitle("Add Weight")
                .setView(weightInput)
                .setPositiveButton("Add", (dialog, which) -> {
                    String weightStr = weightInput.getText().toString();
                    if (!weightStr.isEmpty()) {
                        try {
                            int weight = Integer.parseInt(weightStr);
                            labour.getWeights().add(weight); // Add weight to the labour's list
                            weightAdapter.notifyDataSetChanged();
                            updateTotalWeight(labour, holder);
                            updateWeightsInFirebase(labour); // Update weights in Firebase
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareLabourDetails(Labour labour) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "Labour: " + labour.getName() + "\nDate: " + labour.getDate() + "\nTotal Weight: " + labour.getTotalWeight();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void copyLabourDetailsToClipboard(Labour labour) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Labour Details", "Labour: " + labour.getName() + "\nDate: " + labour.getDate() + "\nTotal Weight: " + labour.getTotalWeight());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(Labour labour, int position) {
        View editView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_labour, null);
        EditText editName = editView.findViewById(R.id.editLabourName);
        EditText editDate = editView.findViewById(R.id.editWorkDate);

        editName.setText(labour.getName());
        editDate.setText(labour.getDate());

        new AlertDialog.Builder(context)
                .setTitle("Edit Labour")
                .setView(editView)
                .setPositiveButton("Save", (dialog, which) -> {
                    labour.setName(editName.getText().toString());
                    labour.setDate(editDate.getText().toString());
                    notifyItemChanged(position);
                    saveLabourToFirebase(labour); // Save updated labour data to Firebase
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTotalWeight(Labour labour, LabourViewHolder holder) {
        labour.setTotalWeight(labour.calculateTotalWeight());
        holder.totalWeightTextView.setText(String.valueOf(labour.getTotalWeight())); // Update UI with the total weight
    }

    private void saveLabourToFirebase(Labour labour) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("labours").child(userId);

        // Create a unique key using userId, labour name, and date
        String labourKey = labour.getName() + "_" + labour.getDate();
        DatabaseReference currentLabourRef = databaseReference.child(labourKey);

        currentLabourRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Update existing labour entry
                    currentLabourRef.child("weights").setValue(labour.getWeights())
                            .addOnSuccessListener(aVoid -> {
                                currentLabourRef.child("totalWeight").setValue(labour.getTotalWeight())
                                        .addOnSuccessListener(aVoid1 -> {
                                            Toast.makeText(context, "Labour details updated!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to update total weight: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to update weights: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Create a new labour entry
                    currentLabourRef.setValue(labour)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Labour details saved!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(context, "Error checking data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWeightsInFirebase(Labour labour) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("labours").child(userId);

        // Create a unique key using labour name and date
        String labourKey = labour.getName() + "_" + labour.getDate();
        DatabaseReference currentLabourRef = databaseReference.child(labourKey);

        // Update the weights field only
        currentLabourRef.child("weights").setValue(labour.getWeights())
                .addOnSuccessListener(aVoid -> {
                    currentLabourRef.child("totalWeight").setValue(labour.getTotalWeight())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(context, "Weight added and updated in Firebase!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to update total weight: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update weights: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

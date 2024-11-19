package com.example.farmer.home.bottomtab.labour;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LabourAdapter extends RecyclerView.Adapter<LabourAdapter.LabourViewHolder> {

    private final List<Labour> labourList;
    private final Context context;
    private final List<Labour> originalLabourList;
    private final Runnable onLabourListChanged;

    private static final String PREFERENCE_KEY = "LabourData";
    private static final String LABOUR_LIST_KEY = "LabourList";

    // Constructor
    public LabourAdapter(List<Labour> labourList, Context context, List<Labour> originalLabourList, Runnable onLabourListChanged) {
        this.labourList = labourList;
        this.context = context;
        this.originalLabourList = originalLabourList;
        this.onLabourListChanged = onLabourListChanged;
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
        holder.CropName.setText(labour.getCropName());
        holder.laborWorkingType.setText(labour.getWorkingType());
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

        WeightAdapter weightAdapter = new WeightAdapter(labour.getWeights(), weight -> {
            // Handle weight changes here if needed
        });

        holder.weightRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.weightRecyclerView.setAdapter(weightAdapter);

        holder.addWeightBtn.setOnClickListener(v -> showAddWeightDialog(labour, weightAdapter, holder));
        holder.subWeightBtn.setOnClickListener(v -> {
            if (!labour.getWeights().isEmpty()) {
                labour.getWeights().remove(labour.getWeights().size() - 1);
                weightAdapter.notifyDataSetChanged();
                updateTotalWeight(labour, holder);
                saveLabourData(); // Save updated data to SharedPreferences
            }
        });

        holder.shareBtn.setOnClickListener(v -> shareLabourDetails(labour));
        holder.copyBtn.setOnClickListener(v -> copyLabourDetailsToClipboard(labour));
        holder.editBtn.setOnClickListener(v -> showEditDialog(labour, position));
        holder.deleteBtn.setOnClickListener(v -> {
            if (position >= 0 && position < labourList.size()) {
                // Find the labour in the original list to delete
                Labour labourToRemove = labourList.get(position);

                // Remove the labour from both lists
                originalLabourList.remove(labourToRemove); // Remove from the original list
                labourList.remove(position); // Remove from the displayed list

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, labourList.size()); // Update the range after removal

                // Save the updated list to SharedPreferences
                saveLabourData();
                onLabourListChanged.run(); // Update the main list if needed

                if (labourList.isEmpty()) {
                    notifyDataSetChanged(); // Ensure all views are cleared when the list is empty
                }

                Toast.makeText(context, "Labour details deleted", Toast.LENGTH_SHORT).show();
            }
        });


        updateTotalWeight(labour, holder); // Update the total weight dynamically
    }

    @Override
    public int getItemCount() {
        return labourList.size();
    }

    static class LabourViewHolder extends RecyclerView.ViewHolder {
        TextView labourNameTextView, workDateTextView, totalWeightTextView, laborWorkingType, CropName;
        RecyclerView weightRecyclerView;
        LinearLayout LabourModificationMenu;
        MaterialButton addWeightBtn, subWeightBtn, shareBtn, copyBtn, editBtn, deleteBtn;

        LabourViewHolder(@NonNull View itemView) {
            super(itemView);
            labourNameTextView = itemView.findViewById(R.id.laborName);
            workDateTextView = itemView.findViewById(R.id.workDate);
            totalWeightTextView = itemView.findViewById(R.id.totalWeight);
            weightRecyclerView = itemView.findViewById(R.id.AddWeightRecyclerView);
            addWeightBtn = itemView.findViewById(R.id.addWeightBtn);
            subWeightBtn = itemView.findViewById(R.id.removeWeightBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            copyBtn = itemView.findViewById(R.id.copyBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            LabourModificationMenu = itemView.findViewById(R.id.menuTask);
            laborWorkingType = itemView.findViewById(R.id.laborWorkingType);
            CropName = itemView.findViewById(R.id.CropName);
        }
    }

    // Save labour list to SharedPreferences
    private void saveLabourData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(originalLabourList); // Convert the original list to JSON
        editor.putString(LABOUR_LIST_KEY, json);
        editor.apply(); // Save data asynchronously
    }

    // Load labour list from SharedPreferences
    public static List<Labour> loadLabourData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LABOUR_LIST_KEY, null);

        if (json != null) {
            Type type = new TypeToken<List<Labour>>() {
            }.getType();
            return gson.fromJson(json, type); // Convert JSON back to list
        } else {
            return new ArrayList<>(); // Return empty list if no data found
        }
    }

    private void showAddWeightDialog(Labour labour, WeightAdapter weightAdapter, LabourViewHolder holder) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialogue_add_weight, null);
        TextInputEditText weightInput = dialogView.findViewById(R.id.weightInput);
        Button addButton = dialogView.findViewById(R.id.addButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        addButton.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString();
            if (!weightStr.isEmpty()) {
                try {
                    int weight = Integer.parseInt(weightStr);
                    labour.getWeights().add(weight);
                    weightAdapter.notifyDataSetChanged();
                    updateTotalWeight(labour, holder);
                    saveLabourData();
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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

        TextInputEditText editName = editView.findViewById(R.id.editLabourName);
        TextInputEditText editDate = editView.findViewById(R.id.editWorkDate);
        Button saveButton = editView.findViewById(R.id.saveButton);
        Button cancelButton = editView.findViewById(R.id.cancelButton);

        editName.setText(labour.getName());
        editDate.setText(labour.getDate());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(editView)
                .setCancelable(false)
                .create();

        saveButton.setOnClickListener(v -> {
            labour.setName(editName.getText().toString());
            labour.setDate(editDate.getText().toString());
            notifyItemChanged(position);
            saveLabourData();
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    // Update the total weight displayed
    private void updateTotalWeight(Labour labour, LabourViewHolder holder) {
        int totalWeight = 0;
        for (int weight : labour.getWeights()) {
            totalWeight += weight;
        }
        holder.totalWeightTextView.setText("Total: " + totalWeight);
    }
}

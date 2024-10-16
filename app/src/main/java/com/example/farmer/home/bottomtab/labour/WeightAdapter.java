package com.example.farmer.home.bottomtab.labour;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private final List<Integer> weightList;
    private final OnWeightClickListener listener;

    public interface OnWeightClickListener {
        void onWeightClick(int weight);
    }

    public WeightAdapter(List<Integer> weightList, OnWeightClickListener listener) {
        this.weightList = weightList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        Integer weight = weightList.get(position);
        holder.weightTextView.setText(String.valueOf(weight));

        holder.itemView.setOnClickListener(v -> listener.onWeightClick(weight));
    }

    @Override
    public int getItemCount() {
        return weightList.size();
    }

    static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView;

        WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
        }
    }
}

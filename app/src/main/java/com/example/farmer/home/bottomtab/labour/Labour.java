package com.example.farmer.home.bottomtab.labour;

import java.util.ArrayList;
import java.util.List;

public class Labour {
    private String name;
    private String date;
    private List<Integer> weights;
    private int totalWeight;

    // Constructor
    public Labour(String name, String date) {
        this.name = name;
        this.date = date;
        this.weights = new ArrayList<>(); // Initialize weights list
        this.totalWeight = 0; // Initialize total weight
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    // Method to calculate total weight
    public int calculateTotalWeight() {
        int total = 0;
        for (int weight : weights) {
            total += weight;
        }
        return total;
    }
}

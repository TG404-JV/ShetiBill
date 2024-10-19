package com.example.farmer.home.bottomtab.labour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Labour {
    private String name;
    private String date; // Stored as String in "dd/MM/yyyy" format
    private List<Integer> weights;
    private int totalWeight;

    // Constructor
    public Labour(String name, String date) {
        this.name = name;
        this.date = date;
        this.weights = new ArrayList<>(); // Initialize weights list
        this.totalWeight = calculateTotalWeight(); // Initialize total weight based on weights
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
        this.totalWeight = calculateTotalWeight(); // Recalculate total weight if needed
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public void addWeight(int weight) {
        weights.add(weight); // Add weight to the list
        this.totalWeight = calculateTotalWeight(); // Update total weight
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

    // Method to get date as Date object for sorting
    public Date getDateAsObject() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(date); // Convert the string date to Date object
        } catch (ParseException e) {
            e.printStackTrace(); // Log the exception
            return null; // Return null if parsing fails
        }
    }
}

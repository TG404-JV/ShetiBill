package com.example.farmer.cropmarket;

public class Crop {

    private String cropName;
    private String cropQuantity;
    private String cropPrice;
    private String location; // Field for location
    private String contactNumber; // Field for contact number
    private String imageUrl; // URL of the crop image
    private String sellerName; // Changed 'name' to 'sellerName' for clarity

    // No-argument constructor (required by Firebase)
    public Crop() {
        // Default constructor required for calls to DataSnapshot.getValue(Crop.class)
    }

    // Constructor with parameters
    public Crop(String cropName, String cropQuantity, String cropPrice, String location, String contactNumber, String sellerName, String imageUrl) {
        this.cropName = cropName;
        this.cropQuantity = cropQuantity;
        this.cropPrice = cropPrice;
        this.location = location; // Assign location
        this.contactNumber = contactNumber; // Assign contact number
        this.imageUrl = imageUrl;
        this.sellerName = sellerName; // Assign seller name
    }

    // Getters
    public String getCropName() {
        return cropName;
    }

    public String getCropQuantity() {
        return cropQuantity;
    }

    public String getCropPrice() {
        return cropPrice;
    }

    public String getLocation() {
        return location; // Getter for location
    }

    public String getContactNumber() {
        return contactNumber; // Getter for contact number
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSellerName() {
        return sellerName; // Getter for seller name
    }

    // Setters
    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public void setCropQuantity(String cropQuantity) {
        this.cropQuantity = cropQuantity;
    }

    public void setCropPrice(String cropPrice) {
        this.cropPrice = cropPrice;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    // Override toString() method for better debugging
    @Override
    public String toString() {
        return "Crop{" +
                "cropName='" + cropName + '\'' +
                ", cropQuantity='" + cropQuantity + '\'' +
                ", cropPrice='" + cropPrice + '\'' +
                ", location='" + location + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", sellerName='" + sellerName + '\'' +
                '}';
    }
}

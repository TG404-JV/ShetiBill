package com.example.farmer.fertilizer;


import org.json.JSONException;
import org.json.JSONObject;

public class FertilizerExpenditure {
    private String itemName;
    private String purchaseDate;
    private String purchaseAmount; // Field for the purchase amount
    private String paymentMode;
    private String receiptImagePath;

    // Constructor
    public FertilizerExpenditure(String itemName, String purchaseDate, String purchaseAmount, String paymentMode, String receiptImagePath) {
        this.itemName = itemName;
        this.purchaseDate = purchaseDate;
        this.purchaseAmount = purchaseAmount;
        this.paymentMode = paymentMode;
        this.receiptImagePath = receiptImagePath;
    }

    // Getter methods
    public String getItemName() {
        return itemName;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getPurchaseAmount() { // Getter for purchase amount
        return purchaseAmount;
    }

    public String getAmount()
    {
        return purchaseAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getReceiptImagePath() {
        return receiptImagePath;
    }

    // Static method to create an instance from a JSON object
    public static FertilizerExpenditure fromJson(JSONObject jsonObject) {
        try {
            String itemName = jsonObject.optString("ItemName", "");
            String purchaseDate = jsonObject.optString("PurchaseDate", "");
            String purchaseAmount = jsonObject.optString("PurchaseAmount", "");
            String paymentMode = jsonObject.optString("PaymentMode", "");
            String receiptPath = jsonObject.optString("ReceiptPath", ""); // Use "ReceiptPath" to match saveData()

            return new FertilizerExpenditure(itemName, purchaseDate, purchaseAmount, paymentMode, receiptPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

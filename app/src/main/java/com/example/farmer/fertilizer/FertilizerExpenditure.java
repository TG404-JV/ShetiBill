package com.example.farmer.fertilizer;


import org.json.JSONException;
import org.json.JSONObject;

public class FertilizerExpenditure {
    private String itemName;
    private String purchaseDate;
    private String purchaseAmount;
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

    // Getter for itemName
    public String getItemName() {
        return itemName;
    }

    // Getter for purchaseDate
    public String getPurchaseDate() {
        return purchaseDate;
    }

    // Getter for purchaseAmount
    public String getPurchaseAmount() {
        return purchaseAmount;
    }

    // Getter for paymentMode
    public String getPaymentMode() {
        return paymentMode;
    }

    // Getter for receiptImagePath
    public String getReceiptImagePath() {
        return receiptImagePath;
    }

    // Static method to create an instance from JSON
    public static FertilizerExpenditure fromJson(JSONObject jsonObject) throws JSONException {
        String itemName = jsonObject.optString("ItemName");
        String purchaseDate = jsonObject.optString("PurchaseDate");
        String purchaseAmount = jsonObject.optString("PurchaseAmount");
        String paymentMode = jsonObject.optString("PaymentMode");
        String receiptImagePath = jsonObject.optString("ReceiptImagePath", null);

        return new FertilizerExpenditure(itemName, purchaseDate, purchaseAmount, paymentMode, receiptImagePath);
    }
}


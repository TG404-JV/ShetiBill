package com.example.farmer.fertilizer

import org.json.JSONObject


class FertilizerExpenditure // Constructor
    (
// Getter methods
    @JvmField val itemName: String?, @JvmField val purchaseDate: String?, // Getter for purchase amount
    val amount: String?, // Field for the purchase amount
    @JvmField val paymentMode: String?, @JvmField val receiptImagePath: String?
) {
    companion object {
        // Static method to create an instance from a JSON object
        @JvmStatic
        fun fromJson(jsonObject: JSONObject): FertilizerExpenditure? {
            try {
                val itemName = jsonObject.optString("ItemName", "")
                val purchaseDate = jsonObject.optString("PurchaseDate", "")
                val purchaseAmount = jsonObject.optString("PurchaseAmount", "")
                val paymentMode = jsonObject.optString("PaymentMode", "")
                val receiptPath =
                    jsonObject.optString("ReceiptPath", "") // Use "ReceiptPath" to match saveData()

                return FertilizerExpenditure(
                    itemName,
                    purchaseDate,
                    purchaseAmount,
                    paymentMode,
                    receiptPath
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}

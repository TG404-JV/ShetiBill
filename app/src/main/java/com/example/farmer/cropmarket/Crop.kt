package com.example.farmer.cropmarket

class Crop {
    // Getter and setter for cropId
    @JvmField
    var cropId: String? = null // New field for crop ID

    // Setters
    // Getters
    @JvmField
    var cropName: String? = null
    @JvmField
    var cropQuantity: String? = null
    @JvmField
    var cropPrice: String? = null

    // Getter for location
    @JvmField
    var location: String? = null // Field for location

    // Getter for contact number
    @JvmField
    var contactNumber: String? = null // Field for contact number
    @JvmField
    var imageUrl: String? = null // URL of the crop image

    // Getter for seller name
    @JvmField
    var sellerName: String? = null // Changed 'name' to 'sellerName' for clarity

    // No-argument constructor (required by Firebase)
    constructor()

    // Constructor with parameters including cropId
    constructor(
        cropId: String?,
        cropName: String?,
        cropQuantity: String?,
        cropPrice: String?,
        location: String?,
        contactNumber: String?,
        sellerName: String?,
        imageUrl: String?
    ) {
        this.cropId = cropId // Assign cropId
        this.cropName = cropName
        this.cropQuantity = cropQuantity
        this.cropPrice = cropPrice
        this.location = location // Assign location
        this.contactNumber = contactNumber // Assign contact number
        this.imageUrl = imageUrl
        this.sellerName = sellerName // Assign seller name
    }

    // Override toString() method for better debugging
    override fun toString(): String {
        return "Crop{" +
                "cropId='" + cropId + '\'' +  // Include cropId in toString
                ", cropName='" + cropName + '\'' +
                ", cropQuantity='" + cropQuantity + '\'' +
                ", cropPrice='" + cropPrice + '\'' +
                ", location='" + location + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", sellerName='" + sellerName + '\'' +
                '}'
    }
}

package com.example.farmer.cropmarket

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.cropmarket.CropAdapter.CropViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class CropAdapter // Constructor
// Keep this constructor definition
    (
    private val cropList: MutableList<Crop>,
    private val onCropItemClickListener: OnCropItemClickListener, // New interface for long clicks
    private val onCropItemLongClickListener: OnCropItemLongClickListener
) : RecyclerView.Adapter<CropViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_item, parent, false)
        return CropViewHolder(view)
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        val crop = cropList.get(position)
        holder.cropNameTextView.setText(crop.cropName)
        holder.cropQuantityTextView.setText(crop.cropQuantity)
        holder.cropPriceTextView.setText(crop.cropPrice)
        holder.cropSellerNameTextView.setText(crop.sellerName)
        holder.sellerLocationTextView.setText(crop.location)

        // Load crop image with placeholder
        Picasso.get()
            .load(crop.imageUrl)
            .placeholder(R.drawable.sample_crop_image) // Add a placeholder image resource
            .into(holder.cropImageView)

        // Handle click for crop item
        holder.itemView.setOnClickListener(View.OnClickListener { view: View? ->
            onCropItemClickListener.onCropClick(
                crop
            )
        })

        // Handle long click for crop item
        holder.itemView.setOnLongClickListener(OnLongClickListener { v: View? ->
            onCropItemLongClickListener.onCropLongClick(crop) // Trigger long click event
            true // Indicate that the long click was handled
        })

        // Handle the call button click
        holder.callButton.setOnClickListener(View.OnClickListener { v: View? ->
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + crop.contactNumber))
            holder.itemView.getContext().startActivity(callIntent)
        })
    }

    override fun getItemCount(): Int {
        return cropList.size
    }

    fun interface OnCropItemClickListener {
        fun onCropClick(crop: Crop?)
    }

    fun interface OnCropItemLongClickListener {
        // New interface for long clicks
        fun onCropLongClick(crop: Crop?)
    }

    class CropViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cropNameTextView: TextView
        var cropQuantityTextView: TextView
        var cropPriceTextView: TextView
        var cropSellerNameTextView: TextView // Field for seller name
        var sellerLocationTextView: TextView // Field for location
        var cropImageView: ShapeableImageView? // ImageView for crop image
        var callButton: MaterialButton // Call button

        init {
            cropNameTextView = itemView.findViewById<TextView>(R.id.cropNameTextView)
            cropQuantityTextView = itemView.findViewById<TextView>(R.id.cropQuantityTextView)
            cropPriceTextView = itemView.findViewById<TextView>(R.id.cropPriceTextView)
            cropSellerNameTextView =
                itemView.findViewById<TextView>(R.id.cropSellerNameTextView) // Field for seller name
            sellerLocationTextView =
                itemView.findViewById<TextView>(R.id.sellerLocationTextView) // Field for location
            cropImageView =
                itemView.findViewById<ShapeableImageView?>(R.id.cropImageView) // ImageView for crop image
            callButton =
                itemView.findViewById<MaterialButton>(R.id.callButton) // ImageButton for call action
        }
    }
}

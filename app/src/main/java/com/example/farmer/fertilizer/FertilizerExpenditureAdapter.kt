package com.example.farmer.fertilizer

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FertilizerExpenditureAdapter(
    private val expenditureList: MutableList<FertilizerExpenditure>,
    private val context: Context
) : RecyclerView.Adapter<FertilizerExpenditureAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.fertilizer_expenditure_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expenditure = expenditureList.get(position)

        // Bind data to views
        holder.tvItemName.setText(expenditure.itemName)
        holder.tvPurchaseDate.setText(expenditure.purchaseDate)
        holder.tvPurchaseAmount.setText(expenditure.amount)
        holder.tvPaymentMode.setText(expenditure.paymentMode)

        // Retrieve image
        val imagePath = expenditure.receiptImagePath
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {
                // Load URI or file path using Glide
                Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.ic_payment_mode_img) // Default placeholder
                    .into(holder.IVfertilizerName)
            } else {
                // Decode Base64 image if the path is encoded data
                try {
                    val decodedBytes = Base64.decode(imagePath, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    holder.IVfertilizerName.setImageBitmap(bitmap)
                } catch (e: IllegalArgumentException) {
                    holder.IVfertilizerName.setImageResource(R.drawable.ic_payment_mode_img) // Default image for errors
                    Toast.makeText(context, "Invalid image format", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            holder.IVfertilizerName.setImageResource(R.drawable.ic_payment_mode_img) // Default image
        }

        // View image in a popup on click
        holder.viewImg.setOnClickListener(View.OnClickListener { v: View? ->
            showImagePopup(
                imagePath
            )
        })

        // Copy button functionality
        holder.copyBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val data =
                expenditure.itemName + " - " + expenditure.amount + " - " + expenditure.paymentMode
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Fertilizer Data", data)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Data copied to clipboard", Toast.LENGTH_SHORT).show()
        })

        // Share button functionality
        holder.shareBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            val shareBody =
                "Fertilizer Purchase\nItem: " + expenditure.itemName + "\nAmount: " + expenditure.amount + "\nPayment Mode: " + expenditure.paymentMode
            context.startActivity(Intent.createChooser(shareIntent, "Share using"))
        })

        // Edit button functionality
        holder.editBtn.setOnClickListener(View.OnClickListener { v: View? ->
            Toast.makeText(context, "Edit functionality", Toast.LENGTH_SHORT).show()
        })

        holder.FertilizerDetails.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (holder.isCardVisible) {
                    // Show the card button with animation
                    holder.cardBtn.setVisibility(View.VISIBLE)
                    holder.cardBtn.animate()
                        .alpha(1f) // Fade in
                        .translationY(0f) // Slide in (or set your preferred translation direction)
                        .setDuration(300) // Animation duration
                        .start()
                    holder.isCardVisible = false
                } else {
                    // Hide the card button with animation
                    holder.cardBtn.animate()
                        .alpha(0f) // Fade out
                        .translationY(-100f) // Slide out (you can adjust the value to your preference)
                        .setDuration(300) // Animation duration
                        .withEndAction(object : Runnable {
                            override fun run() {
                                holder.cardBtn.setVisibility(View.GONE)
                            }
                        })
                        .start()
                    holder.isCardVisible = true
                }
            }
        })

        holder.FertilizerCollaps.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (holder.isCollapsVisible) {
                    // Show FertilizerDetails with animation
                    holder.FertilizerDetails.setVisibility(View.VISIBLE)
                    holder.FertilizerDetails.animate()
                        .alpha(1f) // Fade in
                        .translationY(0f) // Slide in (you can adjust the direction or value)
                        .setDuration(300) // Animation duration
                        .start()
                    holder.isCollapsVisible = false
                } else {
                    // Hide FertilizerDetails with animation
                    holder.FertilizerDetails.animate()
                        .alpha(0f) // Fade out
                        .translationY(-100f) // Slide out (you can adjust the value to your preference)
                        .setDuration(300) // Animation duration
                        .withEndAction(object : Runnable {
                            override fun run() {
                                holder.FertilizerDetails.setVisibility(View.GONE)
                            }
                        })
                        .start()
                    holder.isCollapsVisible = true
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return expenditureList.size
    }

    // Method to show image popup
    private fun showImagePopup(imagePath: String?) {
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(context, "No image available", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.image_popup)
        dialog.getWindow()!!
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val popupImageView = dialog.findViewById<PhotoView>(R.id.popupImageView)

        if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {
            // Load URI or file path into the popup
            Glide.with(context)
                .load(imagePath)
                .into(popupImageView)
        } else {
            // Decode Base64 image for the popup
            try {
                val decodedBytes = Base64.decode(imagePath, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                popupImageView.setImageBitmap(bitmap)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Invalid image format", Toast.LENGTH_SHORT).show()
            }
        }

        // Share Button functionality - Share image directly
        dialog.findViewById<View?>(R.id.btnShare)
            .setOnClickListener(View.OnClickListener { v: View? ->
                val bitmap = (popupImageView.getDrawable() as BitmapDrawable).getBitmap()
                val path = saveImageToExternalStorage(bitmap) // Save image to external storage
                if (path != null) {
                    val imageFile = File(path)
                    val uri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".fileprovider",
                        imageFile
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.setType("image/*")
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri) // Share the image URI
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                } else {
                    Toast.makeText(context, "Failed to save the image", Toast.LENGTH_SHORT).show()
                }
            })

        // Download Button functionality - Save image to file manager
        dialog.findViewById<View?>(R.id.btnDownload)
            .setOnClickListener(View.OnClickListener { v: View? ->
                val bitmap = (popupImageView.getDrawable() as BitmapDrawable).getBitmap()
                val path = saveImageToExternalStorage(bitmap) // Save image to external storage
                if (path != null) {
                    Toast.makeText(context, "Image saved to " + path, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to save the image", Toast.LENGTH_SHORT).show()
                }
            })

        dialog.findViewById<View?>(R.id.closePopupButton)
            .setOnClickListener(View.OnClickListener { v: View? -> dialog.dismiss() })
        dialog.show()
    }

    // Method to save image to external storage and return the file path
    private fun saveImageToExternalStorage(bitmap: Bitmap): String? {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "ShetiBill"
        )
        if (!directory.exists()) {
            directory.mkdirs() // Create the directory if it doesn't exist
        }

        val fileName = "image_" + System.currentTimeMillis() + ".jpg"
        val file = File(directory, fileName)

        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) // Save the image as JPEG
                fos.flush()
                return file.getAbsolutePath() // Return the path where the image is saved
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItemName: TextView
        var tvPurchaseDate: TextView
        var tvPurchaseAmount: TextView
        var tvPaymentMode: TextView
        var IVfertilizerName: ImageView

        var viewImg: MaterialButton
        var cardBtn: LinearLayout
        var FertilizerDetails: LinearLayout
        var copyBtn: MaterialButton
        var shareBtn: MaterialButton
        var editBtn: MaterialButton

        var FertilizerCollaps: RelativeLayout

        var isCardVisible: Boolean = false
        var isCollapsVisible: Boolean = false

        init {
            tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
            tvPurchaseDate = itemView.findViewById<TextView>(R.id.tvPurchaseDate)
            tvPurchaseAmount = itemView.findViewById<TextView>(R.id.tvPurchaseAmount)
            tvPaymentMode = itemView.findViewById<TextView>(R.id.tvPaymentMode)
            IVfertilizerName = itemView.findViewById<ImageView>(R.id.IVfertilizerName)
            cardBtn = itemView.findViewById<LinearLayout>(R.id.cardBtn)
            copyBtn = itemView.findViewById<MaterialButton>(R.id.CopyBtn)
            shareBtn = itemView.findViewById<MaterialButton>(R.id.ShareBtn)
            editBtn = itemView.findViewById<MaterialButton>(R.id.EditBtn)
            viewImg = itemView.findViewById<MaterialButton>(R.id.viewReceiptButton)
            FertilizerDetails = itemView.findViewById<LinearLayout>(R.id.FertilizerDetails)
            FertilizerCollaps = itemView.findViewById<RelativeLayout>(R.id.FertilizerCollaps)
            cardBtn.setVisibility(View.GONE)
            FertilizerDetails.setVisibility(View.GONE)
        }
    }
}

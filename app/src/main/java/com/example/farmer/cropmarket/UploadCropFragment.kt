package com.example.farmer.cropmarket

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.cropmarket.CropAdapter.OnCropItemClickListener
import com.example.farmer.cropmarket.CropAdapter.OnCropItemLongClickListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class UploadCropFragment : Fragment() {
    private var cropRecyclerView: RecyclerView? = null
    private var cropAdapter: CropAdapter? = null
    private var cropList: MutableList<Crop?>? = null
    private var imageUri: Uri? = null

    // Firebase references
    private var cropReference: DatabaseReference? = null
    private var storageReference: StorageReference? = null

    // Store the dialog reference
    private var addCropDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_crop, container, false)

        // Initialize RecyclerView and Adapter
        cropRecyclerView = view.findViewById<RecyclerView>(R.id.cropRecyclerView)
        cropRecyclerView!!.setLayoutManager(LinearLayoutManager(getContext()))

        cropList = ArrayList<Crop?>()
        cropAdapter = CropAdapter(cropList as MutableList<Crop>, { crop: Crop? ->
            // Handle crop click here (e.g., show details or allow editing)
            Toast.makeText(getContext(), "Clicked on: " + crop!!.cropName, Toast.LENGTH_SHORT)
                .show()
        }, { crop: Crop? ->
            // Handle crop long click here
            showDeleteConfirmationDialog(crop!!)
        })

        cropRecyclerView!!.setAdapter(cropAdapter)

        // Initialize Firebase Database and Storage
        val database = FirebaseDatabase.getInstance()
        cropReference = database.getReference("crops")
        storageReference =
            FirebaseStorage.getInstance().getReference("crop_images") // Firebase Storage for images

        // Fetch crop details from Firebase
        fetchCropsFromFirebase()

        // Initialize Floating Action Button
        val fabAddCrop = view.findViewById<ExtendedFloatingActionButton>(R.id.addListingFab)
        fabAddCrop.setOnClickListener(View.OnClickListener { v: View? -> showAddCropDialog() })

        return view
    }

    private fun showAddCropDialog() {
        // Create the dialog instance
        addCropDialog = Dialog(requireContext())
        addCropDialog!!.setContentView(R.layout.dialog_add_crop)

        val dialogCropNameEditText =
            addCropDialog!!.findViewById<TextInputEditText>(R.id.cropNameEditText)
        val dialogCropQuantityEditText =
            addCropDialog!!.findViewById<TextInputEditText>(R.id.cropQuantityEditText)
        val dialogCropPriceEditText =
            addCropDialog!!.findViewById<TextInputEditText>(R.id.cropPriceEditText)
        val dialogLocationEditText =
            addCropDialog!!.findViewById<TextInputEditText>(R.id.locationEditText)
        val dialogContactEditText =
            addCropDialog!!.findViewById<TextInputEditText>(R.id.contactEditText)
        val sellerName = addCropDialog!!.findViewById<TextInputEditText>(R.id.sellerNameEditText)
        val dialogUploadCropButton =
            addCropDialog!!.findViewById<MaterialButton>(R.id.addCropButton)
        val cropImageView = addCropDialog!!.findViewById<ImageView>(R.id.cropImageView)

        addCropDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        // Image picker when clicking on ImageView
        cropImageView.setOnClickListener(View.OnClickListener { v: View? -> openImagePicker() })

        dialogUploadCropButton.setOnClickListener(View.OnClickListener { v: View? ->
            val cropName = dialogCropNameEditText.getText().toString().trim { it <= ' ' }
            val cropQuantity = dialogCropQuantityEditText.getText().toString().trim { it <= ' ' }
            val cropPrice = dialogCropPriceEditText.getText().toString().trim { it <= ' ' }
            val location = dialogLocationEditText.getText().toString().trim { it <= ' ' }
            val contactNumber = dialogContactEditText.getText().toString().trim { it <= ' ' }
            val seller = sellerName.getText().toString().trim { it <= ' ' }

            // Validate input
            if (cropName.isEmpty() || cropQuantity.isEmpty() || cropPrice.isEmpty() || location.isEmpty() || contactNumber.isEmpty() || imageUri == null) {
                Toast.makeText(
                    getContext(),
                    "Please fill in all fields and upload an image",
                    Toast.LENGTH_SHORT
                ).show()

            }

            // Upload the crop image and details to Firebase
            uploadCropToFirebase(cropName, cropQuantity, cropPrice, location, contactNumber, seller)
            addCropDialog!!.dismiss() // Close dialog after initiating the upload
        })

        addCropDialog!!.show() // Show the dialog
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData() // Get the image URI from device storage

            // Set the selected image in the ImageView of the dialog
            if (addCropDialog != null) {
                val cropImageView = addCropDialog!!.findViewById<ImageView?>(R.id.cropImageView)
                if (cropImageView != null) {
                    cropImageView.setImageURI(imageUri)
                }
            }
        }
    }

    private fun uploadCropToFirebase(
        cropName: String?,
        cropQuantity: String?,
        cropPrice: String?,
        location: String?,
        contactNumber: String?,
        seller: String?
    ) {
        if (imageUri != null) {
            val fileReference =
                storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            fileReference.putFile(imageUri!!)
                .addOnSuccessListener(OnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                    // Retrieve the download URL of the uploaded image
                    fileReference.getDownloadUrl()
                        .addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                            val imageUrl = uri.toString()
                            // Create a new Crop object with the image URL
                            val cropId = cropReference!!.push().getKey() // Get the new unique ID
                            val newCrop = Crop(
                                cropId,
                                cropName,
                                cropQuantity,
                                cropPrice,
                                location,
                                contactNumber,
                                seller,
                                imageUrl
                            ) // Updated constructor

                            // Upload the crop details along with the image URL to Firebase
                            cropReference!!.child(cropId!!).setValue(newCrop)
                                .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                                    Toast.makeText(
                                        getContext(),
                                        "Crop uploaded successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    cropList!!.add(newCrop)
                                    cropAdapter!!.notifyItemInserted(cropList!!.size - 1)
                                })
                                .addOnFailureListener(OnFailureListener { e: Exception? ->
                                    Toast.makeText(
                                        getContext(),
                                        "Failed to upload crop",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e(TAG, "Failed to upload crop: ", e)
                                })
                        })
                }).addOnFailureListener(OnFailureListener { e: Exception? ->
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                        .show()
                    Log.e(TAG, "Failed to upload image: ", e)
                })
        }
    }

    private fun fetchCropsFromFirebase() {
        cropReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cropList!!.clear() // Clear the list to avoid duplicates
                for (cropSnapshot in dataSnapshot.getChildren()) {
                    val crop = cropSnapshot.getValue<Crop?>(Crop::class.java)
                    if (crop != null) {
                        crop.cropId = cropSnapshot.getKey() // Set crop ID to match Firebase key
                        cropList!!.add(crop)
                    }
                }
                cropAdapter!!.notifyDataSetChanged() // Notify adapter about data change
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(getContext(), "Failed to load crops", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failed to load crops: ", databaseError.toException())
            }
        })
    }

    private fun showDeleteConfirmationDialog(crop: Crop) {
        AlertDialog.Builder(getContext())
            .setTitle("Delete Crop")
            .setMessage("Are you sure you want to delete this crop?")
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    deleteCrop(crop)
                })
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteCrop(crop: Crop) {
        cropReference!!.child(crop.cropId!!).removeValue() // Use the correct crop ID for deletion
            .addOnSuccessListener(OnSuccessListener { aVoid: Void? ->
                cropList!!.remove(crop)
                cropAdapter!!.notifyDataSetChanged() // Notify adapter about the deletion
                Toast.makeText(getContext(), "Crop deleted successfully!", Toast.LENGTH_SHORT)
                    .show()
            })
            .addOnFailureListener(OnFailureListener { e: Exception? ->
                Toast.makeText(getContext(), "Failed to delete crop", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failed to delete crop: ", e)
            })
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAG = "UploadCropFragment"
    }
}

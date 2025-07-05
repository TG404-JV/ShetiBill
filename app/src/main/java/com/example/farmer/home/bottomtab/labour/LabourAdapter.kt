package com.example.farmer.home.bottomtab.labour

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private const val PREFERENCE_KEY = "LabourData"
private const val LABOUR_LIST_KEY = "LabourList"


class LabourAdapter(
    private val labourList: MutableList<Labour>,
    private val context: Context,
    private val originalLabourList: MutableList<Labour>,
    private val onLabourListChanged: () -> Unit
) : RecyclerView.Adapter<LabourAdapter.LabourViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_labour, parent, false)
        return LabourViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabourViewHolder, position: Int) {
        val labour = labourList[position]
        holder.labourNameTextView.text = labour.name
        holder.workDateTextView.text = labour.date
        holder.cropName.text = labour.cropName
        holder.laborWorkingType.text = labour.workingType
        holder.labourModificationMenu.visibility = View.GONE

        holder.labourNameTextView.setOnClickListener {
            if (holder.labourModificationMenu.visibility == View.GONE) {
                holder.labourModificationMenu.visibility = View.VISIBLE
                holder.labourModificationMenu.alpha = 0f
                holder.labourModificationMenu.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .start()
            } else {
                holder.labourModificationMenu.animate()
                    .alpha(0f)
                    .setDuration(400)
                    .withEndAction { holder.labourModificationMenu.visibility = View.GONE }
                    .start()
            }
        }

        val weightAdapter = WeightAdapter(labour.weights) { /* Handle weight changes if needed */ }
        holder.weightRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.weightRecyclerView.adapter = weightAdapter

        holder.addWeightBtn.setOnClickListener { showAddWeightDialog(labour, weightAdapter, holder) }
        holder.subWeightBtn.setOnClickListener {
            if (labour.weights.isNotEmpty()) {
                labour.weights.removeAt(labour.weights.size - 1)
                weightAdapter.notifyDataSetChanged()
                updateTotalWeight(labour, holder)
                saveLabourData()
            }
        }

        holder.shareBtn.setOnClickListener { shareLabourDetails(labour) }
        holder.copyBtn.setOnClickListener { copyLabourDetailsToClipboard(labour) }
        holder.editBtn.setOnClickListener { showEditDialog(labour, position) }
        holder.deleteBtn.setOnClickListener {
            if (position in 0 until labourList.size) {
                val labourToRemove = labourList[position]
                originalLabourList.remove(labourToRemove)
                labourList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, labourList.size)
                saveLabourData()
                onLabourListChanged()
                if (labourList.isEmpty()) {
                    notifyDataSetChanged()
                }
                Toast.makeText(context, "Labour details deleted", Toast.LENGTH_SHORT).show()
            }
        }

        updateTotalWeight(labour, holder)

        holder.labourDetail.setOnClickListener {
            if (holder.isLabourDetailsVisible) {
                collapseView(holder.labourMoreDetails)
                holder.isLabourDetailsVisible = false
            } else {
                expandView(holder.labourMoreDetails)
                holder.isLabourDetailsVisible = true
            }
        }
    }

    override fun getItemCount(): Int = labourList.size

    class LabourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labourNameTextView: TextView = itemView.findViewById(R.id.laborName)
        val workDateTextView: TextView = itemView.findViewById(R.id.workDate)
        val totalWeightTextView: TextView = itemView.findViewById(R.id.totalWeight)
        val weightRecyclerView: RecyclerView = itemView.findViewById(R.id.AddWeightRecyclerView)
        val addWeightBtn: MaterialButton = itemView.findViewById(R.id.addWeightBtn)
        val subWeightBtn: MaterialButton = itemView.findViewById(R.id.removeWeightBtn)
        val shareBtn: MaterialButton = itemView.findViewById(R.id.shareBtn)
        val copyBtn: MaterialButton = itemView.findViewById(R.id.copyBtn)
        val editBtn: MaterialButton = itemView.findViewById(R.id.editBtn)
        val deleteBtn: MaterialButton = itemView.findViewById(R.id.deleteBtn)
        val labourModificationMenu: LinearLayout = itemView.findViewById(R.id.menuTask)
        val laborWorkingType: TextView = itemView.findViewById(R.id.laborWorkingType)
        val cropName: TextView = itemView.findViewById(R.id.CropName)
        val labourDetail: LinearLayout = itemView.findViewById(R.id.LabourHeader)
        val labourMoreDetails: LinearLayout = itemView.findViewById(R.id.LabourMoreDetails)
        var isLabourDetailsVisible: Boolean = false
    }

    private fun saveLabourData() {
        val sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(originalLabourList)
        editor.putString(LABOUR_LIST_KEY, json)
        editor.apply()
    }

    private fun showAddWeightDialog(labour: Labour, weightAdapter: WeightAdapter, holder: LabourViewHolder) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialogue_add_weight, null)
        val weightInput: TextInputEditText = dialogView.findViewById(R.id.weightInput)
        val addButton: Button = dialogView.findViewById(R.id.addButton)
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        addButton.setOnClickListener {
            val weightStr = weightInput.text.toString()
            if (weightStr.isNotEmpty()) {
                try {
                    val weight = weightStr.toInt()
                    labour.weights.add(weight)
                    weightAdapter.notifyDataSetChanged()
                    updateTotalWeight(labour, holder)
                    saveLabourData()
                    dialog.dismiss()
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun shareLabourDetails(labour: Labour) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Labour: ${labour.name}\nDate: ${labour.date}\nTotal Weight: ${labour.totalWeight}")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun copyLabourDetailsToClipboard(labour: Labour) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Labour Details", "Labour: ${labour.name}\nDate: ${labour.date}\nTotal Weight: ${labour.totalWeight}")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showEditDialog(labour: Labour, position: Int) {
        val editView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_labour, null)
        val editName: TextInputEditText = editView.findViewById(R.id.editLabourName)
        val editDate: TextInputEditText = editView.findViewById(R.id.editWorkDate)
        val saveButton: Button = editView.findViewById(R.id.saveButton)
        val cancelButton: Button = editView.findViewById(R.id.cancelButton)

        editName.setText(labour.name)
        editDate.setText(labour.date)

        val dialog = AlertDialog.Builder(context)
            .setView(editView)
            .setCancelable(false)
            .create()

        saveButton.setOnClickListener {
            labour.name = editName.text.toString()
            labour.date = editDate.text.toString()
            notifyItemChanged(position)
            saveLabourData()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updateTotalWeight(labour: Labour, holder: LabourViewHolder) {
        val totalWeight = labour.weights.sum()
        holder.totalWeightTextView.text = "Total: $totalWeight"
    }

    private fun collapseView(view: View) {
        val initialHeight = view.height
        val animator = ValueAnimator.ofInt(initialHeight, 0).apply {
            duration = 300
            addUpdateListener {
                view.layoutParams.height = it.animatedValue as Int
                view.requestLayout()
            }
        }
        animator.start()
    }

    private fun expandView(view: View) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = view.measuredHeight
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE
        val animator = ValueAnimator.ofInt(0, targetHeight).apply {
            duration = 300
            addUpdateListener {
                view.layoutParams.height = it.animatedValue as Int
                view.requestLayout()
            }
        }
        animator.start()
    }

    companion object {
        fun loadLabourData(context: Context): List<Labour> {
            val sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
            val json = sharedPreferences.getString(LABOUR_LIST_KEY, null)
            return if (json != null) {
                val type = object : TypeToken<List<Labour>>() {}.type
                Gson().fromJson(json, type)
            } else {
                emptyList()
            }
        }
    }
}
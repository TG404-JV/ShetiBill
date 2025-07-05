package com.example.farmer.home.bottomtab.labour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R

class WeightAdapter(
    private var weightList: MutableList<Int>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weight, parent, false)
        return WeightViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val weight = weightList[position]
        holder.weightTextView.text = weight.toString()
        holder.itemView.setOnClickListener { listener(weight) }
    }

    override fun getItemCount(): Int = weightList.size

    fun updateWeights(newWeightList: MutableList<Int>) {
        weightList = newWeightList
        notifyDataSetChanged()
    }

    class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weightTextView: TextView = itemView.findViewById(R.id.weightTextView)
    }
}
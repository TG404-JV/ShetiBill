package com.example.farmer.home.bottomtab

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.fertilizer.FertilizerExpenditure
import com.example.farmer.fertilizer.FertilizerExpenditure.Companion.fromJson
import com.example.farmer.fertilizer.FertilizerExpenditureAdapter
import com.example.farmer.home.FertilizerExpenditurePrintAdapter
import org.json.JSONArray
import org.json.JSONException

class MainFertilizerExpenditureFragment : Fragment() {
    private var recyclerViewExpenditures: RecyclerView? = null
    private var adapter: FertilizerExpenditureAdapter? = null
    private var expenditureList: MutableList<FertilizerExpenditure>? = null

    private var totalSpendingAmount: TextView? = null
    private var averageSpendingAmount: TextView? = null
    private var printButton: ImageView? = null
    private var Amount_Section: LinearLayout? = null
    private var Statistics_Section: LinearLayout? = null
    private var isSectionVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_fertilizer_expenditure, container, false)

        recyclerViewExpenditures = view.findViewById<RecyclerView>(R.id.recyclerViewExpenditures)
        recyclerViewExpenditures!!.setLayoutManager(LinearLayoutManager(getActivity()))

        expenditureList = ArrayList<FertilizerExpenditure>()
        adapter = FertilizerExpenditureAdapter(expenditureList!!, requireContext())
        recyclerViewExpenditures!!.setAdapter(adapter)

        totalSpendingAmount = view.findViewById<TextView>(R.id.totalSpendingAmount)
        averageSpendingAmount = view.findViewById<TextView>(R.id.averageSpendingAmount)
        printButton = view.findViewById<ImageView>(R.id.printButton)
        Amount_Section = view.findViewById<LinearLayout>(R.id.Amount)
        Statistics_Section = view.findViewById<LinearLayout>(R.id.Statistics)


        Amount_Section!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (isSectionVisible) {
                    // Slide up animation
                    Statistics_Section!!.animate()
                        .translationY(Statistics_Section!!.getHeight().toFloat())
                        .setDuration(300)
                        .withEndAction(object : Runnable {
                            override fun run() {
                                Statistics_Section!!.setVisibility(View.GONE)
                            }
                        })
                    isSectionVisible = false
                } else {
                    // Make the section visible and slide down animation
                    Statistics_Section!!.setVisibility(View.VISIBLE)
                    Statistics_Section!!.setTranslationY(Statistics_Section!!.getHeight().toFloat())
                    Statistics_Section!!.animate()
                        .translationY(0f)
                        .setDuration(300)
                    isSectionVisible = true
                }
            }
        })

        printButton!!.setOnClickListener(View.OnClickListener { v: View? -> printExpenditures() })

        reloadData()

        return view
    }

    private fun reloadData() {
        expenditureList!!.clear()
        expenditureList!!.addAll(loadSavedData() as Collection<FertilizerExpenditure>)
        adapter!!.notifyDataSetChanged()
        updateTotalAndAverage()
    }

    private fun loadSavedData(): MutableList<FertilizerExpenditure?> {
        val sharedPreferences =
            requireActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE)
        val expenditureList: MutableList<FertilizerExpenditure?> =
            ArrayList<FertilizerExpenditure?>()

        val expenditureData: String = sharedPreferences.getString("expenditure_list", "[]")!!
        try {
            val jsonArray = JSONArray(expenditureData)
            for (i in 0..<jsonArray.length()) {
                val expenditure = fromJson(jsonArray.getJSONObject(i))
                expenditureList.add(expenditure)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return expenditureList
    }

    private fun updateTotalAndAverage() {
        var total = 0.0
        for (expenditure in expenditureList!!) {
            total += expenditure.amount!!.toDouble() // Assuming getAmount() returns the expenditure amount
        }

        totalSpendingAmount!!.setText(getString(R.string.currency_format, total))
        val average = if (expenditureList!!.size > 0) total / expenditureList!!.size else 0.0
        averageSpendingAmount!!.setText(getString(R.string.currency_format, average))
    }

    private fun printExpenditures() {
        val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager

        // Load your logo from resources
        val logoBitmap = BitmapFactory.decodeResource(
            getResources(),
            R.drawable.ic_shetibill_logo
        ) // Replace 'logo' with your logo resource name

        // Pass the bitmap to the adapter
        val adapter: PrintDocumentAdapter =
            FertilizerExpenditurePrintAdapter(expenditureList, logoBitmap)

        printManager.print(
            "Fertilizer Expenditure Summary",
            adapter,
            PrintAttributes.Builder().build()
        )
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }
}

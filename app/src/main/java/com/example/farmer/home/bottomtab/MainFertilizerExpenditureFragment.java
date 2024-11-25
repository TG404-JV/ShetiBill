package com.example.farmer.home.bottomtab;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.example.farmer.fertilizer.FertilizerExpenditure;
import com.example.farmer.fertilizer.FertilizerExpenditureAdapter;
import com.example.farmer.home.FertilizerExpenditurePrintAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class MainFertilizerExpenditureFragment extends Fragment {

    private RecyclerView recyclerViewExpenditures;
    private FertilizerExpenditureAdapter adapter;
    private List<FertilizerExpenditure> expenditureList;

    private TextView totalSpendingAmount;
    private TextView averageSpendingAmount;
    private ImageView printButton;
   private LinearLayout Amount_Section,Statistics_Section;
   private Boolean isSectionVisible=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_fertilizer_expenditure, container, false);

        recyclerViewExpenditures = view.findViewById(R.id.recyclerViewExpenditures);
        recyclerViewExpenditures.setLayoutManager(new LinearLayoutManager(getActivity()));

        expenditureList = new ArrayList<>();
        adapter = new FertilizerExpenditureAdapter(expenditureList, getContext());
        recyclerViewExpenditures.setAdapter(adapter);

        totalSpendingAmount = view.findViewById(R.id.totalSpendingAmount);
        averageSpendingAmount = view.findViewById(R.id.averageSpendingAmount);
        printButton = view.findViewById(R.id.printButton);
        Amount_Section=view.findViewById(R.id.Amount);
        Statistics_Section=view.findViewById(R.id.Statistics);


        Amount_Section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSectionVisible) {
                    // Slide up animation
                    Statistics_Section.animate()
                            .translationY(Statistics_Section.getHeight())
                            .setDuration(300)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    Statistics_Section.setVisibility(View.GONE);
                                }
                            });
                    isSectionVisible = false;
                } else {
                    // Make the section visible and slide down animation
                    Statistics_Section.setVisibility(View.VISIBLE);
                    Statistics_Section.setTranslationY(Statistics_Section.getHeight());
                    Statistics_Section.animate()
                            .translationY(0)
                            .setDuration(300);
                    isSectionVisible = true;
                }
            }
        });

        printButton.setOnClickListener(v -> printExpenditures());

        reloadData();

        return view;
    }

    private void reloadData() {
        expenditureList.clear();
        expenditureList.addAll(loadSavedData());
        adapter.notifyDataSetChanged();
        updateTotalAndAverage();
    }

    private List<FertilizerExpenditure> loadSavedData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE);
        List<FertilizerExpenditure> expenditureList = new ArrayList<>();

        String expenditureData = sharedPreferences.getString("expenditure_list", "[]");
        try {
            JSONArray jsonArray = new JSONArray(expenditureData);
            for (int i = 0; i < jsonArray.length(); i++) {
                FertilizerExpenditure expenditure = FertilizerExpenditure.fromJson(jsonArray.getJSONObject(i));
                expenditureList.add(expenditure);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return expenditureList;
    }

    private void updateTotalAndAverage() {
        double total = 0;
        for (FertilizerExpenditure expenditure : expenditureList) {
            total += Double.parseDouble(expenditure.getAmount()); // Assuming getAmount() returns the expenditure amount
        }

        totalSpendingAmount.setText(getString(R.string.currency_format, total));
        double average = expenditureList.size() > 0 ? total / expenditureList.size() : 0;
        averageSpendingAmount.setText(getString(R.string.currency_format, average));
    }

    private void printExpenditures() {
        PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);

        // Load your logo from resources
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_shetibill_logo); // Replace 'logo' with your logo resource name

        // Pass the bitmap to the adapter
        PrintDocumentAdapter adapter = new FertilizerExpenditurePrintAdapter(getContext(), expenditureList, logoBitmap);

        printManager.print("Fertilizer Expenditure Summary", adapter, new PrintAttributes.Builder().build());
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }
}

package com.example.farmer.home.bottomtab;

import android.app.DatePickerDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.example.farmer.home.bottomtab.labour.Labour;
import com.example.farmer.home.bottomtab.labour.LabourAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;

public class MainFarmExpenditureFragment extends Fragment {

    private FloatingActionButton addLabourFab;
    private RecyclerView labourRecyclerView;
    private LabourAdapter labourAdapter;
    private List<Labour> labourList, filteredLabourList;
    private EditText searchBar;
    private Spinner sortSpinner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final String PREFERENCE_KEY = "LabourData"; // SharedPreferences key
    private static final String LABOUR_LIST_KEY = "LabourList"; // Key for saving labour list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_farm_expenditure, container, false);

        // Initialize views
        addLabourFab = view.findViewById(R.id.addLabourBtn);
        labourRecyclerView = view.findViewById(R.id.AddWeightRecyclerView);
        searchBar = view.findViewById(R.id.searchBar);
        sortSpinner = view.findViewById(R.id.sortSpinner);

        // Load the labour list from SharedPreferences
        labourList = loadLabourData();
        filteredLabourList = new ArrayList<>(labourList); // Clone the list for filtering

        // Initialize adapter with the filtered labour list
        labourAdapter = new LabourAdapter(filteredLabourList, requireContext());
        labourRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        labourRecyclerView.setAdapter(labourAdapter);

        // Set up search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLabour(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up sort functionality
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortLabourList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set FAB click listener to open dialog for adding labour
        addLabourFab.setOnClickListener(v -> showAddLabourDialog());

        // Add item decoration to reduce spacing between items
        labourRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 4; // Adjust this value to your desired spacing
            }
        });

        return view;
    }

    // Filter labour list based on search query
    private void filterLabour(String query) {
        filteredLabourList.clear();
        if (query.isEmpty()) {
            filteredLabourList.addAll(labourList);
        } else {
            filteredLabourList.addAll(labourList.stream()
                    .filter(labour -> labour.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        labourAdapter.notifyDataSetChanged();
    }

    // Sort labour list based on selected sorting option
    private void sortLabourList(int sortOption) {
        filteredLabourList.clear();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        for (Labour labour : labourList) {
            try {
                Date labourDate = dateFormat.parse(labour.getDate());
                if (labourDate != null) {
                    switch (sortOption) {
                        case 0: // Daily
                            if (isSameDay(labourDate, currentDate)) {
                                filteredLabourList.add(labour);
                            }
                            break;
                        case 1: // Weekly
                            if (isWithinWeek(labourDate, currentDate)) {
                                filteredLabourList.add(labour);
                            }
                            break;
                        case 2: // Monthly
                            if (isWithinMonth(labourDate, currentDate)) {
                                filteredLabourList.add(labour);
                            }
                            break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        labourAdapter.notifyDataSetChanged();
    }

    // Helper methods to check date ranges
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isWithinWeek(Date date, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date weekAgo = calendar.getTime();
        return date.after(weekAgo) && date.before(currentDate);
    }

    private boolean isWithinMonth(Date date, Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1);
        Date monthAgo = calendar.getTime();
        return date.after(monthAgo) && date.before(currentDate);
    }

    // Method to show dialog to add labour name and date using custom buttons
    private void showAddLabourDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_labour, null);

        EditText labourNameEditText = dialogView.findViewById(R.id.labourNameEditText);
        TextView labourDateTextView = dialogView.findViewById(R.id.labourDateTextView);
        Button addButton = dialogView.findViewById(R.id.add_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        labourDateTextView.setOnClickListener(v -> showDatePickerDialog(labourDateTextView));

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        addButton.setOnClickListener(v -> {
            String labourName = labourNameEditText.getText().toString();
            String labourDate = labourDateTextView.getText().toString();

            if (!labourName.isEmpty() && !labourDate.isEmpty()) {
                Labour newLabour = new Labour(labourName, labourDate);
                labourList.add(newLabour);
                saveLabourData();
                filterLabour(searchBar.getText().toString()); // Re-filter based on search term
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter name and date", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Date picker dialog for selecting the date
    private void showDatePickerDialog(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateTextView.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Save labour list to SharedPreferences
    private void saveLabourData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(labourList);
        editor.putString(LABOUR_LIST_KEY, json);
        editor.apply();
    }

    // Load labour list from SharedPreferences
    private List<Labour> loadLabourData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LABOUR_LIST_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Labour>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }
}

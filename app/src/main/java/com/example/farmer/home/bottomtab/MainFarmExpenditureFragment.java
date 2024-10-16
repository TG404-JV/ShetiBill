package com.example.farmer.home.bottomtab;

import android.app.DatePickerDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.example.farmer.home.bottomtab.labour.Labour;
import com.example.farmer.home.bottomtab.labour.LabourAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainFarmExpenditureFragment extends Fragment {

    private FloatingActionButton addLabourFab;
    private RecyclerView labourRecyclerView;
    private LabourAdapter labourAdapter;
    private List<Labour> labourList;
    private String userId; // Add userId

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_farm_expenditure, container, false);

        // Initialize views
        addLabourFab = view.findViewById(R.id.addLabourBtn);
        labourRecyclerView = view.findViewById(R.id.AddWeightRecyclerView); // Ensure this matches your layout file

        // Initialize labour list
        labourList = new ArrayList<>();

        // Get the current user's ID from FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Retrieve the user ID
        } else {
            Toast.makeText(getContext(), "User not authenticated!", Toast.LENGTH_SHORT).show();
        }

        // Initialize adapter with context and userId
        if (userId != null) {
            labourAdapter = new LabourAdapter(labourList, requireContext(), userId);
        }

        labourRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        labourRecyclerView.setAdapter(labourAdapter);

        // Set FAB click listener to open dialog for adding labor
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

    // Method to show dialog to add labor name and date using custom buttons
    private void showAddLabourDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_labour, null);

        // Initialize UI components in the dialog
        EditText labourNameEditText = dialogView.findViewById(R.id.labourNameEditText);
        TextView labourDateTextView = dialogView.findViewById(R.id.labourDateTextView);
        Button addButton = dialogView.findViewById(R.id.add_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        // Set up the DatePicker for the date field
        labourDateTextView.setOnClickListener(v -> showDatePickerDialog(labourDateTextView));

        // Create the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)  // Disable cancelling by clicking outside the dialog
                .create();

        // Handle "Add" button click
        addButton.setOnClickListener(v -> {
            String labourName = labourNameEditText.getText().toString();
            String labourDate = labourDateTextView.getText().toString();

            if (!labourName.isEmpty() && !labourDate.isEmpty()) {
                // Create new Labour object and add to the list
                Labour newLabour = new Labour(labourName, labourDate);
                labourList.add(newLabour);
                labourAdapter.notifyDataSetChanged();

                // Dismiss the dialog after adding the labour
                dialog.dismiss();
            } else {
                // Show an error message if fields are empty
                Toast.makeText(getContext(), "Please enter name and date", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Cancel" button click
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    // Date picker dialog for selecting date
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
}

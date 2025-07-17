package com.example.trackmymoney.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.trackmymoney.Adapters.CategoryAdapter;
import com.example.trackmymoney.CategoryItem;
import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.Model.SharedViewModel;
import com.example.trackmymoney.R;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;

/**
 * AddFragment allows users to add a new expense entry.
 * Users can select category, amount, and date, and save data to Room DB.
 */
public class AddFragment extends Fragment {

    // Input field for displaying selected date
    EditText dateinput;

    /**
     * Inflates the fragment's layout and sets up the category spinner
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Spinner to display categories with icons
        Spinner spinner = view.findViewById(R.id.spinner);

        // Category list for spinner
        List<CategoryItem> categoryList = new ArrayList<>();
        categoryList.add(new CategoryItem("Select Category", R.drawable.baseline_keyboard_arrow_down_24));
        categoryList.add(new CategoryItem("Food", R.drawable.utensils_solid));
        categoryList.add(new CategoryItem("Travel", R.drawable.plane_solid));
        categoryList.add(new CategoryItem("Shopping", R.drawable.bag_shopping_solid));
        categoryList.add(new CategoryItem("other", R.drawable.plus_solid));

        // Adapter to bind category list to spinner
        CategoryAdapter adapter = new CategoryAdapter(requireContext(), categoryList);
        spinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Handles back press within the fragment by popping the back stack
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle back press specifically for this fragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    /**
     * Handles data saving and event listeners when the fragment resumes
     */
    @Override
    public void onResume() {
        super.onResume();
        Button add = null;

        // ViewModel used for sharing data between fragments/activities
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // SharedPreferences for balance and user data
        SharedPreferences cache = requireContext().getSharedPreferences("Balances", Context.MODE_PRIVATE);
        SharedPreferences metadata = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        metadata.getString("email", "empty"); // Access user email (not assigned)

        try {
            add = getActivity().findViewById(R.id.addbutton);
            Appdatabase db = DatabaseProvider.getDatabase(getContext());
            expenseDetailsDao expenses;

            // Get DAO from database (throws error if null)
            try {
                assert db != null;
                expenses = db.expensesdao();
            } catch (AssertionError e) {
                Log.e("debug addfragment", "error at line 72");
                throw new RuntimeException("error");
            }

            // Access UI elements
            EditText amount = getActivity().findViewById(R.id.amouninput);
            Spinner accesscat = getActivity().findViewById(R.id.spinner);
            Button date = getActivity().findViewById(R.id.dateselector);
            dateinput = getActivity().findViewById(R.id.dateinput);

            // Disable manual input on date field
            dateinput.setKeyListener(null);
            dateinput.setCursorVisible(false);

            // Open calendar on clicking date selector button
            date.setOnClickListener(v -> opendialog());

            // Also open calendar when clicking the date text field
            dateinput.setOnClickListener(v -> opendialog());

            // Save expense when 'Add' button is clicked
            add.setOnClickListener(v -> {
                long longamount;
                String strcategory, strdate, email;
                CategoryItem categoryitem = (CategoryItem) accesscat.getSelectedItem();

                // Validate all inputs
                if (!amount.getText().equals("") && !dateinput.getText().toString().equals("") && !categoryitem.getCategoryName().toString().equals("")) {
                    longamount = Long.parseLong(amount.getText().toString());
                    strcategory = categoryitem.getCategoryName();
                    strdate = dateinput.getText().toString();
                    email = metadata.getString("email", "empty");

                    // Save the expense to the database
                    expenses.add(longamount, strcategory, strdate, email);

                    // Extract month from selected date
                    int startindex = strdate.indexOf('/');
                    int endindex = strdate.indexOf("/", startindex + 1);
                    int month = Integer.parseInt(strdate.substring(startindex + 1, endindex));
                    int currentmonth = LocalDate.now().getMonthValue();

                    // Only update balance if selected month is current month
                    if (currentmonth == month) {
                        if (cache != null) {
                            // Subtract expense from current balance
                            cache.edit().putLong("currentbalance", cache.getLong("currentbalance", 0) - longamount).commit();
                            viewModel.setDecrement(longamount);
                            Toast.makeText(getContext(), "saved successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("FATAL exception", "error at line 129 file AddFragment");
                            expenses.Deleteexpense(strcategory, longamount, strdate, email);
                        }
                    } else {
                        Toast.makeText(requireContext(), "this month is not current month", Toast.LENGTH_LONG).show();
                    }

                    // Set current month in ViewModel
                    viewModel.setthismonth(month);

                } else {
                    Toast.makeText(requireContext(), "please put input in the inputbox", Toast.LENGTH_LONG).show();
                }
            });

        } catch (NullPointerException e) {
            Log.e("tag", "Error in accessing elements in add fragment");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens a DatePickerDialog with custom theme and sets selected date to input field
     */
    private void opendialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show date picker dialog with custom theme
        DatePickerDialog dialog = new DatePickerDialog(
                this.requireContext(),
                R.style.Dialogtheme,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateinput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day
        );

        // Customize button colors of dialog
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            int color = ContextCompat.getColor(requireContext(), R.color.colorPrimary);

            if (positiveButton != null) positiveButton.setTextColor(color);
            if (negativeButton != null) negativeButton.setTextColor(color);
        });

        dialog.show(); // Show the dialog
    }
}

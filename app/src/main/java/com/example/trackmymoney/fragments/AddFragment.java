package com.example.trackmymoney.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Also import your adapter and model if needed
import com.example.trackmymoney.Adapters.CategoryAdapter;
import com.example.trackmymoney.CategoryItem;
import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.Model.SharedViewModel;
import com.example.trackmymoney.R;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;


//Add fragment basically save new expense page
//This class consist of java logic for saving new expense commited by user
public class AddFragment extends Fragment {

    EditText dateinput ;

   public View onCreateView(LayoutInflater inflater , ViewGroup container,Bundle savedInstanceState){
       View view = inflater.inflate(R.layout.fragment_add,container,false);

       Spinner spinner = view.findViewById(R.id.spinner);

       List<CategoryItem> categoryList = new ArrayList<>();
       categoryList.add(new CategoryItem("Select Category", R.drawable.baseline_keyboard_arrow_down_24));
       categoryList.add(new CategoryItem("Food", R.drawable.utensils_solid));
       categoryList.add(new CategoryItem("Travel", R.drawable.plane_solid));
       categoryList.add(new CategoryItem("Shopping", R.drawable.bag_shopping_solid));
       categoryList.add(new CategoryItem("other",R.drawable.plus_solid));

       CategoryAdapter adapter = new CategoryAdapter(requireContext(), categoryList);
       spinner.setAdapter(adapter);

       return view;
   }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }



    //handling database operations here also saving the data in database
    @Override
    public void onResume() {
        super.onResume();
        Button add = null ;
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class); // or any long value
        SharedPreferences cache = requireContext().getSharedPreferences("Balances", Context.MODE_PRIVATE);
        SharedPreferences metadata = requireContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE) ;
        metadata.getString("email" , "empty");
        //loading database
        try {
            add = getActivity().findViewById(R.id.addbutton);
            Appdatabase db = DatabaseProvider.getDatabase(getContext()) ;
            expenseDetailsDao expenses ;

            //assert db is not null if assertion fails will get error on this line
            try {
                assert db != null ;
                expenses = db.expensesdao() ;
            } catch (AssertionError e) {
                Log.e("debug addfragment" , "error at line 72") ;
                throw new RuntimeException("error");
            }

            //Accessing buttons as java objects
            EditText amount = getActivity().findViewById(R.id.amouninput) ;
            Spinner accesscat = getActivity().findViewById(R.id.spinner) ;
            Button date = getActivity().findViewById(R.id.dateselector) ;
            dateinput = getActivity().findViewById(R.id.dateinput) ;
            dateinput.setKeyListener(null);
            dateinput.setCursorVisible(false);


            //listeners they will work only when user performs an action
            date.setOnClickListener(v -> {
                 //call function to get calender view
                opendialog();
            });

            //listener to call open calendar view
            dateinput.setOnClickListener(v -> {
                 opendialog() ;
            });

            //when user click save button all the info is saved inside the database
            //The below clicklistener does that
            add.setOnClickListener(v -> {
                long longamount;
                String strcategory, strdate , email;
                CategoryItem categoryitem = (CategoryItem) accesscat.getSelectedItem();
                if (!amount.getText().equals("") && !dateinput.getText().toString().equals("") && !categoryitem.getCategoryName().toString().equals("")) {
                    longamount = Long.parseLong(amount.getText().toString());
                    strcategory = categoryitem.getCategoryName();
                    strdate = dateinput.getText().toString();
                    email = metadata.getString("email" , "empty") ;
                    expenses.add(longamount, strcategory, strdate , email);
                    int startindex = dateinput.getText().toString().indexOf('/');
                    int endindex = strdate.indexOf("/", startindex + 1);
                    int month = Integer.parseInt(strdate.substring(startindex + 1, endindex));
                    int currentmonth = LocalDate.now().getMonthValue();

                    if (currentmonth == month) {
                        if (cache != null) {
                            cache.edit().putLong("currentbalance", cache.getLong("currentbalance", 0) - longamount).commit();
                            viewModel.setDecrement(longamount);// or any long value
                            Toast.makeText(getContext(), "saved successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("FATAL exception", "error at line 129 file AddFragment");
                            expenses.Deleteexpense(strcategory, longamount, strdate,email);
                        }
                    }else {
                        Toast.makeText(requireContext() , "this month is not current month" ,Toast.LENGTH_LONG).show() ;
                    }
                    viewModel.setthismonth(month);
                }else {
                    Toast.makeText(requireContext() , "please put input in the inputbox" , Toast.LENGTH_LONG).show() ;
                }

            }) ;
        }catch(NullPointerException e) {
            Log.e("tag" , "Error in accessing elements in add fragment") ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    //Method to invoke dialog for selecting date
    private void opendialog() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this.requireContext(),R.style.Dialogtheme , new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                 dateinput.setText((String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year)));
            }
        } , year , month , day ) ;
        dialog.show();

    }
}


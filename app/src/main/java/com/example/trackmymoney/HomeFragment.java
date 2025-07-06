package com.example.trackmymoney;

import static android.media.MediaCodec.MetricsConstants.MODE;
import static android.os.ParcelFileDescriptor.MODE_WORLD_WRITEABLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.trackmymoney.Adapters.ScrollAdapter;
import com.example.trackmymoney.Dao.expensesDao;
import com.example.trackmymoney.Model.expenses;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;

import java.time.LocalDate;
import java.util.List;





//Home Page Logic
//This page will show recent spends also the how much user spend from user balance


public class HomeFragment extends Fragment {

    private Appdatabase database;
    private List<expenses> expenslist;
    private expensesDao dataaccessobj;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    //on View created
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Database using databaseProvider
        try {
            database = DatabaseProvider.getDatabase(this.getContext());
        } catch (Exception e) {
            throw new RuntimeException("error in databaseprovider");
        }

        //initializing dataaccess object using database
        if (database != null) {
            dataaccessobj = database.expensesdao();
            // expenslist = dataaccessobj.getallexpenses();
        } else {
            dataaccessobj = null;
        }

        //wave view
        WaveView waveview = view.findViewById(R.id.waveview);
        //we have to change limit dynamically
        SharedPreferences getlimit = requireContext().getSharedPreferences("states and limits", Context.MODE_PRIVATE);

        long limit = 1000000L;
        long sum = dataaccessobj.getSum();
        float percent = ((float) sum / limit) * 100;
        percent = percent / 100;
        waveview.setPercentage(percent);

        //populating spinner with choices
        Spinner myspinner = view.findViewById(R.id.changeexpenseviewbutton);
        String[] items = {"Today", "last 5 expenses"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        myspinner.setAdapter(adapter);


        String item = myspinner.getSelectedItem().toString();

        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonth().getValue();
        int day = date.getDayOfMonth();
        String today = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        if (item.equals("Today")) {
            expenslist = dataaccessobj.getExpensesByDate(today);
        }

        myspinner.setClickable(true);

        //spinner selection
        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // get selected item
                String item = parent.getItemAtPosition(position).toString();
                //if items is las 5 expenses then show last five expenses
                if (item.equals("last 5 expenses")) {
                    assert dataaccessobj != null;
                    expenslist = dataaccessobj.getLastFiveExpenses();
                    // Update your UI or adapter here
                }
                // Handle other selections if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle case when nothing is selected
            }
        });

        LinearLayout containerLayout = getActivity().findViewById(R.id.scrollcontainer);
        ScrollAdapter scrolladapter = new ScrollAdapter(this.getContext(), expenslist);
        scrolladapter.populateInto(containerLayout);


    }


    //on resume
    @Override
    public void onResume() {
        super.onResume();

        Spinner myspinner = getActivity().findViewById(R.id.changeexpenseviewbutton);
        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // get selected item
                String item = parent.getItemAtPosition(position).toString();
                //if items is las 5 expenses then show last five expenses
                if (item.equals("last 5 expenses")) {
                    assert dataaccessobj != null;
                    expenslist = dataaccessobj.getLastFiveExpenses();
                    LinearLayout containerLayout = getActivity().findViewById(R.id.scrollcontainer);
                    ScrollAdapter scrolladapter = new ScrollAdapter(getContext(), expenslist);
                    scrolladapter.populateInto(containerLayout);
                    // Update your UI or adapter here
                }
                // Handle other selections if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        //set button
        //take motion events as arguments
        //set threshold distance and threshold velocity if both of the threshold breaks then only do action
        //check difference between final x and starting x co-ordinate
        //check difference between final y and starting y co-ordinate
        //check if differnce y is greater than difference x if yes then perform action
        //if difference is positive then swipe is up
        //if difference is negative then swipe is down


    }
}


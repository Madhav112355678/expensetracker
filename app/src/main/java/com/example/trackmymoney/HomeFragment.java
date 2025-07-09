package com.example.trackmymoney;

import static android.media.MediaCodec.MetricsConstants.MODE;
import static android.os.ParcelFileDescriptor.MODE_WORLD_WRITEABLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackmymoney.Adapters.ScrollAdapter;
import com.example.trackmymoney.Dao.expensesDao;
import com.example.trackmymoney.Model.SharedViewModel;
import com.example.trackmymoney.Model.expenses;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;





//Home Page Logic
//This page will show recent spends also the how much user spend from user balance


public class HomeFragment extends Fragment {

    private Appdatabase database;
    private List<expenses> expenslist;
    private expensesDao dataaccessobj;
    private SharedPreferences balance ;

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
        SharedPreferences getlimit = requireContext().getSharedPreferences("Balances", Context.MODE_PRIVATE);
        TextView percentview = getActivity().findViewById(R.id.percentviewer) ;
        long limit = getlimit.getLong("balance" , 0) ;
        long sum = dataaccessobj.getSum();
        float percents = ((float) getSumOfMonth() / limit) * 100;
        float percent = percents / 100;
        waveview.setPercentage(percent);
        if(percent > 1) {
            waveview.setPercentage(0.98f);
            percentview.setText("Overspended");
        }else{
            percentview.setText(String.valueOf((int)percents) + "%");
        }

        //populating spinner with choices
        Spinner myspinner = view.findViewById(R.id.changeexpenseviewbutton);
        String[] items = {"Today", "last 5 expenses" , "last 5 days"};
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

        //get sharepreferences
        balance = requireContext().getSharedPreferences("Balances", Context.MODE_PRIVATE);
        long monthlybalance = balance.getLong("balance" , 0l) ;
        long monthlybalanceimmutable = balance.getLong("incrementalbalance" , 0l) ;
// ##efficient coding
        TextView clicktoswitch = getActivity().findViewById(R.id.quote) ;
        ConstraintLayout hideforsometime = getActivity().findViewById(R.id.incrementdecrementviewer) ;
        TextView quote = getActivity().findViewById(R.id.quote);
        TextView showincrement = getActivity().findViewById(R.id.showincrement) ;
        TextView showdecrement = getActivity().findViewById(R.id.showdecrement) ;
        TextView balanceviewer = getActivity().findViewById(R.id.balanceshower) ;
        ConstraintLayout balancemanipulator = getActivity().findViewById(R.id.balancemanuipulator) ;
        Button add = getActivity().findViewById(R.id.addbalance) ;
        Button reset = getActivity().findViewById(R.id.resetbalance) ;
        balanceviewer.setText( String.valueOf(monthlybalance));
        TextView percentview = getActivity().findViewById(R.id.percentviewer) ;
        EditText putb = getActivity().findViewById(R.id.putbalance) ;

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getDecrement().observe(getViewLifecycleOwner(), value -> {
            showdecrement.setText("-" + String.valueOf(value));
        });


        //make show button visible and clickhandler invisible
        clicktoswitch.setOnClickListener(v -> {

            if(balancemanipulator.getVisibility() == View.GONE) {
                hideforsometime.setVisibility(View.GONE);
                quote.setVisibility(View.GONE);
                getActivity().findViewById(R.id.putbalance).setVisibility(View.VISIBLE);
                balancemanipulator.setVisibility(View.VISIBLE);
                showincrement.setVisibility(View.GONE);
                showdecrement.setVisibility(View.GONE);
                balanceviewer.setVisibility(View.GONE);
            }
        });


        //show put balance
        add.setOnClickListener(v -> {
                EditText put = getActivity().findViewById(R.id.putbalance);
                if (put.getText() != null && !put.getText().toString().equals("")) {
                    long data = Long.parseLong(put.getText().toString());
                    balance.edit().putLong("balance", (balance.getLong("balance", 0) + data)).commit();
                    if (balance.getLong("incrementalbalance", 0l) == 0l) {
                        balance.edit().putLong("incrementalbalance", data);
                    }
                    put.setVisibility(View.GONE);
                    balanceviewer.setVisibility(View.VISIBLE);
                    balanceviewer.setText(String.valueOf(balance.getLong("balance", 0)));
                    quote.setVisibility(View.VISIBLE);
                    balancemanipulator.setVisibility(View.GONE);
                    hideforsometime.setVisibility(View.VISIBLE);
                    showincrement.setText("+" + String.valueOf(data));
                    showincrement.setVisibility(View.VISIBLE);
                    showdecrement.setVisibility(View.VISIBLE);
                    long sum = dataaccessobj.getSum();
                    float percents = ((float) getSumOfMonth() / balance.getLong("balance", 0)) * 100;
                    float percent = percents / 100;
                    ((WaveView) getActivity().findViewById(R.id.waveview)).setPercentage(percent);
                    if (percent > 1) {
                        percentview.setText("Overspended");
                    } else {
                        percentview.setText(String.valueOf(percents) + "%");
                    }
                } else {
                    Toast.makeText(getContext(), "Input is Empty", Toast.LENGTH_LONG).show();
                    ;
                }
        });



        reset.setOnClickListener(v -> {

            if (putb.getText() != null && !putb.getText().toString().equals("")) {
                EditText put = getActivity().findViewById(R.id.putbalance);
                long data = Long.parseLong(put.getText().toString());
                balance.edit().putLong("balance", data).commit();
                put.setVisibility(View.GONE);
                if (dataaccessobj.getExpensesByMonth(String.valueOf(LocalDate.now().getMonthValue())) != null) {
                    balanceviewer.setText(String.valueOf(balance.getLong("balance", 0) - getSumOfMonth()));
                } else {
                    balanceviewer.setText(String.valueOf(balance.getLong("balance", 0)));
                }
                balance.edit().putLong("balance", data);
                quote.setVisibility(View.VISIBLE);
                balancemanipulator.setVisibility(View.GONE);
                hideforsometime.setVisibility(View.VISIBLE);
                showincrement.setText("+" + String.valueOf(data));
                showincrement.setVisibility(View.VISIBLE);
                showdecrement.setVisibility(View.VISIBLE);
                balanceviewer.setVisibility(View.VISIBLE);
                long sum = dataaccessobj.getSum();
                float percents = ((float) getSumOfMonth() / balance.getLong("balance", 0)) * 100;
                float percent = percents / 100;
                ((WaveView) getActivity().findViewById(R.id.waveview)).setPercentage(percent);
                if (percent > 1) {
                    percentview.setText("Overspended");
                } else {
                    percentview.setText(String.valueOf(percents) + "%");
                }
            }else  {
                 Toast.makeText(getContext() , "please put the value" , Toast.LENGTH_LONG).show();
                }
        });


        Spinner myspinner = getActivity().findViewById(R.id.changeexpenseviewbutton);

        //logic for item selection of myspinner
        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // get selected item
                String item = parent.getItemAtPosition(position).toString();
                //if items is last 5 expenses then show last five expenses
                if (item.equals("last 5 expenses")) {
                    assert dataaccessobj != null;
                    expenslist = dataaccessobj.getLastFiveExpenses();
                    // Update your UI or adapter here
                } else if (item.equals("last 5 days")) {
                    LocalDate today = LocalDate.now();
                    int day = today.getDayOfMonth();
                    List<List<expenses>> list = new ArrayList<>() ;
                    if (day >= 5) {
                        for (int i = day; i >= day - 5; i--) {
                            String str =  String.valueOf(i) + "/" + String.valueOf(today.getMonth().getValue()) + "/" + String.valueOf(today.getYear()) ;
                            list.add(dataaccessobj.getExpensesByDate(str)) ;
                        }
                    } else {
                        for (int i = 1; i <= day; i++) {
                           String str = String.valueOf(i) + "/" + String.valueOf(today.getMonth().getValue()) + "/" + String.valueOf(today.getYear()) ;
                           list.add(dataaccessobj.getExpensesByDate(str)) ;
                        }
                    }
                    expenslist.clear() ;
                    for(int i = 0 ; i < list.size() ; i++) {
                       for(int j = 0 ; j < list.get(i).size() ; j++) {
                            expenslist.add(list.get(i).get(j)) ;
                       }
                    }
                }
                LinearLayout containerLayout = getActivity().findViewById(R.id.scrollcontainer);
                ScrollAdapter scrolladapter = new ScrollAdapter(getContext(), expenslist);
                scrolladapter.populateInto(containerLayout);
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

        LinearLayout containerLayout = getActivity().findViewById(R.id.scrollcontainer);
        ScrollAdapter scrolladapter = new ScrollAdapter(this.getContext(), expenslist);
        scrolladapter.populateInto(containerLayout);


    }

public long getSumOfMonth() {
        LocalDate local = LocalDate.now() ;
        int year = local.getYear() ;
        int day = local.getDayOfMonth() ;
        int month = local.getMonthValue() ;
        String parsedstring = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year) ;
        long sum = 0L ;
        for(int i = 1 ; i <= 28 ; i++) {
            parsedstring = String.valueOf(i) + "/" + String.valueOf(month) + "/" + String.valueOf(year) ;
            if(i == day) {
                sum += dataaccessobj.getSumPerDay(parsedstring) ;
               break ;
            }
            sum += dataaccessobj.getSumPerDay(parsedstring) ;
        }
        return sum ;
    }

    public void setbalance(Context currentcontext) {

    }

}


package com.example.trackmymoney.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackmymoney.Adapters.ScrollAdapter;
import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.Model.SharedViewModel;
import com.example.trackmymoney.Model.expensedetails;
import com.example.trackmymoney.R;
import com.example.trackmymoney.WaveView;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


//Home Page Logic
//This page will show recent spends also the how much user spend from user balance


public class HomeFragment extends Fragment {

    //all class memebers data
    private Appdatabase database;
    private List<expensedetails> expenslist;
    private expenseDetailsDao dataaccessobj;
    private SharedPreferences balance;
    private SharedPreferences shared ;
    private boolean isSuccessful;
    private String email ;


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
            dataaccessobj = database.expensesdao() ;
            // expenslist = dataaccessobj.getallexpenses();
        } else {
            dataaccessobj = null;
        }

        SharedPreferences shard = requireActivity().getSharedPreferences("Balances", MODE_PRIVATE);
        shared = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        email = shared.getString("email" , "") ;
      //  shard.edit().putLong("limit", 0L).commit();
      //  shard.edit().putLong("currentbalance", 0L).commit();

        //populating spinner with choices
        Spinner myspinner = view.findViewById(R.id.changeexpenseviewbutton);
        String[] items = {"Today", "last 5 expenses", "last 5 days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        myspinner.setAdapter(adapter);


        String item = myspinner.getSelectedItem().toString();

        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonth().getValue();
        int day = date.getDayOfMonth();
        String today = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        if (item.equals("Today")) {
            expenslist = dataaccessobj.getExpensesByDate(today , email);
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
                    expenslist = dataaccessobj.getLastFiveExpenses(email);
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
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        balance = requireContext().getSharedPreferences("Balances", MODE_PRIVATE);
        TextView clicktoswitch = getActivity().findViewById(R.id.quote);
        ConstraintLayout hideforsometime = getActivity().findViewById(R.id.incrementdecrementviewer);
        TextView quote = getActivity().findViewById(R.id.quote);
        TextView showincrement = getActivity().findViewById(R.id.showincrement);
        TextView showdecrement = getActivity().findViewById(R.id.showdecrement);
        TextView balanceviewer = getActivity().findViewById(R.id.balanceshower);
        ConstraintLayout balancemanipulator = getActivity().findViewById(R.id.balancemanuipulator);
        Button add = getActivity().findViewById(R.id.addbalance);
        Button reset = getActivity().findViewById(R.id.resetbalance);
        EditText putbalance = getActivity().findViewById(R.id.putbalance);
        WaveView wave = getActivity().findViewById(R.id.waveview);
        FrameLayout wavecontainer = getActivity().findViewById(R.id.wave1) ;
        TextView showlimit = getActivity().findViewById(R.id.limitview) ;
        ConstraintLayout limitmanipulator = getActivity().findViewById(R.id.limithandler) ;
        Button addlimit = limitmanipulator.findViewById(R.id.addlimit) ;
        Button setlimit = limitmanipulator.findViewById(R.id.resetlimit) ;
        EditText putlimit = limitmanipulator.findViewById(R.id.putlimit) ;
        showlimit.setClickable(true);
        //set Balance
        //Whenever
        isSuccessful = setWave(getContext());
        AtomicInteger val = new AtomicInteger();
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getthismonth().observe(getViewLifecycleOwner() ,  value -> {
              val.set(value);

        });
        if(val.get() == LocalDate.now().getMonthValue() || val.get() == 0) {
            viewModel.getDecrement().observe(getViewLifecycleOwner(), value -> {
                showdecrement.setText(String.valueOf(value));
            });
            balanceviewer.setText(String.valueOf("₹" + balance.getLong("currentbalance", 0)));
            showlimit.setHint(String.valueOf("₹" + balance.getLong("limit", 0l)));
        }


        showlimit.setOnClickListener(v -> {
             if(limitmanipulator.getVisibility() == View.GONE) {
                   wavecontainer.setVisibility(View.GONE);
                   limitmanipulator.setVisibility(View.VISIBLE);
             }
        }) ;


        addlimit.setOnClickListener(v -> {
            if(!putlimit.getText().toString().equals("")) {
                long data = Long.parseLong(putlimit.getText().toString()) ;
                balance.edit().putLong("limit", balance.getLong("limit", 0L) + data).commit();
                showlimit.setText(String.valueOf(balance.getLong("limit" , 0l)));
            }else {
                Snackbar.make(getActivity().findViewById(R.id.scrollcontainer) , "please put limit as input" , Toast.LENGTH_LONG).show() ;
                return ;
            }
            limitmanipulator.setVisibility(View.GONE);
            wavecontainer.setVisibility(View.VISIBLE);
            setWave(requireContext()) ;
        });


        setlimit.setOnClickListener(v -> {
             if(!putlimit.getText().toString().equals("")) {
                  long data = Long.parseLong(putlimit.getText().toString()) ;
                  balance.edit().putLong("limit" ,data).commit() ;
                 showlimit.setText(String.valueOf(balance.getLong("limit" , 0l)));
             }else {
                  Snackbar.make(getActivity().findViewById(R.id.scrollcontainer) , "please put limit as input" , Toast.LENGTH_LONG).show() ;
                  return ;
             }
             limitmanipulator.setVisibility(View.GONE);
             wavecontainer.setVisibility(View.VISIBLE);
             setWave(requireContext()) ;
        });

        //make show button visible and clickhandler invisible
        clicktoswitch.setOnClickListener(v -> {
            if (balancemanipulator.getVisibility() == View.GONE) {
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
            long data = 0l;
            if (!putbalance.getText().toString().equals("") && Long.parseLong(putbalance.getText().toString()) > 0) {
                data = Long.parseLong(putbalance.getText().toString());
                putbalance.setVisibility(View.GONE);
            } else {
                return;
            }
            balanceviewer.setVisibility(View.VISIBLE);
            putbalance.setVisibility(View.GONE);
            balance.edit().putLong("currentbalance", balance.getLong("currentbalance", 0l) + data).commit();
            // update limit
         //  balance.edit().putLong("limit", balance.getLong("limit", 0L) + data).commit();
            balanceviewer.setText( "₹" + String.valueOf(balance.getLong("currentbalance", 0)));
            quote.setVisibility(View.VISIBLE);
            balancemanipulator.setVisibility(View.GONE);
            hideforsometime.setVisibility(View.VISIBLE);
            showincrement.setText("+" + String.valueOf(data));
            showincrement.setVisibility(View.VISIBLE);
            showdecrement.setVisibility(View.VISIBLE);
            setWave(getContext());
        });


        reset.setOnClickListener(v -> {

            long data = 0l;
            if (!putbalance.getText().toString().equals("") && Long.parseLong(putbalance.getText().toString()) > 0) {
                data = Long.parseLong(putbalance.getText().toString());
                balance.edit().putLong("currentbalance", data).commit();
                putbalance.setVisibility(View.GONE);
            } else {
                return;
            }
          //  balance.edit().putLong("limit", data).commit();
            balanceviewer.setText(String.valueOf(  "₹" + balance.getLong("currentbalance", 0l)));
            quote.setVisibility(View.VISIBLE);
            balancemanipulator.setVisibility(View.GONE);
            hideforsometime.setVisibility(View.VISIBLE);
            showincrement.setText("+" + String.valueOf(data));
            showincrement.setVisibility(View.VISIBLE);
            showdecrement.setVisibility(View.VISIBLE);
            balanceviewer.setVisibility(View.VISIBLE);
            long sum = dataaccessobj.getSum(email);
            setWave(getContext());
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
                    expenslist = dataaccessobj.getLastFiveExpenses(email);
                    // Update your UI or adapter here
                } else if (item.equals("last 5 days")) {
                    LocalDate today = LocalDate.now();
                    int day = today.getDayOfMonth();
                    List<List<expensedetails>> list = new ArrayList<>();
                    if (day >= 5) {
                        for (int i = day; i >= day - 5; i--) {
                            String str = String.valueOf(i) + "/" + String.valueOf(today.getMonth().getValue()) + "/" + String.valueOf(today.getYear());
                            list.add(dataaccessobj.getExpensesByDate(str ,email ));
                        }
                    } else {
                        for (int i = 1; i <= day; i++) {
                            String str = String.valueOf(i) + "/" + String.valueOf(today.getMonth().getValue()) + "/" + String.valueOf(today.getYear());
                            list.add(dataaccessobj.getExpensesByDate(str , email));
                        }
                    }
                    expenslist.clear();
                    for (int i = 0; i < list.size(); i++) {
                        for (int j = 0; j < list.get(i).size(); j++) {
                            expenslist.add(list.get(i).get(j));
                        }
                    }
                }else {
                    expenslist = dataaccessobj.getExpensesByDate(String.valueOf(LocalDate.now().getDayOfMonth()) + "/" + String.valueOf(LocalDate.now().getMonthValue()) + "/" + String.valueOf(LocalDate.now().getYear()) , email) ;
                }


                balanceviewer.setText(String.valueOf("₹" + balance.getLong("currentbalance", 0)));
                showlimit.setHint(String.valueOf("₹" + balance.getLong("limit", 0l)));
                LinearLayout containerLayout = getActivity().findViewById(R.id.scrollcontainer);
                ScrollAdapter scrolladapter = new ScrollAdapter(getContext(), expenslist);
                scrolladapter.populateInto(containerLayout);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                List<expensedetails> exp = dataaccessobj.getExpensesByDate(LocalDate.now().toString() , email);
                expenslist = exp;
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
        LinearLayout linearLayout = getActivity().findViewById(R.id.scrollcontainer);
        View expenseListView = getActivity().findViewById(R.id.expenselist);
        expenseListView.setClickable(true);

       /* expenseListView.setOnTouchListener((v, event) -> {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                ConstraintLayout view = (ConstraintLayout) linearLayout.getChildAt(i);
                view.setOnTouchListener(new SwipeToDeleteTouchListener(getContext(), view, linearLayout , email));
            }
            return true;
        });

        */



        LinearLayout containerLayout = getActivity().findViewById(R.id.scrollcontainer);
        ScrollAdapter scrolladapter = new ScrollAdapter(this.getContext(), expenslist);
        scrolladapter.populateInto(containerLayout);
    }



    //set wave on different situations but logic should be same and robust
    @SuppressLint("SetTextI18n")
    public boolean setWave(Context context) {
            long current = balance.getLong("currentbalance", 0L);
            long limit = balance.getLong("limit", 0L);
            if (limit > 0) {
            WaveView wave = this.getActivity().findViewById(R.id.waveview);
            float percentage = ((float)getSumOfMonth() / (float)limit) * 100;
            float fitted = percentage / 100;
            TextView percentviewer = this.getActivity().findViewById(R.id.percentviewer) ;
            if (fitted > 1) {
                wave.setPercentage(0.98f);
                percentviewer.setText("Overspended");
                return false;
            } else {
                wave.setPercentage(fitted);
                percentviewer.setText((int) percentage + "%") ;
            }
        } else {
            Toast.makeText(getContext(), "please initialize data", Toast.LENGTH_LONG).show();
            return false ;
        }
        return true ;
    }



public long getSumOfMonth() {
    LocalDate local = LocalDate.now();
    int year = local.getYear();
    int day = local.getDayOfMonth();
    int month = local.getMonth().getValue();
    String parsedstring = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
    long sum = 0L;
    for (int i = 1; i <= 28; i++) {
        parsedstring = String.valueOf(i) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        if (i == day) {
            sum += dataaccessobj.getSumPerDay(parsedstring , email);
            break;
        }
        sum += dataaccessobj.getSumPerDay(parsedstring , email);
    }
    return sum;
}
}


class SwipeToDeleteTouchListener implements View.OnTouchListener {
    //just a random class
    private GestureDetector gestureDetector;
    private View targetView;
    private LinearLayout parentLayout;
    private Context context;
    private String email ;

    public SwipeToDeleteTouchListener(Context context, View targetView, LinearLayout parentLayout , String email) {
        this.context = context;
        this.targetView = targetView;
        this.parentLayout = parentLayout;
        this.email = email ;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 50;
            private static final int SWIPE_VELOCITY_THRESHOLD = 60;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        String selecteddate = ((TextView) targetView.findViewById(R.id.dateview)).getText().toString();
                        long selectedamount = Long.parseLong(((TextView) targetView.findViewById(R.id.amountview)).getText().toString().substring(1));
                        String seletedcategory = ((TextView) targetView.findViewById(R.id.categoryview)).getText().toString();
                        if (onSwipeRight(seletedcategory, selectedamount, selecteddate, context, email)) {
                            Log.d("DeleteDebug", "Deleting: " + selecteddate + ", " + selectedamount + ", " + seletedcategory + ", " + email);
                            parentLayout.removeView(targetView);
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("debug", "could not delete item");
                            return false ;
                        }
                        return true;
                    }
                }
                return false ;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }


    public boolean onSwipeRight(String date , long amount , String category , Context context , String email) {
        Appdatabase database = null ;
            try {
                database = DatabaseProvider.getDatabase(context) ;
            } catch (Exception e) {
                throw new RuntimeException("error in databaseprovider");
            }

            try {
                database.expensesdao().Deleteexpense(category, amount, date , email );
            }catch(Exception e) {

                Log.d("#Error" , "DatabaseError:HomeFragment:381") ;
                return false ;
            }
        return true ;
}



}


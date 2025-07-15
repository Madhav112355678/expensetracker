package com.example.trackmymoney.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.Model.expensedetails;
import com.example.trackmymoney.R;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link report_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */

//This class shows statistics of user expenditure
public class report_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //database handlers variables
    private Appdatabase database;
    private List<expensedetails> expenslist;
    private expenseDetailsDao dataaccessobj;
    private String email ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private SharedPreferences userpreferences;

    public report_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment report_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static report_fragment newInstance(String param1, String param2) {
        report_fragment fragment = new report_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            String temp= getArguments().getString(ARG_PARAM2);
        }

        userpreferences = requireContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE) ;
        email = userpreferences.getString("email" , "") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        try {
            database = DatabaseProvider.getDatabase(this.getContext());
        } catch (Exception e) {
            throw new RuntimeException("error in databaseprovider");
        }

        //initializing dataaccess object using database
        if (database != null) {
            dataaccessobj = database.expensesdao();
            expenslist = dataaccessobj.getallexpenses();
        } else {
            dataaccessobj = null;
        }


        //report fragment on change
        PieChart pieChart = view.findViewById(R.id.pieChart);
        //populating data in the chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        if(dataaccessobj.getSumByfood(email) > 0) {
            entries.add(new PieEntry(dataaccessobj.getSumByfood(email) , "food")) ;
        }

        if (dataaccessobj.getSumByOther(email) > 0) {
            entries.add(new PieEntry(dataaccessobj.getSumByOther(email), "other"));

        }
        if (dataaccessobj.getSumByShopping(email) > 0) {
            entries.add(new PieEntry(dataaccessobj.getSumByShopping(email), "Shopping"));

        }

        if(dataaccessobj.getSumByTravel(email) > 0) {
            entries.add(new PieEntry(dataaccessobj.getSumByTravel(email), "Travel"));

        }



        PieDataSet dataSet = new PieDataSet(entries, "Spending Breakdown");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setCenterText("Expenses");
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();

        //get today's date
        LocalDate date = LocalDate.now();
        int day = date.getDayOfMonth();
        int month = date.getMonth().getValue();
        int year = date.getYear();
        String datetofind;
        long totalof7days = 0L;
        List<Long> data7days = new ArrayList<>();

        //putting spending data in array to populate data in the charts
        for (int i = 1; i <= day; i++) {

            datetofind = String.valueOf(i) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
            if (dataaccessobj.getSumPerDay(datetofind , email) > 0) {
                totalof7days += dataaccessobj.getSumPerDay(datetofind , email);
            }
            if (i % 7.00 == 0.00) {
                data7days.add(totalof7days);
                totalof7days = 0L;
            }
        }
        if (totalof7days > 0) {
            data7days.add(totalof7days);
        }


        //barchar handling
        BarChart bar = view.findViewById(R.id.barchart) ;
        ArrayList<BarEntry> arr = new ArrayList<>() ;
        long j = 1 ;
        //creating bar entry for each week till the day of month
        BarChart barChart = view.findViewById(R.id.barchart);

// Simulated x-axis for weeks (e.g., 1, 2, 3...) instead of weirdly growing j *= 7
        for (int i = 0; i < data7days.size(); i++) {
            arr.add(new BarEntry(i + 1, data7days.get(i))); // Notice i (not j) for consistent weekly spacing
        }

        final ArrayList<String> weekLabels = new ArrayList<>();
        for (int i = 0; i < data7days.size(); i++) {
            weekLabels.add("Week " + (i + 1));
        }


// BarDataSet with styling
        BarDataSet datas = new BarDataSet(arr, "Spending Breakdown (Weekly)");
        datas.setColors(Color.parseColor("#FF6F61")); // Warm Coral
        datas.setValueTextColor(Color.BLACK);
        datas.setValueTextSize(14f);
        datas.setDrawValues(true);
        datas.setBarBorderWidth(0.5f);

// BarData
        BarData barData = new BarData(datas);
        barData.setBarWidth(0.4f); // Optional: Adjust bar thickness

// Configure the chart
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true); // Auto-resize to match bars
        barChart.setDrawGridBackground(true);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

// X-axis formatting

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // One label per bar
        xAxis.setLabelCount(weekLabels.size());
        xAxis.setTextColor(Color.BLACK); // Optional: set label color
        xAxis.setTextSize(12f);
        barChart.setDrawGridBackground(true);// Optional: set label size

// 3. Set the custom formatter
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < weekLabels.size()) ? weekLabels.get(index) : "";
            }
        });


// Y-axis styling
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

// Animation
        barChart.animateY(1200, Easing.EaseInOutBounce);

// Refresh
        barChart.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

}
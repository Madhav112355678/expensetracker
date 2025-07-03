package com.example.trackmymoney;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackmymoney.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link report_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class report_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        PieChart pieChart = view.findViewById(R.id.pieChart);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(500f, "Food"));
        entries.add(new PieEntry(300f, "Study"));
        entries.add(new PieEntry(200f, "Luxury"));

        PieDataSet dataSet = new PieDataSet(entries, "Spending Breakdown");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setCenterText("Expenses");
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();

        //barchar handling
        BarChart bar = view.findViewById(R.id.barchart) ;
        ArrayList<BarEntry> arr = new ArrayList<>() ;
        arr.add(new BarEntry(1f , 600f)) ;
        arr.add(new BarEntry(7f , 700f)) ;
        arr.add(new BarEntry(14f , 1200f)) ;
        arr.add(new BarEntry(21f , 1500f)) ;
        arr.add(new BarEntry(30f , 1600f)) ;
        BarDataSet datas = new BarDataSet(arr , "spending breakdown in weeks") ;
        datas.addColor(R.color.transperent);
        datas.setValueTextSize(15f);
        BarData barred = new BarData(datas) ;

        bar.setData(barred);
        bar.setFitBars(true);
        bar.setDrawBarShadow(true);
        bar.getDescription().setEnabled(false);
        bar.animateX(2000);
        bar.animateY(2000);
        bar.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

}
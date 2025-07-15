package com.example.trackmymoney.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackmymoney.R;

public class HistoryFragment extends Fragment {



    Activity active ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        Spinner myspinner = view.findViewById(R.id.history_filters);
        String[] items = {"Today", "Last 5 days" , "Last month" , "This month" , "This year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        myspinner.setAdapter(adapter);
        active = getActivity() ;

        RecyclerView recycle = getActivity().findViewById(R.id.showhistoryitems) ;
        recycle.getAdapter() ;
        return view;
    }




    @Override
    public void onResume() {
        super.onResume();

    }
}

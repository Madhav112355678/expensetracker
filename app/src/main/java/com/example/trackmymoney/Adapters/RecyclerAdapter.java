package com.example.trackmymoney.Adapters;

import android.widget.Adapter;

import com.example.trackmymoney.Model.expensedetails;

public class RecyclerAdapter extends Adapter {
    expensedetails details ;

    public RecyclerAdapter(expensedetails detailes) {
         details = detailes ;
    }


}

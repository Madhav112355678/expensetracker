package com.example.trackmymoney.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trackmymoney.Model.expensedetails;
import com.example.trackmymoney.R;

import java.util.List;

public class ScrollAdapter extends LinearLayout {

    private Context context;
    private List<expensedetails> items;

    public ScrollAdapter(Context context, List<expensedetails> items) {
        super(context);
        setOrientation(VERTICAL);
        this.context = context;
        this.items = items;

    }

    public void populateInto(LinearLayout container) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        if(items == null) {
            throw new NullPointerException();
        }
        for (expensedetails item : items) {
            View view = inflater.inflate(R.layout.single_item_layout, container, false);
            TextView amount = view.findViewById(R.id.amountview);
            TextView category = view.findViewById(R.id.categoryview);
            TextView date = view.findViewById(R.id.dateview) ;
            amount.setText( "₹" + String.valueOf(item.amount));
            category.setText(item.category);
            date.setText(String.valueOf(item.date)) ;
            container.addView(view);
        }
    }
}


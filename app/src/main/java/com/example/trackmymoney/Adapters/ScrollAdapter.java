package com.example.trackmymoney.Adapters;


import com.example.trackmymoney.database.Appdatabase ;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.trackmymoney.Model.expensedetails;
import com.example.trackmymoney.Model.* ;
import com.example.trackmymoney.R;
import com.example.trackmymoney.database.DatabaseProvider;

import java.util.List;

public class ScrollAdapter extends LinearLayout {
     Appdatabase database ;
    private Context context;
    private List<expensedetails> items;
    SharedPreferences balance ;

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
            AppCompatImageButton delete = view.findViewById(R.id.deletebutton) ;
            amount.setText( "₹" + String.valueOf(item.amount));
            category.setText(item.category);
            date.setText(String.valueOf(item.date)) ;



            delete.setOnClickListener(v -> {
                try {
                    database = DatabaseProvider.getDatabase(this.getContext());
                } catch (Exception e) {
                    throw new RuntimeException("error in databaseprovider");
                }
                Long amt=item.amount;
                Log.d("itemAmount","item amount"+amt);
                balance = context.getSharedPreferences("Balances", Context.MODE_PRIVATE);
                Long addedbalance = balance.getLong("currentbalance",0L) +item.amount ;
                Log.d("addedBalanceDebug","added balance = "+addedbalance);
                balance.edit().putLong("currentbalance",addedbalance).commit();
                Log.d( "checkbalance", String.valueOf(balance.getLong("currentbalance" , 0l))) ;
                database.expensesdao().delete(item);
                items.remove(item);
                container.removeView(view);
            });
            container.addView(view);
        }
    }
}


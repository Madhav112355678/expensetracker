package com.example.trackmymoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<CategoryItem> {

    public CategoryAdapter(@NonNull Context context, @NonNull List<CategoryItem> categories) {
        super(context, 0, categories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_with_icon, parent, false);
        }

        CategoryItem currentItem = getItem(position);

        ImageView icon = view.findViewById(R.id.icon);
        TextView categoryName = view.findViewById(R.id.categoryName);

        if (currentItem != null) {
            icon.setImageResource(currentItem.getIconResId());
            categoryName.setText(currentItem.getCategoryName());
        }

        return view;
    }
}

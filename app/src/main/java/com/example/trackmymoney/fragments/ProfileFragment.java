package com.example.trackmymoney.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trackmymoney.LoginActivity;
import com.example.trackmymoney.R;

public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //onCReate view loads all buttons and set on click listener
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Access views from fragment layout
        Button logoutButton = view.findViewById(R.id.logout_button);
        TextView nameView = view.findViewById(R.id.profile_name);
        TextView emailView = view.findViewById(R.id.profile_email);

        // Load shared preferences
        SharedPreferences sharedPrefs = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPrefs.getString("username", "");
        String email = sharedPrefs.getString("email", "");

        // Set text fields
        nameView.setText(username);
        emailView.setText(email);

        // Logout logic
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
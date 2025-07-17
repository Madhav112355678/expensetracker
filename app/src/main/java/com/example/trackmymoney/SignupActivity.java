package com.example.trackmymoney;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trackmymoney.Dao.UserDao;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;

/**
 * SignupActivity handles user registration.
 * It validates user input, hashes the password,
 * and inserts the new user record into the local Room database.
 */
public class SignupActivity extends AppCompatActivity {

    // Database-related fields
    private DatabaseProvider db;         // Not used in code, declared (could be removed if unused)
    private UserDao dao;                 // Data Access Object for User table
    private Appdatabase appdatabase;     // App's Room database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge layout
        setContentView(R.layout.activity_signup);

        // Handles window insets to avoid UI overlaps with system bars (status bar, nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize Room database instance
        try {
            appdatabase = DatabaseProvider.getDatabase(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e); // Crash app if DB setup fails
        }

        // Initialize DAO to interact with user table
        try {
            dao = appdatabase.UserDao();
        } catch (Exception e) {
            throw new RuntimeException(e); // Crash app if DAO retrieval fails
        }

        // Bind input fields from layout
        EditText usernameinput = this.findViewById(R.id.textInputEditText);
        EditText useremailinput = this.findViewById(R.id.editTextTextEmailAddress);
        EditText userpassword = this.findViewById(R.id.editTextTextPassword2);
        // TextView lognav = this.findViewById(R.id.textView2); // (Currently unused)

        // Set listener for signup button
        this.findViewById(R.id.signupbutton).setOnClickListener(v -> {
            String username = usernameinput.getText().toString().trim();
            String email = useremailinput.getText().toString().trim();
            String password = userpassword.getText().toString().trim();

            // Validate user input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Hash the password using SHA-256
                String hashedpassword = LoginActivity.hashPassword(password);

                // Insert user into the database
                dao.insertuser(username, hashedpassword, email);

                // Show success message and navigate to login screen
                Toast.makeText(getApplicationContext(), "successful sign up", Toast.LENGTH_SHORT).show();
                Intent gotologin = new Intent(SignupActivity.this, LoginActivity.class);
                gotologin.putExtra("issignedup", true); // Optional flag to indicate source
                startActivity(gotologin);
                finish(); // Close SignupActivity
            }
        });
    }
}

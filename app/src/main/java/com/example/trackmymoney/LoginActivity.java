package com.example.trackmymoney;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackmymoney.Dao.UserDao;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * LoginActivity handles the user login functionality.
 * It verifies user credentials from the local Room database
 * and saves login status using SharedPreferences.
 */
public class LoginActivity extends AppCompatActivity {

    private UserDao dataaccessobject; // Data Access Object for interacting with user table
    private Appdatabase database;     // App's Room database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge layout rendering
        setContentView(R.layout.activity_login); // Sets the layout for the activity

        // UI elements from the layout
        EditText nameInp = findViewById(R.id.usernameinput);
        EditText emailInp = findViewById(R.id.emailinput);
        EditText passwordInp = findViewById(R.id.passwordinput);
        Button loginBtn = findViewById(R.id.loginbutton);
        TextView gotosignupactivity = findViewById(R.id.switchtosignup);

        // SharedPreferences to store login session data
        SharedPreferences userdata = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Initialize database using a singleton provider class
        try {
            database = DatabaseProvider.getDatabase(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e); // If database creation fails, crash the app
        }

        // Get DAO for performing login queries
        try {
            dataaccessobject = database.UserDao();
        } catch (Exception e) {
            Log.d("#Database error", "Error:LoginActivity Line 47");
            throw new RuntimeException(e);
        }

        // Login button click listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInp.getText().toString().trim();
                String email = emailInp.getText().toString().trim();
                String password = passwordInp.getText().toString().trim();

                // Check if all fields are filled
                if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                    SharedPreferences.Editor editor = userdata.edit();

                    // Retrieve hashed password from the database using entered email
                    String hashedpassword = dataaccessobject.getpasswordwithemail(email);

                    // If email not found in database or returns empty
                    if (hashedpassword == null || hashedpassword.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "problem with password", Toast.LENGTH_SHORT).show();
                    }
                    // Compare entered password with the stored hashed password
                    else if (hashedpassword.equals(hashPassword(password))) {
                        // Store login status and user info in SharedPreferences
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", name);
                        editor.putString("email", email);
                        editor.apply();

                        // Show success message and redirect to main activity
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // Close LoginActivity
                    } else {
                        // If credentials don't match
                        Snackbar.make(findViewById(R.id.loginbutton), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // If fields are left empty
                    Toast.makeText(LoginActivity.this, "enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TextView click to switch to SignupActivity
        gotosignupactivity.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Hashes a plain-text password using SHA-256 algorithm.
     *
     * @param password Plain-text password
     * @return Hashed password as hexadecimal string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // Convert byte to hex
            }

            return sb.toString(); // Return hashed password string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

}

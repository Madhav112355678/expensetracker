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

public class LoginActivity extends AppCompatActivity {

    private UserDao dataaccessobject;
    private Appdatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText nameInp = findViewById(R.id.usernameinput);
        EditText emailInp = findViewById(R.id.emailinput);
        EditText passwordInp = findViewById(R.id.passwordinput);
        Button loginBtn = findViewById(R.id.loginbutton);
        TextView gotosignupactivity = findViewById(R.id.switchtosignup);
        SharedPreferences userdata = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        try {
            database = DatabaseProvider.getDatabase(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            dataaccessobject = database.UserDao();
        } catch (Exception e) {
            Log.d("#Database error", "Error:LoginActivity Line 47");
            throw new RuntimeException(e);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInp.getText().toString().trim();
                String email = emailInp.getText().toString().trim();
                String password = passwordInp.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                    SharedPreferences.Editor editor = userdata.edit();

                    String hashedpassword = dataaccessobject.getpasswordwithemail(email);
                    if (hashedpassword == null || hashedpassword.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "problem with password", Toast.LENGTH_SHORT).show();
                    } else if (hashedpassword.equals(hashPassword(password))) {
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", name);
                        editor.putString("email", email);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.loginbutton), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "enter all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //goto signupactivity
        gotosignupactivity.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    //hashing function
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }


}
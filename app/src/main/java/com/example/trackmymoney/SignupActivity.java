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
import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;

import org.w3c.dom.Text;

public class SignupActivity extends AppCompatActivity {

    //fields
    private  DatabaseProvider db ;
    private UserDao dao ;
    private Appdatabase appdatabase ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            appdatabase = DatabaseProvider.getDatabase(getApplicationContext()) ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            dao = appdatabase.UserDao();
        }catch(Exception e) {
            throw new RuntimeException(e) ;
        }


        //take all variable
        EditText usernameinput = this.findViewById(R.id.textInputEditText) ;
        EditText useremailinput = this.findViewById(R.id.editTextTextEmailAddress);
        EditText userpassword = this.findViewById(R.id.editTextTextPassword2) ;
        //TextView lognav = this.findViewById(R.id.textView2) ;

        this.findViewById(R.id.signupbutton).setOnClickListener(v -> {
              String username = usernameinput.getText().toString().trim();
              String email = useremailinput.getText().toString().trim() ;
              String password = userpassword.getText().toString().trim() ;

              if(username.isEmpty() ||  email.isEmpty() || password.isEmpty()) {
                  Toast.makeText(getApplicationContext() , "Please fill all fields" , Toast.LENGTH_SHORT).show() ;
              } else {
                  String hashedpassword = LoginActivity.hashPassword(password) ;
                  dao.insertuser(username , hashedpassword , email);
                  Toast.makeText(getApplicationContext() , "successful sign up" , Toast.LENGTH_SHORT).show() ;
                  Intent gotologin = new Intent(SignupActivity.this , LoginActivity.class) ;
                  gotologin.putExtra("issignedup" , true) ;
                  startActivity(gotologin);
                  finish() ;
              }
        });


    }
}
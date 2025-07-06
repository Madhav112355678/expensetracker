package com.example.trackmymoney;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ReportFragment;

import com.example.trackmymoney.Dao.expensesDao;
import com.example.trackmymoney.Model.expenses;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;


//Main activity of Applcation this activity host every fragment in our application
//it handles navigation logic
public class MainActivity extends AppCompatActivity {

    public Appdatabase database ;
    public List<expenses> expenslist ;

    androidx.constraintlayout.widget.ConstraintLayout bottomNavigation;

    //call to oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();


        //building singleton database
        try {
            database = DatabaseProvider.getDatabase(this.getApplicationContext());
        }catch(Exception e) {
             throw new RuntimeException("error in databaseprovider") ;
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);
        //home button click
        Button home = findViewById(R.id.homebutton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            }
        });

        //add button click
        Button add = findViewById(R.id.addbutton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddFragment()).commit();
            }
        });

        //stats button click
        Button stats = findViewById(R.id.statsbutton);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new report_fragment()).commit();
            }
        });
    }



    // getting access to table
    protected void Start() {
        super.onStart() ;
        expensesDao dataaccessobj;
            if (database != null) {
                dataaccessobj = database.expensesdao();
                expenslist = dataaccessobj.getallexpenses();
            }


            //testing if expenslist is working or not
            if (expenslist != null) {
                for (expenses expens : expenslist) {
                    String print = (expens.amount) + expens.category + expens.date;
                    Toast.makeText(getApplicationContext(), print, Toast.LENGTH_LONG).show();
                }

        }
    }



    @Override
    protected void onDestroy() {
        database.close() ;
        super.onDestroy() ;
    }
}
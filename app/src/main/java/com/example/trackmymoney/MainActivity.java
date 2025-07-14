package com.example.trackmymoney;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.Model.expensedetails;
import com.example.trackmymoney.database.Appdatabase;
import com.example.trackmymoney.database.DatabaseProvider;
import com.example.trackmymoney.fragments.AddFragment;
import com.example.trackmymoney.fragments.HistoryFragment;
import com.example.trackmymoney.fragments.HomeFragment;
import com.example.trackmymoney.fragments.ProfileFragment;
import com.example.trackmymoney.fragments.report_fragment;

import java.util.List;


//Main activity of Applcation this activity host every fragment in our application
//it handles navigation logic
public class MainActivity extends AppCompatActivity {

    public Appdatabase database ;
    public List<expensedetails> expenslist ;

    androidx.constraintlayout.widget.ConstraintLayout bottomNavigation;

    //call to oncreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        NavController navController = navHostFragment.getNavController();


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
                navController.navigate(R.id.action_global_homeFragment);
            }
        });
         //database.expensesdao().deleteAll();
        SharedPreferences shard = getSharedPreferences("Balances" , MODE_PRIVATE) ;
        shard.edit().remove("limit") ;
        shard.edit().remove("currentbalance") ;


       //add button click listener
        Button add = findViewById(R.id.addbutton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               navController.navigate(R.id.action_global_addFragment);
            }
        });

        //stats button click
        Button stats = findViewById(R.id.statsbutton);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_global_report_fragment);
            }
        });

        ///profile button click
        ImageButton profile = findViewById(R.id.profilebutton);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_global_profileFragment);
            }
        });

       Button history = findViewById(R.id.showhistory) ;
        history.setOnClickListener(v -> {
          navController.navigate(R.id.action_global_historyFragment); ;
        });
    }

    // getting access to table
    protected void Start() {
        super.onStart() ;
        expenseDetailsDao dataaccessobj;
        if (database != null) {
            dataaccessobj = database.expensesdao();
            expenslist = dataaccessobj.getallexpenses();
        }


        //testing if expenslist is working or not
        if (expenslist != null) {
            for (expensedetails expens : expenslist) {
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
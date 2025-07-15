package com.example.trackmymoney.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;

// A helper class to create singleton of our database object everytime we don't need to build new database
// this insures only one INSTANCE OF database class should be open at a time
public class DatabaseProvider {

    public static Appdatabase INSTANCE ;

    //helper class to get Database as a singleton everywhere they are static methods
    public static Appdatabase getDatabase(final Context context) throws Exception {

        //try context is not null
        try {
            assert context != null;
        } catch (AssertionError e) {
            Log.e("debug", "Error in Databaseprovider.java line 14");
            throw new Exception("error in accessing context in Databaseprovider line 20") ;
        }

        synchronized (DatabaseProvider.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, Appdatabase.class, "trialdb").
                        allowMainThreadQueries().build();
            }
        }

        return INSTANCE ;
    }

}

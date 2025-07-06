package com.example.trackmymoney.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.trackmymoney.Dao.expensesDao;
import com.example.trackmymoney.Model.expenses;


// this creates bridge between appdatabase and databaseaccessobject in our case expenses dao using this we can query database with expensedao
@Database(entities = {expenses.class},version = 1,exportSchema = false)
public abstract class Appdatabase extends RoomDatabase {
    public abstract expensesDao expensesdao() ;
}

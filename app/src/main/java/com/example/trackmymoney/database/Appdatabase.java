package com.example.trackmymoney.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.trackmymoney.Dao.UserDao;
import com.example.trackmymoney.Dao.expenseDetailsDao;
import com.example.trackmymoney.Model.User;
import com.example.trackmymoney.Model.expensedetails;


// this creates bridge between appdatabase and databaseaccessobject in our case expenses dao using this we can query database with expensedao
@Database(entities = {expensedetails.class, User.class},version = 1,exportSchema = false)
public abstract class Appdatabase extends RoomDatabase {


    public abstract expenseDetailsDao expensesdao() ;
    public abstract UserDao UserDao() ;
}

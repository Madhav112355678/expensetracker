package com.example.trackmymoney.Dao;

import static android.icu.text.MessagePattern.ArgType.SELECT;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.trackmymoney.Model.User;

@Dao
public interface UserDao {

    //all queries

    @Query("SELECT password FROM User WHERE username = :username")
    public String getPassword(String username) ;

    @Query("SELECT password FROM User WHERE email = :email")
    public String getpasswordwithemail(String email) ;

    @Query("INSERT INTO User(username , password , email) VALUES(:username , :password , :email)")
    public void insertuser(String username , String password , String email) ;
}

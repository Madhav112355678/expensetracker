package com.example.trackmymoney.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import javax.xml.namespace.QName;

@Entity(tableName = "User" , indices = {@Index(value = {"email"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    public int userid ;

    @ColumnInfo(name = "username")
    public String username ;

    @ColumnInfo(name = "password")
    public String password ;

    @ColumnInfo(name = "email")
    public String email ;
 }

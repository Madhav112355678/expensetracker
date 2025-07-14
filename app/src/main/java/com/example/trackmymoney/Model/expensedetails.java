package com.example.trackmymoney.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Update;

//entity class for accessing data from the user
//expenses table
//this is our table model which is replicated in room database as a table
@Entity(tableName = "expensedetails" , foreignKeys = @ForeignKey(entity = User.class , parentColumns = "email" , childColumns = "email" , onDelete = ForeignKey.CASCADE))
public class expensedetails {

    @PrimaryKey(autoGenerate = true)
    public int expenseid ;

    @ColumnInfo(name = "amount")
    public long amount ;

    @ColumnInfo(name = "category")
    public String category ;


    @ColumnInfo(name = "date")
    public String date ;

    @ColumnInfo(name = "email")
    public String email ;

    @ColumnInfo(name = "password")
    public String password ;
}

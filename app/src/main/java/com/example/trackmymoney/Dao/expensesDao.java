package com.example.trackmymoney.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.trackmymoney.Model.expenses;

import java.util.List;


// This is expense data access object for from here we will call to database using this interface
//access object is a interface which is used to querying our database it is a layer above our datamodel
@Dao
public interface expensesDao {

    @Query("SELECT  * FROM expenses")
   public List<expenses> getallexpenses() ;

    @Query("SELECT * FROM expenses WHERE date like '%' || :month || '%'")
    public List<expenses> getExpensesByMonth(String month);

    @Query("INSERT INTO expenses(amount , category , date) VALUES(:amount , :category , :date)")
    public void add(long amount ,String category ,String date) ;

    @Query("SELECT * FROM expenses WHERE category = :category")
    public List<expenses> selectbycategory(String category) ;

    @Query("SELECT * FROM expenses WHERE amount >= :data")
    public List<expenses> showByAmount(long data) ;

    @Query("SELECT * FROM expenses WHERE date LIKE :currentdate ")
    public List<expenses> getExpensesByDate(String currentdate) ;

    @Query("SELECT * FROM expenses ORDER BY  expenseid DESC LIMIT 5")
    public List<expenses> getLastFiveExpenses() ;

    //querie's to get sum from database
    @Query("SELECT SUM(amount) FROM expenses WHERE date = :requesteddate")
    public long getSumPerDay(String requesteddate) ;
    @Query("SELECT SUM(amount) FROM expenses")
    public long getSum() ;
    @Query("SELECT SUM(amount) FROM expenses WHERE category = 'Food'")
            public int getSumByfood() ;
    @Query("SELECT SUM(amount) FROM expenses WHERE category = 'Shopping'")
            public int getSumByShopping();
    @Query("SELECT SUM(amount) FROM expenses WHERE category = 'Travel'")
            public int getSumByTravel();
    @Query("SELECT SUM(amount) FROM expenses WHERE category = 'other'")
            public int getSumByOther() ;

    //Delete from expenses
    @Query("DELETE FROM expenses WHERE category = :category AND amount = :amount AND date = :date")
           public void Delete(String category , long amount , String date) ;
}

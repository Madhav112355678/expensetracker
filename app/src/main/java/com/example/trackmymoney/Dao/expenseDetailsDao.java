package com.example.trackmymoney.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.example.trackmymoney.Model.expensedetails;

import java.util.List;


// This is expense data access object for from here we will call to database using this interface
//access object is a interface which is used to querying our database it is a layer above our datamodel
@Dao
public interface expenseDetailsDao {

    @Query("SELECT  * FROM expensedetails")
   public List<expensedetails> getallexpenses() ;

    @Query("SELECT * FROM expensedetails WHERE date like '%' || :month || '%' AND email = :email")
    public List<expensedetails> getExpensesByMonth(String month , String email);

    @Query("INSERT INTO expensedetails(amount , category , date , email) VALUES(:amount , :category , :date , :email)")
    public void add(long amount ,String category ,String date , String email) ;

    @Query("SELECT * FROM expensedetails WHERE category = :category AND email = :email")
    public List<expensedetails> selectbycategory(String category , String email) ;

    @Query("SELECT * FROM expensedetails WHERE amount >= :data AND email = :email")
    public List<expensedetails> showByAmount(long data , String email) ;

    @Query("SELECT * FROM expensedetails WHERE date LIKE :currentdate AND email = :email")
    public List<expensedetails> getExpensesByDate(String currentdate , String email) ;

    @Query("SELECT * FROM expensedetails WHERE email = :email ORDER BY  expenseid DESC LIMIT 5")
    public List<expensedetails> getLastFiveExpenses(String email) ;

    //querie's to get sum from database
    @Query("SELECT SUM(amount) FROM expensedetails WHERE date = :requesteddate AND email = :email")
    public long getSumPerDay(String requesteddate , String email) ;
    @Query("SELECT SUM(amount) FROM expensedetails WHERE email = :email")
    public long getSum(String email) ;
    @Query("SELECT SUM(amount) FROM expensedetails WHERE category = 'Food' AND email = :email")
            public int getSumByfood(String email) ;
    @Query("SELECT SUM(amount) FROM expensedetails WHERE category = 'Shopping' AND email = :email")
            public int getSumByShopping(String email);
    @Query("SELECT SUM(amount) FROM expensedetails WHERE category = 'Travel' AND email = :email")
            public int getSumByTravel(String email);
    @Query("SELECT SUM(amount) FROM expensedetails WHERE category = 'other' AND email = :email")
            public int getSumByOther(String email) ;

    //Delete from expenses

    @Delete
    void delete(expensedetails expensedetails) ;
    @Query("DELETE FROM expensedetails WHERE category = :category AND amount = :amount AND date = :date AND email = :email")
           public void Deleteexpense(String category , long amount , String date , String email) ;

    @Query("DELETE FROM expensedetails WHERE email = :email")
      public void deleteAll(String email) ;
}

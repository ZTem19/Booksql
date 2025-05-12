/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package booksql;

import java.sql.*;

/**
 *
 * @author zander
 */
public class StatsDAO {
    
    private static final String mPopBook = "select book_id, title, num_checked_out from book order by num_checked_out desc;";
    private static final String lPopBook = "select book_id, title, num_checked_out from book order by num_checked_out asc;";
    private static final String genres = "select genre, sum(num_checked_out) from book group by genre order by sum(num_checked_out) desc;";
    private static final String mUser = "select userid, fname, middle_inital, lname, num_books_checked_out from \"user\" order by num_books_checked_out desc;";
                
    private Connection conn;
    
    public StatsDAO(Connection conn){
        this.conn = conn;
    }
    
    //Returns 
    // arr[0] int: userid
    // arr[1] String: userName
    // arr[2] int: numBooks
    public Object[] getMUser() throws SQLException{
        Object[] arr = new Object[3];
        Statement stmt = conn.createStatement();
        System.out.println(mUser);
        ResultSet rs = stmt.executeQuery(mUser);
        rs.next();
        
        //Construct Name
        String name = rs.getString("fname").concat(" ").concat(rs.getString("middle_inital")).concat(". ").
                concat(rs.getString("lname"));
        
        arr[0] = rs.getInt("userid");
        arr[1] = name;
        arr[2] = rs.getInt("num_books_checked_out");
        return arr;
    }
    
    //Returns 
    // arr[0] int: bookId
    // arr[1] String: title
    // arr[2] int: numTimes
    public Object[] getMPopBook() throws SQLException{
        Object[] arr = new Object[3];
        Statement stmt = conn.createStatement();
        System.out.println(mPopBook);
        ResultSet rs = stmt.executeQuery(mPopBook);
        rs.next();
        arr[0] = rs.getInt("book_id");
        arr[1] = rs.getString("title");
        arr[2] = rs.getInt("num_checked_out");
        return arr;
    }
    
    //Returns 
    // arr[0] int: bookId
    // arr[1] String: title
    // arr[2] int: numTimes
    public Object[] getLPopBook() throws SQLException{
        Object[] arr = new Object[3];
        Statement stmt = conn.createStatement();
        System.out.println(lPopBook);
        ResultSet rs = stmt.executeQuery(lPopBook);
        rs.next();
        arr[0] = rs.getInt("book_id");
        arr[1] = rs.getString("title");
        arr[2] = rs.getInt("num_checked_out");
        return arr;
    }
    
    //Returns 
    // arr[0] String: mPopGenre
    // arr[1] String: lPopGenre
    public String[] getGenres() throws SQLException{
        String[] arr = new String[2];
        Statement stmt = conn.createStatement();
        System.out.println(genres);
        ResultSet rs = stmt.executeQuery(genres);
        rs.next();
        arr[0] = rs.getString("genre");
        // Go to last line
        while(!rs.isLast()){
            rs.next();
        }
        arr[1] = rs.getString("genre");
        return arr;
    }
}

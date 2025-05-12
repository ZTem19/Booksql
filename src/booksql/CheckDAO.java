/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package booksql;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.postgresql.util.PSQLException;

/** Handles the check in, check out and checkout queue
 *
 * @author zander
 */
public class CheckDAO {

    private static final String getAllCheckedOut = """
                                                   select bookid, checked_out.userid, b.title, fname, middle_inital, lname, checkout_date, due_date from checked_out
                                                   join "user" on checked_out.userid = "user".userid
                                                   join book b on checked_out.bookid = b.book_id order by due_date desc;""";
    
    private static final String checkOutBook = "insert into checked_out (bookid, userid, checkout_date, due_date) values (?, ?, ?, ?);";
    
            
    private Connection conn;
    
    public CheckDAO(Connection conn){
        this.conn = conn;
    }
    
    public ArrayList<CheckedOutEntry> getAllEntries(){
        ArrayList<CheckedOutEntry> entries = new ArrayList<>();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getAllCheckedOut);
            
            while(rs.next()){
                entries.add(new CheckedOutEntry(
                    rs.getInt("bookid"),
                    rs.getInt("userid"),
                    rs.getString("title"),
                    rs.getString("fname").concat(" ").concat(rs.getString("middle_inital")).concat(". ").concat(rs.getString("lname")),
                    rs.getDate("checkout_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate()
                ));
            }
            
            
        }catch(Exception e ){
            e.printStackTrace();
        }
        return entries;
    }
    
    
    public void checkOutBook(int bookID, int userID) throws SQLException{
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(checkOutBook);
            LocalDate currentDate = LocalDate.now();
            LocalDate dueDate = currentDate.plusDays(14);

            pstmt.setInt(1, bookID);
            pstmt.setInt(2, userID);
            pstmt.setDate(3, Date.valueOf(currentDate));
            pstmt.setDate(4, Date.valueOf(dueDate));

            System.out.println(pstmt.toString());
            int numOfAdded = pstmt.executeUpdate();
            System.out.println("Number of entries added: " + numOfAdded);

        }catch(SQLException e){
            throw e;
        }
    }
    
    public static class CheckedOutEntry{
        public int bookid;
        public int userid;
        public String bookName;
        public String userName;
        public LocalDate checkOutDate;
        public LocalDate dueDate;
        
        public CheckedOutEntry(){}
        
        public CheckedOutEntry(int bookid, int userid, String bookName, String userName, LocalDate checkedOutDate, LocalDate dueDate){
            this.bookid = bookid;
            this.userid = userid;
            this.bookName = bookName;
            this.userName = userName;
            this.checkOutDate = checkedOutDate;
            this.dueDate = dueDate;
        }
    }
}

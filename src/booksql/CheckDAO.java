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
    
    private static final String checkInBook = "delete from checked_out where bookid = ? and userid = ?;";
    
    private static final String isCopiesAvailable = "select num_available from book where book_id = ?;";
    
    private static final String updateBookCopies = "update book set num_available = num_available + ? where book_id = ?;";
    
    private static final String updateUserBooks = "update \"user\" set num_books_checked_out = num_books_checked_out + ? where userid = ?;";
    
            
    private Connection conn;
    
    public CheckDAO(Connection conn){
        this.conn = conn;
    }
    
        
    public boolean canCheckOutBook(int bookID){
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(isCopiesAvailable);

            pstmt.setInt(1, bookID);

            System.out.println(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt("num_available") > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
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
            
            this.removeCopyFromBook(bookID);
            this.addBookToUser(userID);
            

        }catch(SQLException e){
            throw e;
        }
    }
    
    public int checkInBook(int bookID, int userID) throws SQLException{
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(checkInBook);

            pstmt.setInt(1, bookID);
            pstmt.setInt(2, userID);

            System.out.println(pstmt.toString());
            int numOfRemoved = pstmt.executeUpdate();
            System.out.println("Number of entries Removed: " + numOfRemoved);
            
            this.removeBookFromUser(userID);
            this.addCopyToBook(bookID);
            
            return numOfRemoved;

        }catch(SQLException e){
            throw e;
        }
    }
    
    private void addBookToUser(int userID){
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(updateUserBooks);

            pstmt.setInt(1, 1);
            pstmt.setInt(2, userID);

            System.out.println(pstmt.toString());
            int numOfUpdated = pstmt.executeUpdate();
            System.out.println("numOfUpdated: " + numOfUpdated);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private void removeBookFromUser(int userID){
        
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(updateUserBooks);

            pstmt.setInt(1, -1);
            pstmt.setInt(2, userID);

            System.out.println(pstmt.toString());
            int numOfUpdated = pstmt.executeUpdate();
            System.out.println("numOfUpdated: " + numOfUpdated);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private void addCopyToBook(int bookID){
        
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(updateBookCopies);

            pstmt.setInt(1, 1);
            pstmt.setInt(2, bookID);

            System.out.println(pstmt.toString());
            int numOfUpdated = pstmt.executeUpdate();
            System.out.println("numOfUpdated: " + numOfUpdated);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private void removeCopyFromBook(int bookID){
        
        try{
            PreparedStatement pstmt = this.conn.prepareStatement(updateBookCopies);

            pstmt.setInt(1, -1);
            pstmt.setInt(2, bookID);

            System.out.println(pstmt.toString());
            int numOfUpdated = pstmt.executeUpdate();
            System.out.println("numOfUpdated: " + numOfUpdated);

        }catch(SQLException e){
            e.printStackTrace();
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package booksql;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author zander
 */
public class UserDAO {
    
    private static final String selectAllUsers = "select * from \"user\"";
    
    private Connection conn;
    
    public UserDAO(Connection conn){
        this.conn = conn;
    }
    
    
    public ArrayList<User> getAllUsers(){
        ArrayList<User> users = new ArrayList();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectAllUsers.concat(";"));
            
            while(rs.next()){
                users.add(new User(
                        rs.getInt("userid"),
                        rs.getString("fname"),
                        rs.getString("middle_inital").charAt(0),
                        rs.getString("lname"),
                        rs.getInt("num_books_checked_out"),
                        new BigDecimal(rs.getString("balance").replaceAll("[$,]",""))
                ));
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
       return users;
    }
    
    public ArrayList<User> getBooksWithFilter(Filter filter){
        ArrayList<User> users = new ArrayList();
        try{
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(selectAllUsers);
            StringBuilder strBuilder = new StringBuilder(selectAllUsers);
            
            if(!filter.fname.isBlank()){
                strBuilder.append("");
            }
            if(!filter.lname.isBlank()){
                strBuilder.append("");
            }
            
            if(filter.balanceSort != null || filter.idSort != null || filter.numBooksSort != null){
                strBuilder.append("order by ");
                if(filter.idSort != null){
                    strBuilder.append("userid ").append((filter.idSort ? "asc" : "desc"));
                }
                else if(filter.numBooksSort != null){
                    strBuilder.append("").append((filter.idSort ? "asc" : "desc"));
                }
                else if(filter.balanceSort != null){
                    strBuilder.append("userid ").append((filter.idSort ? "asc" : "desc"));
                }
            }
            PreparedStatement pstmt = conn.prepareStatement(strBuilder.append(";").toString());
            ResultSet rs = pstmt.executeQuery();
            
            
            while(rs.next()){
                users.add(new User(
                        rs.getInt("userid"),
                        rs.getString("fname"),
                        rs.getString("middle_inital").charAt(0),
                        rs.getString("lname"),
                        rs.getInt("num_books_checked_out"),
                        new BigDecimal(rs.getString("balance").replaceAll("[$,]",""))
                ));
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
       return users;
    }
    
    public class User {
        public int id;
        public String fname;
        public char middleInitial;
        public String lname;
        public int numOfBooks;
        public BigDecimal balance;
        
        public User(int id, String fname, char middleInitial, String lname, int numOfBooks, BigDecimal balance){
            this.fname = fname;
            this.middleInitial = middleInitial;
            this.lname = lname;
            this.numOfBooks = numOfBooks;
            this.balance = balance;
        }
    }
    
    public static class Filter {
        public Boolean idSort;
        public String fname;
        public String lname;
        public Boolean numBooksSort;
        public Boolean balanceSort;
        
        public Filter(){
            this.idSort = null;
            this.fname = "";
            this.lname = "";
            this.numBooksSort = null;
            this.balanceSort = null;
        }
        
        public Filter(
                Boolean idSort, 
                String fname, 
                String lname, 
                Boolean numBooksSort,
                Boolean balanceSort
        ){
            this.idSort = idSort;
            this.fname = fname;
            this.lname = lname;
            this.numBooksSort = numBooksSort;
            this.balanceSort = balanceSort;
        }
        
        public int getOrderByCount(){
            if(this.balanceSort != null || this.idSort != null || this.numBooksSort != null){
                
            }
        }
    }
    
    
    
    
    
}

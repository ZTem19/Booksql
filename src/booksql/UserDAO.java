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
            
            boolean fnameBlank = filter.fname.isBlank();
            boolean lnameBlank = filter.lname.isBlank();
            
            if(!fnameBlank){
                strBuilder.append(" where lower(fname) like ?");
            }
            if(!lnameBlank){
                if(!fnameBlank){
                    strBuilder.append(" and lower(lname) like ?");
                }else{
                    strBuilder.append(" where lower(lname) like ?");                 
                }
            }
            
            if(filter.balanceSort != null || filter.idSort != null || filter.numBooksSort != null){
                strBuilder.append(" order by ");
                if(filter.idSort != null){
                    strBuilder.append("userid ").append((filter.idSort ? "asc" : "desc"));
                }
                if(filter.numBooksSort != null){
                    if(filter.idSort != null){
                        strBuilder.append(",");
                    }
                    strBuilder.append("num_books_checked_out ").append((filter.numBooksSort ? "asc" : "desc"));
                }
                if(filter.balanceSort != null){
                    if(filter.idSort != null || filter.numBooksSort != null){
                        strBuilder.append(",");
                    }
                    strBuilder.append("balance ").append((filter.balanceSort ? "asc" : "desc"));
                }
            }
            strBuilder.append(";");
            String str = strBuilder.toString();
            PreparedStatement pstmt = conn.prepareStatement(str);
            
            int paraIndex = 1;
            if(!fnameBlank){
                pstmt.setString(paraIndex++, filter.fname + '%');
            }
            if(!lnameBlank){
                pstmt.setString(paraIndex++, filter.lname + '%');
            }
            System.out.println(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            
            
            while(rs.next()){
                users.add(new User(
                        Integer.parseInt(rs.getString("userid")),
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
    }
    
    
    
    
    
}

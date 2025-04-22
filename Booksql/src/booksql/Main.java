/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package booksql;

import java.sql.*;
/**
 *
 * @author zander
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello world! we love books books books.");
        
        String jdbcUrl = "jdbc:postgresql://localhost:5432/inclass_db";
        String username = ""; // Enter your own credentials here
        String password = ""; // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        
        Connection connection;
        
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Loaded Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Made connection");
        } catch(Exception e){
            System.out.println("Issue connecting to the postgresql db.");
        }
        // TODO code application logic here
    }
    
}

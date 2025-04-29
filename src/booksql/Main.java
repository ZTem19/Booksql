/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package booksql;

import booksql.GUIComponents.Window;
import java.sql.*;
import booksql.forms.BookDisplay;
import javax.swing.SwingUtilities;

/**
 *
 * @author zander
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("We love books books books.\n--------------------------");
        
        String jdbcUrl = "jdbc:postgresql://localhost:5432/BookSQL";
        String username = EnvReader.getUsername();
        String password = EnvReader.getPassword();
        
        Connection connection;
        
        try 
        {
            Class.forName("org.postgresql.Driver");
            System.out.println("Loaded Driver");
            
            DatabaseAccess.init(jdbcUrl, username, password);
            System.out.println("Made connection");
            
            // Run the Swing form
            SwingUtilities.invokeLater(() -> new BookDisplay().setVisible(true));
            
        } catch(Exception e){
            System.out.println("Issue connecting to the postgresql db.");
        }
        
        Window win = new Window();
        win.setVisible(true);
        
        
    }
    
}

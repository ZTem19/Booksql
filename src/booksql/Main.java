/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package booksql;

import booksql.GUIComponents.Window;
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
        System.out.println("We love books books books.\n--------------------------");
        
        String jdbcUrl = EnvReader.getURL();
        String username = EnvReader.getUsername();
        String password = EnvReader.getPassword();
        
        Window win = new Window(jdbcUrl, username, password);
        win.setVisible(true);
        
        
    }
    
}

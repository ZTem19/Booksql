/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksql;

import java.sql.Connection;
import java.util.ArrayList;
import booksql.DataModels.Publisher;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author evans
 */
public class PublisherDAO {
    private Connection conn;
    
    public PublisherDAO(Connection conn){
        this.conn = conn;
    }
    
    public ArrayList<Publisher> getAllPublishersOrderById() throws SQLException 
    {
        ArrayList<Publisher> publishers = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM publisher Order By publisher_id");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Publisher p = new Publisher(
                rs.getInt("publisher_id"),               
                rs.getString("name"),                       
                rs.getDate("date_founded"),
                rs.getString("email"));
            publishers.add(p);
        }
        return publishers;
    }
}

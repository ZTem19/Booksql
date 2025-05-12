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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author evans
 */
public class PublisherDAO {
    private Connection conn;
    
    public PublisherDAO(Connection conn){
        this.conn = conn;
    }
    
    public void insertPublisher(Publisher publisher) throws SQLException {
    String sql = "INSERT INTO publisher (name, date_founded, email) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        // Set the parameters for the query
        stmt.setString(1, publisher.getName());
        stmt.setDate(2, (java.sql.Date) publisher.getDateFounded());
        stmt.setString(3, publisher.getEmail());

        // Execute the update
        stmt.executeUpdate();
    }
   
}
    public void deletePublisher(int publisherId) throws SQLException {
    String sql = "DELETE FROM publisher WHERE publisher_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, publisherId);
        stmt.executeUpdate();
    }
}

public Publisher getPublisherById(int publisherId) throws SQLException {
    String sql = "SELECT * FROM publisher WHERE publisher_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, publisherId);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
        return new Publisher(
            rs.getInt("publisher_id"),
            rs.getString("name"),
            rs.getDate("date_founded"),
            rs.getString("email")
        );
    }
    return null;
}

public void updatePublisher(Publisher publisher) throws SQLException {
    String sql = "UPDATE publisher SET name = ?, date_founded = ?, email = ? WHERE publisher_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, publisher.getName());
    stmt.setDate(2, (java.sql.Date) publisher.getDateFounded());
    stmt.setString(3, publisher.getEmail());
    stmt.setInt(4, publisher.getPublisherId());
    stmt.executeUpdate();
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

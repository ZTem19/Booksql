/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksql;

import booksql.DataModels.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author evans
 */
public class AuthorDAO 
{
    private Connection conn;
    
    public AuthorDAO(Connection conn){
        this.conn = conn;
    }
    
    public ArrayList<Author> getAllAuthorsOrderById() throws SQLException 
    {
        ArrayList<Author> authors = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM author Order By author_id");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Author a = new Author(
                rs.getInt("author_id"),               
                rs.getString("first_name"),                       
                rs.getString("last_name"),
                rs.getDate("birth_date"));
            authors.add(a);
        }
        return authors;
    }
    
    public Author getAuthorById(int id) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM author WHERE author_id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new Author(
                rs.getInt("author_id"),               
                rs.getString("first_name"),               
                rs.getString("last_name"),         
                rs.getDate("birth_date")                      
            );
        } 
        else 
        {
            return null;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksql;
import java.sql.*;

/**
 *
 * @author evans
 */
public class DatabaseAccess 
{
    private static Connection conn;
    private static BookDAO bookDAO;
    private static PublisherDAO publisherDAO;
    private static AuthorDAO authorDAO;
    private static UserDAO userDAO;
    private static CheckDAO checkDAO;
    private static StatsDAO statsDAO;

    public static void init(String url, String username, String password) throws SQLException {
        if (conn == null) {
            conn = DriverManager.getConnection(url, username, password);
            bookDAO = new BookDAO(conn);
            publisherDAO = new PublisherDAO(conn);
            authorDAO = new AuthorDAO(conn);
            userDAO = new UserDAO(conn);
            checkDAO = new CheckDAO(conn);
            statsDAO = new StatsDAO(conn);
        }
    }
    
    public static StatsDAO getStatsDAO(){
        return statsDAO;
    }
    
    public static UserDAO getUserDao(){
        return userDAO;
    }

    public static BookDAO getBookDAO() {
        return bookDAO;
    }
    
    public static PublisherDAO getPublisherDAO()
    {
        return publisherDAO;
    }
    
    public static AuthorDAO getAuthorDAO()
    {
        return authorDAO;
    }
    
    public static CheckDAO getCheckDAO(){
        return checkDAO;
    }

    public static Connection getConnection() {
        return conn;
    }
}

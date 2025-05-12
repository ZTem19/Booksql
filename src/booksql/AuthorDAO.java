package booksql;

import booksql.DataModels.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorDAO {
    private Connection conn;
    public AuthorDAO(Connection conn) {
        this.conn = conn;
    }

    public ArrayList<Author> getAllAuthorsOrderById() throws SQLException {
        ArrayList<Author> authors = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM author ORDER BY author_id");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Author a = new Author(
                rs.getInt("author_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getDate("birth_date")
            );
            authors.add(a);
        }

        return authors;
    }

    public Author getAuthorById(int id) throws SQLException {
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
        } else {
            return null;
        }
    }

    public void createAuthor(Author author) throws SQLException {
        String sql = "INSERT INTO author (first_name, last_name, birth_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setDate(3, new java.sql.Date(author.getBirthDate().getTime()));
            stmt.executeUpdate();
        }
    }

    public void updateAuthor(Author author) throws SQLException {
        String sql = "UPDATE author SET first_name = ?, last_name = ?, birth_date = ? WHERE author_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setDate(3, new java.sql.Date(author.getBirthDate().getTime()));
            stmt.setInt(4, author.getAuthorId());
            stmt.executeUpdate();
        }
    }

    public void deleteAuthor(int authorId) throws SQLException {
        String sql = "DELETE FROM author WHERE author_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            stmt.executeUpdate();
        }
    }
}

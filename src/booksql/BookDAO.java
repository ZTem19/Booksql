package booksql;

import booksql.DataModels.Book;
import booksql.DataModels.FullBook;
import booksql.DataModels.Author;

import java.sql.*;
import java.util.ArrayList;

public class BookDAO {
    private Connection conn;

    public BookDAO(Connection conn) {
        this.conn = conn;
    }

    public ArrayList<Book> getAllBooks() throws SQLException {
        ArrayList<Book> books = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Book b = new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("num_total"),
                rs.getInt("num_available"),
                rs.getInt("publisher_id"),
                rs.getDate("date_published")
            );
            books.add(b);
        }
        return books;
    }

    public ArrayList<FullBook> getAllFullBooks() throws SQLException {
        ArrayList<FullBook> books = new ArrayList<>();
        String query = "SELECT * FROM full_book_info_et_al";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            FullBook book = new FullBook(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("num_total"),
                rs.getInt("num_available"),
                rs.getDate("date_published"),
                rs.getString("publisher_name"),
                rs.getString("author_display")
            );
            books.add(book);
        }

        return books;
    }

    public ArrayList<FullBook> getAllFullBooksWithFilters(String title, String genre, String author, String publisher)
            throws SQLException {
        ArrayList<FullBook> books = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM full_book_info_et_al WHERE 1=1");
        ArrayList<Object> parameters = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            query.append(" AND LOWER(title) LIKE ?");
            parameters.add("%" + title.toLowerCase() + "%");
        }
        if (genre != null && !genre.isEmpty()) {
            query.append(" AND LOWER(genre) LIKE ?");
            parameters.add("%" + genre.toLowerCase() + "%");
        }
        if (author != null && !author.isEmpty()) {
            query.append(" AND LOWER(author_display) LIKE ?");
            parameters.add("%" + author.toLowerCase() + "%");
        }
        if (publisher != null && !publisher.isEmpty()) {
            query.append(" AND LOWER(publisher_name) LIKE ?");
            parameters.add("%" + publisher.toLowerCase() + "%");
        }

        PreparedStatement stmt = conn.prepareStatement(query.toString());

        for (int i = 0; i < parameters.size(); i++) {
            stmt.setObject(i + 1, parameters.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            FullBook book = new FullBook(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("num_total"),
                rs.getInt("num_available"),
                rs.getDate("date_published"),
                rs.getString("publisher_name"),
                rs.getString("author_display")
            );
            books.add(book);
        }

        return books;
    }

    public Book getBookById(int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE book_id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("num_total"),
                rs.getInt("num_available"),
                rs.getInt("publisher_id"),
                rs.getDate("date_published")
            );
        } else {
            return null;
        }
    }

    public ArrayList<Author> getAuthorsByBookId(int id) throws SQLException {
        ArrayList<Author> authors = new ArrayList<>();
        String query = "SELECT a.author_id, first_name, last_name, birth_date FROM author a " +
                       "JOIN author_book ab ON a.author_id = ab.author_id " +
                       "WHERE ab.book_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id);
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

    public String getBookTitlesByAuthorId(int authorId) throws SQLException {
        StringBuilder titles = new StringBuilder();
        String query = "SELECT b.title FROM book b " +
                       "JOIN author_book ab ON b.book_id = ab.book_id " +
                       "WHERE ab.author_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, authorId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            if (titles.length() > 0) {
                titles.append(", ");
            }
            titles.append(rs.getString("title"));
        }

        return titles.toString();
    }

    public int createBook(Book book) throws SQLException {
        String insertBookSQL = "INSERT INTO book (title, description, genre, num_total, num_available, publisher_id, date_published) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertBookSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getDescription());
            stmt.setString(3, book.getGenre());
            stmt.setInt(4, book.getNumTotal());
            stmt.setInt(5, book.getNumAvailable());
            stmt.setInt(6, book.getPublisherId());
            stmt.setDate(7, new java.sql.Date(book.getDatePublished().getTime()));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve book_id.");
            }
        }
    }

    public void updateBook(Book b) throws SQLException {
        String query = "UPDATE book SET title = ?, description = ?, genre = ?, num_total = ?, num_available = ?, publisher_id = ?, date_published = ? WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, b.getTitle());
            stmt.setString(2, b.getDescription());
            stmt.setString(3, b.getGenre());
            stmt.setInt(4, b.getNumTotal());
            stmt.setInt(5, b.getNumAvailable());
            stmt.setInt(6, b.getPublisherId());
            stmt.setDate(7, new java.sql.Date(b.getDatePublished().getTime()));
            stmt.setInt(8, b.getBookId());
            stmt.executeUpdate();
        }
    }

    public void deleteBook(int bookId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM book WHERE book_id = ?")) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public boolean isAuthorForBookInDB(String firstName, String lastName, java.util.Date birthDate, int bookId) throws SQLException {
        String query = "SELECT a.author_id FROM author a JOIN author_book ab ON a.author_id = ab.author_id " +
                       "WHERE a.first_name = ? AND a.last_name = ? AND a.birth_date = ? AND ab.book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setDate(3, new Date(birthDate.getTime()));
            stmt.setInt(4, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void addAuthorForBook(String firstName, String lastName, java.util.Date birthDate, int bookId) throws SQLException {
        String checkAuthorSQL = "SELECT author_id FROM author WHERE first_name = ? AND last_name = ? AND birth_date = ?";
        String insertAuthorSQL = "INSERT INTO author (first_name, last_name, birth_date) VALUES (?, ?, ?)";
        String insertAuthorBookSQL = "INSERT INTO author_book (author_id, book_id) VALUES (?, ?)";

        try (
            PreparedStatement checkAuthorStmt = conn.prepareStatement(checkAuthorSQL);
            PreparedStatement insertAuthorStmt = conn.prepareStatement(insertAuthorSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement insertAuthorBookStmt = conn.prepareStatement(insertAuthorBookSQL)
        ) {
            conn.setAutoCommit(false);

            checkAuthorStmt.setString(1, firstName);
            checkAuthorStmt.setString(2, lastName);
            checkAuthorStmt.setDate(3, new Date(birthDate.getTime()));
            ResultSet rs = checkAuthorStmt.executeQuery();

            int authorId;
            if (rs.next()) {
                authorId = rs.getInt("author_id");
            } else {
                insertAuthorStmt.setString(1, firstName);
                insertAuthorStmt.setString(2, lastName);
                insertAuthorStmt.setDate(3, new Date(birthDate.getTime()));
                insertAuthorStmt.executeUpdate();
                ResultSet generatedKeys = insertAuthorStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    authorId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve author_id.");
                }
            }

            insertAuthorBookStmt.setInt(1, authorId);
            insertAuthorBookStmt.setInt(2, bookId);
            insertAuthorBookStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void deleteAuthorFromBook(int authorId, int bookId) throws SQLException {
        String deleteAuthorBookSQL = "DELETE FROM author_book WHERE author_id = ? AND book_id = ?";
        String checkAuthorUseSQL = "SELECT COUNT(*) FROM author_book WHERE author_id = ?";
        String deleteAuthorSQL = "DELETE FROM author WHERE author_id = ?";

        try (
            PreparedStatement deleteAuthorBookStmt = conn.prepareStatement(deleteAuthorBookSQL);
            PreparedStatement checkAuthorUseStmt = conn.prepareStatement(checkAuthorUseSQL);
            PreparedStatement deleteAuthorStmt = conn.prepareStatement(deleteAuthorSQL)
        ) {
            conn.setAutoCommit(false);

            deleteAuthorBookStmt.setInt(1, authorId);
            deleteAuthorBookStmt.setInt(2, bookId);
            deleteAuthorBookStmt.executeUpdate();

            checkAuthorUseStmt.setInt(1, authorId);
            ResultSet rs = checkAuthorUseStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                deleteAuthorStmt.setInt(1, authorId);
                deleteAuthorStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public int getAuthorCountForBook(int bookId) throws SQLException {
        String query = "SELECT COUNT(*) AS author_count FROM author_book WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("author_count");
            return 0;
        }
    }
}

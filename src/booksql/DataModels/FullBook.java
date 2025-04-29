/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksql.DataModels;

/**
 *
 * @author evans
 */

import java.util.Date;

public class FullBook {
    private int bookId;
    private String title;
    private String description;
    private String genre;
    private int numTotal;
    private int numAvailable;
    private Date datePublished;
    private String publisherName;
    private String authors;

    // Constructor
    public FullBook(int bookId, String title, String description, String genre,
                        int numTotal, int numAvailable, Date datePublished,
                        String publisherName, String authors) {
        this.bookId = bookId;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.numTotal = numTotal;
        this.numAvailable = numAvailable;
        this.datePublished = datePublished;
        this.publisherName = publisherName;
        this.authors = authors;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getNumTotal() {
        return numTotal;
    }

    public void setNumTotal(int numTotal) {
        this.numTotal = numTotal;
    }

    public int getNumAvailable() {
        return numAvailable;
    }

    public void setNumAvailable(int numAvailable) {
        this.numAvailable = numAvailable;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
}


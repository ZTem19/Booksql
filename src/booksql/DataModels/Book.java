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

public class Book {
    private int bookId;
    private String title;
    private String description;
    private String genre;
    private int numTotal;
    private int numAvailable;
    private int publisherId;
    private Date datePublished;

    // Constructor
    public Book(int bookId, String title, String description, String genre, 
                int numTotal, int numAvailable, int publisherId, Date datePublished) {
        this.bookId = bookId;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.numTotal = numTotal;
        this.numAvailable = numAvailable;
        this.publisherId = publisherId;
        this.datePublished = datePublished;
    }
    
    public Book() {}

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
    
    public int getNumAvailable(){
        return numAvailable;
    }

    public void setNumTotal(int numTotal) {
        this.numTotal = numTotal;
    }
    
    public void setNumAvailable(int numAvailable){
        this.numAvailable = numAvailable;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", totalCopies=" + numTotal +
                ", availableCopies=" + numAvailable +
                ", publisherId=" + publisherId +
                ", datePublished=" + datePublished +
                '}';
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksql.DataModels;
import java.util.Date;

/**
 *
 * @author evans
 */
public class Publisher 
{
    private int publisherId;
    private String name;
    private Date dateFounded;
    private String email;

    public Publisher(int publisherId, String name, Date dateFounded, String email) {
        this.publisherId = publisherId;
        this.name = name;
        this.dateFounded = dateFounded;
        this.email = email;
    }

    // No-arg constructor
    public Publisher() {}

    // Getters and Setters
    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateFounded() {
        return dateFounded;
    }

    public void setDateFounded(Date dateFounded) {
        this.dateFounded = dateFounded;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // toString
    @Override
    public String toString() {
        return "Publisher{" +
                "publisherId=" + publisherId +
                ", name='" + name + '\'' +
                ", dateFounded=" + dateFounded +
                ", email='" + email + '\'' +
                '}';
    }
}

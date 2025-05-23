/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package booksql.GUIComponents;

import booksql.DatabaseAccess;
import booksql.GUIComponents.Navbar.View;
import booksql.GUIComponents.Views.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 *
 * @author zander
 */
public class Window extends javax.swing.JFrame implements NavbarListener{
    
    private String dbURL, dbUsername, dbPassword;

    private Navbar nav;
    private JPanel currentView;
    HashMap<View, JPanel> views;
    /**
     * Creates new form Window
     */
    public Window(String url, String name, String password) {
        initComponents();
        
        this.dbURL = url;
        this.dbUsername = name;
        this.dbPassword = password;
        if(initDB()){               //Only addd navbar and other views if db connection is ok
            this.nav = new Navbar();
            this.add(this.nav, BorderLayout.WEST);
            this.nav.addListener(this);


            this.views = new HashMap<>(7);
            initViews();

            changeVeiw(View.HOME);
        }
    }
    
    private void initViews(){
        this.views.put(View.HOME, new HomeView());
        this.views.put(View.CHECKIN, new CheckInView());
        this.views.put(View.CHECKOUT, new CheckOutView());
        this.views.put(View.USERLOOKUP, new UserLookUpView());
        this.views.put(View.BOOKSEARCH, new BookSearchView());
        this.views.put(View.AUTHORSEARCH, new AuthorSearchView());
        this.views.put(View.PUBLISHERSEARCH, new PublisherSearchView());
        this.views.put(View.STATS, new StatsView());
    }
    
    @Override
    public void onNavBtnClicked(View v) {
        changeVeiw(v);
    }
    
    
    private void changeVeiw(View v){
        JPanel newView = this.views.get(v);
        if(newView != null){
            if(this.currentView != null){
                this.remove(currentView);
            }
            this.add(newView, BorderLayout.CENTER);
            this.currentView = newView;
        }
        else{
            System.out.println("Unable to find view: " + v.name());
        }
        
        revalidate();
        repaint();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Booksql");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1200, 800));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean initDB() {
        try 
        {
            Class.forName("org.postgresql.Driver");
            System.out.println("Loaded Driver");
            
            DatabaseAccess.init(this.dbURL, this.dbUsername, this.dbPassword);
            System.out.println("Made connection");
            
            return true;
            
        } catch(Exception e){
            System.out.println("Issue connecting to the postgresql db.");
            e.printStackTrace();
            this.add(new DBError(this.dbURL, this.dbUsername, this.dbPassword), BorderLayout.CENTER);
            return false;
        }
        
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

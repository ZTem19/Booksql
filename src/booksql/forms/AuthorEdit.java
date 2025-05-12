package booksql.forms;

import booksql.AuthorDAO;
import booksql.DataModels.Author;
import booksql.DatabaseAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.SQLException;

public class AuthorEdit extends JDialog {

    private int authorId;
    private Author author;

    private JTextField firstNameText;
    private JTextField lastNameText;
    private JTextField birthDateText;
    private JButton submitButton;

    public AuthorEdit(JFrame parent, boolean modal, int authorId) {
        super(parent, modal);
        this.authorId = authorId;
        setTitle("Edit Author");
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        loadAuthor();
        populateFields();
        pack();
    }

    private void initComponents() {
        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        JLabel birthDateLabel = new JLabel("Birth Date (yyyy-mm-dd):");

        firstNameText = new JTextField(20);
        lastNameText = new JTextField(20);
        birthDateText = new JTextField(10);

        submitButton = new JButton("Submit Changes");
        submitButton.addActionListener(this::handleSubmit);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(firstNameText, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(lastNameText, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(birthDateLabel, gbc);
        gbc.gridx = 1;
        panel.add(birthDateText, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        setContentPane(panel);
    }

    private void loadAuthor() {
        try {
            AuthorDAO authorDAO = DatabaseAccess.getAuthorDAO();
            author = authorDAO.getAuthorById(authorId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load author: " + e.getMessage());
            dispose();
        }
    }

    private void populateFields() {
        if (author != null) {
            firstNameText.setText(author.getFirstName());
            lastNameText.setText(author.getLastName());
            birthDateText.setText(author.getBirthDate().toString());
        }
    }

    private void handleSubmit(ActionEvent evt) {
        String firstName = firstNameText.getText().trim();
        String lastName = lastNameText.getText().trim();
        String birthDateStr = birthDateText.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || birthDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        Date birthDate;
        try {
            birthDate = Date.valueOf(birthDateStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-mm-dd.");
            return;
        }

        Author updatedAuthor = new Author(authorId, firstName, lastName, birthDate);

        try {
            AuthorDAO authorDAO = DatabaseAccess.getAuthorDAO();
            authorDAO.updateAuthor(updatedAuthor);
            JOptionPane.showMessageDialog(this, "Author updated successfully.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update author: " + e.getMessage());
        }
    }
}

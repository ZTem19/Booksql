package booksql.GUIComponents.Views;

import booksql.AuthorDAO;
import booksql.DataModels.Author;
import booksql.DatabaseAccess;
import booksql.forms.AuthorAdd;
import booksql.forms.AuthorEdit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorSearchView extends JPanel {

    private DefaultTableModel tableModel;

    public AuthorSearchView() {
        initComponents();
        configureTable();
        setupSearchPlaceholders();
        setupLiveSearchListeners();
        setupButtonListeners();
        loadAuthors();
    }

    private void configureTable() {
        tableModel = new DefaultTableModel(new Object[]{"ID", "First Name", "Last Name", "Birth Date", "Edit", "Delete"}, 0);
        authorTable.setModel(tableModel);
        authorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        authorTable.setAutoCreateRowSorter(true); // Enable sorting

        TableColumnModel colModel = authorTable.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(50);   // ID
        colModel.getColumn(1).setPreferredWidth(150);  // First Name
        colModel.getColumn(2).setPreferredWidth(150);  // Last Name
        colModel.getColumn(3).setPreferredWidth(150);  // Birth Date
        colModel.getColumn(4).setPreferredWidth(75);   // Edit
        colModel.getColumn(5).setPreferredWidth(75);   // Delete

        authorTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        authorTable.getColumn("Edit").setCellEditor(new EditButtonEditor(new JCheckBox()));
        authorTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete", Color.RED, Color.WHITE));
        authorTable.getColumn("Delete").setCellEditor(new DeleteButtonEditor(new JCheckBox()));
    }

    private void setupSearchPlaceholders() {
        addPlaceholder(firstNameSearchText, "First Name");
        addPlaceholder(lastNameSearchText, "Last Name");
        addPlaceholder(birthDateSearchText, "Birth Date");
    }

    private void setupLiveSearchListeners() {
        DocumentListener searchListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadAuthors(); }
            public void removeUpdate(DocumentEvent e) { loadAuthors(); }
            public void changedUpdate(DocumentEvent e) {}
        };
        firstNameSearchText.getDocument().addDocumentListener(searchListener);
        lastNameSearchText.getDocument().addDocumentListener(searchListener);
        birthDateSearchText.getDocument().addDocumentListener(searchListener);
    }

    private void setupButtonListeners() {
        addAuthor.addActionListener(e -> {
            AuthorAdd dialog = new AuthorAdd((JFrame) SwingUtilities.getWindowAncestor(this), true);
            dialog.setVisible(true);
            loadAuthors();
        });
        searchButton.addActionListener(e -> loadAuthors());
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void loadAuthors() {
        try {
            AuthorDAO dao = DatabaseAccess.getAuthorDAO();
            ArrayList<Author> authors = dao.getAllAuthorsOrderById();

            String firstName = sanitizeSearch(firstNameSearchText, "First Name");
            String lastName = sanitizeSearch(lastNameSearchText, "Last Name");
            String birthDate = sanitizeSearch(birthDateSearchText, "Birth Date");

            tableModel.setRowCount(0);
            for (Author a : authors) {
                if (a.getFirstName().toLowerCase().contains(firstName)
                        && a.getLastName().toLowerCase().contains(lastName)
                        && a.getBirthDate().toString().toLowerCase().contains(birthDate)) {
                    tableModel.addRow(new Object[]{
                            a.getAuthorId(),
                            a.getFirstName(),
                            a.getLastName(),
                            a.getBirthDate().toString(),
                            "Edit",
                            "Delete"
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading authors: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String sanitizeSearch(JTextField field, String placeholder) {
        String text = field.getText().trim().toLowerCase();
        return text.equals(placeholder.toLowerCase()) ? "" : text;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String label) {
            this(label, UIManager.getColor("Button.background"), UIManager.getColor("Button.foreground"));
        }

        public ButtonRenderer(String label, Color bg, Color fg) {
            setText(label);
            setOpaque(true);
            setBackground(bg);
            setForeground(fg);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class EditButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton("Edit");
        private int selectedRow;

        public EditButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            int authorId = (int) authorTable.getValueAt(selectedRow, 0);
            AuthorEdit dialog = new AuthorEdit((JFrame) SwingUtilities.getWindowAncestor(button), true, authorId);
            dialog.setVisible(true);
            loadAuthors();
            return "Edit";
        }
    }

    class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton("Delete");
        private int selectedRow;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(Color.RED);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            int authorId = (int) authorTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(button, "Delete author ID " + authorId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    AuthorDAO dao = DatabaseAccess.getAuthorDAO();
                    dao.deleteAuthor(authorId);
                    JOptionPane.showMessageDialog(button, "Author deleted.");
                    loadAuthors();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(button, "Error deleting author: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
            return "Delete";
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        authorTable = new javax.swing.JTable();
        firstNameSearchText = new javax.swing.JTextField();
        lastNameSearchText = new javax.swing.JTextField();
        birthDateSearchText = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        addAuthor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(52, 81, 128));

        authorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(authorTable);

        firstNameSearchText.setText("First Name");
        firstNameSearchText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameSearchTextActionPerformed(evt);
            }
        });

        lastNameSearchText.setText("Last Name");

        birthDateSearchText.setText("Birth Date");

        searchButton.setText("Search");

        jLabel1.setText("Enter Your Search Options:");

        jLabel2.setText("Add an Author:");

        addAuthor.setText("Add Author");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("AUTHOR SEARCH");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(38, 38, 38))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(firstNameSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lastNameSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(birthDateSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(searchButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addAuthor)
                        .addGap(26, 26, 26))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstNameSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastNameSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(birthDateSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton)
                    .addComponent(addAuthor))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void firstNameSearchTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameSearchTextActionPerformed
    }//GEN-LAST:event_firstNameSearchTextActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAuthor;
    private javax.swing.JTable authorTable;
    private javax.swing.JTextField birthDateSearchText;
    private javax.swing.JTextField firstNameSearchText;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField lastNameSearchText;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables
}
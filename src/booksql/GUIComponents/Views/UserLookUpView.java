/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package booksql.GUIComponents.Views;

import booksql.DatabaseAccess;
import booksql.UserDAO;
import booksql.UserDAO.Filter;
import booksql.UserDAO.User;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author zander
 */
public class UserLookUpView extends javax.swing.JPanel {

    private DefaultTableModel tableModel;
    private JTableHeader tableHeader;
    private UserDAO userDAO;
    private Filter filter;
    private HashMap<Integer, String> colNames = new HashMap(Map.ofEntries(
            Map.entry(0, "ID"),
            Map.entry(1, "First Name"),
            Map.entry(2, "Middle Initial"),
            Map.entry(3, "Last Name"),
            Map.entry(4, "Number of Books Checked Out"),
            Map.entry(5, "Balance"),
            Map.entry(6, "Delete")
    ));
    
    
    /**
     * Creates new form UserLookUpView
     */
    public UserLookUpView() {
        initComponents();
        this.userDAO = DatabaseAccess.getUserDao();
        this.filter = new Filter();
        
        tableModel = new DefaultTableModel();
        
        userTable.setModel(tableModel);
        userTable.setRowHeight(25);
        userTable.setMaximumSize(new Dimension(2000, 2000));
        tableModel.addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                // Get the row and column index of the edited cell
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if(row == -1 || column == -1 || column == 6){
                    return;
                }
                
                if(column == 0 || column == 4 ){
                    JOptionPane.showMessageDialog(null, "You cannot edit this column!");
                    loadUsers();
                    return;
                }
                
                String columnName = tableModel.getColumnName(column);
                Object newValue = tableModel.getValueAt(row, column);
                User u = null;
                try{
                    u = new User(
                    (int) tableModel.getValueAt(row, 0),
                    (String)tableModel.getValueAt(row, 1),
                    (char)tableModel.getValueAt(row, 2),
                    (String)tableModel.getValueAt(row, 3),
                    (int)tableModel.getValueAt(row, 4),
                    new BigDecimal(tableModel.getValueAt(row, 5).toString().trim().replaceAll("[,]", ""))
                );
                } catch(NumberFormatException nfe){
                    JOptionPane.showMessageDialog(null, "Invalid value for balance!");
                    loadUsers();
                    return;
                }
                
                
                handleEdit(u);
                
                System.out.println("Cell edited at row " + row + ", column '" + columnName + "' with new value: " + newValue);
            }
        });
        
        this.setIdentifiers();
        
        this.tableHeader = userTable.getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int col = userTable.columnAtPoint(me.getPoint());
                headerSort(col);
            }
        });
        
        
        loadUsers();
    }
    
    private void headerSort(int i){
        // Change filter based on current states
        switch(i){
            case 0:
                if(this.filter.idSort == null){
                    this.filter.idSort = true;
                }else if(this.filter.idSort == true){
                    this.filter.idSort = false;
                }else {
                    this.filter.idSort = null;
                }
                break;
                
            case 4:
                if(this.filter.numBooksSort == null){
                    this.filter.numBooksSort = true;
                }else if(this.filter.numBooksSort == true){
                    this.filter.numBooksSort = false;
                }else {
                    this.filter.numBooksSort = null;
                }
                break;
            
            case 5:
                if(this.filter.balanceSort == null){
                    this.filter.balanceSort = true;
                }else if(this.filter.balanceSort == true){
                    this.filter.balanceSort = false;
                }else {
                    this.filter.balanceSort = null;
                }
                break;
            default:
        }
        setIdentifiers();
        loadUsers();
    }
    
    
    private void loadUsers(){
        this.clearUsers();
        
        ArrayList<User> users= this.userDAO.getBooksWithFilter(this.filter);
        for(User u : users){ 
            this.tableModel.addRow(new Object[] {
                u.id,
                u.fname,
                u.middleInitial,
                u.lname,
                u.numOfBooks,
                u.balance,
                null
            });
        }
    }
    
    private void clearUsers(){
        this.tableModel.setRowCount(0);
        this.revalidate();
    }
    
    private void setIdentifiers(){
        if(this.filter.idSort != null){
            colNames.remove(0);
            colNames.put(0, "ID".concat(filter.idSort ? " ▼" : " ▲"));
        }else{
            colNames.remove(0);
            colNames.put(0, "ID");
        }
        
        if(this.filter.numBooksSort != null){
            colNames.remove(4);
            colNames.put(4, "Number of Books Checked Out".concat(filter.numBooksSort ? " ▼" : " ▲"));
        }else{
            colNames.remove(4);
            colNames.put(4, "Number of Books Checked Out");
        }
                
        if(this.filter.balanceSort != null){
            colNames.remove(5);
            colNames.put(5, "Balance".concat(filter.balanceSort ? " ▼" : " ▲"));
        }else{
            colNames.remove(5);
            colNames.put(5, "Balance");
        }
        
        Object[] names = new Object[colNames.size()];
        for(int i = 0; i < names.length; i++){
            names[i] = colNames.get(i);
        }
        this.tableModel.setColumnIdentifiers(names);
        
        TableColumnModel columnModel = userTable.getColumnModel();
        this.userTable.setPreferredSize(new Dimension(1000, 600));
        columnModel.getColumn(0).setPreferredWidth(75);   
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(100);  
        columnModel.getColumn(3).setPreferredWidth(200);  
        columnModel.getColumn(4).setPreferredWidth(225);  
        columnModel.getColumn(5).setPreferredWidth(150);
        columnModel.getColumn(6).setPreferredWidth(100);
        
        userTable.getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        userTable.getColumn("Delete").setCellEditor(new DeleteButtonEditor(new JCheckBox()));
    }
    
    private void handleEdit(User u){
        this.userDAO.editUser(u);
        loadUsers();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        usersTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        addUserBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        firstNameInput = new javax.swing.JTextField();
        lastNameInput = new javax.swing.JTextField();
        clearFilterBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        newUserFNameInput = new javax.swing.JTextField();
        newUserLNameInput = new javax.swing.JTextField();
        newUserErrorLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        newUserMiddleInitialInput = new javax.swing.JTextField();

        setBackground(new java.awt.Color(52, 81, 128));
        setPreferredSize(new java.awt.Dimension(1000, 1000));

        usersTitle.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        usersTitle.setForeground(new java.awt.Color(255, 255, 255));
        usersTitle.setText("Users");

        userTable.setModel(new javax.swing.table.DefaultTableModel(
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
        userTable.setFocusTraversalPolicyProvider(true);
        userTable.setMaximumSize(new java.awt.Dimension(2147483647, 800));
        userTable.setPreferredSize(new java.awt.Dimension(800, 800));
        userTable.setRowMargin(5);
        jScrollPane1.setViewportView(userTable);

        addUserBtn.setBackground(new java.awt.Color(51, 51, 51));
        addUserBtn.setForeground(new java.awt.Color(255, 255, 255));
        addUserBtn.setText("Add User");
        addUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserBtnActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Search by First Name");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Search By Last Name");

        firstNameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameInputActionPerformed(evt);
            }
        });
        firstNameInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                firstNameInputKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                firstNameInputKeyTyped(evt);
            }
        });

        lastNameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastNameInputActionPerformed(evt);
            }
        });
        lastNameInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lastNameInputKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lastNameInputKeyReleased(evt);
            }
        });

        clearFilterBtn.setBackground(new java.awt.Color(51, 51, 51));
        clearFilterBtn.setForeground(new java.awt.Color(255, 255, 255));
        clearFilterBtn.setText("Clear Filters");
        clearFilterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFilterBtnActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("First Name");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Last Name");

        newUserFNameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUserFNameInputActionPerformed(evt);
            }
        });

        newUserLNameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUserLNameInputActionPerformed(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Middle Initial");

        newUserMiddleInitialInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUserMiddleInitialInputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(usersTitle)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(firstNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lastNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(clearFilterBtn))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 938, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(newUserFNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(newUserLNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(newUserMiddleInitialInput, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(addUserBtn)))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(428, 428, 428)
                        .addComponent(newUserErrorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(usersTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(firstNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(lastNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearFilterBtn))
                .addGap(47, 47, 47)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newUserFNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newUserLNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newUserMiddleInitialInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addUserBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 235, Short.MAX_VALUE)
                .addComponent(newUserErrorLabel)
                .addGap(40, 40, 40))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserBtnActionPerformed
        String fname = this.newUserFNameInput.getText().trim();
        String lname = this.newUserLNameInput.getText().trim();
        
        String mInitialStr = this.newUserMiddleInitialInput.getText().trim();
        if(mInitialStr.isBlank() || mInitialStr.isEmpty()){
            this.newUserErrorLabel.setText("Middle Inital cannot be empty");
            this.newUserErrorLabel.setForeground(Color.red);
            return;
        }
        
        char mInitial = this.newUserMiddleInitialInput.getText().trim().charAt(0);
        
        if(fname.isBlank() || lname.isBlank()){
            this.newUserErrorLabel.setText("No part of name can be empty.");
            this.newUserErrorLabel.setForeground(Color.red);
            return;
        }
        
        this.newUserErrorLabel.setText("");
        this.newUserFNameInput.setText("");
        this.newUserLNameInput.setText("");
        this.newUserMiddleInitialInput.setText("");
        
        this.userDAO.addUser(new User(
                0,
                fname, 
                mInitial,
                lname,
                0,
                new BigDecimal(0)
        ));
        loadUsers();
        
    }//GEN-LAST:event_addUserBtnActionPerformed

    private void firstNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameInputActionPerformed
        
    }//GEN-LAST:event_firstNameInputActionPerformed

    private void lastNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameInputActionPerformed
        
    }//GEN-LAST:event_lastNameInputActionPerformed

    private void clearFilterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFilterBtnActionPerformed
        this.firstNameInput.setText("");
        this.lastNameInput.setText("");
        this.filter = new Filter();
        this.loadUsers();
        this.setIdentifiers();
    }//GEN-LAST:event_clearFilterBtnActionPerformed

    private void firstNameInputKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameInputKeyTyped
        
    }//GEN-LAST:event_firstNameInputKeyTyped

    private void firstNameInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameInputKeyReleased
        this.filter.fname = this.firstNameInput.getText();
        loadUsers();
    }//GEN-LAST:event_firstNameInputKeyReleased

    private void lastNameInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameInputKeyPressed
        
    }//GEN-LAST:event_lastNameInputKeyPressed

    private void lastNameInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameInputKeyReleased
        this.filter.lname = this.lastNameInput.getText();
        loadUsers();
    }//GEN-LAST:event_lastNameInputKeyReleased

    private void newUserFNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newUserFNameInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newUserFNameInputActionPerformed

    private void newUserLNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newUserLNameInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newUserLNameInputActionPerformed

    private void newUserMiddleInitialInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newUserMiddleInitialInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newUserMiddleInitialInputActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUserBtn;
    private javax.swing.JButton clearFilterBtn;
    private javax.swing.JTextField firstNameInput;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastNameInput;
    private javax.swing.JLabel newUserErrorLabel;
    private javax.swing.JTextField newUserFNameInput;
    private javax.swing.JTextField newUserLNameInput;
    private javax.swing.JTextField newUserMiddleInitialInput;
    private javax.swing.JTable userTable;
    private javax.swing.JLabel usersTitle;
    // End of variables declaration//GEN-END:variables

    
    class DeleteButtonRenderer extends JButton implements TableCellRenderer 
    {
        public DeleteButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(Color.RED);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Delete");
            return this;
        }
    }
    
    class DeleteButtonEditor extends DefaultCellEditor 
    {
        private JButton button;
        private boolean clicked;
        private int selectedRow;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Delete");
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(Color.RED);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped(); // Stops editing
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
            clicked = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // All code which is called when button is clicked
                int userId = (int) userTable.getValueAt(selectedRow, 0);
                int response = JOptionPane.showConfirmDialog(button,
                    "Are you sure you want to delete user with ID: " + userId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        UserDAO userDao = DatabaseAccess.getUserDao();
                        userDao.deleteUser(userId);
                        JOptionPane.showMessageDialog(button, "User deleted.");
                        
                        javax.swing.SwingUtilities.invokeLater(() -> loadUsers());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(button, "Error deleting user: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}

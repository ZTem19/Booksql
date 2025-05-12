/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package booksql.GUIComponents.Views;
import booksql.DatabaseAccess;
import booksql.PublisherDAO;
import booksql.DataModels.Publisher;
import booksql.forms.publisherAdd;
import booksql.forms.PublisherEdit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author braxton
 */
public class PublisherSearchView extends javax.swing.JPanel {
    private DefaultTableModel tableModel;

    
   
    public PublisherSearchView() {
        initComponents();
        tableModel = new DefaultTableModel();
        
        

        tableModel.setColumnIdentifiers(
    new String[] {"publisher_id", "name", "date_founded", "email", "Edit", "Delete"});
        publisherTable.setModel(tableModel);
        publisherTable.getColumn("Edit").setCellRenderer(new PublisherSearchView.EditButtonRenderer());
        publisherTable.getColumn("Edit").setCellEditor(new PublisherSearchView.EditButtonEditor(new JCheckBox()));
        publisherTable.getColumn("Delete").setCellRenderer(new PublisherSearchView.DeleteButtonRenderer());
        publisherTable.getColumn("Delete").setCellEditor(new PublisherSearchView.DeleteButtonEditor(new JCheckBox()));
        
        TableColumnModel columnModel = publisherTable.getColumnModel();
        
        columnModel.getColumn(0).setPreferredWidth(60);   
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(200);  
        columnModel.getColumn(3).setPreferredWidth(100);  
        columnModel.getColumn(4).setPreferredWidth(300);  
        columnModel.getColumn(5).setPreferredWidth(100);
        
        addPublisher.addActionListener(e -> {
            publisherAdd addFrame = new publisherAdd(this); // assuming this constructor is valid
            addFrame.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
            addFrame.setVisible(true);
        });


        loadPublishers();
    }
    public void loadPublishers() {
        try {
            PublisherDAO publisherDAO = DatabaseAccess.getPublisherDAO();
            ArrayList<Publisher> publishers = publisherDAO.getAllPublishersOrderById();
            
            tableModel.setRowCount(0);
            
            for (Publisher p : publishers) {
                tableModel.addRow(new Object[]{
                    p.getPublisherId(),
                    p.getName(),
                    p.getDateFounded(),
                    p.getEmail()
                });
            }
        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading publishers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    class EditButtonRenderer extends JButton implements TableCellRenderer {
        public EditButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Edit" : value.toString());
            return this;
        }
    }
    
    class EditButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;

        public EditButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
            label = (value == null) ? "Edit" : value.toString();
            button.setText(label);
            clicked = true;
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                // All code which is called when button is clicked
                int publisherId = (int) publisherTable.getValueAt(selectedRow, 0);
                
                // Trigger edit form
                PublisherEdit dialog = new PublisherEdit(
                    (JFrame) SwingUtilities.getWindowAncestor(button),                                          
                    publisherId
                );
                dialog.setVisible(true);
                loadPublishers();
            }
            clicked = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            try
            {
               super.fireEditingStopped();
            }
            catch (Exception e)
            {
            }
        }
    }
    
    class DeleteButtonRenderer extends JButton implements TableCellRenderer 
    {
        public DeleteButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(Color.RED);
        }

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

        public Object getCellEditorValue() {
            if (clicked) {
                // All code which is called when button is clicked
                int publisherId = (int) publisherTable.getValueAt(selectedRow, 0);
                int response = JOptionPane.showConfirmDialog(button,
                    "Are you sure you want to delete Publisher ID " + publisherId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PublisherDAO publisherDAO = DatabaseAccess.getPublisherDAO();
                        publisherDAO.deletePublisher(publisherId);
                        JOptionPane.showMessageDialog(button, "Publisher deleted.");
                        loadPublishers();
                        
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(button, "Error deleting publisher: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                selectedRow = 0;
            }
            clicked = false;
            return "Delete";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            try
            {
                super.fireEditingStopped();
            }
            catch (Exception e)
            {
            }
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

        addPublisher = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        publisherTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(52, 81, 128));

        addPublisher.setText("Add Publisher");

        publisherTable.setName(""); // NOI18N
        jScrollPane1.setViewportView(publisherTable);
        publisherTable.getAccessibleContext().setAccessibleParent(publisherTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(addPublisher)
                .addGap(46, 224, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(addPublisher)
                .addGap(62, 62, 62))
        );
    }// </editor-fold>//GEN-END:initComponents


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPublisher;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable publisherTable;
    // End of variables declaration//GEN-END:variables
}

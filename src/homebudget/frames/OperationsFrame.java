package homebudget.frames;

import static homebudget.HomeBudget.getOperationsCtrl;
import homebudget.views.OprtTableCellRender;
import homebudget.models.OperationsTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

public class OperationsFrame extends JDialog {
    /** номер выбранной мышью строки */
    int selectionRow = -1;
    /** родитель */
    TrsctFrame parent;
    /** тип операций */
    final int type;
    
    public OperationsFrame(Frame parent, boolean modal, int type) {
        super(parent, modal);
        this.parent = (TrsctFrame) parent;
        this.type = type;
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth())/2, (screenSize.height - getHeight())/2);
        getContentPane().setBackground(Color.white);
        typeOprtTypeComboBox.setBackground(Color.white);
        // иконки
        addBtn.setIcon( new ImageIcon(getClass().getResource("images/addIcon.png")) );
        delBtn.setIcon( new ImageIcon(getClass().getResource("images/delIcon.png")) );
        // таблица
        table.setModel( new OperationsTableModel(getOperationsCtrl().getData()) );
        for(int i=0; i<2; i++) 
            table.getColumnModel().getColumn(i).setCellRenderer( new OprtTableCellRender() );
        table.getTableHeader().setFont(new Font("Times New Roman", 1, 14));
        tablePane.getViewport().setBackground(Color.white);      
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        addBtn = new javax.swing.JButton();
        delBtn = new javax.swing.JButton();
        inputNewOprtField = new javax.swing.JTextField();
        typeOprtTypeComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Операции");

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        tablePane.setViewportView(table);

        addBtn.setBackground(new java.awt.Color(255, 255, 255));
        addBtn.setEnabled(false);
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        delBtn.setBackground(new java.awt.Color(255, 255, 255));
        delBtn.setEnabled(false);
        delBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delBtnActionPerformed(evt);
            }
        });

        inputNewOprtField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        inputNewOprtField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inputNewOprtFieldMouseClicked(evt);
            }
        });
        inputNewOprtField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputNewOprtFieldKeyReleased(evt);
            }
        });

        typeOprtTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Доход", "Расход", "Доход и расход" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputNewOprtField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeOprtTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tablePane, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(inputNewOprtField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(delBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(typeOprtTypeComboBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        //  выделение строки
        if(selectionRow == table.getSelectedRow()){
            selectionRow=-1;
            table.getSelectionModel().clearSelection();
            delBtn.setEnabled(false);
        }
        else{
            selectionRow = table.getSelectedRow();
            delBtn.setEnabled(true);
        }
    }//GEN-LAST:event_tableMouseClicked

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        String name = inputNewOprtField.getText();
        // проверяет наличие значения в модели
        if(((OperationsTableModel) table.getModel()).isValue(name)){
            homebudget.views.JCompErrorAnimation.execute(inputNewOprtField);
            return;
        }
        
        int type = typeOprtTypeComboBox.getSelectedIndex();
        getOperationsCtrl().add(name, type);
        table.setModel( new OperationsTableModel(getOperationsCtrl().getData()) );
        for(int i=0; i<2; i++) 
            table.getColumnModel().getColumn(i).setCellRenderer( new OprtTableCellRender() );
        inputNewOprtField.setText("");
        addBtn.setEnabled(false);
        parent.updateComboBox(type);
    }//GEN-LAST:event_addBtnActionPerformed

    private void delBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBtnActionPerformed
        String name = (String)(table.getValueAt(table.getSelectedRow(), 0));
        getOperationsCtrl().remove(name);
        table.setModel( new OperationsTableModel(getOperationsCtrl().getData()) );
        for(int i=0; i<2; i++) 
            table.getColumnModel().getColumn(i).setCellRenderer( new OprtTableCellRender() );
        delBtn.setEnabled(false);
        parent.updateComboBox(type);
    }//GEN-LAST:event_delBtnActionPerformed

    private void inputNewOprtFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputNewOprtFieldKeyReleased
        String text = inputNewOprtField.getText();
        // Ограничение длины текста 10 символами
        if(text.toCharArray().length > 10){
            text = text.substring(0, 10);
            inputNewOprtField.setText(text);
        }
        if( text.equals("")) addBtn.setEnabled(false); 
        else addBtn.setEnabled(true);
    }//GEN-LAST:event_inputNewOprtFieldKeyReleased

    private void inputNewOprtFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputNewOprtFieldMouseClicked
        inputNewOprtField.setText("");
        table.clearSelection();
        delBtn.setEnabled(false);
    }//GEN-LAST:event_inputNewOprtFieldMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton delBtn;
    private javax.swing.JTextField inputNewOprtField;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tablePane;
    private javax.swing.JComboBox<String> typeOprtTypeComboBox;
    // End of variables declaration//GEN-END:variables
}

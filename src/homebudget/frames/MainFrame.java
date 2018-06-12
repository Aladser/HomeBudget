package homebudget.frames;

import homebudget.views.TsctTableCellRender;
import static homebudget.HomeBudget.getTransationsCtrl;
import homebudget.models.TransactionsTableModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class MainFrame extends javax.swing.JFrame {

    public MainFrame() {
        // рендеринг окна
        initComponents();
        getContentPane().setBackground(Color.white);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth())/2, (screenSize.height - getHeight())/2);
        tableScrollPane.setOpaque(true);
        tableScrollPane.getViewport().setBackground(Color.white);
        
        // таблица transactions
        TransactionsTableModel model = new TransactionsTableModel(getTransationsCtrl().getData());
        table.setModel(model);
        table.getColumnModel().getColumn(0).setCellRenderer( new TsctTableCellRender() );
        table.getColumnModel().getColumn(1).setCellRenderer( new TsctTableCellRender() );
        table.setRowHeight(30);
        table.getTableHeader().setFont(new java.awt.Font("Times New Roman", 1, 14));
        table.getTableHeader().setBackground(new java.awt.Color(240,240,240));
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableScrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        opertsBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Домашний бюджет 2");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        tableScrollPane.setViewportView(table);

        opertsBtn.setBackground(new java.awt.Color(255, 255, 255));
        opertsBtn.setText("Операции");
        opertsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opertsBtnActionPerformed(evt);
            }
        });

        addBtn.setBackground(new java.awt.Color(255, 255, 255));
        addBtn.setText("Добавить");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(opertsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(opertsBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addBtn))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void opertsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opertsBtnActionPerformed
        new OperationsFrame(this, true).setVisible(true);
    }//GEN-LAST:event_opertsBtnActionPerformed

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        System.out.println("!");
    }//GEN-LAST:event_tableMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton opertsBtn;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables
}

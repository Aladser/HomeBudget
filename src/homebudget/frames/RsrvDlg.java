package homebudget.frames;

import homebudget.HomeBudget;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class RsrvDlg extends javax.swing.JDialog {
    TrsctFrame owner;

    public RsrvDlg(TrsctFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        owner = (TrsctFrame) parent;
        rsrvFld.setText(Integer.toString(HomeBudget.getReserveValue()));
        
        int x =  parent.getX() + (parent.getWidth() - getWidth())/2;
        int y =  parent.getY() + (parent.getHeight() - getHeight())/2;
        setLocation(x, y);
        getContentPane().setBackground(Color.white);
        rsrvFld.setFont(parent.DIGFONT.deriveFont(Font.PLAIN, 50));
        okBtn.setIcon( new ImageIcon(getClass().getResource("resources/images/okIcon.png")) );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rsrvFld = new javax.swing.JTextField();
        okBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Копилка");

        rsrvFld.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        rsrvFld.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rsrvFld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rsrvFldMouseClicked(evt);
            }
        });
        rsrvFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rsrvFldActionPerformed(evt);
            }
        });
        rsrvFld.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rsrvFldKeyReleased(evt);
            }
        });

        okBtn.setBackground(new java.awt.Color(255, 255, 255));
        okBtn.setEnabled(false);
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(344, 344, 344)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rsrvFld, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(75, Short.MAX_VALUE)
                .addComponent(rsrvFld, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rsrvFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rsrvFldMouseClicked
        rsrvFld.setText("");
        okBtn.setEnabled(true);
    }//GEN-LAST:event_rsrvFldMouseClicked

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        String strVal = rsrvFld.getText();
        int num = strVal.equals("") ? 0 : Integer.parseInt(strVal);
        HomeBudget.setReserveValue(num);
        owner.updateReserveFld();
        dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void rsrvFldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rsrvFldKeyReleased
        String value = rsrvFld.getText();
        // ограничение длины
        if(value.length() > 10){
            value = value.substring(0, 10);
            rsrvFld.setText(value);
            return;
        }
        
        try{Integer.parseInt(value);}
        catch(java.lang.NumberFormatException exc1){
            try{value=value.substring(0, value.length()-1);}
            catch( java.lang.StringIndexOutOfBoundsException exc2){}
        }
        rsrvFld.setText(value);
        if(value.equals(""))value = "0";
        try {
            okBtn.setEnabled(Integer.parseInt(value)<=(int)HomeBudget.TRSCTS.getBalance());
        } catch (SQLException ex) {
            Logger.getLogger(RsrvDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_rsrvFldKeyReleased

    private void rsrvFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rsrvFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rsrvFldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton okBtn;
    private javax.swing.JTextField rsrvFld;
    // End of variables declaration//GEN-END:variables
}

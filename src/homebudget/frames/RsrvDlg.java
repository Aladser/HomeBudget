package homebudget.frames;

import homebudget.HomeBudget;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class RsrvDlg extends javax.swing.JDialog {
    TrsctFrame owner;
    /** Резерв */
    int rsrvValue;
    /** Баланс */
    int balance;

    public RsrvDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        owner = (TrsctFrame) parent;
        rsrvValue = owner.launcher.getReserveValue();
        try {
            balance = (int) owner.launcher.TRSCTS.getBalance();
        } catch (SQLException ex) {
            Logger.getLogger(RsrvDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
        rsrvFld.setText(Integer.toString(rsrvValue));
        
        int x =  parent.getX() + (parent.getWidth() - getWidth())/2;
        int y =  parent.getY() + (parent.getHeight() - getHeight())/2;
        setLocation(x, y);
        getContentPane().setBackground(Color.white);
        rsrvLbl.setFont(HomeBudget.DIGFONT.deriveFont(Font.PLAIN, 30));
        rsrvFld.setFont(HomeBudget.DIGFONT.deriveFont(Font.PLAIN, 50));
        okBtn.setIcon( new ImageIcon(getClass().getResource("images/okIcon.png")) );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rsrvLbl = new javax.swing.JLabel();
        rsrvFld = new javax.swing.JTextField();
        okBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        rsrvLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rsrvLbl.setText("Копилка");

        rsrvFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rsrvFld.setText("jTextField1");
        rsrvFld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rsrvFldMouseClicked(evt);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rsrvLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rsrvFld, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(194, 194, 194)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rsrvLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rsrvFld, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rsrvFldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rsrvFldMouseClicked
        rsrvFld.setText("");
        okBtn.setEnabled(true);
    }//GEN-LAST:event_rsrvFldMouseClicked

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        int num = rsrvFld.getText().equals("") ? 0 : Integer.parseInt(rsrvFld.getText());
        owner.launcher.setReserveValue(num);
        owner.updateReserve();
        String oldStr = "reserve = " + rsrvValue;
        String newStr = "reserve = " + num;
        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get("data.txt");
        try {
            Files.write(path, new String(Files.readAllBytes(path), charset)
                    .replace(oldStr, newStr).getBytes(charset));
        } catch (IOException ex) {
            Logger.getLogger(RsrvDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
    }//GEN-LAST:event_okBtnActionPerformed

    private void rsrvFldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rsrvFldKeyReleased
        String value = rsrvFld.getText();
        try{Integer.parseInt(value);}
        catch(java.lang.NumberFormatException exc1){
            try{value=value.substring(0, value.length()-1);}
            catch( java.lang.StringIndexOutOfBoundsException exc2){}
        }
        rsrvFld.setText(value);
        if(value.equals(""))value = "0";
        okBtn.setEnabled(Integer.parseInt(value)<=balance);
    }//GEN-LAST:event_rsrvFldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton okBtn;
    private javax.swing.JTextField rsrvFld;
    private javax.swing.JLabel rsrvLbl;
    // End of variables declaration//GEN-END:variables
}

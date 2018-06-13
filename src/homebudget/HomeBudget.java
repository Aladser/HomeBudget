package homebudget;

import homebudget.frames.TrsctFrame;
import homebudget.models.DBControl;
import homebudget.controllers.OperationsTableCtrl;
import homebudget.controllers.TranscationsTableCtrl;
import java.awt.EventQueue;
import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class HomeBudget {
    public static final TranscationsTableCtrl TRSCTS = (TranscationsTableCtrl) getDB().getTable(0);
    public static final OperationsTableCtrl OPRTS = (OperationsTableCtrl) getDB().getTable(1);
    
    // проверяет наличие БД 
    private static DBControl getDB(){
        String DB_PATH = "hbdb.s3db";
        if(!new File(DB_PATH).exists()){
            JOptionPane.showMessageDialog(null, "Файл \"" + DB_PATH + "\" не найден!. Приложение будет закрыто.");
            System.exit(42);
        }
        return new DBControl(DB_PATH);
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {try {
            new TrsctFrame().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(HomeBudget.class.getName()).log(Level.SEVERE, null, ex);
            }
});        
    }
    
}

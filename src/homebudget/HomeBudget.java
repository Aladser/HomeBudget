package homebudget;

import homebudget.frames.MainFrame;
import homebudget.models.DBControl;
import homebudget.controllers.OperationsTableCtrl;
import homebudget.controllers.TranscationsTableCtrl;
import java.awt.EventQueue;
import java.io.File;
import javax.swing.JOptionPane;

public class HomeBudget {
    // проверяет наличие БД 
    private static DBControl getDB(){
        String DB_PATH = "hbdb.s3db";
        if(!new File(DB_PATH).exists()){
            JOptionPane.showMessageDialog(null, "Файл \"" + DB_PATH + "\" не найден!. Приложение будет закрыто.");
            System.exit(42);
        }
        return new DBControl(DB_PATH);
    }
    
    public static TranscationsTableCtrl getTransationsCtrl(){
        return (TranscationsTableCtrl) getDB().getTable(0);
    }
    
    public static OperationsTableCtrl getOperationsCtrl(){
        return (OperationsTableCtrl) getDB().getTable(1);
    }
     
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {new MainFrame().setVisible(true);});        
    }
    
}

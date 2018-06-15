package homebudget;

import homebudget.frames.TrsctFrame;
import homebudget.models.DBControl;
import homebudget.controllers.OperationsTableCtrl;
import homebudget.controllers.TranscationsTableCtrl;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class HomeBudget {
    public final TranscationsTableCtrl TRSCTS;
    public final OperationsTableCtrl OPRTS;
    public final Font DIGITAL_FONT;
    
    public HomeBudget(){
        TRSCTS = (TranscationsTableCtrl) getDB().getTable(0);
        OPRTS = (OperationsTableCtrl) getDB().getTable(1);
        DIGITAL_FONT = createDigitalFont();
    }
    
    // проверяет наличие БД 
    private DBControl getDB(){
        String DB_PATH = "hbdb.s3db";
        if(!new File(DB_PATH).exists()){
            JOptionPane.showMessageDialog(null, "Файл \"" + DB_PATH + "\" не найден!. Приложение будет закрыто.");
            System.exit(42);
        }
        return new DBControl(DB_PATH);
    }

    // загружает цифровой шрифт
    private Font createDigitalFont(){
        try {
            return Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("digital.ttf"));
        } catch (java.io.IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, "Файл шрифтов digital.ttf не найден!. Будет использован стандартный шрифт."
            );
            return new Font("Consolas", java.awt.Font.PLAIN, 15);
        } catch (java.awt.FontFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Не удалось установить шрифт digital.ttf!. Будет использован стандартный шрифт."
            );
            return new Font("Consolas", Font.PLAIN, 15);
        }
    }
    
    /** Обнулить часы даты
     * @param cldr
     * @return  */
    public static GregorianCalendar setHourZero(GregorianCalendar cldr){
        cldr.set(Calendar.HOUR_OF_DAY, 0);
        cldr.set(Calendar.MINUTE, 0);
        cldr.set(Calendar.SECOND, 0);
        cldr.set(Calendar.MILLISECOND, 0);
        return cldr;
    }
    
    public static void main(String[] args) {
        HomeBudget launcher = new HomeBudget();
        EventQueue.invokeLater(() -> {try {
            new TrsctFrame(launcher).setVisible(true);
            } catch (SQLException | AWTException ex) {
                Logger.getLogger(HomeBudget.class.getName()).log(Level.SEVERE, null, ex);
            }
        });        
    }
    
}

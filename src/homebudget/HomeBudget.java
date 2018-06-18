package homebudget;

import homebudget.frames.TrsctFrame;
import homebudget.models.DBControl;
import homebudget.controllers.OperationsTableCtrl;
import homebudget.controllers.TranscationsTableCtrl;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class HomeBudget {
    public final TranscationsTableCtrl TRSCTS;
    public final OperationsTableCtrl OPRTS;
    public static Font DIGITAL_FONT;
    
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
    
    /** Обнулить часы даты
     * @param cldr
     * @return  */
    public static GregorianCalendar setHourZero(GregorianCalendar cldr){
        GregorianCalendar rslt = new GregorianCalendar();
        rslt.setTime(cldr.getTime());
        rslt.set(Calendar.HOUR_OF_DAY, 0);
        rslt.set(Calendar.MINUTE, 0);
        rslt.set(Calendar.SECOND, 0);
        rslt.set(Calendar.MILLISECOND, 0);
        return rslt;
    }
    
    
    /** коррекция finalDate
     * @param cldr
     * @return  */
    public static GregorianCalendar setFinalDate(GregorianCalendar cldr){
        GregorianCalendar rslt = setHourZero(cldr);
        rslt.setTimeInMillis(rslt.getTimeInMillis()+86400000);
        return rslt;
    }
    
    /**
     * Денежный формат
     * @param src 
     * @return  
     */
    public static String formatMoney(double src){
        // округление
        int intSrc = (int)(src*100);
        src = ((double)intSrc)/100;
        // число как строка
        String strNumber = Integer.toString((int)src);
        char[] charNumber = strNumber.toCharArray();
        // если разрядность меньше 4
        if(strNumber.length() < 4) return Double.toString(src);
        String rslt = "";
        // шапка числа
        int numberHeader = strNumber.length()%3;
        int i=0;
        for(; i<numberHeader; i++) rslt+= charNumber[i];
        if(numberHeader != 0) rslt+=" ";
        // заполенние целой части числа
        while(i<strNumber.length()){
            for(int j=0; j<3; j++) rslt+=charNumber[i++];
            if(i!=strNumber.length())rslt+=" ";
        }
        // дробная часть
        rslt+=".";
        int fraction = (int) (Math.round(src*100) - (int)src*100);
        if(fraction == 0) rslt += "00";
        else if(fraction < 10) rslt += "0"+fraction;
        else rslt += fraction;
        
        return rslt;
    }
    
    // загружает цифровой шрифт
    private Font createDigitalFont(){
        try {
            return Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("digital.ttf"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Файл шрифтов digital.ttf не найден!. Будет использован стандартный шрифт."
            );
            return new Font("Consolas", java.awt.Font.PLAIN, 15);
        } catch (FontFormatException e) {
            JOptionPane.showMessageDialog(null, "Не удалось установить шрифт digital.ttf!. Будет использован стандартный шрифт."
            );
            return new Font("Consolas", Font.PLAIN, 15);
        }
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {try {
            new TrsctFrame(new HomeBudget()).setVisible(true);
            } catch (SQLException | AWTException ex) {
                Logger.getLogger(HomeBudget.class.getName()).log(Level.SEVERE, null, ex);
            }
        });        
    }
    
}

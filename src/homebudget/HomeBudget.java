package homebudget;

import homebudget.frames.TrsctFrame;
import homebudget.models.DBControl;
import homebudget.controllers.OperationsTableCtrl;
import homebudget.controllers.TranscationsTableCtrl;
import homebudget.frames.RsrvDlg;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public abstract class HomeBudget {
    public static final TranscationsTableCtrl TRSCTS = (TranscationsTableCtrl) getDB().getTable(0);
    public static final OperationsTableCtrl OPRTS = (OperationsTableCtrl) getDB().getTable(1);
    
    
    /** Возвращает резервную сумму(копилку)
     * @return  */
    public static int getReserveValue(){
        try {
            String strVal = new Scanner(new FileReader("data.rsv")).nextLine();
            strVal = strVal.substring(strVal.indexOf(" ")+3, strVal.length());
            return Integer.parseInt(strVal);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HomeBudget.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    /**  Устанавливает резервную сумму(копилку)
     * @param val */
    public static void setReserveValue(int val){
        String header = "reserve = ";
        String oldStr = header + getReserveValue();
        String newStr = header + val;
        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get("data.rsv");
        try {
            Files.write(path, new String(Files.readAllBytes(path), charset)
                    .replace(oldStr, newStr).getBytes(charset));
        } catch (IOException ex) {
            Logger.getLogger(RsrvDlg.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    // проверяет наличие БД 
    private static DBControl getDB(){
        String DB_PATH = "hbdb.s3db";
        if(!new File(DB_PATH).exists()){
            JOptionPane.showMessageDialog(
                    null, 
                    "Файл \"" + DB_PATH + "\" не найден! Приложение будет закрыто."
            );
            System.exit(42);
        }
        return new DBControl(DB_PATH);
    }
    
    /** Установить начало дня
     * @param cldr
     * @return  */
    public static GregorianCalendar setDayStart(GregorianCalendar cldr){
        GregorianCalendar rslt = new GregorianCalendar();
        rslt.setTime(cldr.getTime());
        rslt.set(Calendar.HOUR_OF_DAY, 0);
        rslt.set(Calendar.MINUTE, 0);
        rslt.set(Calendar.SECOND, 0);
        rslt.set(Calendar.MILLISECOND, 0);
        return rslt;
    }
        
    /** Установить конец дня
     * @param cldr
     * @return  */
    public static GregorianCalendar setDayFinal(GregorianCalendar cldr){
        GregorianCalendar rslt = setDayStart(cldr);
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
        String fraction = Double.toString(src);
        fraction = fraction.substring(fraction.indexOf("."), fraction.length());
        if(fraction.equals(".0")) rslt += ".00";
        else rslt += fraction;
        return rslt;
    }
    
    /**
     * Денежный формат
     * @param src 
     * @return  
     */
    public static String formatMoney(int src){
        // число как строка
        String strNumber = Integer.toString(src);
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
        return rslt;
    }

    public static void main(String[] args) throws IOException {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        // Проверяет наличие файла data.txt
        if(!new File("data.rsv.").exists()){
            JOptionPane.showMessageDialog(
                    null, 
                    "Файл data.rsv не найден! Приложение будет закрыто."
            );
            System.exit(42);
        }
        
        EventQueue.invokeLater(() -> {
            try {
                new TrsctFrame().setVisible(true);
            } catch (SQLException | AWTException ex) {
                Logger.getLogger(HomeBudget.class.getName()).log(Level.SEVERE, null, ex);
            }
        });        
    }
    
}

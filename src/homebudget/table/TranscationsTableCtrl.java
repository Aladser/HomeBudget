package homebudget.table;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

/** Контроллер таблицы БД */
public class TranscationsTableCtrl extends DBTableCtrl{
    public TranscationsTableCtrl(DBConnection db, String dbName) {
        super(db, dbName);
    }
    
    /**
     * Добавить строку
     * @param name
     * @param value
     * @param type
     */
    public void add(String name, double value, int type){
        query = "INSERT INTO "+dbName+" VALUES ('";
        query += name + "', ";
        query += value + ", ";
        query += type + ", ";
        query += formatDate() + ")";
        executeQueryNoRes(query);
    }
    
    /**
     * Получение таблицы за указанный промежуток времени
     * @param startDate
     * @param finalDate
     * @return 
     */
    public ArrayList<TransactionsTableLine> getData(long startDate, long finalDate){
        startDate = formatDate(startDate);
        finalDate = formatDate(finalDate);
        String sql = "SELECT name, SUM(value*type) value, date FROM "+dbName;
        sql += " WHERE date>"+startDate+" AND date<"+finalDate+" GROUP BY name";
        return mGetData(sql);
    }
    /**
     * Получение таблицы за указанный день
     * @param date
     * @return 
     */
    public ArrayList<TransactionsTableLine> getData(long date){
        date = formatDate(date);
        String sql = "SELECT name, SUM(value*type) value, date FROM "+dbName;
        sql += " WHERE date="+date+" GROUP BY name";
        return mGetData(sql);
    }
    /**
     * Получение таблицы
     * @return 
     */
    public ArrayList<TransactionsTableLine> getData(){
        return mGetData("SELECT name, SUM(value*type) value, date FROM "+dbName + " GROUP BY name"); 
    }
    
    public ArrayList<TransactionsTableLine> mGetData(String sql){
        resSet = executeQuery(sql);
        ArrayList<TransactionsTableLine> rslt = new ArrayList<>();
        try {
            while( resSet.next() ){
                TransactionsTableLine line = new TransactionsTableLine();
                line.name = resSet.getString("name");
                line.value = resSet.getDouble("value");
                line.date = new Date();
                line.date.setTime(resSet.getLong("date"));
                rslt.add(line);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка TranscationsTableCtrl.mGetData()");
            System.exit(42);
        }
        return rslt;        
    } 

    // обнуляет часы даты
    private long formatDate(){return mFormatDate(new GregorianCalendar());}    
    private long formatDate(long date){
        GregorianCalendar cldr = new GregorianCalendar();
        cldr.setTimeInMillis(date);
        return mFormatDate(cldr);
    }
    private long mFormatDate(GregorianCalendar cldr){
        cldr.set(GregorianCalendar.HOUR, 0);
        cldr.set(GregorianCalendar.MINUTE, 0);
        cldr.set(GregorianCalendar.SECOND, 0);
        cldr.set(GregorianCalendar.MILLISECOND, 0);
        return cldr.getTimeInMillis();
    }
}

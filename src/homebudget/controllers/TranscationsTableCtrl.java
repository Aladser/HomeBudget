package homebudget.controllers;

import homebudget.models.DBConnection;
import homebudget.models.TransactionsTableLine;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

/** Контроллер таблицы БД */
public class TranscationsTableCtrl extends DBTableCtrl{
    public static final int LAST_DAY_RECORD = 0;
    public static final int LAST_MONTH_RECORD = 1;
    public static final int FIRST_DAY_RECORD = 2;
    
    public TranscationsTableCtrl(DBConnection db, String dbName){super(db, dbName);}
    
    /**
     * получить Date ключевых дат. 
     * Дата последней записи = 0;
     * дате 1 числа последнего месяца = 1;
     * Дата первой записи = 2;
     * @param type
     * @return 
     * @throws java.sql.SQLException 
     */
    public Date getDate(int type) throws SQLException{
        switch(type){
            case 0:
                query = "SELECT MAX(date) date FROM "+ dbName;
                break;
            case 1:
                query = "SELECT MAX(date) date FROM "+ dbName;
                break;
            default:
                query = "SELECT MIN(date) date FROM "+ dbName;
        }
        Date date = new Date( executeQuery(query).getLong("date") );
        if(type==1){
            GregorianCalendar cldr = new GregorianCalendar();
            cldr.setTime(date);
            cldr.set(Calendar.DAY_OF_MONTH, 1);
            date = cldr.getTime();
        }
        return date;
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
        query += formatDate().getTime() + ")";
        executeQueryNoRes(query);
    }
    
    public void clearTable(){ executeQueryNoRes("DELETE FROM " + dbName);}
    
    /**
     * Получение таблицы за указанный промежуток времени
     * @param startDate
     * @param finalDate
     * @return 
     */
    public ArrayList<TransactionsTableLine> getData(Date startDate, Date finalDate){
        startDate = formatDate(startDate);
        finalDate = formatDate(finalDate);
        String sql = "SELECT name, SUM(value*type) value, date FROM "+dbName+" WHERE date";
        sql += startDate.getTime()==finalDate.getTime() ? 
                "="+startDate.getTime() : 
                ">"+startDate.getTime()+" AND date<"+finalDate.getTime();
        sql += " GROUP BY name ORDER BY value DESC";
        resSet = executeQuery(sql);
        ArrayList<TransactionsTableLine> rslt = new ArrayList<>();
        try {
            while( resSet.next() ){
                TransactionsTableLine line = new TransactionsTableLine();
                line.name = resSet.getString("name");
                line.value = resSet.getDouble("value");
                rslt.add(line);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка TranscationsTableCtrl.mGetData()");
            System.exit(42);
        }
        return rslt;        
    } 

    // обнуляет часы даты
    private Date formatDate(){return mFormatDate(new GregorianCalendar());}    
    private Date formatDate(Date date){
        GregorianCalendar cldr = new GregorianCalendar();
        cldr.setTime(date);
        return mFormatDate(cldr);
    }
    private Date mFormatDate(GregorianCalendar cldr){
        cldr.set(GregorianCalendar.HOUR, 0);
        cldr.set(GregorianCalendar.MINUTE, 0);
        cldr.set(GregorianCalendar.SECOND, 0);
        cldr.set(GregorianCalendar.MILLISECOND, 0);
        return cldr.getTime();
    }
}

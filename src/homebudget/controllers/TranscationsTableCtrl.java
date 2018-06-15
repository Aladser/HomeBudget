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
    
    /** Получить дату первой записи
     * @return 
     * @throws java.sql.SQLException */
    public Date getFirstRecordDate() throws SQLException{
        query = "SELECT MIN(date) val FROM "+ dbName;
        return new Date(executeQuery(query).getLong("val"));
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
        query += new Date().getTime() + ")";
        executeQueryNoRes(query);
    }
    
    public void clearTable(){
        query = "DELETE FROM "+dbName+" WHERE date=(SELECT MAX(date) FROM transactions)";
        executeQueryNoRes(query);
    }
    
    public ArrayList<TransactionsTableLine> getData(Date startDate, Date finalDate){
        query = "SELECT name, SUM(value*type) value, date FROM "+dbName+" WHERE date";
        query += ">"+startDate.getTime()+" AND date<"+finalDate.getTime();
        return mGetData();
    }
    public ArrayList<TransactionsTableLine> getData(){
        query = "SELECT name, SUM(value*type) value, date FROM "+dbName;
        return mGetData();
    }    
    
    /**
     * Получение таблицы за указанный промежуток времени
     * @return 
     */
    public ArrayList<TransactionsTableLine> mGetData(){
        query += " GROUP BY name ORDER BY value DESC";
        resSet = executeQuery(query);
        ArrayList<TransactionsTableLine> rslt = new ArrayList<>();
        TransactionsTableLine line;
        try {
            while( resSet.next() ){
                line = new TransactionsTableLine();
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
    
    /** баланс
     * @return 
     * @throws java.sql.SQLException */
    public double getBalance() throws SQLException{
        return executeQuery("SELECT SUM(value*type) val FROM transactions").getDouble("val");
    }
}

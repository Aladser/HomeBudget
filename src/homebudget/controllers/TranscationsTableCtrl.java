package homebudget.controllers;

import homebudget.HomeBudget;
import homebudget.models.DBConnection;
import homebudget.models.TransactionsTableLine;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public GregorianCalendar getFirstRecordDate() throws SQLException{
        query = "SELECT MIN(date) val FROM "+ dbName;
        long time = executeQuery(query).getLong("val");
        GregorianCalendar rslt = new GregorianCalendar();
        rslt.setTimeInMillis(time);
        rslt = HomeBudget.setDayStart(rslt);
        return rslt;
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
    
    /** Удалить последнюю запись */
    public void removeLast(){
        query = "DELETE FROM "+dbName+" WHERE date=(SELECT MAX(date) FROM transactions)";
        executeQueryNoRes(query);
    }
    
    /** Удалить данные */
    public void removeData(){executeQueryNoRes("DELETE FROM "+dbName);}

    /**
     * Получение таблицы за указанный промежуток времени
     * @param startDate
     * @param finalDate
     * @return 
     */
    public ArrayList<TransactionsTableLine> getData(GregorianCalendar startDate, GregorianCalendar finalDate){
        query = "SELECT name, value*type value, date FROM "+dbName+" WHERE date";
        query += ">="+startDate.getTimeInMillis()+" AND date<="+finalDate.getTimeInMillis();        
        query += " ORDER BY date DESC";
        resSet = executeQuery(query);
        ArrayList<TransactionsTableLine> rslt = new ArrayList<>();
        TransactionsTableLine line;
        try {
            while( resSet.next() ){
                line = new TransactionsTableLine();
                line.name = resSet.getString("name");
                line.value = resSet.getDouble("value");
                line.date = resSet.getLong("date");
                rslt.add(line);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка TranscationsTableCtrl.mGetData()");
            System.exit(42);
        }
        return rslt;        
    }
    
    /** Общий доход за указанное врем
     * @param isIncome 0-доход, 1-расход
     * @param startDate
     * @param finalDate
     * @return */
    public double getTotalBudgetPart(boolean isIncome, GregorianCalendar startDate, GregorianCalendar finalDate){
        int type = isIncome ? 1 : -1;
        query = "SELECT SUM(value) val FROM "+dbName+" WHERE type="+type+" AND date>=";
        query += startDate.getTimeInMillis()+" AND date<="+finalDate.getTimeInMillis();
        resSet = executeQuery(query);
        try {        
            return resSet.getDouble("val");
        } catch (SQLException ex) {
            Logger.getLogger(TranscationsTableCtrl.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }        
        return -1;
    }

    public static class OperationStatistic{
        public String name;
        public double value;
        public double percent;
    }
    
    public ArrayList<OperationStatistic> getTrsctStatistic(int type, GregorianCalendar startDate, GregorianCalendar finalDate){
        int sort = type== 0 ? 1: -1;
        query = "SELECT name, SUM(value*type) value FROM "+dbName+" WHERE type="+sort+" AND date>=";
        query += startDate.getTimeInMillis()+" AND date<="+finalDate.getTimeInMillis();
        query +=  " GROUP BY name ORDER BY value";
        if(type == 0) query += " DESC";
        ArrayList<OperationStatistic> rslt = new ArrayList<>();
        OperationStatistic line;
        resSet = executeQuery(query);
        try {
            while( resSet.next() ){
                line = new OperationStatistic();
                line.name = resSet.getString("name");
                line.value = type==1 ?  resSet.getDouble("value")*(-1) : resSet.getDouble("value"); 
                rslt.add(line);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка TranscationsTableCtrl.getOprtStatistic()");
            System.exit(42);
        }
        double sum = 0;
        for(int i=0; i<rslt.size(); i++) sum += rslt.get(i).value;
        for(int i=0; i<rslt.size(); i++){
            line = rslt.get(i);
            line.percent = line.value * 100 / sum;
            line.percent *= 100;
            line.percent = (int)line.percent;
            line.percent /= 100;
        }
        return rslt;
    } 
    
    /** Баланс
     * @return 
     * @throws java.sql.SQLException */
    public double getBalance() throws SQLException{
        return executeQuery("SELECT SUM(value*type) val FROM transactions").getDouble("val");
    }
}

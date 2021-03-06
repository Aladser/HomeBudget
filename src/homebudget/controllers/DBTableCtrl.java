package homebudget.controllers;

import homebudget.models.DBConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** БД таблица */
public abstract class DBTableCtrl {
    final DBConnection db;   // соединение с БД
    final String dbName;            // имя БД таблицы
    protected String query;                   // строковый вид запроса
    protected java.sql.ResultSet resSet;      // результат запроса
    
    public DBTableCtrl(DBConnection db, String dbName){
        this.db = db;
        this.dbName = dbName;
    }
    
    /** запрос в БД без результата
     * @param query */
    protected void executeQueryNoRes(String query){
        StringBuilder sql = new StringBuilder();
        sql.append(query);
        try {
            db.executeQuery(sql.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DBTableCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** запрос в БД с результатом
     * @param query
     * @return  */
    protected java.sql.ResultSet executeQuery(String query){
        StringBuilder sql = new StringBuilder();
        sql.append(query);
        try {
            return db.executeQuery(sql.toString());
        } catch (SQLException ex) {
            Logger.getLogger(DBTableCtrl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Число записей
     * @return
     * @throws java.sql.SQLException
     */
    public int getRecordsNumber() throws java.sql.SQLException{
        return executeQuery("SELECT COUNT(time) val FROM "+dbName).getInt("val");
    }
    
}

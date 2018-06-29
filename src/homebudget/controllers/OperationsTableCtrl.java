package homebudget.controllers;

import homebudget.models.DBConnection;
import homebudget.models.OperationsTableLine;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class OperationsTableCtrl extends DBTableCtrl{
    public OperationsTableCtrl(DBConnection db, String dbName) {super(db, dbName);}
    
    /**
     * Добавить строку
     * @param name
     * @param type
     */
    public void add(String name, int type){
        executeQueryNoRes("INSERT INTO "+dbName+" VALUES ('" + name + "', "+type+")");
    }
    
    /** Удалить строку
     * @param name название  
     */
    public void remove(String name){
        executeQueryNoRes("DELETE FROM "+dbName+" WHERE name = '"+name+"'");       
    }
    
    /** Удалить все */
    public void removeData(){
        executeQueryNoRes("DELETE FROM "+dbName);
    }
    
    public boolean isValue(String name) throws SQLException{
        query = "SELECT COUNT() count FROM "+dbName+" WHERE name = '"+name+"'";
        return executeQuery(query).getInt("count") != 0;
    }
    
    
    
    public ArrayList<OperationsTableLine> getData(int type){
        String sql = "SELECT * FROM " + dbName + " WHERE type = "+type+"  OR type=2 ORDER BY type";
        return mGetData(sql);
    }
    public ArrayList<OperationsTableLine> getData(){
        String sql = "SELECT * FROM " + dbName + " ORDER BY type";
        return mGetData(sql);
    }
    /**
     * Получение таблицы
     * @param sql
     * @return 
     */
    public ArrayList<OperationsTableLine> mGetData(String sql){
        resSet = executeQuery(sql);
        ArrayList<OperationsTableLine> rslt = new ArrayList<>();
        try {
            OperationsTableLine data;
            while( resSet.next() ){
                data = new OperationsTableLine();
                data.name = resSet.getString("name");
                data.type = resSet.getInt("type");
                rslt.add(data); 
            }
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(null, "OperationsTableCtrl.getData(). Ошибка метода getData()");
            System.exit(42);
        }
        return rslt;        
    }     
}

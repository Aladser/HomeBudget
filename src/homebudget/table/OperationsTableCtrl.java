package homebudget.table;

import java.util.ArrayList;
import javax.swing.JOptionPane;

public class OperationsTableCtrl extends DBTableCtrl{
    public OperationsTableCtrl(DBConnection db, String dbName) {super(db, dbName);}
    
    /**
     * Добавить строку
     * @param name
     */
    public void add(String name){executeQueryNoRes("INSERT INTO "+dbName+" VALUES (" + name + ")");}
    
    /** Удалить строку
     * @param name название  
     */
    public void remove(String name){executeQueryNoRes("DELETE FROM "+dbName+" WHERE name = '"+name+"'");       }
    
    /**
     * Получение таблицы
     * @return 
     */
    public java.util.ArrayList<OperationsTableLine> getData(){
        resSet = executeQuery( "SELECT * FROM " + dbName + " ORDER BY type");
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

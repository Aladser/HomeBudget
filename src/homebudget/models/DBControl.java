package homebudget.models;

import homebudget.models.DBConnection;
import homebudget.controllers.DBTableCtrl;
import homebudget.controllers.TranscationsTableCtrl;
import homebudget.controllers.OperationsTableCtrl;

/** Подключение локальной БД к проекту */
public class DBControl {
    private final DBConnection cnct;
    
    // Список таблиц базы
    private final TranscationsTableCtrl trsctTable;
    private final OperationsTableCtrl oprtTable;
    
    
    /**
     * Подключение локальной БД к проекту
     * @param dbPath путь к БД
    */
    public DBControl(String dbPath){
        cnct = new DBConnection(dbPath);
        cnct.connect();
        trsctTable = new TranscationsTableCtrl(cnct, "transactions");
        oprtTable = new OperationsTableCtrl(cnct, "operations");
    }
    
    public DBTableCtrl getTable(int index){
        return index==0 ? trsctTable : oprtTable;
    }
}

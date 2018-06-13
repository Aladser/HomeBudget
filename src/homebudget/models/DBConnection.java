package homebudget.models;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** СОЕДИНЕНИЕ С ЛОКАЛЬНОЙ БД */
public class DBConnection {
    private java.sql.Connection conn;
    private final String dbpath;
    private static java.sql.Statement statmt;
    private java.sql.ResultSet resSet;
    
    /**
     * СОЕДИНЕНИЕ С БД
     * @param dbpath путь к БД
     */
    public DBConnection(String dbpath){
        conn = null;
        this.dbpath = dbpath;
    }

    //** ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ **
    public void connect(){
        try{
	    Class.forName("org.sqlite.JDBC");
            try{
                conn = java.sql.DriverManager.getConnection("jdbc:sqlite:"+dbpath);
                statmt = conn.createStatement();
            } catch (java.sql.SQLException ex) {
                java.util.logging.Logger.getLogger(DBConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                System.exit(42);
            }
        }catch(ClassNotFoundException e){
            javax.swing.JOptionPane.showMessageDialog(null, 
                    "Не найдена библиотека sqlitejdbc.jar. Приложение будет закрыто");
            System.exit(42);
        }
    }
    
    //** ЗАКРЫТИЕ СОЕДИНЕНИЯ **
    public void close() throws java.sql.SQLException{
        conn.close();
        statmt.close();
	resSet.close();
    }

    //** ЗАПРОС В БАЗУ ДАННЫХ **
    public java.sql.ResultSet executeQuery(String query) throws SQLException{
        try{
            if( query.contains("SELECT") ){ 
                resSet = statmt.executeQuery(query);
                return resSet;
            }
            else{ 
                statmt.executeUpdate(query);
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

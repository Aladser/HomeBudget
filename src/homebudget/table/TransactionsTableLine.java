package homebudget.table;

import java.util.Date;

public class TransactionsTableLine {
    public String name;                     
    public double value;
 
    /**
     * Строка таблицы игр
     * @param n название
     * @param v значение
     * @param st доход или расход
     * @param lDate
     */
    public TransactionsTableLine(String n, double v, int st, long lDate){
        name = n;
        value = v*st;
    }
    
    /** создает пустую строку таблицы игр */
    public TransactionsTableLine(){}
}

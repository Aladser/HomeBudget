package homebudget.models;

public class TransactionsTableLine {
    public String name;                     
    public double value;
    public long date;
 
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
        date = lDate;
    }
    
    /** создает пустую строку таблицы игр */
    public TransactionsTableLine(){}
}

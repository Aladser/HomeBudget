package homebudget.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/** Модель таблицы */
public class TransactionsTableModel implements TableModel{
    final Set<TableModelListener> lstnrs;
    /** данные таблицы */
    final ArrayList<TransactionsTableLine> lines;
    
    public TransactionsTableModel( ArrayList<TransactionsTableLine> list){
        lstnrs = new HashSet<>();
        lines = list;
    }
    
    @Override
    public int getRowCount() {return lines.size();}

    @Override
    public int getColumnCount() {return 3;}

    @Override
    public String getColumnName(int index){
        switch(index){
            case 0: return "Операция";
            case 1: return "Сумма";
            default: return "Дата";
        } 
    }

    @Override
    public Class<?> getColumnClass(int index) {
        switch(index){
            case 0: return String.class;
            case 1: return Double.class;
            default: return Long.class;
        } 
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TransactionsTableLine data = lines.get(rowIndex);
        switch(columnIndex){
            case 0: return data.name;
            case 1: return data.value;
            default: return data.date;
        } 
    }

    @Override
    public void addTableModelListener(TableModelListener l) {lstnrs.add(l);}

    @Override
    public void removeTableModelListener(TableModelListener l) {lstnrs.remove(l);}

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
    
}

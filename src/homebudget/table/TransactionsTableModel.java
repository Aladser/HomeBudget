package homebudget.table;

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
    public int getColumnCount() {return 2;}

    @Override
    public String getColumnName(int index){ 
        return index==0 ? "Название" : "Сумма"; 
    }

    @Override
    public Class<?> getColumnClass(int index) { 
        return index==0 ? String.class : Double.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TransactionsTableLine data = lines.get(rowIndex);
        return columnIndex==0 ? data.name : data.value; 
    }

    @Override
    public void addTableModelListener(TableModelListener l) {lstnrs.add(l);}

    @Override
    public void removeTableModelListener(TableModelListener l) {lstnrs.remove(l);}

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
    
}

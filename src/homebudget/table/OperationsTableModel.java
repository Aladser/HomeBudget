package homebudget.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class OperationsTableModel implements TableModel{
    final Set<TableModelListener> lstnrs;
    /** данные таблицы */
    final ArrayList<OperationsTableLine> lines;
    
    public OperationsTableModel(ArrayList<OperationsTableLine> list){
        lstnrs = new HashSet<>();
        lines = list;
    }

    @Override
    public int getRowCount() { return lines.size(); }

    @Override
    public int getColumnCount() { return 2; }

    @Override
    public String getColumnName(int columnIndex) {
        return columnIndex==0 ? "Название" : "Тип операции"; 
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) { 
        return columnIndex==0 ? String.class : Integer.class; 
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OperationsTableLine data = lines.get(rowIndex);
        return columnIndex==0 ? data.name : data.type;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    @Override
    public void addTableModelListener(TableModelListener l) {lstnrs.add(l);}

    @Override
    public void removeTableModelListener(TableModelListener l) {lstnrs.remove(l);}
}

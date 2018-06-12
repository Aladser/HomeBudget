package homebudget.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class OperationTableModel implements TableModel{
    final Set<TableModelListener> lstnrs;
    /** данные таблицы */
    final ArrayList<String> lines;
    
    public OperationTableModel(ArrayList<String> list){
        lstnrs = new HashSet<>();
        lines = list;
    }

    @Override
    public int getRowCount() { return lines.size(); }

    @Override
    public int getColumnCount() { return 2; }

    @Override
    public String getColumnName(int columnIndex) { return "Название"; }

    @Override
    public Class<?> getColumnClass(int columnIndex) { return String.class; }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {return lines.get(rowIndex);}

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    @Override
    public void addTableModelListener(TableModelListener l) {lstnrs.add(l);}

    @Override
    public void removeTableModelListener(TableModelListener l) {lstnrs.remove(l);}
}

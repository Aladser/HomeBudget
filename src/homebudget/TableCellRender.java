package homebudget;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Рендер отрисоски таблицы MainFrame */
public class TableCellRender extends DefaultTableCellRenderer{
    @Override
    public java.awt.Component getTableCellRendererComponent(
            JTable table, 
            Object value, 
            boolean isSelected,		
            boolean hasFocus, 
            int row, 
            int column) 
    {
        
        // Содержание JLabel    
        String text = null;
        if(value.getClass() == String.class) text = "   " + (String)value;
        else if(value.getClass() == Double.class)
            text = Double.toString((double)value) + " \u0584";
        else{
            Date d = (Date)value;
            text = new SimpleDateFormat("dd.MM.yyyy").format(d) + " г.";
            
        }
        
        JLabel cell = new JLabel(text);
        if (column == 1){
            if((double)value>1) cell.setForeground(Color.green);
            else if ((double)value<1) cell.setForeground(Color.red);
        }
        
        cell.setFont(new Font("Arial", 0, 14));
        // выравнивание первой колонки
        if(column != 0)  cell.setHorizontalAlignment(SwingConstants.CENTER);
        
        return cell;
    }
}

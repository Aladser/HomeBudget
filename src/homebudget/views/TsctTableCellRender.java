package homebudget.views;

import homebudget.HomeBudget;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Рендер отрисоски таблицы MainFrame */
public class TsctTableCellRender extends DefaultTableCellRenderer{
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
        String text;
        if(value.getClass() == String.class) 
            text = "   " + (String)value;
        else if(value.getClass() == Double.class){
            text = ((double)value)>0 ? "+" : "";
            text += HomeBudget.formatMoney((double)value) + " P";
        }
        else
            text = new SimpleDateFormat("dd MMMM yyyy HH:mm   ").format(new Date((long) value));
        JLabel cell = new JLabel(text);
        // cell-рендер
        switch (column) {
            case 0:
                cell.setHorizontalAlignment(SwingConstants.LEFT);
                cell.setFont(new Font("Times New Roman", 0, 18));
                break;
            case 1:
                if((double)value>0) cell.setForeground(Color.green);
                else if ((double)value<0) cell.setForeground(Color.red);
                else cell.setForeground(Color.black);
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setFont(new Font("Digital-7 Mono", 0, 18));
                break;
            default:
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setFont(new Font("Times New Roman", 0, 18));
        }
        return cell;
    }
}

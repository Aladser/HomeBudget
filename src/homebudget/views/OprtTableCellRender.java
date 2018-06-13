package homebudget.views;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Рендер отрисоски таблицы Operations */
public class OprtTableCellRender extends DefaultTableCellRenderer{
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
        if(value.getClass() == String.class) text = "   " + (String)value;
        else{
            switch((int)value){
                case 0:
                    text = "Доход  ";
                    break;
                case 1:
                    text = "Расход  ";
                    break;
                default:
                    text = "Доход, расход  ";
                    break;
            }
        }
        
        JLabel cell = new JLabel(text);
        cell.setOpaque(true); // прозрачность для возможности установки фона
        // фон ячейки
        Color bgColor = isSelected ? new Color(200,200,200) : Color.white;
        cell.setBackground(bgColor);
        // шрифт
        Font font = column==0 ? new Font("Arial", 1, 14) : new Font("Arial", 0, 14);
        cell.setFont(font);
        // выравнивание
        int align = column==0 ? SwingConstants.LEFT : SwingConstants.RIGHT; 
        cell.setHorizontalAlignment(align);
        
        table.setRowHeight(30); //высота строк
        return cell;
    }
}

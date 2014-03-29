import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		setHorizontalAlignment(JLabel.CENTER);
		
		Font font = new Font("helvetica", Font.BOLD, 12);
		
		if (value == null || value.equals(""))
			return cell;
		else if (value.toString().startsWith("¬")){
    		Map  attributes = font.getAttributes();
    		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
    		
    		font = new Font(attributes);
    		
    		setFont(font);
    		
    		setForeground(Color.GRAY);
    		
    		setText(value.toString().substring(1));
    	}
		else if (value.toString().equals("N/A")){
			font = new Font("helvetica", Font.PLAIN, 12);
			
			setForeground(Color.gray);
		}
		else if (value.toString().charAt(0) >= '0' && value.toString().charAt(0) <= '9'){
			font = new Font("helvetica", Font.PLAIN, 12);
    		
    		setFont(font);
    		
    		setForeground(Color.black);
    		
    		setText(value.toString());
    	}		
    	else {
    		font = new Font("helvetica", Font.BOLD, 12);
    	
    		setFont(font);
    		
    		setForeground(Color.black);
    		
    		setText(value.toString());
    	}

		if (row % 2 != 0)
			cell.setBackground(new Color(230, 230, 230));
		else {
			cell.setBackground(new Color(215, 215, 215));
		}

		return cell;
	}
}
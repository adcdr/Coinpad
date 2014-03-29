import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class CustomTableHeaderRenderer extends JLabel implements TableCellRenderer {
	private TableCellRenderer delegate;
	
	public CustomTableHeaderRenderer(TableCellRenderer delegate) {
		this.delegate = delegate;
	}
	
	 @Override
	 public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

	    Font labelFont = new Font("Arial", Font.PLAIN, 14);
	    
	    if (c instanceof JLabel) {
	        JLabel label = (JLabel) c;
	        label.setFont(labelFont);
	        label.setOpaque(true);
	        label.setHorizontalAlignment(SwingConstants.CENTER);
	        label.setBorder(BorderFactory.createEtchedBorder());
	    }
	    
	    return c;
	}
}
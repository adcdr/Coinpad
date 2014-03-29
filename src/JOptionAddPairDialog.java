import java.awt.Dimension;

import javax.swing.*;

public class JOptionAddPairDialog {
	JOptionSettingsPane jOptionSettingsPane;
	SpringLayout layout;
	JPanel frame;
	JLabel messageLabel1, label1;
	JComboBox fromCombo, toCombo;
	
	public JOptionAddPairDialog(JOptionSettingsPane jOptionSettingsPane) {
		this.jOptionSettingsPane = jOptionSettingsPane;
		
		layout = new SpringLayout();
		
		frame = new JPanel();
		frame.setPreferredSize(new Dimension(150, 60));
		frame.setBackground(null);
		frame.setLayout(layout);

		messageLabel1 = new JLabel("Select a pair: ");
		label1 = new JLabel("/");
		
		fromCombo = new JComboBox(jOptionSettingsPane.coinListModel.toArray());
		toCombo = new JComboBox(jOptionSettingsPane.coinListModel.toArray());
	}
	
	public void showDialog(){		
		//message label
		layout.putConstraint(SpringLayout.WEST, messageLabel1, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel1, 5, SpringLayout.NORTH, frame);
    	frame.add(messageLabel1, 0);
    	
    	//from combo box
    	layout.putConstraint(SpringLayout.WEST, fromCombo, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, fromCombo, 5, SpringLayout.SOUTH, messageLabel1);
    	frame.add(fromCombo, 0);
    	
    	//"/"
    	layout.putConstraint(SpringLayout.WEST, label1, 1, SpringLayout.EAST, fromCombo);
    	layout.putConstraint(SpringLayout.NORTH, label1, 5, SpringLayout.SOUTH, messageLabel1);
    	frame.add(label1, 0);
    	
    	//to combo box
    	layout.putConstraint(SpringLayout.WEST, toCombo, 5, SpringLayout.EAST, fromCombo);
    	layout.putConstraint(SpringLayout.NORTH, toCombo, 5, SpringLayout.SOUTH, messageLabel1);
    	frame.add(toCombo, 0);
		
		int result = JOptionPane.showConfirmDialog(jOptionSettingsPane.frame, frame, "Add Pair", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (result == JOptionPane.OK_OPTION){
			String pairToAdd1 = fromCombo.getSelectedItem().toString() + "/"
						+ toCombo.getSelectedItem().toString();
			
			String pairToAdd2 = toCombo.getSelectedItem().toString() + "/" 
					+ fromCombo.getSelectedItem().toString();
			
			if (jOptionSettingsPane.pairListModel.contains(pairToAdd1)
					|| jOptionSettingsPane.pairListModel.contains(pairToAdd2)){
				JOptionPane.showMessageDialog(jOptionSettingsPane.frame,
						pairToAdd1 + " already exists.", "Error", 
					    JOptionPane.PLAIN_MESSAGE);
			}
			else if (pairToAdd1.equals(pairToAdd2)){
				JOptionPane.showMessageDialog(jOptionSettingsPane.frame,
						"Can not pair a coin with itself.", "Error", 
					    JOptionPane.PLAIN_MESSAGE);
			}
			else if (jOptionSettingsPane.pairListModel.contains("¬" + pairToAdd1)) {
				int deletedPairIndex = jOptionSettingsPane.pairListModel.indexOf("¬" + pairToAdd1);
				
				jOptionSettingsPane.pairListModel.set(deletedPairIndex, pairToAdd1);
			}
			else if (jOptionSettingsPane.pairListModel.contains("¬" + pairToAdd2)) {
				int deletedPairIndex = jOptionSettingsPane.pairListModel.indexOf("¬" + pairToAdd2);
				
				jOptionSettingsPane.pairListModel.set(deletedPairIndex, pairToAdd2);
			}
			else {				
				jOptionSettingsPane.pairListModel.add(0, pairToAdd1);
				
				jOptionSettingsPane.pairList.setSelectedIndex(0);				
				
				JScrollBar vertical = jOptionSettingsPane.pairScroll.getVerticalScrollBar();
				vertical.setValue(0);			
			}
		}
	}
}
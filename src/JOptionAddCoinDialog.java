import java.awt.Color;
import java.awt.Dimension;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class JOptionAddCoinDialog {
	JOptionSettingsPane jOptionSettingsPane;
	SpringLayout layout;
	JPanel frame;
	JLabel messageLabel1;
	JFormattedTextField formattedTextField1;
	
	public JOptionAddCoinDialog(JOptionSettingsPane jOptionSettingsPane) {
		this.jOptionSettingsPane = jOptionSettingsPane;
		
		layout = new SpringLayout();
		
		frame = new JPanel();
		frame.setPreferredSize(new Dimension(150, 40));
		frame.setBackground(null);
		frame.setLayout(layout);

		messageLabel1 = new JLabel("Coin Name: ");
		
		try {
			formattedTextField1 = new JFormattedTextField(new MaskFormatter("UUU"));
			
			formattedTextField1.setPreferredSize(new Dimension(35, 20));
			
		} catch (ParseException e) {
			System.out.println("JOptionAddCoinDialog - Bad Formatter");
			e.printStackTrace();
		}
	}
	
	public void showDialog(){		
		//message label
		layout.putConstraint(SpringLayout.WEST, messageLabel1, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel1, 5, SpringLayout.NORTH, frame);
    	frame.add(messageLabel1, 0);
    	
    	//text field
    	layout.putConstraint(SpringLayout.WEST, formattedTextField1, 5, SpringLayout.EAST, messageLabel1);
    	layout.putConstraint(SpringLayout.NORTH, formattedTextField1, 5, SpringLayout.NORTH, frame);
    	frame.add(formattedTextField1, 0);
		
		int result = JOptionPane.showConfirmDialog(jOptionSettingsPane.frame, frame, "Add Coin", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (result == JOptionPane.OK_OPTION){
			String coinToAdd = formattedTextField1.getValue().toString();
			
			if (jOptionSettingsPane.coinListModel.contains("¬" + coinToAdd)){
				int coinIndex = jOptionSettingsPane.coinListModel.indexOf("¬" + coinToAdd);
				String coinColorCode = (String) jOptionSettingsPane.coinColorCodeMap.get(coinToAdd);
				
				jOptionSettingsPane.coinListModel.set(coinIndex, coinToAdd);
				jOptionSettingsPane.coinColorCodeMap.put(coinToAdd, coinColorCode);
			}
			else if (jOptionSettingsPane.coinListModel.contains(coinToAdd)){
				JOptionPane.showMessageDialog(jOptionSettingsPane.frame,
						coinToAdd +" already exists.", "Error", 
					    JOptionPane.PLAIN_MESSAGE);
			}
			else {
				synchronized(jOptionSettingsPane.coinPad.coinColorCodeMap){
					jOptionSettingsPane.coinPad.coinColorCodeMap.put(coinToAdd, "#DEDEDE");
				}
				
				jOptionSettingsPane.coinListModel.add(0, coinToAdd);
				
				jOptionSettingsPane.coinList.setSelectedIndex(0);
				
				JScrollBar vertical = jOptionSettingsPane.coinScroll.getVerticalScrollBar();
				vertical.setValue(0);
				
				jOptionSettingsPane.selectedColorLabel2.setForeground(new Color(0xDEDEDE));
				jOptionSettingsPane.selectedColorLabel2.setText(coinToAdd);				
			}
		}
	}
}
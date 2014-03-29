import java.awt.Dimension;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

import org.joda.time.DateTime;

public class JOptionWithdrawDialog {
	CoinPad coinPad;
	SpringLayout layout;
	JPanel frame;
	JLabel messageLabel1, messageLabel2, messageLabel3, messageLabel4;
	JComboBox comboBox1;
	JTextField  textField1, textField2;
	
	public JOptionWithdrawDialog(CoinPad coinPad) {
		this.coinPad = coinPad;
		String [] coins = coinPad.coinsArrayList.toArray(new String[coinPad.coinsArrayList.size()]);
		
		layout = new SpringLayout();
		
		frame = new JPanel();
		frame.setPreferredSize(new Dimension(320, 80));
		frame.setBackground(null);
		frame.setLayout(layout);
		
		messageLabel1 = new JLabel("Choose the coin and amount you wish to withdraw.");
		messageLabel2 = new JLabel("Coin: ");
		
		comboBox1 = new JComboBox(coins);
		
		messageLabel3 = new JLabel("Amount: ");
		
		textField1 = new JTextField();
		DocumentFilter onlyFloatFilter = new FloatDocumentFilter();
        ((AbstractDocument)textField1.getDocument()).setDocumentFilter(onlyFloatFilter);
		textField1.setPreferredSize(new Dimension(50,20));
		
		messageLabel4 = new JLabel("Fee %: ");
		
		textField2 = new JTextField();
		DocumentFilter percentageFilter = new PercentageDocumentFilter();
        ((AbstractDocument)textField2.getDocument()).setDocumentFilter(percentageFilter);
		textField2.setPreferredSize(new Dimension(30,20));
   }
	
	public void showDialog(){		
		//message label 1
		layout.putConstraint(SpringLayout.WEST, messageLabel1, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel1, 5, SpringLayout.NORTH, frame);
    	frame.add(messageLabel1, 0);
    	
    	//message label 2
    	layout.putConstraint(SpringLayout.WEST, messageLabel2, 5, SpringLayout.WEST, messageLabel1);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel2, 20, SpringLayout.SOUTH, messageLabel1);
    	frame.add(messageLabel2, 0);
    	
    	//coin combo box
    	layout.putConstraint(SpringLayout.WEST, comboBox1, 5, SpringLayout.EAST, messageLabel2);
    	layout.putConstraint(SpringLayout.NORTH, comboBox1, 0, SpringLayout.NORTH, messageLabel2);
    	frame.add(comboBox1, 0);
    	
    	//message label 3
    	layout.putConstraint(SpringLayout.WEST, messageLabel3, 10, SpringLayout.EAST, comboBox1);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel3, 0, SpringLayout.NORTH, comboBox1);
    	frame.add(messageLabel3, 0);
    	
    	//amount text area
    	layout.putConstraint(SpringLayout.WEST, textField1, 5, SpringLayout.EAST, messageLabel3);
    	layout.putConstraint(SpringLayout.NORTH, textField1, 0, SpringLayout.NORTH, messageLabel3);
    	frame.add(textField1, 0);
    	
    	//message label 4
    	layout.putConstraint(SpringLayout.WEST, messageLabel4, 10, SpringLayout.EAST, textField1);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel4, 0, SpringLayout.NORTH, textField1);
    	frame.add(messageLabel4, 0);
    	
    	//fee text area
    	layout.putConstraint(SpringLayout.WEST, textField2, 5, SpringLayout.EAST, messageLabel4);
    	layout.putConstraint(SpringLayout.NORTH, textField2, 0, SpringLayout.NORTH, messageLabel4);
    	frame.add(textField2, 0);
    			
		int result = JOptionPane.showConfirmDialog(coinPad, frame, "Withdraw", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (result == JOptionPane.OK_OPTION){
			String coin = comboBox1.getSelectedItem().toString();
			float coinBalance = coinPad.db.getCoinCapital(coin);
			float amount = Float.valueOf(textField1.getText());
			
			if (amount > coinBalance)
				JOptionPane.showMessageDialog(coinPad, "You have " + coinBalance + " " + coin + ".\n" +
						"You tried to withdraw "  + amount + " " + coin + ".", "Withdraw Error", 
						JOptionPane.PLAIN_MESSAGE);
			else {
				float fee = Float.valueOf(textField2.getText());
				float price = -1.0f;
				float capital = coinPad.db.getCoinCapital(coin) - amount;
				
				//update database
				coinPad.db.insertCapitalRow(coin, "W", amount);
				coinPad.db.insertInHistory(coin, "W", amount, price, fee, capital, -0.0f);
				
				//update gui
				coinPad.addHistoryTableRow(DateTime.now().toString(), coin, amount, price, fee, "W", capital, -0.0f);
				coinPad.updateCoinStatusPane();
				coinPad.updateChart(coinPad.chartRange);
			}
		}
	}
}
import java.awt.Dimension;

import javax.swing.*;

public class JOptionNewDialog {
	CoinPad coinPad;
	SpringLayout layout;
	JPanel frame;
	JLabel messageLabel1, label1;
	JComboBox fromCombo, toCombo;
	
	public JOptionNewDialog(CoinPad coinPad) {
		this.coinPad = coinPad;
		
		layout = new SpringLayout();
		
		frame = new JPanel();
		frame.setPreferredSize(new Dimension(150, 40));
		frame.setBackground(null);
		frame.setLayout(layout);

		messageLabel1 = new JLabel("Are you sure you want to delete all data?");
	}
	
	public void showDialog(){
		//message label
		layout.putConstraint(SpringLayout.WEST, messageLabel1, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, messageLabel1, 5, SpringLayout.NORTH, frame);
    	frame.add(messageLabel1, 0);
		
		int result = JOptionPane.showConfirmDialog(coinPad, frame, "New", 
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (result == JOptionPane.OK_OPTION){
			coinPad.db.dropDatabase();
			coinPad.db = new DBConnection();
			
			for (int i=0; i<coinPad.portfolioTableModel.getRowCount(); i++){
				for (int j=1; j<coinPad.portfolioTableModel.getColumnCount(); j++)
					coinPad.portfolioTableModel.setValueAt(0.0f, i, j);
			}
			coinPad.portfolioTableModel.fireTableDataChanged();
			coinPad.historyTableData.removeAllElements();
			coinPad.historyTableModel.fireTableDataChanged();
			
			coinPad.updateChart(coinPad.chartRange);
			coinPad.updateCoinStatusPane();
		}
	}
}
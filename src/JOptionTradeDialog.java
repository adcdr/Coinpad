import java.awt.Dimension;

import javax.swing.*;

import org.joda.time.DateTime;

public class JOptionTradeDialog {
	CoinPad coinPad;
	SpringLayout layout;
	JPanel frame;
	JLabel messageLabel1, mesageLabel2;
	JTextArea  textArea1, textArea2, textArea3, textArea4, textArea5;
	
	public JOptionTradeDialog(CoinPad coinPad) {
		this.coinPad = coinPad;
		
		layout = new SpringLayout();
		
		frame = new JPanel();
		frame.setPreferredSize(new Dimension(320, 100));
		frame.setBackground(null);
		frame.setLayout(layout);
		
		messageLabel1 = new JLabel("Trade [coin amount] [coin]s for [price] BTC?");
		
		textArea1 = new JTextArea(coinPad.coinPairsCombo.getSelectedItem().toString());
		textArea1.setEditable(false);
		textArea1.setOpaque(false);
		textArea1.setPreferredSize(new Dimension(60,15));
		
		if (coinPad.buyRadio.isSelected())
			textArea2 = new JTextArea(coinPad.amountField.getText() + " B");
		else
			textArea2 = new JTextArea(coinPad.amountField.getText() + " S");
		textArea2.setEditable(false);
		textArea2.setOpaque(false);
		textArea2.setPreferredSize(new Dimension(60,15));
		
		textArea3 = new JTextArea(coinPad.priceField.getText());
		textArea3.setEditable(false);
		textArea3.setOpaque(false);
		textArea3.setPreferredSize(new Dimension(60,15));
		
		textArea4 = new JTextArea(coinPad.feeField.getText() + "%");
		textArea4.setEditable(false);
		textArea4.setOpaque(false);
		textArea4.setPreferredSize(new Dimension(60,15));
		
		textArea5 = new JTextArea("Profit");
		textArea5.setEditable(false);
		textArea5.setOpaque(false);
		textArea5.setPreferredSize(new Dimension(60,15));
   }
	
	public void showDialog(){
		JLabel label1 = new JLabel("Pair:");
		JLabel label2 = new JLabel("Amount:");
		JLabel label3 = new JLabel("Price:");
		JLabel label4 = new JLabel("Fee:");
		JLabel label5 = new JLabel("Projected Profit:");
		JLabel label6 = new JLabel("Are you sure want to complete this trade?");
		
		//pair label
		layout.putConstraint(SpringLayout.WEST, label1, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, label1, 5, SpringLayout.NORTH, frame);
    	frame.add(label1, 0);
    	
    	//pair text area
    	layout.putConstraint(SpringLayout.WEST, textArea1, 5, SpringLayout.EAST, label1);
    	layout.putConstraint(SpringLayout.NORTH, textArea1, 5, SpringLayout.NORTH, frame);
    	frame.add(textArea1, 0);
    	
    	//amount label
    	layout.putConstraint(SpringLayout.WEST, label2, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, label2, 5, SpringLayout.SOUTH, label1);
    	frame.add(label2, 0);
    	
    	//amount text area
    	layout.putConstraint(SpringLayout.WEST, textArea2, 5, SpringLayout.EAST, label2);
    	layout.putConstraint(SpringLayout.NORTH, textArea2, 0, SpringLayout.NORTH, label2);
    	frame.add(textArea2, 0);
    	
    	//price label
    	layout.putConstraint(SpringLayout.WEST, label3, 5, SpringLayout.EAST, textArea2);
    	layout.putConstraint(SpringLayout.NORTH, label3, 0, SpringLayout.NORTH, textArea2);
    	frame.add(label3, 0);
    	
    	//price text area
    	layout.putConstraint(SpringLayout.WEST, textArea3, 5, SpringLayout.EAST, label3);
    	layout.putConstraint(SpringLayout.NORTH, textArea3, 0, SpringLayout.NORTH, label3);
    	frame.add(textArea3, 0);
    	
    	//fee label
    	layout.putConstraint(SpringLayout.WEST, label4, 5, SpringLayout.EAST, textArea3);
    	layout.putConstraint(SpringLayout.NORTH, label4, 0, SpringLayout.NORTH, textArea3);
    	frame.add(label4, 0);
    	
    	//fee text area
    	layout.putConstraint(SpringLayout.WEST, textArea4, 5, SpringLayout.EAST, label4);
    	layout.putConstraint(SpringLayout.NORTH, textArea4, 0, SpringLayout.NORTH, label4);
    	frame.add(textArea4, 0);
    	
    	//profit loss % label
    	layout.putConstraint(SpringLayout.WEST, label5, 0, SpringLayout.WEST, label2);
    	layout.putConstraint(SpringLayout.NORTH, label5, 5, SpringLayout.SOUTH, label2);
    	frame.add(label5, 0);
    	
    	//profit loss % text field
    	layout.putConstraint(SpringLayout.WEST, textArea5, 5, SpringLayout.EAST, label5);
    	layout.putConstraint(SpringLayout.NORTH, textArea5, 0, SpringLayout.NORTH, label5);
    	frame.add(textArea5, 0);
    	
    	//message label
    	layout.putConstraint(SpringLayout.WEST, label6, 40, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, label6, 20, SpringLayout.SOUTH, textArea5);
    	frame.add(label6, 0);
		
		int result = JOptionPane.showConfirmDialog(coinPad, frame, "Confirm Trade", JOptionPane.YES_NO_OPTION
				, JOptionPane.PLAIN_MESSAGE);
		
		if (result == JOptionPane.YES_OPTION){
			//buying or selling?
			boolean buy = coinPad.buyRadio.isSelected();
			String pair = coinPad.coinPairsCombo.getSelectedItem().toString();
			float amount = Float.parseFloat(coinPad.amountField.getText());
			float price = Float.parseFloat(coinPad.priceField.getText());
			float fee = Float.parseFloat(coinPad.feeField.getText());
			float cost = price * amount;
			
			float totalBoughtFee = coinPad.db.getTotalBoughtFee(pair);
			float totalBoughtNoFee = coinPad.db.getTotalBoughtNoFee(pair);
			float totalSoldFee = coinPad.db.getTotalSoldFee(pair);
			float totalSoldNoFee = coinPad.db.getTotalSoldNoFee(pair);
			float lastBuyPrice = coinPad.db.getLastBuy(pair);
			float lastSellPrice = coinPad.db.getLastSell(pair);
			float averageBuyPrice = coinPad.db.getAverageBuy(pair);
			float averageSellPrice = coinPad.db.getAverageSell(pair);
			float profit;
			
			//------------store the previous trade metrics-------------------
			boolean lastBuy = coinPad.db.getBuy(pair);
			float lastAmount = coinPad.db.getAmount(pair);
			float lastPrice = coinPad.db.getLastPrice(pair);
			float lastFee = coinPad.db.getFee(pair);
			float lastCost = lastPrice * lastAmount;
			
			float lastTotalBoughtFee = coinPad.db.getBoughtFee(pair);
			float lastTotalBoughtNoFee = coinPad.db.getBoughtNoFee(pair);
			float lastTotalSoldFee = coinPad.db.getTotalSoldFee(pair);
			float lastTotalSoldNoFee = coinPad.db.getTotalSoldNoFee(pair);
			float lastLastBuyPrice = coinPad.db.getLastBuyPrice(pair);
			float lastLastSellPrice = coinPad.db.getLastSellPrice(pair);
			float lastAverageBuyPrice = coinPad.db.getAverageBuyPrice(pair);
			float lastAverageSellPrice = coinPad.db.getAverageSellPrice(pair);
			float lastProfit = coinPad.db.getProfit(pair);
			
			coinPad.lastTrade = new Trade(lastBuy, pair, lastAmount, lastPrice, lastFee, lastTotalBoughtFee,
					lastTotalBoughtNoFee, lastTotalSoldFee, lastTotalSoldNoFee, lastLastBuyPrice,
					lastLastSellPrice, lastAverageBuyPrice, lastAverageSellPrice, lastProfit);
			
			coinPad.undoTradeMenuItem.setEnabled(true);
			//-----------------------------------------------------------------
			
			if (buy){
				totalBoughtNoFee += amount;
				
				averageBuyPrice = ((amount * price) + ((totalBoughtNoFee-amount) * averageBuyPrice))
						/ totalBoughtNoFee;
				
				totalBoughtFee = totalBoughtFee + (amount * (1 - (fee/100)));

				lastBuyPrice = price;
			}
			else {
				totalSoldNoFee += amount;
				
				averageSellPrice = ((amount * price) + ((totalSoldNoFee-amount) * averageSellPrice))
						/ totalSoldNoFee;
				
				totalSoldFee = totalSoldFee + (amount * (1 - (fee/100)));
				
				lastSellPrice = price;
			}
			
			//calculate overall profit
			profit = (averageSellPrice * totalSoldFee) / (averageBuyPrice * totalBoughtFee);
			
			//update profile table
			coinPad.db.updateProfile(pair, amount, cost, buy, lastSellPrice, averageSellPrice,
					totalSoldFee, totalSoldNoFee, lastBuyPrice, averageBuyPrice, totalBoughtFee,
					totalBoughtNoFee, profit);
			
			//insert in capital table for both coins
			String coins[] = pair.split("/");
			
			coinPad.db.insertCapitalRow(coins[0], buy ? "B" : "S", amount); //from coin
			coinPad.db.insertCapitalRow(coins[1], buy ? "S" : "B", amount); //to coin
			
			//insert in history table
			float fromCoinCapital = coinPad.db.getCoinCapital(coins[0]);
			float toCoinCapital = coinPad.db.getCoinCapital(coins[1]);
			
			coinPad.db.insertInHistory(pair, buy ? "B" : "S", amount, price, fee, fromCoinCapital, toCoinCapital);
			
			//update JTables
			coinPad.addPortfolioTableRow(pair, amount, cost, buy, lastBuyPrice, lastSellPrice, 
					averageBuyPrice, averageSellPrice, totalBoughtNoFee, totalSoldNoFee, profit);
			
			coinPad.addHistoryTableRow(DateTime.now().toString(), pair, amount, price, fee, buy ? "B" : "S",
					fromCoinCapital, toCoinCapital);
			
			coinPad.updateChart(coinPad.chartRange);
			coinPad.updateCoinStatusPane();
		}
	}
}
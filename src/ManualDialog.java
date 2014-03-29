import javax.swing.*;

public class ManualDialog{
	CoinPad coinPad;
	JPanel frame;
	JLabel messageLabel;
	
	public ManualDialog(CoinPad theCoinPad){
		this.coinPad = theCoinPad;
		
		frame = new JPanel();
		frame.setSize(620, 640);
		
		messageLabel = new JLabel();
		messageLabel.setSize(620, 640);
		
		messageLabel.setText("<html><p><b>How do I simulate a trade?</b></p>" +
"<p>" +
"<p>First you will need to simulate a deposit of the currency that you wish to trade with. This can be done from \"File>Deposit\".</p>" +
"<p>Then you can make a trade by selecting the correct options and selecting all the values in the trade panel (at the</p>" +
"<p>top of the window) and clicking \"Trade\". You will be asked to confirm the trade, then all the relevant tables will be</p>" +
"<p>populated with the trade details.</p>" +
"<p>" +
"<p>" +
"<p><b>What is the portfolio table?</b></p>" +
"<p>" +
"<p>The portfolio table keeps track of the all the trades that have been made with a certain coin pairing. It also provides some</p>" +
"<p> useful metrics that will help you in deciding what trades to make in the future. Key's used in the amount column are: </p>" +
"<p>\"B\" = Buy</p>" +
"<p>\"S\" = Sell</p>" +
"<p>" +
"<p>" +
"<p><b>What is the history table?</b></p>" +
"<p>" +
"<p>The history table shows all the deposits, withdraws and trades made. Keys used in the amount column are:</p>" +
"<p>\"B\" = Buy</p>" +
"<p>\"S\" = Sell</p>" +
"<p>\"W\" = Withdraw</p>" +
"<p>\"D\" = Deposit</p>" +
"<p>" +
"<p>" +
"<p><b>What is the capital chart and how do I use it?</b></p>" +
"<p>" +
"<p>The capital chart shows how much you capital of a certain coin has changed over time. To select the time range of the </p>" +
"<p>chart use the buttons below it. The \"custom\" button allows you to change the time range and interval amount manually; you</p>" +
"<p> can do this using the arrow keys and applying the changes with the enter key.</p>" +
"<p>To change which coins are displayed tick the check box for the coin in the coin panel at the bottom of the screen.</p>" +
"<p>" +
"<p>" +
"<p><b>How can I change the color of coin check box, and add or remove coins and pairs?</p></b>" +
"<p>" +
"<p>You can do all this from the settings options in \"Edit > Settings\". When you delete a coin it will be marked for </p>" +
"<p>deletion with a line through it, but it will only be removed once you withdraw all the capital you have with that coin.</p></html>");
		
		frame.add(messageLabel);
	}
	
	public void showDialog(){
		JOptionPane.showMessageDialog(coinPad, frame, "Manual", JOptionPane.PLAIN_MESSAGE);
	}
}
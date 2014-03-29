import javax.swing.*;

public class AboutDialog{
	CoinPad coinPad;
	JPanel frame;
	JLabel messageLabel;
	
	public AboutDialog(CoinPad theCoinPad){
		this.coinPad = theCoinPad;
		
		frame = new JPanel();
		frame.setSize(620, 640);
		
		messageLabel = new JLabel();
		messageLabel.setSize(620, 640);
		
		messageLabel.setText("<html><p><b>CoinPad v0.1 alpha</b></p>" +
"<p>" +
"<p>Please send feedback to coinpadfeedback@gmail.com</p>");		
		frame.add(messageLabel);
	}
	
	public void showDialog(){
		JOptionPane.showMessageDialog(coinPad, frame, "About", JOptionPane.PLAIN_MESSAGE);
	}
}
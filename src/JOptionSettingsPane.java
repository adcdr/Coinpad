import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JOptionSettingsPane implements ActionListener{
	CoinPad coinPad;
	SpringLayout layout;
	JPanel frame, setColorPanel, missingBorderLinePanel;
	JList coinList, pairList;
	DefaultListModel coinListModel, pairListModel;
	JScrollPane coinScroll, pairScroll;
	int selectedCoinIndex, selectedPairIndex;
//	Settings settings;
	String windowTitle;
	JTextField  textField1, textField2, textField4, textField5, textField6;
	JButton deleteCoinButton, deletePairButton, addCoinButton, addPairButton, applyColorButton;
	JColorChooser colorChooser;
	JLabel selectedColorLabel, selectedColorLabel2;
	AbstractColorChooserPanel simpleColorChooser;
	HashMap coinColorCodeMap;
	CustomListCellRenderer coinListCellRenderer;
	
	public JOptionSettingsPane(CoinPad theCoinPad){
		this.coinPad = theCoinPad;
		windowTitle = "Settings";
		coinColorCodeMap = coinPad.coinColorCodeMap;
		
		//coin list
		coinListModel = new DefaultListModel();

		ArrayList<String> coinsArrayList = coinPad.db.getCoins();
		String [] coins = coinsArrayList.toArray(new String[coinsArrayList.size()]);
		
		for (int i=0; i<coins.length; i++){
			coinListModel.addElement(coins[i]);
		}
		
		coinList = new JList(coinListModel);
		CustomListSelectionListener coinListListener = new CustomListSelectionListener(this, "coin");
		coinList.addListSelectionListener(coinListListener);		
		coinList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		coinListCellRenderer = new CustomListCellRenderer(coinColorCodeMap);	
		coinList.setCellRenderer(coinListCellRenderer);
		
		coinScroll = new JScrollPane(coinList);		
		coinScroll.setPreferredSize(new Dimension(150, 170));
		
		selectedCoinIndex = -1;
		
		//pair list
		pairListModel = new DefaultListModel();
		
		ArrayList<String> pairsArrayList = coinPad.db.getPairs();		
		String [] pairs = pairsArrayList.toArray(new String[pairsArrayList.size()]);
		
		for (int i=0; i<pairs.length; i++){
			pairListModel.addElement(pairs[i]);
		}
		
		pairList = new JList(pairListModel);
		pairList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		CustomListSelectionListener pairListListener = new CustomListSelectionListener(this, "pair");
		pairList.addListSelectionListener(pairListListener);
		pairList.setCellRenderer(new CustomListCellRenderer(coinColorCodeMap));
		
		selectedPairIndex = -1;
		
		pairScroll = new JScrollPane(pairList);
		pairScroll.setPreferredSize(new Dimension(150, 170));		
		
		layout = new SpringLayout();
		
		frame = new JPanel();
		frame.setPreferredSize(new Dimension(375, 430));
		frame.setBackground(null);
		frame.setLayout(layout);
		
		setColorPanel = new JPanel();
		setColorPanel.setBorder(BorderFactory.createTitledBorder("Set Coin Color"));
		setColorPanel.setLayout(new SpringLayout());
		setColorPanel.setPreferredSize(new Dimension(364, 170));
		
		//draw the missing vertical line on border
		missingBorderLinePanel = new JPanel();
		missingBorderLinePanel.setBackground(new Color(184,207,229));
		missingBorderLinePanel.setPreferredSize(new Dimension(1, 110));
		
		deleteCoinButton = new JButton("Delete");
		deleteCoinButton.addActionListener(this);
		deleteCoinButton.setActionCommand("delete_coin");
		
		deletePairButton = new JButton("Delete");
		deletePairButton.addActionListener(this);
		deletePairButton.setActionCommand("delete_pair");
		
		addCoinButton = new JButton("Add");
		addCoinButton.addActionListener(this);
		addCoinButton.setActionCommand("add_coin");
		
		addPairButton = new JButton("Add");
		addPairButton.addActionListener(this);
		addPairButton.setActionCommand("add_pair");
		
		applyColorButton = new JButton("Apply Color");
		applyColorButton.addActionListener(this);
		applyColorButton.setActionCommand("apply_color");		
		applyColorButton.setPreferredSize(new Dimension(100, 20));
		
		textField1 = new JTextField("");
		textField1.setPreferredSize(new Dimension(340,30));
		
		textField2 = new JTextField("");
		textField2.setPreferredSize(new Dimension(340,30));
		
		textField4 = new JTextField("");
		textField4.setPreferredSize(new Dimension(40,30));
		
		textField5 = new JTextField("");
		textField5.setPreferredSize(new Dimension(40,30));
		
		textField6 = new JTextField("");
		textField6.setPreferredSize(new Dimension(40,30));
		
		selectedColorLabel = new JLabel("Coin: ");
		selectedColorLabel2 = new JLabel("[Select a coin.]");
		
		colorChooser = new JColorChooser(Color.gray);
		
		AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
            if (accp.getDisplayName().equals("Swatches")) {
            	simpleColorChooser = accp;
            }
        }
        
        simpleColorChooser.setOpaque(false);
        
        colorChooser.getSelectionModel().addChangeListener(new CustomChangeListener(this));
        
        createGUI();
	}
	
	private void createGUI(){
		JLabel label1 = new JLabel("Coins");
		JLabel label2 = new JLabel("Pairs");
		
		//Coins
		layout.putConstraint(SpringLayout.WEST, label1, 9, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, label1, 5, SpringLayout.NORTH, frame);
    	frame.add(label1, 0);
    	
    	layout.putConstraint(SpringLayout.WEST, coinScroll, 0, SpringLayout.WEST, label1);
    	layout.putConstraint(SpringLayout.NORTH, coinScroll, 5, SpringLayout.SOUTH, label1);
    	frame.add(coinScroll, 0);
    	
    	layout.putConstraint(SpringLayout.WEST, deleteCoinButton, 0, SpringLayout.WEST, label1);
    	layout.putConstraint(SpringLayout.NORTH, deleteCoinButton, 5, SpringLayout.SOUTH, coinScroll);
    	frame.add(deleteCoinButton, 0);
    	
    	layout.putConstraint(SpringLayout.WEST, addCoinButton, 5, SpringLayout.EAST, deleteCoinButton);
    	layout.putConstraint(SpringLayout.NORTH, addCoinButton, 5, SpringLayout.SOUTH, coinScroll);
    	frame.add(addCoinButton, 0);
    	
    	//Pairs
    	layout.putConstraint(SpringLayout.WEST, label2, 55, SpringLayout.EAST, coinScroll);
    	layout.putConstraint(SpringLayout.NORTH, label2, 5, SpringLayout.NORTH, frame);
    	frame.add(label2, 0);
    	
    	layout.putConstraint(SpringLayout.WEST, pairScroll, 0, SpringLayout.WEST, label2);
    	layout.putConstraint(SpringLayout.NORTH, pairScroll, 5, SpringLayout.SOUTH, label2);
    	frame.add(pairScroll, 0);
    	
    	layout.putConstraint(SpringLayout.WEST, deletePairButton, 0, SpringLayout.WEST, pairScroll);
    	layout.putConstraint(SpringLayout.NORTH, deletePairButton, 5, SpringLayout.SOUTH, pairScroll);
    	frame.add(deletePairButton, 0);

    	//add pair button
    	layout.putConstraint(SpringLayout.WEST, addPairButton, 5, SpringLayout.EAST, deletePairButton);
    	layout.putConstraint(SpringLayout.NORTH, addPairButton, 5, SpringLayout.SOUTH, pairScroll);
    	frame.add(addPairButton, 0);
    	
    	//---------------------------------------------------------------------------------------------
    	//Set Color Chooser
    	SpringLayout layout2 = new SpringLayout();
    	
    	setColorPanel.setLayout(layout2);
    	
    	layout2.putConstraint(SpringLayout.WEST, simpleColorChooser, 0, SpringLayout.WEST, setColorPanel);
    	layout2.putConstraint(SpringLayout.NORTH, simpleColorChooser, 0, SpringLayout.NORTH, setColorPanel);
    	setColorPanel.add(simpleColorChooser, 0);
    	
    	layout2.putConstraint(SpringLayout.WEST, selectedColorLabel, 5, SpringLayout.WEST, setColorPanel);
    	layout2.putConstraint(SpringLayout.NORTH, selectedColorLabel, 5, SpringLayout.SOUTH, simpleColorChooser);
    	setColorPanel.add(selectedColorLabel, 0);
    	
    	layout2.putConstraint(SpringLayout.WEST, selectedColorLabel2, 5, SpringLayout.EAST, selectedColorLabel);
    	layout2.putConstraint(SpringLayout.NORTH, selectedColorLabel2, 5, SpringLayout.SOUTH, simpleColorChooser);
    	setColorPanel.add(selectedColorLabel2, 0);

    	layout2.putConstraint(SpringLayout.WEST, applyColorButton, 250, SpringLayout.WEST, setColorPanel);
    	layout2.putConstraint(SpringLayout.NORTH, applyColorButton, 5, SpringLayout.SOUTH, simpleColorChooser);
    	setColorPanel.add(applyColorButton, 0);

    	//---------------------------------------------------------------------------------------------
    	
    	layout.putConstraint(SpringLayout.WEST, setColorPanel, 5, SpringLayout.WEST, frame);
    	layout.putConstraint(SpringLayout.NORTH, setColorPanel, 25, SpringLayout.SOUTH, addCoinButton);
    	frame.add(setColorPanel, 0);
    	
    	layout.putConstraint(SpringLayout.WEST, missingBorderLinePanel, -3, SpringLayout.EAST, setColorPanel);
    	layout.putConstraint(SpringLayout.NORTH, missingBorderLinePanel, 45, SpringLayout.SOUTH, addCoinButton);
    	frame.add(missingBorderLinePanel, 0);
	}
	
	public void showDialog(){
		Object[] options = {"Save", "Cancel"};
		
		int result = JOptionPane.showOptionDialog(coinPad, frame, windowTitle, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, "default");
		
		if (result == JOptionPane.OK_OPTION){ 	//Save settings		
			ArrayList<String> coinPadPairs = coinPad.db.getPairs();
			
			//save pairs in db
			for (int i=0; i<pairListModel.size(); i++){
				String nextPair = pairListModel.get(i).toString();
				
				if (nextPair.startsWith("¬")){
					//delete row from portfolio table
					coinPad.db.deleteProfileRow(nextPair.substring(1));
					
					//and from the list
					pairListModel.removeElement(nextPair);
				}
				else if (!coinPadPairs.contains(nextPair)){
					coinPad.db.insertInProfile(nextPair, 0.0f, 0.0f, true, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
				}
			}
			
			//update table
			coinPad.emptyPortfolioTable();
			coinPad.populatePortfolioTable();
			
			ArrayList<String> dbCoinsArrayList = coinPad.db.getCoins();
			String [] dbCoins = dbCoinsArrayList.toArray(new String[dbCoinsArrayList.size()]);
			
			//delete coin if 0 balance : else mark for deletion
			for (int i=0; i<dbCoins.length; i++){		
				if (!coinListModel.contains(dbCoins[i])){
					float capital = coinPad.db.getCoinCapital(dbCoins[i]);

					if (capital == 0.0f){
						//delete row from coin table
						coinPad.db.deleteCoinRow(dbCoins[i]);

						//and from the list
						coinListModel.removeElement("¬" + dbCoins[i]);
					}
					else {
						//add ¬ to start of pair name in db to mark for strikethrough font
						if (!dbCoins[i].startsWith("¬"))
							coinPad.db.updateCoinName(dbCoins[i], "¬" + dbCoins[i]);
					}
				}
			}
			
			//update coins in db
			for (int i=0; i<coinListModel.getSize(); i++){
				synchronized(coinPad.coinColorCodeMap){
					String nextCoin = coinListModel.getElementAt(i).toString();
					String oldCoin, nextCoinColorCode;
					
					if (nextCoin.startsWith("¬")) { //coin marked for deletion
						oldCoin = nextCoin.substring(1, 4);
						
						coinPad.db.updateCoinName(oldCoin, nextCoin);
					}
					else { //new coin or coin undeleted
						if (dbCoinsArrayList.contains("¬" + nextCoin)){ //undeletedd
							
							coinPad.db.updateCoinName("¬" + nextCoin, nextCoin);
						}
						else if (!dbCoinsArrayList.contains(nextCoin)) { //new coin
							nextCoinColorCode = (String) coinPad.coinColorCodeMap.get(nextCoin);
							
							coinPad.db.insertCoinRow(nextCoin, nextCoinColorCode);
							
							coinPad.db.insertCapitalRow(nextCoin, "D", 0.0f);
						}
					}
				}
			}
			
			//update coin panel
			Iterator it = coinColorCodeMap.entrySet().iterator();
			
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				coinPad.db.updateCoinColor((String)pairs.getKey(), (String)pairs.getValue());
			}
			
			coinPad.updateCoinStatusPane();
			coinPad.coinPairsCombo.setModel(new DefaultComboBoxModel(pairListModel.toArray()));
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "delete_coin"){
			if (selectedCoinIndex >= 0)	{
				String coin = coinList.getSelectedValue().toString();
						
				if (!coin.startsWith("¬")) {
					int result = JOptionPane.showOptionDialog(frame, 
							"Are you sure you want to delete coin: " + coin + "?",
							"Delete Pair", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, null, "default");
					
					if (result == JOptionPane.YES_OPTION){
						//delete coin in coin list
						String coinToRemove = coinListModel.getElementAt(selectedCoinIndex).toString();
						
						coinListModel.setElementAt("¬" + coinToRemove, selectedCoinIndex);
						
						//mark coin as deleted in pairs list
						for (int i=0; i<pairListModel.size(); i++){
							if (pairListModel.getElementAt(i).toString().startsWith(coinToRemove)
									|| pairListModel.getElementAt(i).toString().endsWith(coinToRemove)){
								String pairToChange = pairListModel.getElementAt(i).toString();
	
								pairListModel.set(i, "¬" + pairToChange);
								
								selectedColorLabel2.setForeground(Color.black);
								
								selectedColorLabel2.setText("[Select a coin.]");
							}
						}
					}
				}
			}
		}
		else if (e.getActionCommand() == "delete_pair"){
			if (selectedPairIndex >= 0) {
				String pair = pairListModel.get(selectedPairIndex).toString();
						
				if (!pair.startsWith("¬")) {
					int result = JOptionPane.showOptionDialog(frame, 
							"Are you sure you want to delete pair: " + pair + "?",
							"Delete Pair", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, null, "default");
					
					if (result == JOptionPane.YES_OPTION){
						//mark pair as deleted
						for (int i=0; i<pairListModel.size(); i++){
							if (pairListModel.getElementAt(i).toString().equals(pair)){	
								pairListModel.set(i, "¬" + pair);
							}
						}
					}
				}
			}
		}
		else if (e.getActionCommand() == "add_coin"){
			JOptionAddCoinDialog jOptionAddCoinDialog = new JOptionAddCoinDialog(this);
			
			jOptionAddCoinDialog.showDialog();
		}
		else if (e.getActionCommand() == "add_pair"){
			JOptionAddPairDialog jOptionPairDialog = new JOptionAddPairDialog(this);
			
			jOptionPairDialog.showDialog();
		}
		else if (e.getActionCommand() == "apply_color"){
			String coin = selectedColorLabel2.getText();
			Color color = selectedColorLabel2.getForeground();
			
			String hexColor = Integer.toHexString(color.getRGB() & 0xffffff);
			
			if (hexColor.length() < 6) {
				hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
			}
			
			coinColorCodeMap.put(coin, "#" + hexColor);
			
			coinListCellRenderer.refreshColorMap(coinColorCodeMap);
			
			coinList.revalidate();
			coinList.repaint();
		}
	}
}

class CustomChangeListener implements ChangeListener {
	JOptionSettingsPane optionPane;
	
	public CustomChangeListener(JOptionSettingsPane optionPane){
		this.optionPane = optionPane;
	}
	
    public void stateChanged(ChangeEvent e) {
    	if (optionPane.selectedColorLabel2.getText() != "[Select a coin.]")
    			optionPane.selectedColorLabel2.setForeground(optionPane.colorChooser.getColor());
    }
}

class CustomListSelectionListener implements ListSelectionListener {
	JOptionSettingsPane optionPane;
	String listName;
	
	public CustomListSelectionListener(JOptionSettingsPane optionPane, String listName){
		this.optionPane = optionPane;
		this.listName = listName;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = ((JList)e.getSource()).getSelectionModel();

        if (lsm.isSelectionEmpty()) {
            return;
        } else {
            // Find out which indices are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                	if (listName.equals("coin")){
                		optionPane.selectedCoinIndex = i;
	                	String selectedCoin = optionPane.coinList.getSelectedValue().toString();
	                	
	                	optionPane.selectedColorLabel2.setForeground(Color.decode((String)optionPane.coinPad.coinColorCodeMap.get(selectedCoin)));
	                	optionPane.selectedColorLabel2.setText(selectedCoin);
                	}
                	else if (listName.equals("pair")){
                		optionPane.selectedPairIndex = i;
                	}
                }
            }
        }
	}
}
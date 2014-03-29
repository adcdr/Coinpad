import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;


import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;

public class CoinPad extends JFrame implements ActionListener, DateTimeSelectorListener{
	DBConnection db;
	JLayeredPane layeredPane;
	JOptionSettingsPane settingsPane;
	JRadioButton buyRadio, sellRadio;
	JComboBox<String> coinPairsCombo;
	JFormattedTextField amountField, priceField, feeField;
	Vector portfolioTableData, historyTableData;
	DefaultTableModel portfolioTableModel, historyTableModel;
	CustomTableCellRenderer customTableCellRenderer;
	JLabel portfolioLabel, historyLabel, capitalLabel, statusBarLabel;
	ArrayList<String> coinsArrayList;
	volatile HashMap coinColorCodeMap;
	HashMap<String, Float> coinAmountMap;
	JScrollPane coinStatusPane;
	JPanel chartPanel, chartSettingsPanel;
	Chart chart;
	String chartRange = "day";
	DateTimeSelector fromDateTimeSelector, toDateTimeSelector;
	ChartIntervalSelector intervalsSelector;
	JToggleButton dayButton, customButton, monthButton, yearButton, allTimeButton;
	ArrayList<String> selectedCheckBoxes = new ArrayList<String>();
	JCheckBox [] checkBoxes;
	JMenuItem undoTradeMenuItem;
	Trade lastTrade;
	
	public CoinPad(){
		setTitle("CoinPad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
			BufferedImage iconImage = ImageIO.read(new File("icon.png"));
			setIconImage(iconImage);
		} catch (IOException e) {
			System.out.println("CoinPad");
			e.printStackTrace();
		}
        
        db = new DBConnection();
        
        //get coin colours and amounts
        coinsArrayList = db.getCoins();
        
        coinColorCodeMap = (HashMap<String, String>) db.getCoinColorCodeMap();      
		coinAmountMap = new HashMap<String, Float>();

		//populate coinAmount Map
		Iterator it = coinColorCodeMap.entrySet().iterator();
		int index = 0;
		
		while (it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			
			coinAmountMap.put((String) pairs.getKey(), 0.0f);
			index++;
		}
        settingsPane = new JOptionSettingsPane(this);
        
        //get screen size of primary monitor
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
        
        customTableCellRenderer = new CustomTableCellRenderer();
        
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(620, 640));
		layeredPane.setBackground(new Color(0xF3F3F3));
		layeredPane.setOpaque(true);
		
		setJMenuBar(createMenuBar());
		setVisible(true);
		
		JPanel buySellPanel = buySellGUI();
		buySellPanel.setBounds(0, 0, 620, 55);
		
		portfolioLabel = new JLabel("Portfolio");
		portfolioLabel.setForeground(Color.GRAY);
		portfolioLabel.setHorizontalAlignment(JLabel.CENTER);
		portfolioLabel.setBounds(0, 64, 610, 15);
		
		JPanel portfolioTablePanel = portfolioTable();
		portfolioTablePanel.setLayout(null); //removes the padding default FlowLayout has
		portfolioTablePanel.setBounds(10, 80, 700, 155);
		
		populatePortfolioTable();
		
		historyLabel = new JLabel("History");
		historyLabel.setForeground(Color.GRAY);
		historyLabel.setHorizontalAlignment(JLabel.CENTER);
		historyLabel.setBounds(0, 240, 610, 15);
		
		JPanel historyTablePanel = historyTable();
		historyTablePanel.setLayout(null); //removes the padding default FlowLayout has
		historyTablePanel.setBounds(10, 257, 700, 120);
		
		populateHistoryTable();
		
		chartSettingsPanel = createChartSettingsPanel();
		chartSettingsPanel.setBounds(0, 540, 620, 25);
		
		JLabel capitalLabel = new JLabel("Capital");
		capitalLabel.setForeground(Color.gray);
		capitalLabel.setHorizontalAlignment(JLabel.CENTER);
		capitalLabel.setBounds(0, 370, 610, 15);
		
		chartPanel = new JPanel();
		chartPanel.setOpaque(false); 
		updateChart("day");
		chartPanel.setBounds(0, 375, 620, 180);
		
		JPanel chartButtonsPanel = createChartButtons();
		chartButtonsPanel.setBounds(0, 565, 620, 35);
		
		coinStatusPane = coinStatusPane();
		coinStatusPane.setBounds(0, 600, 620, 40);
		
		statusBarLabel = new JLabel("<html>\u2191\u2193: <font color=\"gray\">Change value.</font>  " +
				"&nbsp&nbsp&nbsp \u2190\u2192: <font color=\"gray\">Change selection.</font>" +
				"&nbsp&nbsp&nbsp Esc: <font color=\"gray\">Default value.</font>" +
				"&nbsp&nbsp&nbsp Return: <font color=\"gray\">Apply changes.</font></html>");
		statusBarLabel.setHorizontalAlignment(JLabel.CENTER);
		statusBarLabel.setBackground(coinStatusPane.getBackground());
		statusBarLabel.setOpaque(true);
		statusBarLabel.setBackground(new Color(0xDEDEDE));
		statusBarLabel.setBounds(0, 640, 620, 20);
		
		layeredPane.add(buySellPanel, 2);
		layeredPane.add(portfolioLabel, 2);
		layeredPane.add(portfolioTablePanel, 2);
		layeredPane.add(historyLabel, 2);
		layeredPane.add(historyTablePanel, 2);
		layeredPane.add(capitalLabel, 2);
		layeredPane.add(chartPanel, 2);
		layeredPane.add(chartSettingsPanel, 2);
		layeredPane.add(chartButtonsPanel, 2);
		layeredPane.add(coinStatusPane, 1);
		layeredPane.add(statusBarLabel, 2);
		
		add(layeredPane);
		
		pack();
        setVisible(true);
	}
	
	public void undoTrade(Trade lastTrade){
		float cost = lastTrade.rate * lastTrade.amount;
		
		//update profile table
		db.updateProfile(lastTrade.pair, lastTrade.amount, cost, lastTrade.buy, 
				lastTrade.lastSellPrice, lastTrade.averageSellPrice, lastTrade.totalSoldFee, 
				lastTrade.totalSoldNoFee, lastTrade.lastBuyPrice, lastTrade.averageBuyPrice, 
				lastTrade.totalBoughtFee, lastTrade.totalBoughtNoFee, lastTrade.profit);
		
		//undo capital table for both coins
		String coins[] = lastTrade.pair.split("/");
		
		db.insertCapitalRow(coins[0], lastTrade.buy ? "B" : "S", -lastTrade.amount); //from coin
		db.insertCapitalRow(coins[1], lastTrade.buy ? "S" : "B", -lastTrade.amount); //to coin
		
		//delete from history table		
		DateTime lastCoinHistoryStamp = db.getLastPairHistoryStamp(lastTrade.pair); 
		db.deleteLastHistoryRow(lastCoinHistoryStamp);
		
		//update JTables
		addPortfolioTableRow(lastTrade.pair, lastTrade.amount, cost, lastTrade.buy, lastTrade.lastBuyPrice, 
				lastTrade.lastSellPrice, lastTrade.averageBuyPrice, lastTrade.averageSellPrice, lastTrade.totalBoughtNoFee, 
				lastTrade.totalSoldNoFee, lastTrade.profit);
		
		for (int i=historyTableModel.getRowCount()-1; i>0; i--){
			if (historyTableModel.getValueAt(i, 0).equals(lastCoinHistoryStamp.toString())){
				historyTableModel.removeRow(i);
				historyTableModel.fireTableDataChanged();
				break;
			}
		}
		
		updateChart(chartRange);
		updateCoinStatusPane();
	}
	
	public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        //File menu
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        
        JMenuItem menuItem = new JMenuItem("Import");
        /*menuItem.setActionCommand("import");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Export");
        menuItem.setActionCommand("export");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();*/
        
        menuItem = new JMenuItem("New");
        menuItem.setActionCommand("new");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Deposit");
        menuItem.setActionCommand("deposit");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Withdraw");
        menuItem.setActionCommand("withdraw");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuItem.setActionCommand("exit");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        //Edit menu
        menu = new JMenu("Edit");
        menu.addActionListener(this);
        menuBar.add(menu);
        
        undoTradeMenuItem = new JMenuItem("Undo Trade");
        /*undoTradeMenuItem.setActionCommand("undo_trade");
        undoTradeMenuItem.addActionListener(this);
        undoTradeMenuItem.setEnabled(false);
        menu.add(undoTradeMenuItem);*/
        
        menuItem = new JMenuItem("Settings");
        menuItem.setActionCommand("settings");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        //Help menu
        menu = new JMenu("Help");
        menu.setActionCommand("help");
        menu.addActionListener(this);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Manual");
        menuItem.setActionCommand("manual");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("About");
        menuItem.setActionCommand("about");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }
	
	public JPanel buySellGUI(){
		JPanel panel =  new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
		panel.setOpaque(true);
		panel.setBackground(new Color(0xDEDEDE));
		
		JLabel buySellLabel = new JLabel("Buy/Sell", JLabel.CENTER);
		buySellLabel.setBounds(75, 5, 60, 20);
		buySellLabel.setForeground(Color.GRAY);
		
		Font plainFont = new Font("Arial", Font.PLAIN, 11);
		
		buyRadio = new JRadioButton("Buy");
		buyRadio.setActionCommand("buy");
		buyRadio.setFont(plainFont);
		buyRadio.setSelected(true);
		buyRadio.setOpaque(false);
		
		sellRadio = new JRadioButton("Sell");
		sellRadio.setActionCommand("sell");
		sellRadio.setFont(plainFont);
		sellRadio.setOpaque(false);
		
		ButtonGroup buySellGroup = new ButtonGroup();
		buySellGroup.add(buyRadio);
		buySellGroup.add(sellRadio);
		
		JPanel radioPanel = new JPanel(new GridLayout(1, 0));
		radioPanel.add(buyRadio);
		radioPanel.add(sellRadio);
		radioPanel.setBounds(60, 23, 90, 20);
		radioPanel.setOpaque(false);
		
		JLabel coinLabel = new JLabel("Type", JLabel.CENTER);
		coinLabel.setBounds(160, 5, 80, 20);
		coinLabel.setForeground(Color.GRAY);
		
		Object[] pairsObject = db.getPairs().toArray();
		String[] pairs = Arrays.copyOf(pairsObject, pairsObject.length, String[].class);
		coinPairsCombo = new JComboBox<String>(pairs);
		if (coinPairsCombo.getItemCount() > 0)
			coinPairsCombo.setSelectedIndex(0);
		coinPairsCombo.setBounds(160, 25, 80, 20);
		
		JLabel amountLabel = new JLabel("Amount", JLabel.CENTER);
		amountLabel.setBounds(260, 5, 60, 20);
		amountLabel.setForeground(Color.GRAY);
		
		amountField = new JFormattedTextField();
		DocumentFilter onlyFloatFilter = new FloatDocumentFilter();
        ((AbstractDocument)amountField.getDocument()).setDocumentFilter(onlyFloatFilter);
		amountField.setBounds(260, 25, 60, 20);
		
		JLabel priceLabel = new JLabel("Rate", JLabel.CENTER);
		priceLabel.setBounds(330, 5, 60, 20);
		priceLabel.setForeground(Color.GRAY);
		
		priceField = new JFormattedTextField();
		((AbstractDocument)priceField.getDocument()).setDocumentFilter(onlyFloatFilter);
		priceField.setBounds(330, 25, 60, 20);
		
		JLabel feeLabel = new JLabel("Fee %", JLabel.CENTER);
		feeLabel.setBounds(400, 5, 60, 20);
		feeLabel.setForeground(Color.GRAY);
		
		feeField = new JFormattedTextField();
		DocumentFilter percentageFilter = new PercentageDocumentFilter();
        ((AbstractDocument)feeField.getDocument()).setDocumentFilter(percentageFilter);
		feeField.setBounds(400, 25, 60, 20);
		
		JButton tradeButton = new JButton("Trade");
		tradeButton.setBounds(480, 20, 70, 25);
		tradeButton.setActionCommand("trade");
		tradeButton.addActionListener(this);
		
		panel.add(buySellLabel);
		panel.add(radioPanel);
		panel.add(coinLabel);
		panel.add(coinPairsCombo);
		panel.add(amountLabel);
		panel.add(amountField);
		panel.add(priceLabel);
		panel.add(priceField);
		panel.add(feeLabel);
		panel.add(feeField);
		panel.add(tradeButton);
		
		return panel;
	}
	
	public JPanel createChartSettingsPanel(){
		JPanel panel = new JPanel();
		
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		
		panel.setLayout(flowLayout);
		panel.setOpaque(false);
		
		JLabel chartFromLabel = new JLabel("From: ");
		chartFromLabel.setForeground(Color.GRAY);
		panel.add(chartFromLabel);
		
		fromDateTimeSelector = new DateTimeSelector();
		fromDateTimeSelector.addDateTimeListener(this);
		fromDateTimeSelector.setActionCommand("from");
		panel.add(fromDateTimeSelector.frame);
		
		panel.add(Box.createHorizontalStrut(20));
		
		JLabel chartToLabel = new JLabel("To: ");
		chartToLabel.setForeground(Color.GRAY);
		panel.add(chartToLabel);
		
		toDateTimeSelector = new DateTimeSelector();
		toDateTimeSelector.setMinimum(fromDateTimeSelector.getDateTime());
		toDateTimeSelector.addDateTimeListener(this);
		toDateTimeSelector.setActionCommand("to");
		panel.add(toDateTimeSelector.frame);
		
		fromDateTimeSelector.setMaximum(toDateTimeSelector.getDateTime());
		
		panel.add(Box.createHorizontalStrut(20));
		
		JLabel intervalSelectorLabel = new JLabel("Intervals: ");
		intervalSelectorLabel.setForeground(Color.GRAY);
		panel.add(intervalSelectorLabel);
		
		intervalsSelector = new ChartIntervalSelector();
		intervalsSelector.addDateTimeListener(this);
		panel.add(intervalsSelector.frame);
		
		return panel;
	}

	public JScrollPane coinStatusPane(){
		JPanel panel =  new JPanel();
		GridLayout gridLayout = new GridLayout(1, 6);
		panel.setLayout(gridLayout);
		panel.setBackground(new Color(0xDEDEDE));
		
		JScrollPane scrollPane = new JScrollPane(panel);		
		scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));	
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		//get coin names
		String [] coins = coinsArrayList.toArray(new String[coinsArrayList.size()]);
		Font font = new Font("helvetica", Font.BOLD, 12);
		Map  attributes = font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		Font strikeThroughFont = new Font(attributes);
		
		coinColorCodeMap = db.getCoinColorCodeMap();      
		coinAmountMap = db.getCoinAmountMap();	
		checkBoxes = new JCheckBox[coins.length];
		
		for (int i=0; i<coins.length; i++){
			String coin = coins[i];
			float amount = coinAmountMap.get(coin);

			if (amount != 0){
				if (coin.startsWith("¬")) {
					checkBoxes[i] = new JCheckBox();
					checkBoxes[i].setFont(strikeThroughFont);
					checkBoxes[i].setText(coin.substring(1, 4) + " [" + amount + "]");
				}
				else {
					checkBoxes[i] = new JCheckBox(coin + " [" + amount + "]");
				}
				
				checkBoxes[i].addActionListener(this);
				checkBoxes[i].setActionCommand(coin);
				checkBoxes[i].setOpaque(false);
			}
			else {
				checkBoxes[i] = new JCheckBox(coin + " [0]");

				checkBoxes[i].addActionListener(this);
				checkBoxes[i].setActionCommand(coin);
				checkBoxes[i].setOpaque(false);
			}
			
			if (gridLayout.getColumns()> (i/6)){
				gridLayout.setColumns((i/6)+1);
			}
			
			Color coinColor = Color.decode((String)coinColorCodeMap.get(coin));
			
			checkBoxes[i].setForeground(coinColor);
			
			panel.add(checkBoxes[i]);
			panel.revalidate();
		}
		
		//get the selected coins and make sure they are checked
		selectedCheckBoxes = db.getSelectedCoins();
		
		for (int j=0; j<checkBoxes.length; j++){
			if (selectedCheckBoxes.contains(checkBoxes[j].getText().substring(0, 3)))
				checkBoxes[j].setSelected(true);
			else
				checkBoxes[j].setSelected(false);
		}
		
		return scrollPane;
	}
	
	public void updateCoinStatusPane(){
		JPanel panel =  new JPanel();
		GridLayout gridLayout = new GridLayout(1, 6);
		panel.setLayout(gridLayout);
		panel.setBackground(new Color(0xDEDEDE));
		
		coinStatusPane = new JScrollPane(panel);
		coinStatusPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		coinStatusPane.setBorder(null);
		coinStatusPane.setBounds(0, 600, 620, 40);
		coinStatusPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
		
		layeredPane.remove(1);
		
		//get coin names
		ArrayList<String> coinsArrayList = db.getCoins();
		String [] coins = coinsArrayList.toArray(new String[coinsArrayList.size()]);
		Font font = new Font("helvetica", Font.BOLD, 12);
		Map  attributes = font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		Font strikeThroughFont = new Font(attributes);
		
		coinColorCodeMap = (HashMap<String, String>) db.getCoinColorCodeMap();      
		coinAmountMap = db.getCoinAmountMap();
		
		checkBoxes = new JCheckBox[coins.length];
		
		for (int i=0; i<coins.length; i++){
			String coin = coins[i];
			float amount = coinAmountMap.get(coin);
			
			if (amount != 0){
				if (coin.startsWith("¬")) {
					checkBoxes[i] = new JCheckBox();
					checkBoxes[i].setFont(strikeThroughFont);
					checkBoxes[i].setText(coin.substring(1, 4) + " [" + amount + "]");
				}
				else {
					checkBoxes[i] = new JCheckBox(coin + " [" + amount + "]");
				}
				
				checkBoxes[i].addActionListener(this);
				checkBoxes[i].setActionCommand(coin);
				checkBoxes[i].setOpaque(false);
			}
			else {
				checkBoxes[i] = new JCheckBox(coin + " [0]");

				checkBoxes[i].addActionListener(this);
				checkBoxes[i].setActionCommand(coin);
				checkBoxes[i].setOpaque(false);
			}
			
			if (gridLayout.getColumns()> (i/6)){
				gridLayout.setColumns((i/6)+1);
			}
			
			Color coinColor = Color.decode((String)coinColorCodeMap.get(coin));
			
			checkBoxes[i].setForeground(coinColor);
			
			panel.add(checkBoxes[i]);
			panel.revalidate();
		}
		
		//get the selected coins and make sure they are checked
		selectedCheckBoxes = db.getSelectedCoins();
		
		for (int j=0; j<checkBoxes.length; j++){
			if (selectedCheckBoxes.contains(checkBoxes[j].getText().substring(0, 3)))
				checkBoxes[j].setSelected(true);
			else
				checkBoxes[j].setSelected(false);
		}
		
		layeredPane.add(coinStatusPane, 1);
		
		pack();
	}
	
	public JPanel portfolioTable(){
		JPanel frame = new JPanel();
		portfolioTableData = new Vector(); //table data
		String[] headersArray = {"Type", "Amount", "Cost", "Last Buy", "Last Sell", "Avg. Buy", "Avg. Sell", "\u03A3 Bought", "\u03A3 Sold", "Profit %"};
		Vector headersVector = new Vector(Arrays.asList(headersArray));
		portfolioTableModel = new DefaultTableModel(portfolioTableData, headersVector);
		JTable table = new JTable(portfolioTableModel){
			public boolean isCellEditable(int row, int column) {                
            	return false;               
			};
		};
		
		frame.setOpaque(false);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 600, 151);
		
		JTableHeader header = table.getTableHeader();
		header.setBackground(new Color(200, 200, 200));
		CustomTableHeaderRenderer customTableHeaderRenderer = new CustomTableHeaderRenderer(header.getDefaultRenderer());
		
		for (int i=0; i<table.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setCellRenderer(customTableCellRenderer);
		}
		
		table.setGridColor(new Color(209, 209, 209));
		
		//sort rows descending alphabetical order using first column
		table.setAutoCreateRowSorter(true);	
		DefaultRowSorter sorter = ((DefaultRowSorter)table.getRowSorter());
    	ArrayList list = new ArrayList();
    	list.add( new RowSorter.SortKey(0, SortOrder.ASCENDING) );
    	sorter.setSortKeys(list);
    	sorter.sort();
		
		frame.add(scrollPane);
				
		return frame;
	}
	
	public void emptyPortfolioTable(){
		portfolioTableModel.setRowCount(0);
	}
	
	public void populatePortfolioTable(){
		ResultSet rs = db.getProfileRows();
		String pair;
		boolean buy;
		float volume, cost, lastBuy, lastSell, averageSellPrice, totalSold, 
				averageBuyPrice, totalBought, totalAmount, profit;

		//get data from SQL query
		try {
			while (rs.next()){
				pair = rs.getString(1);
				volume = rs.getFloat(2);
				cost = rs.getFloat(3);
				buy = rs.getBoolean(4);
				lastSell = rs.getFloat(5);
				averageSellPrice = rs.getFloat(6);
				totalSold = rs.getFloat(7);
				lastBuy = rs.getFloat(8);
				averageBuyPrice = rs.getFloat(9);
				totalBought = rs.getFloat(10);
				profit = rs.getFloat(11);
				
				addPortfolioTableRow(pair, volume, cost, buy, lastBuy, lastSell, averageBuyPrice, averageSellPrice, totalBought, totalSold, profit);
			}
		} catch (SQLException e) {
			System.out.println("populateTable - SQLException");
			e.printStackTrace();
		}
	}
	
	public void addPortfolioTableRow(String pair, float volume, float cost, boolean buy, float lastBuyPrice, float lastSellPrice,
			float averageBuyPrice, float averageSellPrice, float totalBought, float totalSold, float profit){
		//if the table already contains the coin update the row
		int coinRow = -1; 
		
		for (int i=0; i<portfolioTableModel.getRowCount(); i++){
			if (portfolioTableModel.getValueAt(i, 0).equals(pair)){
				coinRow = i;
				break;
			}
		}
		
		if (coinRow >= 0){ //coin already exists
			portfolioTableModel.setValueAt(pair, coinRow, 0);
			if (buy)
				portfolioTableModel.setValueAt(Float.toString(volume) + " B", coinRow, 1);
			else
				portfolioTableModel.setValueAt(Float.toString(volume) + " S", coinRow, 1);
			portfolioTableModel.setValueAt(Float.toString(cost), coinRow, 2);
			portfolioTableModel.setValueAt(lastBuyPrice, coinRow, 3);
			portfolioTableModel.setValueAt(lastSellPrice, coinRow, 4);
			portfolioTableModel.setValueAt(averageBuyPrice, coinRow, 5);
			portfolioTableModel.setValueAt(averageSellPrice, coinRow, 6);
			portfolioTableModel.setValueAt(totalBought, coinRow, 7);
			portfolioTableModel.setValueAt(totalSold, coinRow, 8);
			portfolioTableModel.setValueAt(profit, coinRow, 9);
		}
		else {
			Object[] rowData = new Object[10];
			
			rowData[0] = pair;
			if (buy)
				rowData[1] = Float.toString(volume) + " B";
			else
				rowData[1] = Float.toString(volume) + " S";
			rowData[2] = Float.toString(cost);
			rowData[3] = lastBuyPrice;
			rowData[4] = lastSellPrice;
			rowData[5] = averageBuyPrice;
			rowData[6] = averageSellPrice;
			rowData[7] = totalBought;
			rowData[8] = totalSold;
			rowData[9] = profit;
			
			Vector rowVector = new Vector(Arrays.asList(rowData));
			
			portfolioTableData.add(rowVector);
		}
		
		portfolioTableModel.fireTableDataChanged();
	}

	public JPanel historyTable(){
		JPanel frame = new JPanel();
		historyTableData = new Vector(); //table data
		String[] headersArray = {"Time", "Type", "Amount", "Rate", "Fee %", "Capital"}; //column names
		Vector headersVector = new Vector(Arrays.asList(headersArray));
		historyTableModel = new DefaultTableModel(historyTableData, headersVector);
		JTable table = new JTable(historyTableModel){
			public boolean isCellEditable(int row, int column) {                
            	return false;               
			};
		};
		
		frame.setOpaque(false);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 600, 105);
		
		table.setFillsViewportHeight(true); // fill scrollpane with empty rows
		
		JTableHeader header = table.getTableHeader();
		header.setBackground(new Color(200, 200, 200));
		CustomTableHeaderRenderer customTableHeaderRenderer = new CustomTableHeaderRenderer(header.getDefaultRenderer());
		
		for (int i=0; i<table.getColumnCount(); i++){
			table.getColumnModel().getColumn(i).setCellRenderer(customTableCellRenderer);
		}
		
		table.getColumnModel().getColumn(0).setMinWidth(120);
		
		table.setGridColor(new Color(209, 209, 209));
		
		//sort rows descending alphabetical order using first column
		table.setAutoCreateRowSorter(true);
		DefaultRowSorter sorter = ((DefaultRowSorter)table.getRowSorter());
    	ArrayList list = new ArrayList();
    	list.add( new RowSorter.SortKey(0, SortOrder.DESCENDING) );
    	sorter.setSortKeys(list);
    	sorter.sort();
		
		frame.add(scrollPane);
				
		return frame;
	}
	
	public void populateHistoryTable(){
		String coin, time, type;
		float amount, price, fee, fromCapital, toCapital;
		
		ResultSet rs = db.getHistoryRows();

		//get data from SQL query
		try {
			while (rs.next()){
				coin = rs.getString(1);
				type = rs.getString(2);
				amount = rs.getFloat(3);
				price = rs.getFloat(4);
				fee = rs.getFloat(5);
				fromCapital = rs.getFloat(6);
				toCapital = rs.getFloat(7);
				time = rs.getTimestamp(8).toString();
				
				addHistoryTableRow(time, coin, amount, price, fee, type, fromCapital, toCapital);
			}
		} catch (SQLException e) {
			System.out.println("populateHistoryTable - SQLException");
			e.printStackTrace();
		}
	}
	
	public void addHistoryTableRow(String time, String coin, float amount, float price, float fee, String type, float fromCapital, float toCapital){		
		Object[] rowData = new Object[9];
			
		rowData[0] = time;
		rowData[1] = coin;
		rowData[2] = Float.toString(amount) + " " + type;
		if (price == -1.0f)
			rowData[3] = "N/A";
		else
			rowData[3] = price;
		rowData[4] = fee;
		if (coin.length() > 3) //is a pair
			rowData[5] = fromCapital + "/" + toCapital;
		else
			rowData[5] = fromCapital;
			
		Vector rowVector = new Vector(Arrays.asList(rowData));
			
		historyTableData.add(rowVector);
		
		historyTableModel.fireTableDataChanged();
	}
	
	public void updateChart(String chartRange){
		this.chartRange = chartRange;
		
		DateTime startDateTime = DateTime.now();
		DateTime finishDateTime = DateTime.now();
		
		int tickMinutes = 0;
		int intervals = 1;
		
		if (chartRange.equals("day")){
			tickMinutes = 60;
			intervals = 24;
			
			startDateTime = finishDateTime.minusDays(1);
		}
		else if (chartRange.equals("month")){
			tickMinutes = 24*60;
			
			startDateTime = finishDateTime.minusMonths(1);
			
			intervals = Days.daysBetween(startDateTime, finishDateTime).getDays();
		}
		else if (chartRange.equals("year")){
			tickMinutes = 10*24*60;
			intervals = 35;
			
			startDateTime = finishDateTime.minusYears(1);
		}
		else if (chartRange.equals("alltime")){
			int daysDiff = Days.daysBetween(db.getFirstTradeDateTime(), finishDateTime).getDays();
			
			if (chartRange.equals("day")){
				tickMinutes = 60;
				intervals = 24;
				
				startDateTime = finishDateTime.minusDays(1);
			}
			else if (chartRange.equals("month")){
				tickMinutes = 24*60;
				
				startDateTime = finishDateTime.minusMonths(1);
				
				intervals = Days.daysBetween(startDateTime, finishDateTime).getDays();
			}
			else {
				tickMinutes = 10*24*60;
				intervals = 35;
				
				startDateTime = finishDateTime.minusYears(1);
			}
		}
		else if (chartRange.equals("custom")){
			startDateTime = fromDateTimeSelector.getDateTime();
			finishDateTime = toDateTimeSelector.getDateTime();
			
			int minutesDiff = Minutes.minutesBetween(startDateTime, finishDateTime).getMinutes();
			
			tickMinutes = minutesDiff / intervalsSelector.interval;
			intervals = intervalsSelector.interval;
		}
		
		selectedCheckBoxes = db.getSelectedCoins();

		//create data points for each selected coin
		float capPerTickCollection[][] = new float[selectedCheckBoxes.size()][intervals];
		Color[] colorCollection = new Color[selectedCheckBoxes.size()];
		DateTime dateTime1 = finishDateTime;
		DateTime dateTime2 = finishDateTime;
		
		for (int i=0; i<selectedCheckBoxes.size(); i++){
			String coin = selectedCheckBoxes.get(i);
		
			//set the color for each coin
			colorCollection[i] = Color.decode((String)coinColorCodeMap.get(coin));
			
			//set the capital for each coin in each interval
			//(work way towards present)
			dateTime1 = finishDateTime;
			dateTime2 = finishDateTime.minusMinutes(tickMinutes);
			
			for (int j=0; j<intervals; j++){
				capPerTickCollection[i][j] = db.getCoinCapitalInTimeRange(coin, dateTime1, dateTime2);

				dateTime1 = dateTime1.minusMinutes(tickMinutes);
				dateTime2 = dateTime2.minusMinutes(tickMinutes);
			}
		}
		
		fromDateTimeSelector.notSaved = false;
		fromDateTimeSelector.setDateTime(startDateTime);
		toDateTimeSelector.notSaved = false;
		toDateTimeSelector.setDateTime(finishDateTime);
		intervalsSelector.setIntervals(intervals);
		
		chart = new Chart(colorCollection, capPerTickCollection);
		
		if (chartPanel != null)
			chartPanel.removeAll();
		
		chartPanel.add(chart.chartPanel);
		
		revalidate();
		repaint();
	}
	
	public JPanel createChartButtons(){
		JPanel buttonPanel = new JPanel();
		
		dayButton = new JToggleButton("Last Day");
		dayButton.setPreferredSize(new Dimension(100, 20));
		dayButton.addActionListener(this);
		dayButton.setActionCommand("last_day");
		dayButton.setSelected(true);
		
		monthButton = new JToggleButton("Last Month");
		monthButton.setPreferredSize(new Dimension(100, 20));
		monthButton.addActionListener(this);
		monthButton.setActionCommand("last_month");
		
		yearButton = new JToggleButton("Last Year");
		yearButton.setPreferredSize(new Dimension(100, 20));
		yearButton.addActionListener(this);
		yearButton.setActionCommand("last_year");
		
		allTimeButton = new JToggleButton("All Time");
		allTimeButton.setPreferredSize(new Dimension(100, 20));
		allTimeButton.addActionListener(this);
		allTimeButton.setActionCommand("all_time");
		
		customButton = new JToggleButton("Custom");
		customButton.setPreferredSize(new Dimension(100, 20));
		customButton.addActionListener(this);
		customButton.setActionCommand("custom");
		
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setOpaque(false);
		
		buttonPanel.add(dayButton);
		buttonPanel.add(monthButton);
		buttonPanel.add(yearButton);
		buttonPanel.add(allTimeButton);
		buttonPanel.add(customButton);
		
		return buttonPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("new")){
			JOptionNewDialog newDialog = new JOptionNewDialog(this);
			newDialog.showDialog();
		}
		else if (e.getActionCommand().equals("trade")){
			if (amountField.getText().trim().isEmpty())
				JOptionPane.showMessageDialog(this, "Please enter an amount.", "Trade Error", 
						JOptionPane.PLAIN_MESSAGE);
			else if (priceField.getText().trim().isEmpty())
				JOptionPane.showMessageDialog(this, "Please enter a price.", "Trade Error", 
						JOptionPane.PLAIN_MESSAGE); 
			else if (feeField.getText().trim().isEmpty())
				JOptionPane.showMessageDialog(this, "Please enter a fee.", "Trade Error", 
						JOptionPane.PLAIN_MESSAGE);
			else {
				String fromCoin = coinPairsCombo.getSelectedItem().toString().substring(0, 3);;
				
				if (buyRadio.isSelected())
					fromCoin = coinPairsCombo.getSelectedItem().toString().substring(4, 7);;
					
				float fromCoinCapital = db.getCoinCapital(fromCoin);
				
				if (fromCoinCapital < Float.valueOf(amountField.getText()))
					JOptionPane.showMessageDialog(this, "Please make a deposit to complete the trade.\n" +
							"You have " + fromCoinCapital + " " + fromCoin + ".\n" +
							"This trade requires " +amountField.getText()+ " " +fromCoin+ ".",
							"Trade Error", 
							JOptionPane.PLAIN_MESSAGE);
				else {
					JOptionTradeDialog tradeDialog = new JOptionTradeDialog(this);
					tradeDialog.showDialog();
				}
			}
			
			return;
		}
		else if (e.getActionCommand().equals("deposit")){
			JOptionDepositDialog depositDialog = new JOptionDepositDialog(this);
			depositDialog.showDialog();
		}
		else if (e.getActionCommand().equals("withdraw")){
			JOptionWithdrawDialog withdrawDialog = new JOptionWithdrawDialog(this);
			withdrawDialog.showDialog();
		}
		else if (e.getActionCommand().equals("exit")){
			System.exit(0);
		}
		else if (e.getActionCommand().equals("undo_trade")){
			if (undoTradeMenuItem.isEnabled())
				undoTrade(lastTrade);
			
			return;
		}
		else if (e.getActionCommand().equals("buy")){
			sellRadio.setSelected(false);
			
			return;
		}
		else if (e.getActionCommand().equals("sell")){
			buyRadio.setSelected(false);
			
			return;
		}
		else if (e.getActionCommand().equals("settings")){
			settingsPane.showDialog();
			
			return;
		}
		else if (e.getActionCommand().equals("manual")){
			ManualDialog manualDialog = new ManualDialog(this);
			manualDialog.showDialog();
			
			return;
		}
		else if (e.getActionCommand().equals("about")){
			AboutDialog aboutDialog = new AboutDialog(this);
			aboutDialog.showDialog();
			
			return;
		}
		else if (e.getActionCommand().equals("last_day")){
			monthButton.setSelected(false);
			yearButton.setSelected(false);
			allTimeButton.setSelected(false);
			customButton.setSelected(false);
			
			updateChart("day");
		}
		else if (e.getActionCommand().equals("last_month")){
			dayButton.setSelected(false);
			yearButton.setSelected(false);
			allTimeButton.setSelected(false);
			customButton.setSelected(false);
			
			updateChart("month");
		}
		else if (e.getActionCommand().equals("last_year")){
			dayButton.setSelected(false);
			monthButton.setSelected(false);
			allTimeButton.setSelected(false);
			customButton.setSelected(false);
			
			updateChart("year");
		}
		else if (e.getActionCommand().equals("all_time")){
			dayButton.setSelected(false);
			monthButton.setSelected(false);
			yearButton.setSelected(false);
			customButton.setSelected(false);
			
			updateChart("alltime");
		}
		else if (coinsArrayList.contains(e.getActionCommand())){ //coin check box was selected
			JCheckBox checkBox = (JCheckBox) e.getSource();
			
			if (checkBox.isSelected()){
				selectedCheckBoxes.add(e.getActionCommand());
				db.setCoinSelected(e.getActionCommand(), true);
			}
			else {
				selectedCheckBoxes.remove(e.getActionCommand());
				db.setCoinSelected(e.getActionCommand(), false);
			}
			
			updateChart(chartRange);
			
			return;
		}
		else if (e.getActionCommand().equals("custom")){
			dayButton.setSelected(false);
			monthButton.setSelected(false);
			yearButton.setSelected(false);
			allTimeButton.setSelected(false);
			customButton.setSelected(true);
			
			if (layeredPane.getHeight() <= 640) { //status bar not visible
				//show the status bar
				layeredPane.setPreferredSize(new Dimension(620, 660));
				pack();
			}
			
			fromDateTimeSelector.dayTextArea.requestFocus(true);
			
			updateChart("custom");
			
			return;
		}
		
		if (layeredPane.getHeight() >= 660) { //status bar visible
			//hide the status bar
			layeredPane.setPreferredSize(new Dimension(620, 640));
			pack();
		}
	}
	
	public void dateTimeSelectorEventOccurred(DateTimeSelectorEvent e){
		if (e.getSource().getClass() == DateTimeSelector.class){
			toDateTimeSelector.setMinimum(fromDateTimeSelector.getDateTime());
			fromDateTimeSelector.setMaximum(toDateTimeSelector.getDateTime());
			
			if (e.getMessage().equals("return")){
				dayButton.setSelected(false);
				monthButton.setSelected(false);
				yearButton.setSelected(false);
				allTimeButton.setSelected(false);
				customButton.setSelected(true);
				
				updateChart("custom");
			}
			else if (e.getMessage().equals("changed")){
				dayButton.setSelected(false);
				monthButton.setSelected(false);
				yearButton.setSelected(false);
				allTimeButton.setSelected(false);
				customButton.setSelected(true);
				
				if (layeredPane.getHeight() <= 640) { //status bar not visible
					//show the status bar
					layeredPane.setPreferredSize(new Dimension(620, 660));
					pack();
				}
			}
		}
		else if (e.getSource().getClass() == ChartIntervalSelector.class){
			if (e.getMessage().equals("return")){
				dayButton.setSelected(false);
				monthButton.setSelected(false);
				yearButton.setSelected(false);
				allTimeButton.setSelected(false);
				customButton.setSelected(true);
				
				updateChart("custom");
				
				if (layeredPane.getHeight() <= 640) { //status bar not visible
					//show the status bar
					layeredPane.setPreferredSize(new Dimension(620, 660));
					pack();
				}
			}
		}
	}
	
	public static void main(String args[]){
		CoinPad coinPad = new CoinPad();
	}
}

import java.io.File;
import java.io.FileFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.hsqldb.Server;
import org.joda.time.DateTime;

public class DBConnection {
	Server hsqlServer;
	Connection connection;
	String [] pairs = {"BTC/USD", "BTC/RUR", "BTC/EUR", "LTC/USD", "LTC/RUR", "LTC/EUR", "NMC/BTC", "NVC/BTC",
			"NVC/USD", "USD/RUR", "EUR/USD", "TRC/BTC", "PPC/BTC", "PPC/USD", "FTC/BTC", "XPM/BTC"};
	String [] coins = {"BTC", "USD", "EUR", "LTC", "RUR", "NMC", "NVC", "TRC", "PPC", "FTC", "XPM"};
	String [] coinColors = {"#FF0000", "#00CC00", "#0000FF", "#FF00FF", "#FF9900", "#669999",
			"#336600", "#F24602", "#CC6600", "#000066", "#660066"};
	
	public DBConnection(){
		try {
			hsqlServer = new Server();

	        // HSQLDB prints out a lot of informations when
	        // starting and closing, which we don't need now.
	        // Normally you should point the setLogWriter
	        // to some Writer object that could store the logs.
	        hsqlServer.setLogWriter(null);
	        hsqlServer.setSilent(true);
	
	        // The actual database will be named 'xdb' and its
	        // settings and data will be stored in files
	        // testdb.properties and testdb.script
	        hsqlServer.setDatabaseName(0, "ledgerdb");
	        hsqlServer.setDatabasePath(0, "file:ledgerdb");
	
	        // Start the database!
	        hsqlServer.start();
	        
	        // We have here two 'try' blocks and two 'finally'
	        // blocks because we have two things to close
	        // after all - HSQLDB server and connection
	        try {
	            // Getting a connection to the newly started database
	            Class.forName("org.hsqldb.jdbcDriver");
	            // Default user of the HSQLDB is 'sa' with an empty password
	            connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/ledgerdb", "sa", "");
	            
//	            connection.prepareStatement("DROP ledgerdb;").execute();
	            
	            // try creating a 'history' and 'profile' table
	            try {
	            	connection.prepareStatement("CREATE TABLE history (" +
	            			"pair VARCHAR(8), " +
	            			"type VARCHAR(1), " +
	            			"amount FLOAT(10), " +
	            			"price FLOAT(10), " +
	            			"fee FLOAT(10), " +
	            			"fromCapital FLOAT(10), " +
	            			"toCapital FLOAT(10), " +
	            			"stamp TIMESTAMP);").execute(); //YYYY-MM-DD HH:MM:SS
	            	
	            	connection.prepareStatement("CREATE TABLE profile (" +
	            			"pair VARCHAR(8), " +
	            			"amount FLOAT(10), " +
	            			"cost FLOAT(10), " +
	            			"buy BOOLEAN, " +
	            			"lastSellPrice FLOAT(10), " +
	            			"avgSellPrice FLOAT(10), " +
	            			"totalSoldFee FLOAT(10), " +
	            			"totalSoldNoFee FLOAT(10), " +
	            			"lastBuyPrice FLOAT(10), " +
	            			"avgBuyPrice FLOAT(10), " +
	            			"totalBoughtFee FLOAT(10), " +
	            			"totalBoughtNoFee FLOAT(10), " +
	            			"profit FLOAT(5), " +
	            			"stamp TIMESTAMP);").execute();
	            	
	            	connection.prepareStatement("CREATE TABLE capital (" +
	            			"coin VARCHAR(4), " +
	            			"transactionType VARCHAR(1), " +
	            			"capital FLOAT(10), " +
	            			"stamp TIMESTAMP);").execute();
	            	
	            	connection.prepareStatement("CREATE TABLE coin (" +
	            			"coin VARCHAR(4), " +
	            			"colorHex VARCHAR(8), " +
	            			"selected BOOLEAN);").execute();
	            	
	            	//populate profile table          	
	            	for (int i=0; i<pairs.length; i++){
	            		insertInProfile(pairs[i], 0.0f, 0.0f, true, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
	            	}
	            	
	            	//populate capital table	            	
	            	for (int i=0; i<coins.length; i++){
	            		insertCapitalRow(coins[i], "D", 0.0f);
	            	}
	            	
	            	//populate coin table
	            	for (int i=0; i<coins.length; i++){
	            		insertCoinRow(coins[i], coinColors[i]);
	            	}
	            } catch (Exception e) {
	            	System.out.println("Error database creating tables.");
//	            	e.printStackTrace();
	            }
	        } catch (ClassNotFoundException e) {
				System.out.println("DBConnection - Class Not Found");
//				e.printStackTrace();
			} catch (SQLException e) {
				System.out.println("DBConnection - SQL Connection");
				e.printStackTrace();
			} 
//	        finally {
//	            // Closing the connection
//	            if (connection != null) {
//	                try {
//						connection.close();
//					} catch (SQLException e) {
//						System.out.println("DBConnection - SQL Connection 2");
////						e.printStackTrace();
//					}
//	            }
//	        }
	    } catch (Exception e) {
			System.out.println("DBConnection exception");
		} 
//		finally {
//	        // Closing the server
//	        if (hsqlServer != null) {
//	            hsqlServer.stop();
//	        }
//	    }
	}

	public void dropDatabase(){
		try {
			connection.close();
			hsqlServer.stop();
		} catch (SQLException e) {
			System.out.println("DBConnection dropDatabase");
			e.printStackTrace();
		}
		
		File directory = new File("./");  
		   
		File[] toBeDeleted = directory.listFiles(new FileFilter() {  
			public boolean accept(File theFile) {  
				if (theFile.isFile()) {  
					return theFile.getName().startsWith("ledgerdb");  
				}
				
				return false;  
			}  
		});
		
		for(File deletableFile:toBeDeleted){  
			deletableFile.delete();  
		}  
	}
	
	public void insertInHistory(String pair, String type, float amount, float price, float fee, 
			float fromCapital, float toCapital){
		try {
			PreparedStatement p = connection.prepareStatement("insert into history(" +
					"pair, type, amount, price, fee, fromCapital, toCapital, stamp" +
					") VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
			
			p.setString(1, pair);
			p.setString(2, type);
			p.setFloat(3, amount);
			p.setFloat(4, price);
			p.setFloat(5, fee);
			p.setFloat(6, fromCapital);
			p.setFloat(7, toCapital);
			p.setTimestamp(8, new Timestamp(DateTime.now().getMillis()));
			
	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("insertInHistory - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void insertInProfile(String pair, float amount, float cost, boolean buy, float lastSellPrice,
			float averageSellPrice, float totalSoldFee, float totalSoldNoFee, float lastBuyPrice, 
			float averageBuyPrice, float totalBoughtFee, float totalBoughtNoFee, float profit){
		try {
			PreparedStatement p = connection.prepareStatement("insert into profile(" +
					"pair, amount, cost, buy, lastSellPrice, avgSellPrice, totalSoldFee, totalSoldNoFee, " +
					"lastBuyPrice, avgBuyPrice, totalBoughtFee, totalBoughtNoFee, profit, stamp" +
					") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			p.setString(1, pair);
			p.setFloat(2, amount);
			p.setFloat(3, cost);
			p.setBoolean(4, buy);
			p.setFloat(5, lastSellPrice);
			p.setFloat(6, averageSellPrice);
			p.setFloat(7, totalSoldFee);
			p.setFloat(8, totalSoldNoFee);
			p.setFloat(9, lastBuyPrice);
			p.setFloat(10, averageBuyPrice);
			p.setFloat(11, totalBoughtFee);
			p.setFloat(12, totalBoughtNoFee);
			p.setFloat(13, profit);
			p.setTimestamp(14, new Timestamp(DateTime.now().getMillis()));

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("insertInProfile - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void insertCapitalRow(String coin, String transactionType, float amount){
		try {
			float capital = 0.0f;

			//get the current capital
			PreparedStatement p = connection.prepareStatement("SELECT TOP 1 * FROM capital WHERE coin=?" +
					" ORDER BY stamp DESC;");
			
			p.setString(1, coin);
			
			ResultSet rs = p.executeQuery();
			
			while (rs.next())
				capital = rs.getFloat(3);

			//insert new capital
			if (transactionType.equals("D") || transactionType.equals("B"))
				capital += amount;
			else
				capital -= amount;
			
			p = connection.prepareStatement("insert into capital(" +
					"coin, transactionType, capital, stamp" +
					") VALUES (?, ?, ?, ?);");

			p.setString(1, coin);
			p.setString(2, transactionType);
			p.setFloat(3, capital);
			p.setTimestamp(4, new Timestamp(DateTime.now().getMillis()));

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("insertCapitalRow - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void insertCoinRow(String coin, String colorHex){
		try {
			PreparedStatement p = connection.prepareStatement("insert into coin(" +
					"coin, colorHex) VALUES (?, ?);");

			p.setString(1, coin);
			p.setString(2, colorHex);

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("insertCoinRow - SQL Exception");
			e.printStackTrace();
		}
	}
	
	//retrieve coins from profile table
	public HashMap<String, String> getCoinColorCodeMap(){
		HashMap<String, String> coinColorCodeMap = new HashMap<String, String>();
		
		try {			
			//get the current capital
			PreparedStatement p = connection.prepareStatement("SELECT * FROM coin;");
			
			ResultSet rs = p.executeQuery();
			
			while (rs.next()){
				coinColorCodeMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println("coinColorCodeMap - SQL Exception");
			e.printStackTrace();
		}
		
		return coinColorCodeMap;
	}
	
	//retrieve coins from profile table
	public HashMap<String, Float> getCoinAmountMap(){
		HashMap<String, Float> coinAmountMap = new HashMap<String, Float>();
		
		try {			
			//get the current capital
			PreparedStatement p = connection.prepareStatement("SELECT * FROM capital;");
			
			ResultSet rs = p.executeQuery();
			
			while (rs.next()){
				coinAmountMap.put(rs.getString(1), rs.getFloat(3));
			}
		} catch (SQLException e) {
			System.out.println("coinColorCodeMap - SQL Exception");
			e.printStackTrace();
		}
		
		return coinAmountMap;
	}
	
	public void updateProfile(String pair, float amount, float cost, boolean buy, float lastSellPrice,
			float averageSellPrice, float totalSoldFee, float totalSoldNoFee, float lastBuyPrice, 
			float averageBuyPrice, float totalBoughtFee, float totalBoughtNoFee, float profit){
		try {
			PreparedStatement p = connection.prepareStatement("UPDATE profile SET " +
					"amount = ?, cost = ?, buy = ?, lastSellPrice = ?, avgSellPrice = ?, totalSoldFee = ?, " +
					"totalSoldNoFee = ?, lastBuyPrice = ?, avgBuyPrice = ?, totalBoughtFee = ?, " +
					"totalBoughtNoFee = ?, profit = ?, stamp = ?" +
					"WHERE pair=?;");
			
			p.setFloat(1, amount);
			p.setFloat(2, cost);
			p.setBoolean(3, buy);
			p.setFloat(4, lastSellPrice);
			p.setFloat(5, averageSellPrice);
			p.setFloat(6, totalSoldFee);
			p.setFloat(7, totalSoldNoFee);
			p.setFloat(8, lastBuyPrice);
			p.setFloat(9, averageBuyPrice);
			p.setFloat(10, totalBoughtFee);
			p.setFloat(11, totalBoughtNoFee);
			p.setFloat(12, profit);
			p.setTimestamp(13, new Timestamp(DateTime.now().getMillis()));
			p.setString(14, pair);
			
	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("updateProfile - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void updateProfileRow(String oldPair, String newPair){
		try {
			PreparedStatement p = connection.prepareStatement("UPDATE profile SET " +
					"pair=? WHERE pair=?;");
			
			p.setString(1, newPair);
			p.setString(2, oldPair);

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("updateProfileRow - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void updateCoinName(String oldCoin, String newCoin){
		try {
			PreparedStatement p = connection.prepareStatement("UPDATE coin SET " +
					"coin=? WHERE coin=?;");
			
			p.setString(1, newCoin);
			p.setString(2, oldCoin);

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("updateCoinName 1 - SQL Exception");
			e.printStackTrace();
		}
		
		try {
			PreparedStatement p = connection.prepareStatement("UPDATE capital SET " +
					"coin=? WHERE coin=?;");
			
			p.setString(1, newCoin);
			p.setString(2, oldCoin);

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("updateCoinName 2 - SQL Exception");
//			e.printStackTrace();
		}
	}
	
	public void setCoinSelected(String coin, boolean selected){
		try {
			PreparedStatement p = connection.prepareStatement("UPDATE coin SET " +
					"selected=? WHERE coin=?;");
			
			p.setBoolean(1, selected);
			p.setString(2, coin);

	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("updateCoinRow - SQL Exception");
//			e.printStackTrace();
		}
	}
	
	public void updateCoinColor(String coin, String colorHexCode){
		try {
			PreparedStatement p = connection.prepareStatement("UPDATE coin SET " +
					"colorHex=? WHERE coin=?;");
			
			p.setString(1, colorHexCode);
			p.setString(2, coin);
			
	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("updateCoinColor - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void deleteProfileRow(String pair){
		try {
			PreparedStatement p = connection.prepareStatement("DELETE FROM profile WHERE pair=?;");
			
			p.setString(1, pair);
			
	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("deleteProfileRow - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void deleteCapitalRow(String coin){
		try {
			PreparedStatement p = connection.prepareStatement("DELETE FROM capital WHERE coin=?;");
			
			p.setString(1, coin);
			
	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("deleteCapitalRow - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public void deleteCoinRow(String coin){
		try {
			PreparedStatement p = connection.prepareStatement("DELETE FROM coin WHERE coin=?;");

			p.setString(1, coin);
			
	        p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("deleteCoinRow - SQL Exception");
			e.printStackTrace();
		}
	}
	
	public ResultSet getProfileRows(){
		ResultSet rs = null;
		
		try {
			rs = connection.prepareStatement("SELECT * FROM profile;").executeQuery();
		} catch (SQLException e) {
			System.out.println("getProfileRows - SQLException");
//			e.printStackTrace();
		}
		
		return rs;
	}
	
	public ResultSet getHistoryRows(){
		ResultSet rs = null;
		
		try {
			rs = connection.prepareStatement("SELECT * FROM history;").executeQuery();
		} catch (SQLException e) {
			System.out.println("getHistoryRows - SQLException");
//			e.printStackTrace();
		}
		
		return rs;
	}
	
	public float getCoinCapitalBeforeTime(String coin, DateTime time){
		float amount = 0.0f;	
		
		try {
			PreparedStatement p = connection.prepareStatement("SELECT TOP 1 * FROM capital WHERE stamp<=?" +
					" AND coin=? ORDER BY stamp DESC;");
			
			p.setTimestamp(1, new Timestamp(time.getMillis()));
			p.setString(2, coin);
			
			ResultSet rs = p.executeQuery();
			
			while (rs.next())
				amount = rs.getFloat(3);
		} catch (SQLException e) {
				System.out.println("getCoinCapitalBeforeTime SQLException");
			
			e.printStackTrace();
		}
		
		return amount;
	}
	
	public float getCoinCapitalInTimeRange(String coin, DateTime start, DateTime finish){
		float amount = getCoinCapitalBeforeTime(coin, start);

		try {
			ResultSet rs;
			PreparedStatement p = connection.prepareStatement("SELECT * FROM capital " +
					"WHERE stamp BETWEEN ? AND ? AND coin=?");
			
			p.setTimestamp(1, new Timestamp(start.getMillis()));
			p.setTimestamp(2, new Timestamp(finish.getMillis()));
			p.setString(3, coin);
			
			rs = p.executeQuery();
			
			while (rs.next()){
				//get the last amount in range
				amount = rs.getFloat(3);
			}
		} catch (SQLException e) {
			System.out.println("getCoinCapitalInTimeRange SQLException");
			
			e.printStackTrace();
		}
		
		return amount;
	}
	
	public float getTotalSoldFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(7);
			}
		} catch (SQLException e) {
			System.out.println("getTotalSold - SQLException");
			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getTotalSoldNoFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(8);
			}
		} catch (SQLException e) {
			System.out.println("getTotalSold - SQLException");
			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getLastBuy(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(9);
			}
		} catch (SQLException e) {
			System.out.println("getLastBuy - SQLException");
//			e.printStackTrace();
		}
		
		return 0.0f;
	}
	
	public float getLastSell(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(5);
			}
		} catch (SQLException e) {
			System.out.println("getLastBuy - SQLException");
//			e.printStackTrace();
		}
		
		return 0.0f;
	}
	
	public float getAverageBuy(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(10);
			}
		} catch (SQLException e) {
			System.out.println("getAverageBuy - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getTotalBoughtFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(11);
			}
		} catch (SQLException e) {
			System.out.println("getTotalBought - SQLException");
			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getTotalBoughtNoFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(12);
			}
		} catch (SQLException e) {
			System.out.println("getTotalBought - SQLException");
			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getAverageSell(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(6);
			}
		} catch (SQLException e) {
			System.out.println("getAverageSell - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getProfit(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(13);
			}
		} catch (SQLException e) {
			System.out.println("getProfit - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public boolean getBuy(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getBoolean(4);
			}
		} catch (SQLException e) {
			System.out.println("getBuy - SQLException");
//			e.printStackTrace();
			
			return true;
		}
		
		return true;
	}
	
	public float getAmount(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(2);
			}
		} catch (SQLException e) {
			System.out.println("getAmount - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getLastPrice(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				if (getBuy(pair))
					return rs.getFloat(9);
				else
					return rs.getFloat(5);
			}
		} catch (SQLException e) {
			System.out.println("getLastPrice - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getBoughtFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(11);
			}
		} catch (SQLException e) {
			System.out.println("getBoughtFee - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getBoughtNoFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(12);
			}
		} catch (SQLException e) {
			System.out.println("getBoughtNoFee - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getLastSellPrice(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(5);
			}
		} catch (SQLException e) {
			System.out.println("getLastSellPrice - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getLastBuyPrice(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(9);
			}
		} catch (SQLException e) {
			System.out.println("getLastBuyPrice - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getSoldFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(7);
			}
		} catch (SQLException e) {
			System.out.println("getSoldFee - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getAverageSellPrice(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(6);
			}
		} catch (SQLException e) {
			System.out.println("getAverageSellPrice - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getAverageBuyPrice(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(10);
			}
		} catch (SQLException e) {
			System.out.println("getAverageBuyPrice - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getSoldNoFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(8);
			}
		} catch (SQLException e) {
			System.out.println("getSoldNoFee - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public float getFee(String pair){
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM history WHERE pair='"
					+ pair +"';").executeQuery();
			
			if (rs.next()){
				return rs.getFloat(5);
			}
		} catch (SQLException e) {
			System.out.println("getFee - SQLException");
//			e.printStackTrace();
			
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	public ArrayList<String> getPairs(){
		ArrayList<String> pairs = new ArrayList<String>();
		
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM profile;").executeQuery();
			
			while (rs.next()){
				String pair = rs.getString(1);
				
				pairs.add(pair);
			}
		} catch (SQLException e) {
			System.out.println("getPairs - SQLException");
//			e.printStackTrace();
			
			return pairs;
		}
		
		return pairs;
	}
	
	public ArrayList<String> getCoins(){
		ArrayList<String> coins = new ArrayList<String>();
		
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM coin;").executeQuery();
			
			while (rs.next()){
				String coin = rs.getString(1);
				
				coins.add(coin);
			}
		} catch (SQLException e) {
			System.out.println("getCoins - SQLException");
//			e.printStackTrace();
			
			return coins;
		}
		
		return coins;
	}
	
	public ArrayList<String> getSelectedCoins(){
		ArrayList<String> selectedCoins = new ArrayList<String>();
		
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM coin;").executeQuery();
			
			while (rs.next()){
				String coin = rs.getString(1);
				boolean selected = rs.getBoolean(3);
				
				if (selected)
					selectedCoins.add(coin);
			}
		} catch (SQLException e) {
			System.out.println("getSelectedCoins - SQLException");
			e.printStackTrace();
			
			return selectedCoins;
		}
		
		return selectedCoins;
	}
	
	public DateTime getFirstTradeDateTime(){
		DateTime earliestDateTime = DateTime.now();
		
		try {
			ResultSet rs = connection.prepareStatement("SELECT TOP 1 * "
					+ "FROM history ORDER BY stamp").executeQuery();
			
			while (rs.next()){
				earliestDateTime = new DateTime(rs.getTimestamp(7));
			}
		} catch (SQLException e) {
			System.out.println("getFirstTradeDateTime - SQLException");
			e.printStackTrace();
			
			return earliestDateTime;
		}
		
		return earliestDateTime;
	}
	
	public DateTime getLastTradeDateTime(){
		DateTime lastDateTime = DateTime.now();
		
		try {
			ResultSet rs = connection.prepareStatement("SELECT TOP 1 * "
					+ "FROM history ORDER BY stamp DESC").executeQuery();
			
			while (rs.next()){
				lastDateTime = new DateTime(rs.getTimestamp(7));
			}
		} catch (SQLException e) {
			System.out.println("getLastTradeDateTime - SQLException");
			e.printStackTrace();
			
			return lastDateTime;
		}
		
		return lastDateTime;
	}
	
	public DateTime getLastPairHistoryStamp(String pair){
		DateTime lastDateTime = DateTime.now();
		
		try {			
			PreparedStatement p = connection.prepareStatement("SELECT TOP 1 * "
					+ "FROM history WHERE pair=? ORDER BY stamp DESC");
			
			p.setString(1, pair);
			
			ResultSet rs = p.executeQuery();
			
			while (rs.next()){
				lastDateTime = new DateTime(rs.getTimestamp(8));
			}
		} catch (SQLException e) {
			System.out.println("getLastPairHistoryStamp - SQLException");
			e.printStackTrace();
			
			return lastDateTime;
		}
		
		return lastDateTime;
	}
	
	public void deleteLastHistoryRow(DateTime stamp){
		try {
			PreparedStatement p = connection.prepareStatement("DELETE FROM history WHERE stamp=?");
			
			p.setTimestamp(1, new Timestamp(stamp.getMillis()));
			
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("deleteLastHistoryRow - SQLException");
			e.printStackTrace();
		}
	}
	
	public float getCoinCapital(String coin){
		float capital = 0.0f;
		
		try {
			ResultSet rs = connection.prepareStatement("SELECT * FROM capital WHERE coin='" +
					coin + "';").executeQuery();
			
			while (rs.next()){
				capital = rs.getFloat(3);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
			return capital;
		}

		return capital;
	}
}

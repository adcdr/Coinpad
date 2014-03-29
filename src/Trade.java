public class Trade {
	boolean buy;
	String pair;
	Float amount, rate, fee;
	Float totalBoughtFee, totalBoughtNoFee, totalSoldFee, totalSoldNoFee, lastBuyPrice, lastSellPrice,
		averageBuyPrice, averageSellPrice, profit;
	
	public Trade(boolean buy, String pair, Float amount, Float rate, Float fee, Float totalBoughtFee,
			Float totalBoughtNoFee, Float totalSoldFee, Float totalSoldNoFee, Float lastBuyPrice,
			Float lastSellPrice, Float averageBuyPrice, Float averageSellPrice, Float profit){
		this.buy = buy;
		this.pair = pair;
		this.amount = amount;
		this.rate = rate;
		this.fee = fee;
		this.totalBoughtFee = totalBoughtFee;
		this.totalBoughtNoFee = totalBoughtNoFee;
		this.totalSoldFee = totalSoldFee;
		this.totalSoldNoFee = totalSoldNoFee;
		this.lastBuyPrice = lastBuyPrice;
		this.lastSellPrice = lastSellPrice;
		this.averageBuyPrice = averageBuyPrice;
		this.averageSellPrice = averageSellPrice;
		this.profit = profit;
	}
}
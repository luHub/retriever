package nl.thuisbezorgd.miner;

public class Main {

	/**
	 * Prints KPIS for take away for order-takeaway-amsterdam-stadsdeel-binnenstad-1011
	 * @param args
	 */
	public static void main(String[] args) {
		KPIGenerator kpiGenerator = new KPIGenerator();
		String url = "https://www.thuisbezorgd.nl/en/order-takeaway-amsterdam-stadsdeel-binnenstad-1011";
		kpiGenerator.generateKPIS(url);
	}

}

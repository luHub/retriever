package nl.thuisbezorgd.transfer;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import nl.thuisbezorgd.miner.KPIGenerator;

class TransferTest {

	private String domain = "https://www.thuisbezorgd.nl/en/order-takeaway-amsterdam-stadsdeel-binnenstad-1011";
	private DomTransferer domTransfer = new DomTransferer();
	private KPIGenerator kpiGenerator = new KPIGenerator();
	private static Logger logger = LogManager.getLogger();

	@Test
	void conectionToThuisbezorgdTest() throws MalformedURLException, IOException {
		domTransfer.restaurantsFrom(domain);
	}
	
	@Test 
	void generateKPIsTest() {
		this.kpiGenerator.generateKPIS(domain);
	}



}

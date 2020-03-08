package nl.thuisbezorgd.miner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kpis.KPIS;
import nl.thuisbezorgd.transfer.DomTransferer;
import types.Restaurant;

/**
 * Transforms document object model to structured data
 * 
 * @author lucio
 */
public class KPIGenerator {

	// Logger
	private static Logger logger = LogManager.getLogger();

	public void generateKPIS(String url) {
		KPIS kpis = new KPIS();
		try {
			logger.info("Generating KPIS for:" + url);
			DomTransferer domTransfer = new DomTransferer();
			List<Restaurant> restaurants = domTransfer.restaurantsFrom(url);
			Map<Float, Long> distributionOfDeliveryFees = kpis.calcDistributionOfDeliveryFees(restaurants);
			logger.info("Fees Count");
			distributionOfDeliveryFees.entrySet().stream().sorted(Map.Entry.<Float, Long>comparingByKey())
					.forEach(fee -> {
						logger.info(fee.getKey() + ":" + fee.getValue());
					});

			DoubleSummaryStatistics deliveryTimes = kpis.calcDeliveryTimes(restaurants);
			logger.info("Delivery times: " + deliveryTimes.toString());

			int numberOfPostalcodes = kpis.calcNumberOfPostalCodes(restaurants);
			logger.info("Number of postal codes: " + numberOfPostalcodes);

			long numberOfRestaurantsWithWebSites = kpis.calcNumberOfRestaurantsWithWebsite(restaurants);
			logger.info("Number of restaurants with website " + numberOfRestaurantsWithWebSites);

			Map<String, Integer> reviewsByFoodCategory = kpis.calcNumberOfReviewsByFoodCategory(restaurants);
			logger.info("Reviews by Category");
			reviewsByFoodCategory.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue()).forEach(cat -> {
				logger.info(cat.getKey() + ":" + cat.getValue());
			});

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

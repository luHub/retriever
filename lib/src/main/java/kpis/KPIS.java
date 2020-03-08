package kpis;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import types.Address;
import types.Restaurant;

/**
 * Take away KPIs generator KPIS: - Calculate the
 * distribution of delivery fees. - Minimum, maximum, and average delivery times
 * for the region. - Number of postal codes delivering to that region. - Maximum
 * number of reviews per food category. - Number of restaurants with website.
 * @author lucio
 */
public class KPIS {

	public Map<Float, Long> calcDistributionOfDeliveryFees(List<Restaurant> restaurants) {
		return restaurants.stream().map(Restaurant::getDeliveryCost)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	public DoubleSummaryStatistics calcDeliveryTimes(List<Restaurant> restaurants) {
		return restaurants.stream().collect(Collectors.summarizingDouble(Restaurant::getDeliveryTime));
	}

	public int calcNumberOfPostalCodes(List<Restaurant> restaurants) {
		Set<String> postalCodes = restaurants.stream().map(Restaurant::getAddress).map(Address::getPostalCode)
				.collect(Collectors.toSet());
		return postalCodes.size();
	}

	// For each cateegory count how much number of reviews it has.
	public Map<String, Integer> calcNumberOfReviewsByFoodCategory(List<Restaurant> restaurants) {
		Map<String, Integer> numberOfReviewsByFoodCategoty = new HashMap<String, Integer>();
		restaurants.stream().forEach(rest -> {
			int reviewCount = rest.getReviewCount();
			rest.getCategories().forEach(cat -> {
				if (!numberOfReviewsByFoodCategoty.containsKey(cat)) {
					numberOfReviewsByFoodCategoty.put(cat, reviewCount);
				} else {
					numberOfReviewsByFoodCategoty.put(cat, numberOfReviewsByFoodCategoty.get(cat) + reviewCount);
				}
			});
		});
		return numberOfReviewsByFoodCategoty;
	}

	public long calcNumberOfRestaurantsWithWebsite(List<Restaurant> restaurants) {
		return restaurants.stream().filter(Restaurant::hasWebsite).count();
	}
}
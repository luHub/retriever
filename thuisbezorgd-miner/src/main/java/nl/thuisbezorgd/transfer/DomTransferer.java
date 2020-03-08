package nl.thuisbezorgd.transfer;

import org.json.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.utils.JsonUtils;

import types.Address;
import types.Restaurant;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Connects to thuisbezorgd.nl depending on location
 * 
 * @author lucio
 */
public final class DomTransferer {

	// Logger
	private static Logger logger = LogManager.getLogger();

	// Tokens
	final String varToken = "var";
	final String restaurantsToken = "restaurants";
	final String equalsToken = "=";
	final String braketLeftToken = "[";
	final String finalTokens = "];";

	// Address Keys
	private enum AddressKey {

		TYPE("@type"), STREETADDRESS("streetAddress"), ADDRESSLOCALITY("addressLocality"),
		ADDRESSREGION("addressRegion"), POSTALCODE("postalCode"), ADDRESSCOUNTRY("addressCountry");

		private String key;

		private AddressKey(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return key;
		}
	}

	/**
	 * Use https://www.thuisbezorgd.nl/en/ {Add location here}
	 * This method create several https requests use with moderation
	 * The DOM structure could change at any time depending on updates in 
	 * thuisbezorg web page.
	 * @param url
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public List<Restaurant> restaurantsFrom(final String url) throws MalformedURLException, IOException {

		logger.info("Transfering data from: " + url);
		// 1) Extract the restaurants overview by url, this steps requires an internet
		// connection
		final StringBuffer sb = extractVarRestaurants(url);
		// 2) Get the list of all the restaurants from a JSON File
		logger.info("Retrieving  restaurants");
		final JSONArray restaurantsJSON = parseToJSONList(sb);
		// 3) Extract restaurant overview information
		logger.info("Extracting  restaurants overview");
		final List<RestaurantUnderMining> restaurantsOverview = extractOverview(restaurantsJSON);
		// 4) Get information from each restaurant in detail with from restaurants
		// thuisbezorgd
		logger.info("Retrieving  additional information");
		final List<InfoExtraction> infoExtractions = transferDetails(restaurantsOverview);
		// 5) Extract restaurants ids
		logger.info("Extracting additional");
		final Map<String, Restaurant> restaurantsInfo = extractRestaurantInfo(infoExtractions);
		// 6) Merge Data
		logger.info("Merging results");
		final List<Restaurant> restaurants = mergeOverviewAndInfo(restaurantsOverview, restaurantsInfo);
		
		return restaurants;
	}

	private StringBuffer extractVarRestaurants(final String url) throws MalformedURLException, IOException {
		String prevToken = "";
		boolean append = false;
		// Instantiating the URL class
		URL connection = new URL(url);
		// Open Stream
		InputStream connectionStream = connection.openStream();
		// Retrieving the contents of the specified page
		Scanner sc = new Scanner(connectionStream);
		// Instantiating the StringBuffer class to hold the result
		StringBuffer sb = new StringBuffer();
		while (sc.hasNext()) {
			String token = sc.next();

			if (append == false) {
				append = StartAppending(prevToken, token);
			}

			if (append) {
				sb.append(token);
			}

			if (EndAppending(token) && append) {
				break;
			}
			prevToken = token;
		}
		sc.close();
		connectionStream.close();
		return sb;
	}

	/* Place that has all the information about restaurants */
	private boolean StartAppending(String t0, String t1) {
		return this.varToken.equals(t0) && this.restaurantsToken.equals(t1);
	}

	private boolean EndAppending(String t1) {
		return this.finalTokens.equals(t1);
	}

	private JSONArray parseToJSONList(final StringBuffer sb) {
		String rawJsonArray = sb.substring(12, sb.length() - 1).replace(";", "").toString();
		String jsonList = "{" + "restaurants:" + rawJsonArray + "}";
		JSONObject obj = new JSONObject(jsonList);
		JSONArray restaurantsArray = obj.getJSONArray("restaurants");
		return restaurantsArray;
	}

	private List<RestaurantUnderMining> extractOverview(final JSONArray restaurantsArray) {
		List<RestaurantUnderMining> restaurants = new ArrayList();

		// TODO Array convention of thuisbezorg, we need guard to see if these changes, 
		// out of scope for MVP but need to add an exeption if the codes are not longer valid
		final int NAME = 4;
		final int DELIVERY_COST = 14;
		final int AVERAGE_DELIVERY_TIME = 19;
		final int REVIEW_COUNT = 29;
		final int LATITUDE = 16;
		final int LONGITUDE = 17;
		final int INFO = 30;

		// TODO this step could be optimised, using fork not
		// for now because premature optimisation is the root of all evil.
		for (int i = 0; i < restaurantsArray.length(); i++) {
			Restaurant restaurant = new Restaurant();
			JSONArray restaurantJson = (JSONArray) restaurantsArray.get(i);
			List<String> categories = Arrays
					.asList(restaurantJson.getJSONObject(INFO).getString("categories").split(","));
			restaurant.setName(restaurantJson.getString(NAME));
			restaurant.setDeliveryCost(restaurantJson.getFloat(DELIVERY_COST));
			restaurant.setReviewCount(restaurantJson.getJSONArray(REVIEW_COUNT).getInt(1));
			restaurant.setDeliveryTime(restaurantJson.getFloat(AVERAGE_DELIVERY_TIME));
			restaurant.setLatitude(restaurantJson.getDouble(LATITUDE));
			restaurant.setLongitude(restaurantJson.getDouble(LONGITUDE));
			restaurant.setUrl("https://www.thuisbezorgd.nl" + restaurantJson.getJSONObject(INFO).getString("url"));
			restaurant.setCategories(categories);

			// Add temporal id to restaurant
			String id = Integer.toString(i);
			RestaurantUnderMining restaurantUnderMining = new RestaurantUnderMining(id, restaurant);

			restaurants.add(restaurantUnderMining);
		}

		return restaurants;
	}

	/**
	 * Get information from restaurant that is not available in the main web page
	 * like Extract Address and if the restaurant contains a webpage
	 * 
	 * @param restaurantsData
	 * @return
	 * @throws IOException
	 */
	private List<InfoExtraction> transferDetails(final List<RestaurantUnderMining> restaurantsData) throws IOException {
		List<InfoExtraction> restaurantsDetails = new ArrayList<InfoExtraction>();
		// TODO this step could be parallelised, with future tasks and executor, not
		// for now because premature optimisation is the root of all evil.
		for (int i = 0; i < restaurantsData.size(); i++) {
			String restaurantUrl = restaurantsData.get(i).getRestaurant().getUrl();
			logger.debug("Transfering from url: "+restaurantUrl);
			boolean append = false;
			boolean website = false;
			URL connection = new URL(restaurantUrl);
			String temporalId = restaurantsData.get(i).getTemporalId();
			// Open Stream
			InputStream connectionStream = connection.openStream();
			// Retrieving the contents of the specified page
			Scanner sc = new Scanner(connectionStream);
			// Instantiating the StringBuffer class to hold the result
			StringBuffer sb = new StringBuffer();
			while (sc.hasNext()) {

				String token = sc.next();
				if ("title=\"Website".equals(token)) {
					website = true;
				}
				// Get structure
				if (append == true && "</script>".equals(token)) {
					break;
				}

				if (append) {
					sb.append(token);
				}

				if (append == false) {
					append = "type=\"application/ld+json\">".equals(token) ? true : false;
				}
				// End Get Structure
			}
			restaurantsDetails.add(new InfoExtraction(temporalId, sb.toString(), website));
		}
		return restaurantsDetails;
	}

	private Map<String, Restaurant> extractRestaurantInfo(final List<InfoExtraction> restaurantsInfo)
			throws JsonParseException, IOException {

		final Map restaurantsUnderMining = new HashMap<String, RestaurantUnderMining>();

		for (InfoExtraction info : restaurantsInfo) {
			Restaurant restaurant = new Restaurant();
			Address address = extractRestaurantAddress(info);
			restaurant.setAddress(address);
			restaurant.setHasWebsite(info.hasWebsite);
			restaurantsUnderMining.put(info.getTemporalId(), restaurant);
		}

		return restaurantsUnderMining;
	}

	private Address extractRestaurantAddress(InfoExtraction infoExtraction) throws JsonParseException, IOException {

		Object jsonLDInfo = JsonUtils.fromString(infoExtraction.structuredData);
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> restaurantInfoMap = oMapper.convertValue(jsonLDInfo, Map.class);
		Map<String, String> addressMap = oMapper.convertValue(restaurantInfoMap.get("address"), Map.class);

		// TODO Guard with contains to avoid issues
		Address address = new Address();
		address.setType(addressMap.get(AddressKey.TYPE.toString()));
		address.setStreetAddress(addressMap.get(AddressKey.STREETADDRESS.toString()));
		address.setAddressRegion(addressMap.get(AddressKey.ADDRESSREGION.toString()));
		address.setAddressLocality(addressMap.get(AddressKey.ADDRESSLOCALITY.toString()));
		address.setPostalCode(addressMap.get(AddressKey.POSTALCODE.toString()));
		address.setAddressCountry(addressMap.get(AddressKey.ADDRESSCOUNTRY.toString()));

		return address;
	}

	private List<Restaurant> mergeOverviewAndInfo(final List<RestaurantUnderMining> restaurantsOverview,
			final Map<String, Restaurant> restaurantsInfo) {
		//TODO Guard for id not found
		List<Restaurant> restaurants = new ArrayList<Restaurant>();
		for (RestaurantUnderMining restaurantOverview : restaurantsOverview) {
			final String temporalId = restaurantOverview.getTemporalId();
			logger.debug("Extracting from: "+temporalId);
			final Restaurant restaurant = restaurantOverview.getRestaurant();
			final Restaurant restaurantInfo = restaurantsInfo.get(temporalId);
			restaurant.setAddress(restaurantInfo.getAddress());
			restaurant.setHasWebsite(restaurantInfo.hasWebsite());
			
			restaurants.add(restaurant);
		}
		return restaurants;
	}

	/**
	 * Helper class to gather INFO from each restaurant that is not present in the
	 * main web page
	 * 
	 * @author lucio
	 *
	 */
	private final class InfoExtraction {

		String temporalId = ""; // Temporal ID to match later restaurantInfo with restaurantOverview
		String structuredData = "";
		boolean hasWebsite = false;

		public InfoExtraction(final String temporalId, final String strucData, final boolean hasWebsite) {
			this.temporalId = temporalId;
			this.structuredData = strucData;
			this.hasWebsite = hasWebsite;
		}

		public String getTemporalId() {
			return this.temporalId;
		}
	}

	private final class RestaurantUnderMining {
		final String temporalId;
		final Restaurant restaurant;

		public RestaurantUnderMining(final String id, final Restaurant restaurant) {
			this.temporalId = id;
			this.restaurant = restaurant;
		}

		public String getTemporalId() {
			return this.temporalId;
		}

		public Restaurant getRestaurant() {
			return this.restaurant;
		}
	}
}
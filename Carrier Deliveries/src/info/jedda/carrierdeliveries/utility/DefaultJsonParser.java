package info.jedda.carrierdeliveries.utility;

import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.entity.Delivery;
import info.jedda.carrierdeliveries.entity.DeliveryItem;

import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class handling creation of an ArrayList of {@link Delivery} objects
 * (CarrierDeliveries.Deliveries) from a JSON string.
 */
public final class DefaultJsonParser implements JsonParser {
	private DefaultJsonParser() {
	}

	private static class LazyHolder {
		private static final DefaultJsonParser INSTANCE = new DefaultJsonParser();
	}

	public static DefaultJsonParser getInstance() {
		return LazyHolder.INSTANCE;
	}

	@Override
	public void createCarrierDeliveries(String jsonString) throws JSONException, ParseException {

		ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

		final JSONArray jsonDeliveries = new JSONArray(jsonString);

		for (int i = 0; i < jsonDeliveries.length(); i++) {
			JSONObject jsonDelivery = jsonDeliveries.getJSONObject(i);

			Delivery delivery = new Delivery();
			delivery.setDeliveryId(jsonDelivery.getInt("DeliveryId"));
			delivery.setDelivered(jsonDelivery.getBoolean("IsDelivered"));

			String address = jsonDelivery.getString("Address");
			delivery.setAddress(address);

			ArrayList<DeliveryItem> deliveryItems = new ArrayList<DeliveryItem>();
			JSONArray jsonDeliveryItems = jsonDelivery.getJSONArray("DeliveryItems");

			for (int j = 0; j < jsonDeliveryItems.length(); j++) {
				JSONObject jsonDeliveryItem = jsonDeliveryItems.getJSONObject(j);

				DeliveryItem deliveryItem = new DeliveryItem();
				deliveryItem.setJobId(jsonDeliveryItem.getString("JobId"));
				deliveryItem.setJobName(jsonDeliveryItem.getString("JobName"));
				deliveryItem.setBundleSize(jsonDeliveryItem.getInt("BundleSize"));
				deliveryItem.setQuantity(jsonDeliveryItem.getInt("Quantity"));
				deliveryItem.setSelected(false);

				deliveryItems.add(deliveryItem);
			}

			delivery.setDeliveryItems(deliveryItems);
			deliveries.add(delivery);
		}

		CarrierDeliveries.setDeliveries(deliveries);
	}

}

package info.jedda.carrierdeliveries.utility;

import info.jedda.carrierdeliveries.model.CarrierDeliveries;
import info.jedda.carrierdeliveries.model.Delivery;
import info.jedda.carrierdeliveries.model.DeliveryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class handling creation of an ArrayList of {@link Delivery} objects (CarrierDeliveries.Deliveries) from a JSON
 * string.
 */
public class JsonParser {

	public static void createCarrierDeliveries(String jsonString) throws JSONException,
			ParseException {

		ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

		final JSONArray jsonDeliveries = new JSONArray(jsonString);

		for (int i = 0; i < jsonDeliveries.length(); i++) {
			JSONObject jsonDelivery = jsonDeliveries.getJSONObject(i);

			Delivery delivery = new Delivery();
			delivery.setDeliveryId(jsonDelivery.getInt("DeliveryId"));

			if (jsonDelivery.isNull("TimeDelivered")) {
				delivery.setTimeDelivered(null);
			} else {
				delivery.setTimeDelivered(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
						.getDefault()).parse(jsonDelivery.getString("TimeDelivered")));
			}

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

				deliveryItems.add(deliveryItem);
			}

			delivery.setDeliveryItems(deliveryItems);
			deliveries.add(delivery);
		}

		CarrierDeliveries.setDeliveries(deliveries);
	}
}

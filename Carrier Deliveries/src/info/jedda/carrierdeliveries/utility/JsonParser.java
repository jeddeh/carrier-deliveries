package info.jedda.carrierdeliveries.utility;

import java.text.ParseException;

import org.json.JSONException;

public interface JsonParser {
	void createCarrierDeliveries(String jsonString) throws JSONException, ParseException;
}

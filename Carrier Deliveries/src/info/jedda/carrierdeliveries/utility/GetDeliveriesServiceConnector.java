package info.jedda.carrierdeliveries.utility;

import info.jedda.carrierdeliveries.activity.MainActivity;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;

import android.os.AsyncTask;

/**
 * Class responsible for downloading from the web service all the deliveries and delivery items for
 * a particular Carrier Run.
 */
public class GetDeliveriesServiceConnector {

	private String carrierRun;
	private String distributorId;
	private MainActivity activity;

	public GetDeliveriesServiceConnector(MainActivity activity) {
		this.activity = activity;
	}

	public void getDeliveries(String carrierRun, String distributorId) {
		this.carrierRun = carrierRun;
		this.distributorId = distributorId;
		new LongRunningGetIO().execute();
	}

	/**
	 * HTTP IO Class handling connection to the webservice.
	 */
	class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String response = null;

			try {
				response = RestClient.doGet("/api/delivery/" + carrierRun);
				// TODO : response = RestClient.doGet("/api/delivery?carrierRun=" + carrierRun + "&distributorId= + distributiorId);
			} catch (Exception e) {
				// Unable to get deliveries for the carrier run
				// TODO : Log and notify user.
			}

			return response;

			// return
			// "[{\"DeliveryId\":2373,\"CarrierRun\":\"S029\",\"Address\":\"1 FLINT PL , KELLYVILLE\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"948152\",\"JobName\":\"ENHANCE FURNISHINGS W3/4\",\"Quantity\":320,\"BundleSize\":500},{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":320,\"BundleSize\":100},{\"JobId\":\"950740\",\"JobName\":\"RAYS  - CELEBRATE WHATS GREAT - NSW & WA\",\"Quantity\":320,\"BundleSize\":200},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":320,\"BundleSize\":500},{\"JobId\":\"952882\",\"JobName\":\"HOME IMPROVEMENT PEOPLE (30% OFF) W2/4\",\"Quantity\":275,\"BundleSize\":500},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":320,\"BundleSize\":400},{\"JobId\":\"953729\",\"JobName\":\"DAN MURPHY'S OS 14/01\",\"Quantity\":320,\"BundleSize\":160},{\"JobId\":\"953976\",\"JobName\":\"SUPA IGA V1 LIQUOR WC13/01\",\"Quantity\":320,\"BundleSize\":150},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":320,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":320,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":320,\"BundleSize\":150},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":320,\"BundleSize\":150},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":320,\"BundleSize\":200}]},{\"DeliveryId\":2344,\"CarrierRun\":\"S029\",\"Address\":\"10 BARKLEY CLOSE , CHERRYBROOK\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":370,\"BundleSize\":100},{\"JobId\":\"950740\",\"JobName\":\"RAYS  - CELEBRATE WHATS GREAT - NSW & WA\",\"Quantity\":370,\"BundleSize\":200},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":370,\"BundleSize\":500},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":370,\"BundleSize\":400},{\"JobId\":\"953729\",\"JobName\":\"DAN MURPHY'S OS 14/01\",\"Quantity\":370,\"BundleSize\":160},{\"JobId\":\"953987\",\"JobName\":\"NSW IGA 4PP VERSION 2 NON LIQUOR WC13/01\",\"Quantity\":370,\"BundleSize\":400},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":370,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":370,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":370,\"BundleSize\":150},{\"JobId\":\"955929\",\"JobName\":\"1ST CHOICE NATIONAL W1/2 O.S 15.01.2014\",\"Quantity\":370,\"BundleSize\":250},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":370,\"BundleSize\":150},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":370,\"BundleSize\":200}]},{\"DeliveryId\":2345,\"CarrierRun\":\"S029\",\"Address\":\"11 FRANCIS GREENWAY DR , CHERRYBROOK\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":225,\"BundleSize\":100},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":225,\"BundleSize\":500},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":225,\"BundleSize\":400},{\"JobId\":\"953729\",\"JobName\":\"DAN MURPHY'S OS 14/01\",\"Quantity\":225,\"BundleSize\":160},{\"JobId\":\"953987\",\"JobName\":\"NSW IGA 4PP VERSION 2 NON LIQUOR WC13/01\",\"Quantity\":225,\"BundleSize\":400},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":225,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":225,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":225,\"BundleSize\":150},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":225,\"BundleSize\":150},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":225,\"BundleSize\":200}]},{\"DeliveryId\":2388,\"CarrierRun\":\"S029\",\"Address\":\"11 JANICE PLACE , CHERRYBROOK\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":345,\"BundleSize\":100},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":345,\"BundleSize\":500},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":345,\"BundleSize\":400},{\"JobId\":\"953987\",\"JobName\":\"NSW IGA 4PP VERSION 2 NON LIQUOR WC13/01\",\"Quantity\":345,\"BundleSize\":400},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":345,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":345,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":345,\"BundleSize\":150},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":345,\"BundleSize\":150},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":345,\"BundleSize\":200}]},{\"DeliveryId\":2330,\"CarrierRun\":\"S029\",\"Address\":\"12 FOREST GLEN , CHEERYBROOK\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":330,\"BundleSize\":100},{\"JobId\":\"950740\",\"JobName\":\"RAYS  - CELEBRATE WHATS GREAT - NSW & WA\",\"Quantity\":330,\"BundleSize\":200},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":330,\"BundleSize\":500},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":330,\"BundleSize\":400},{\"JobId\":\"953729\",\"JobName\":\"DAN MURPHY'S OS 14/01\",\"Quantity\":330,\"BundleSize\":160},{\"JobId\":\"953987\",\"JobName\":\"NSW IGA 4PP VERSION 2 NON LIQUOR WC13/01\",\"Quantity\":330,\"BundleSize\":400},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":330,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":330,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":330,\"BundleSize\":150},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":330,\"BundleSize\":150},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":330,\"BundleSize\":200}]},{\"DeliveryId\":2337,\"CarrierRun\":\"S029\",\"Address\":\"12 FOREST GLEN , CHERRYBROOK\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":360,\"BundleSize\":100},{\"JobId\":\"950740\",\"JobName\":\"RAYS  - CELEBRATE WHATS GREAT - NSW & WA\",\"Quantity\":360,\"BundleSize\":200},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":360,\"BundleSize\":500},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":360,\"BundleSize\":400},{\"JobId\":\"953729\",\"JobName\":\"DAN MURPHY'S OS 14/01\",\"Quantity\":360,\"BundleSize\":160},{\"JobId\":\"953987\",\"JobName\":\"NSW IGA 4PP VERSION 2 NON LIQUOR WC13/01\",\"Quantity\":360,\"BundleSize\":400},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":360,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":360,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":360,\"BundleSize\":150},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":360,\"BundleSize\":150},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":360,\"BundleSize\":200}]},{\"DeliveryId\":2380,\"CarrierRun\":\"S029\",\"Address\":\"12 LOCHTON PLACE , KELLYVILLE\",\"TimeDelivered\":null,\"ImagePath\":null,\"DeliveryItems\":[{\"JobId\":\"932923\",\"JobName\":\"PAULS WAREHOUSE W1/2\",\"Quantity\":467,\"BundleSize\":250},{\"JobId\":\"948152\",\"JobName\":\"ENHANCE FURNISHINGS W3/4\",\"Quantity\":731,\"BundleSize\":500},{\"JobId\":\"949301\",\"JobName\":\"OFFICEWORKS W1/2 O.S 16.01.2014\",\"Quantity\":731,\"BundleSize\":100},{\"JobId\":\"949609\",\"JobName\":\"SP01 NEW YEAR,NEW YOU SALE MW1/2 \",\"Quantity\":731,\"BundleSize\":300},{\"JobId\":\"950740\",\"JobName\":\"RAYS  - CELEBRATE WHATS GREAT - NSW & WA\",\"Quantity\":731,\"BundleSize\":200},{\"JobId\":\"951164\",\"JobName\":\"THE REJECT SHOP JANUARY  V3  NSW/ACT\",\"Quantity\":731,\"BundleSize\":500},{\"JobId\":\"953182\",\"JobName\":\"PRICELINE - SUMMER SALE 2\",\"Quantity\":467,\"BundleSize\":120},{\"JobId\":\"953341\",\"JobName\":\"CURTAIN WONDERLAND ACT, NSW & QLD W2\",\"Quantity\":731,\"BundleSize\":400},{\"JobId\":\"953729\",\"JobName\":\"DAN MURPHY'S OS 14/01\",\"Quantity\":731,\"BundleSize\":160},{\"JobId\":\"953987\",\"JobName\":\"NSW IGA 4PP VERSION 2 NON LIQUOR WC13/01\",\"Quantity\":467,\"BundleSize\":400},{\"JobId\":\"954343\",\"JobName\":\"REBEL NEW YEARS RESOLUTION NSW/ACT\",\"Quantity\":731,\"BundleSize\":200},{\"JobId\":\"955719\",\"JobName\":\"BIG W OS 16/01\",\"Quantity\":731,\"BundleSize\":150},{\"JobId\":\"955725\",\"JobName\":\"WOOLIES NSW OS 15/01\",\"Quantity\":731,\"BundleSize\":150},{\"JobId\":\"956203\",\"JobName\":\"COLES NSW STATE O.S 15.01.2014\",\"Quantity\":731,\"BundleSize\":150},{\"JobId\":\"957461\",\"JobName\":\"*PEDROS PIZZA QUAKERS HILL - AREA 3\",\"Quantity\":467,\"BundleSize\":200},{\"JobId\":\"957665\",\"JobName\":\"BCF - FISH, ITS THE NEW LAMB - NSW/ACT\",\"Quantity\":731,\"BundleSize\":200},{\"JobId\":\"953976\",\"JobName\":\"SUPA IGA V1 LIQUOR WC13/01\",\"Quantity\":264,\"BundleSize\":150}]}]";
		}

		@Override
		protected void onPreExecute() {
			activity.showProgressDialog();
		}

		@Override
		protected void onPostExecute(String response) {
			activity.endProgressDialog();

			if (response == null) {
				// Unable to Get deliveries for the carrier run.
				// TODO : Log and notify user.
				return;
			}

			CarrierDeliveries.setCarrierRun(carrierRun);
			try {
				JsonParser.createCarrierDeliveries(response);
				activity.showCarrierRunAddresses();
			} catch (Exception e) {
				// Invalid JSON response from server
				// TODO : Log and notify user.
			}
		}
	}
}

package info.jedda.carrierdeliveries.service;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import info.jedda.carrierdeliveries.activity.MainActivity;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.utility.JsonParser;
import info.jedda.carrierdeliveries.utility.RestClient;

import android.os.AsyncTask;
import android.widget.Toast;

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
	 * HTTP IO Class handling connection to the web service.
	 */
	class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String response = null;

			try {
				Header acceptHeader = new BasicHeader("Accept", "application/json");
				Header authorizationHeader = RestClient.getAuthorizationHeader(carrierRun,
						distributorId);

				Header[] headers = { acceptHeader, authorizationHeader };
				response = RestClient.doGet("/api/delivery/" + carrierRun, headers);
			} catch (Exception e) {
				// Unable to Get the Deliveries for the carrier run from the web service
				return null;
			}

			return response;
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
				Toast.makeText(activity, "Unable to load deliveries for Run " + carrierRun,
						Toast.LENGTH_LONG).show();
				return;
			}

			CarrierDeliveries.setCarrierRun(carrierRun);
			CarrierDeliveries.setDistributorId(distributorId);
			try {
				JsonParser.createCarrierDeliveries(response);
				activity.showCarrierRunAddresses();
			} catch (Exception e) {
				// Invalid JSON response from server
				Toast.makeText(activity, "Unable to load deliveries for Run " + carrierRun,
						Toast.LENGTH_LONG).show();
				return;
			}
		}
	}
}

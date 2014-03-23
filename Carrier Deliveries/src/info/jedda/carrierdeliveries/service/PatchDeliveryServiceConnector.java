package info.jedda.carrierdeliveries.service;

import info.jedda.carrierdeliveries.activity.DeliveryItemsActivity;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.utility.RestClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;
/**
 * Class responsible for uploading to the web service the data and image for a completed Delivery.
 */
public class PatchDeliveryServiceConnector {

	private DeliveryItemsActivity activity;
	private String timeDelivered;
	private String latitudeText;
	private String longitudeText;
	private String imagePath;
	private long deliveryId;

	public PatchDeliveryServiceConnector(DeliveryItemsActivity activity) {
		this.activity = activity;
	}

	public void updateDelivery(long deliveryId, String imagePath, boolean gpsSettingsEnabled,
			Location location) {
		this.deliveryId = deliveryId;
		this.imagePath = imagePath;

		if (!gpsSettingsEnabled) {
			latitudeText = "disabled";
			longitudeText = "disabled";
		} else if (location == null) {
			latitudeText = "";
			longitudeText = "";
		} else {
			latitudeText = String.valueOf(location.getLatitude());
			longitudeText = String.valueOf(location.getLongitude());
		}

		Format df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		this.timeDelivered = df.format(new Date());

		new LongRunningGetIO().execute();
	}

	/**
	 * HTTP IO Class handling connection to the webservice.
	 */
	class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String response = null;

			// TODO : Image size / compression may need adjusting?
			try {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				// Convert image to a base 64 string
				String image;

				if (imagePath == null) {
					image = "";
				} else {
					try {
						Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
						byte[] bs = outputStream.toByteArray();
						image = Base64.encodeToString(bs, Base64.DEFAULT);
					} catch (Exception e) {
						image = "";
					}
				}
				// // Test code - get image example with binary encoding
				// Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),
				// R.drawable.duck);
				// String path = Environment.getExternalStorageDirectory().toString();
				// File file = new File(path, "TestImage.jpg");
				//
				// FileOutputStream output = null;
				// try {
				// output = new FileOutputStream(file);
				// } catch (Exception e) {
				// String ex = e.getMessage();
				//
				// }
				// bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output);
				// output.close();

				// TODO : delete deliveryId TextBody for Phantom use
				builder.addTextBody("deliveryId", Long.toString(deliveryId));

				builder.addTextBody("timeDelivered", timeDelivered);
				builder.addTextBody("latitude", latitudeText);
				builder.addTextBody("longitude", longitudeText);
				builder.addTextBody("image", image);

				// builder.addPart("image", new FileBody(file));

				HttpEntity httpEntity = builder.build();

				Header authorizationHeader = RestClient.getAuthorizationHeader(
						CarrierDeliveries.getCarrierRun(), CarrierDeliveries.getDistributorId());

				Header[] headers = { authorizationHeader };

				// TODO : change to the below for Phantom use
				// response = RestClient.doPatch("/api/deliverycompleted/" + deliveryId, httpEntity,
				// headers);

				response = RestClient.doPatch("/api/deliverycompleted/", httpEntity, headers);
			} catch (Exception e) {
				// Delivery upload failed...
				response = null;
			} finally {
				activity.endProgressDialog();
			}

			return response;
		}

		@Override
		protected void onPreExecute() {
			activity.showProgressDialog();
		}

		@Override
		protected void onPostExecute(String response) {
			// Check if Delivery was successfully uploaded to the web service and updated
			if (response != null) {
				Toast.makeText(activity, "Delivery updated.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(activity, "Upload failed.", Toast.LENGTH_LONG).show();
			}

			File file = new File(imagePath);
			file.delete();
		}
	}
}

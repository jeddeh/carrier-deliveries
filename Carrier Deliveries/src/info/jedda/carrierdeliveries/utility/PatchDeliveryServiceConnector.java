package info.jedda.carrierdeliveries.utility;

import info.jedda.carrierdeliveries.R;
import info.jedda.carrierdeliveries.activity.DeliveryItemsTestActivity;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;

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
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

/**
 * Class responsible for uploading to the web service the data and image for a completed Delivery.
 */
public class PatchDeliveryServiceConnector {

	// TODO : Testing only
	private DeliveryItemsTestActivity activity;
	private String timeDelivered;
	private double latitude;
	private double longitude;
	private String imagePath;
	private long deliveryId;

	// TODO : Testing only
	public PatchDeliveryServiceConnector(DeliveryItemsTestActivity activity) {
		this.activity = activity;
	}

	public void updateDelivery(long deliveryId, String imagePath, double latitude, double longitude) {
		this.deliveryId = deliveryId;
		this.imagePath = imagePath;
		this.latitude = latitude;
		this.longitude = longitude;

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

			// TODO : Had server-side problems, so used a base 64 string - needs fix
			// TODO : Image size / compression may need adjusting?
			try {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				// Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				Bitmap bitmap = BitmapFactory.decodeFile("/drawable/ic_launcher.png");
				// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				// bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
				// byte[] bs = outputStream.toByteArray();
				// String imageEncoded = Base64.encodeToString(bs, Base64.DEFAULT);
				String imageEncoded = "imageTest";

				// TODO : delete the below for Phantom use
				builder.addTextBody("deliveryId", Long.toString(deliveryId));
				builder.addTextBody("timeDelivered", timeDelivered);
				// builder.addTextBody("latitude", String.valueOf(location.getLatitude()));
				// builder.addTextBody("longitude", String.valueOf(location.getLongitude()));
				builder.addTextBody("latitude", String.valueOf(latitude));
				builder.addTextBody("longitude", String.valueOf(longitude));
				builder.addTextBody("imageEncoded", imageEncoded);

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
				Toast.makeText(activity, "Upload failed.", Toast.LENGTH_LONG).show();
				File file = new File(imagePath);
				file.delete();
				
				
				// TODO : Store the delivery details (on external storage?) and retry the upload
				// later (start service?). Better ways??

				// TODO : The Delivery should be Patched before another attempt is made to Get the
				// deliveries for the carrier run from the webservice
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

package info.jedda.carrierdeliveries.utility;

import info.jedda.carrierdeliveries.R;
import info.jedda.carrierdeliveries.activity.DeliveryItemsActivity;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

/**
 * Class responsible for uploading to the web service the data and image for a completed Delivery.
 */
public class PatchDeliveryServiceConnector {

	// TODO : Testing only
	private DeliveryItemsActivity activity;
	private String timeDelivered;
	private double latitude;
	private double longitude;
	private String imagePath;
	private long deliveryId;

	public PatchDeliveryServiceConnector(DeliveryItemsActivity activity) {
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

				// Convert image to a base 64 string
				// TODO : change path to imagePath

				// Test Code
				Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.duck);

				// Bitmap bitmap = BitmapFactory.decodeFile(path);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
				byte[] bs = outputStream.toByteArray();
				String image = Base64.encodeToString(bs, Base64.DEFAULT);

				// // Test code - get image example with binary encoding
				// // TODO : delete test image in drawable folder
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
				builder.addTextBody("latitude", String.valueOf(latitude));
				builder.addTextBody("longitude", String.valueOf(longitude));
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

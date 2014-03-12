package info.jedda.carrierdeliveries.utility;

import info.jedda.carrierdeliveries.activity.DeliveryItemsActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
	// TODO : Considering having a location property set to the location of the phone when
	// the delivery is confirmed. Maybe stamp this on image?

	private DeliveryItemsActivity activity;
	private int deliveryId;
	private String timeDelivered;
	private String imagePath;

	public PatchDeliveryServiceConnector(DeliveryItemsActivity activity) {
		this.activity = activity;
	}

	public void updateDelivery(int deliveryId, String imagePath) {
		this.deliveryId = deliveryId;
		this.imagePath = imagePath;
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

				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
				byte[] bs = outputStream.toByteArray();
				String imageEncoded = Base64.encodeToString(bs, Base64.DEFAULT);

				builder.addTextBody("deliveryId", Integer.toString(deliveryId));
				builder.addTextBody("timeDelivered", timeDelivered);
				builder.addTextBody("imageEncoded", imageEncoded);
				final HttpEntity httpEntity = builder.build();
				response = RestClient.doPatch("/api/deliverycompleted/", httpEntity);
			} catch (Exception e) {
				// Delivery upload failed...
				// TODO : Store the delivery details (on external storage?) and retry the upload
				// later (start service?). Better ways??

				// TODO : Considering means to upload delivery details and image via USB when the
				// driver returns to the warehouse if no Internet connection. (Or maybe not...)

				// TODO : The Delivery should be Patched before another attempt is made to Get the
				// deliveries for the carrier run from the webservice

				// TODO : Log and notify user.
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
			// Delivery successfully uploaded to the webservice and updated
			Toast.makeText(activity, "Delivery updated.", Toast.LENGTH_LONG).show();
			File file = new File(imagePath);
			file.delete();
		}
	}
}

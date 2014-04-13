package info.jedda.carrierdeliveries.service;

import info.jedda.carrierdeliveries.activity.DeliveryItemsActivity;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.utility.ApacheRestClient;
import info.jedda.carrierdeliveries.utility.RestClient;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
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
	private String orientation;
	private String imagePath;
	private long deliveryId;

	private static final int IMAGE_LONGEST_SIDE_LENGTH = 400;

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

			try {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				// Convert image to a base 64 string
				String image;

				if (imagePath == null) {
					image = "";
				} else {
					try {
						Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
						
						// Resize and compress image
						int initialWidth = bitmap.getWidth();
						int initialHeight = bitmap.getHeight();

						int width = initialWidth > initialHeight ? IMAGE_LONGEST_SIDE_LENGTH
								: IMAGE_LONGEST_SIDE_LENGTH * initialWidth / initialHeight;

						int height = initialHeight > initialWidth ? IMAGE_LONGEST_SIDE_LENGTH
								: IMAGE_LONGEST_SIDE_LENGTH * initialHeight / initialWidth;

						bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

						// Re-save new image
						File file = new File(imagePath);
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

						// Transfer Exif data
						ExifInterface oldExif = new ExifInterface(imagePath);
						orientation = oldExif.getAttribute("Orientation");
						
						ExifInterface newExif = new ExifInterface(imagePath);
						
						if (orientation != null) {
							newExif.setAttribute("Orientation",orientation);
						}

						newExif.saveAttributes();

						// Convert re-sized image to base64 string
						ByteArrayOutputStream finalOutputStream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, finalOutputStream);
						byte[] bs = finalOutputStream.toByteArray();
						image = Base64.encodeToString(bs, Base64.DEFAULT);
					} catch (Exception e) {
						image = "";
					} finally {
						if (imagePath != null) {
							File file = new File(imagePath);
							file.delete();
						}
					}
				}

				// TODO : delete deliveryId TextBody for Phantom use
				builder.addTextBody("deliveryId", Long.toString(deliveryId));

				builder.addTextBody("timeDelivered", timeDelivered);
				builder.addTextBody("latitude", latitudeText);
				builder.addTextBody("longitude", longitudeText);
				builder.addTextBody("orientation", orientation);
				builder.addTextBody("image", image);

				// builder.addPart("image", new FileBody(file));

				HttpEntity httpEntity = builder.build();

				RestClient restClient = ApacheRestClient.getInstance();
				
				Header authorizationHeader = restClient.getAuthorizationHeader(
						CarrierDeliveries.getCarrierRun(), CarrierDeliveries.getDistributorId());

				Header[] headers = { authorizationHeader };

				// TODO : change to the below for Phantom use
				// response = RestClient.doPatch("/api/deliverycompleted/" + deliveryId, httpEntity,
				// headers);

				response = restClient.doPatch("/api/deliverycompleted/", httpEntity, headers);
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
				Toast.makeText(
						activity,
						"Delivery updated.\n" + "long : " + longitudeText + ", lat : "
								+ latitudeText, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(activity, "Upload failed.", Toast.LENGTH_LONG).show();
			}

			// File file = new File(imagePath);
			// file.delete();
		}
	}
}

package info.jedda.carrierdeliveries.activity;

import info.jedda.carrierdeliveries.display.DeliveryItemsAdapter;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.entity.DeliveryItem;
import info.jedda.carrierdeliveries.service.PatchDeliveryServiceConnector;
import info.jedda.carrierdeliveries.utility.LocationFinder;

import java.io.File;
import java.util.ArrayList;

import info.jedda.carrierdeliveries.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Class responsible for displaying the job names and quantities (DeliveryItems) for a single
 * Delivery.
 */
public class DeliveryItemsActivity extends Activity {

	private long deliveryId;

	private TextView tvAddressHeader;
	private ListView lvDeliveryItems;
	private Button btnDeliveryComplete;
	private LocationFinder locationFinder;
	private ProgressDialog progress;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deliveryitems);

		// Get the Delivery Items
		Bundle extras = getIntent().getExtras();

		if (extras == null) {
			showEndActivityError();
			return;
		}
		deliveryId = extras.getLong("deliveryId");

		ArrayList<DeliveryItem> deliveryItems = CarrierDeliveries.getDelivery(deliveryId)
				.getDeliveryItems();

		if (deliveryItems.size() == 0) {
			showEndActivityError();
			return;
		}

		tvAddressHeader = (TextView) findViewById(R.id.tvAddressHeader);
		lvDeliveryItems = (ListView) findViewById(R.id.lvDeliveryItems);
		btnDeliveryComplete = (Button) findViewById(R.id.btnDeliveryComplete);
		tvAddressHeader.setText(CarrierDeliveries.getDelivery(deliveryId).getAddress());

		if (CarrierDeliveries.getDelivery(deliveryId).isDelivered() == true) {
			btnDeliveryComplete.setEnabled(false);
			btnDeliveryComplete.setBackgroundColor(Color.parseColor("#ff33b5e5"));
			btnDeliveryComplete.setTextColor(Color.parseColor("#323232"));
		}

		lvDeliveryItems.setAdapter(new DeliveryItemsAdapter(this, deliveryItems));
		
		lvDeliveryItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				boolean isSelected = CarrierDeliveries.getDelivery(deliveryId).getDeliveryItems().get(position).isSelected();
				CarrierDeliveries.getDelivery(deliveryId).getDeliveryItems().get(position).setSelected(!isSelected);
				lvDeliveryItems.invalidateViews();
			}
		});

		// Start GPS tracking
		if (CarrierDeliveries.getDelivery(deliveryId).isDelivered() == false) {
			if (locationFinder == null) {
				locationFinder = new LocationFinder(DeliveryItemsActivity.this);
			}
			locationFinder.start();
		}
	}

	public void clickDeliveryComplete(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// TODO : Try Catch Blocks

		// TODO : What happens if external storage is full on mobile?

		// TODO : Application is currently saving in both the '/DeliveryPhotos/'
		// location AND the
		// default gallery location. Fix needed.

		// TODO : Better ways?
		File storagePath = new File(Environment.getExternalStorageDirectory() + "/DeliveryPhotos/");
		storagePath.mkdirs();

		File imageFile = new File(storagePath, deliveryId + ".jpg");
		Uri imageUri = Uri.fromFile(imageFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("FilePath", imageFile.getPath());
		editor.commit();

		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			return;
		}

		Location location = locationFinder.getLocation();
		boolean gpsSettingsEnabled = locationFinder.isEnabled();
		locationFinder.stop();

		// TODO : Correct the orientation of the image itself

		String filePath = null;

		switch (resultCode) {
		case RESULT_OK:
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			filePath = preferences.getString("FilePath", "");
			break;

		case RESULT_CANCELED:
			// User cancelled the image capture
			return;

		default:
			// Image capture failed - Patch delivery without image
			CarrierDeliveries.getDelivery(deliveryId).setDelivered(true);

			PatchDeliveryServiceConnector serviceConnector = new PatchDeliveryServiceConnector(
					DeliveryItemsActivity.this);
			serviceConnector.updateDelivery(deliveryId, null, gpsSettingsEnabled, location);
		}

		// TODO : (What happens when no Internet connection?)
		CarrierDeliveries.getDelivery(deliveryId).setDelivered(true);

		// Patch Delivery
		PatchDeliveryServiceConnector serviceConnector = new PatchDeliveryServiceConnector(
				DeliveryItemsActivity.this);
		serviceConnector.updateDelivery(deliveryId, filePath, gpsSettingsEnabled, location);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (CarrierDeliveries.getDelivery(deliveryId).isDelivered() == true) {
			// Delivery has been completed, disable Button btnDeliveryComplete
			btnDeliveryComplete.setEnabled(false);
			btnDeliveryComplete.setBackgroundColor(Color.parseColor("#ff33b5e5"));
			btnDeliveryComplete.setTextColor(Color.parseColor("#323232"));
		}
	}

	public void showProgressDialog() {
		progress = new ProgressDialog(this);
		progress.setTitle("Updating delivery...");
		progress.setMessage("Please wait.");
		progress.setCancelable(true);
		progress.isIndeterminate();
		progress.show();
	}

	public void endProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		if (locationFinder != null) {
			locationFinder.stop();
		}

		if (progress != null) {
			progress.dismiss();
		}

		super.onDestroy();
	}

	private void showEndActivityError() {
		Toast.makeText(this, "Unable to load delivery items", Toast.LENGTH_LONG).show();
		finish();
	}
}

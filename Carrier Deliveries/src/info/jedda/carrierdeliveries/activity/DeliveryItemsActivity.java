package info.jedda.carrierdeliveries.activity;

import info.jedda.carrierdeliveries.display.DeliveryItemsAdapter;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.entity.DeliveryItem;
import info.jedda.carrierdeliveries.service.PatchDeliveryServiceConnector;
import info.jedda.carrierdeliveries.utility.DefaultLocationFinder;

import java.io.File;
import java.util.ArrayList;

import info.jedda.carrierdeliveries.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
	private DefaultLocationFinder locationFinder;
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
				boolean isSelected = CarrierDeliveries.getDelivery(deliveryId).getDeliveryItems()
						.get(position).isSelected();
				CarrierDeliveries.getDelivery(deliveryId).getDeliveryItems().get(position)
						.setSelected(!isSelected);
				lvDeliveryItems.invalidateViews();
			}
		});

		// Start GPS tracking
		if (CarrierDeliveries.getDelivery(deliveryId).isDelivered() == false) {
			if (locationFinder == null) {
				locationFinder = new DefaultLocationFinder(DeliveryItemsActivity.this);
			}
			locationFinder.start();
		}
	}

	public void clickDeliveryComplete(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File imageFile = null;
		int lastImageId = -1;

		try {
			File storagePath = new File(Environment.getExternalStorageDirectory()
					+ "/DeliveryPhotos/");
			storagePath.mkdirs();
			imageFile = new File(storagePath, deliveryId + ".jpg");

			// TODO : Change to below after testing (with alternative to concatenation?)
			// File imageFile = new File(this.getExternalCacheDir().getPath() + deliveryId +
			// ".jpg");

			Uri imageUri = Uri.fromFile(imageFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			lastImageId = getMostRecentGalleryImageId();
		} finally {

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("FilePath", imageFile.getPath());
			editor.putInt("lastImageId", lastImageId);
			editor.commit();

			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	}

	private int getMostRecentGalleryImageId() {
		final String[] imageColumns = { MediaStore.Images.Media._ID };
		final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
		final String imageWhere = null;
		final String[] imageArguments = null;

		Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				imageColumns, imageWhere, imageArguments, imageOrderBy);

		if (imageCursor.moveToFirst()) {
			int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
			stopManagingCursor(imageCursor);
			imageCursor.close();
			return id;
		} else {
			return 0;
		}
	}

	private void deleteGalleryImage(int imageId) {
		ContentResolver cr = getContentResolver();
		cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?",
				new String[] { Integer.toString(imageId) });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			return;
		}

		Location location = locationFinder.getLocation();
		boolean gpsSettingsEnabled = locationFinder.isEnabled();
		locationFinder.stop();

		String filePath = null;
		PatchDeliveryServiceConnector serviceConnector;

		switch (resultCode) {
		case RESULT_OK:
			// Image capture succeeded
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			filePath = preferences.getString("FilePath", "");
			CarrierDeliveries.getDelivery(deliveryId).setDelivered(true);

			// Delete image from gallery
			int lastImageId = preferences.getInt("lastImageId", -1);

			if (lastImageId != -1) {
				int thisImageId = getMostRecentGalleryImageId();

				if (thisImageId > lastImageId) {
					deleteGalleryImage(thisImageId);
				}
			}

			// Patch delivery
			serviceConnector = new PatchDeliveryServiceConnector(DeliveryItemsActivity.this);
			serviceConnector.updateDelivery(deliveryId, filePath, gpsSettingsEnabled, location);

		case RESULT_CANCELED:
			// User cancelled the image capture
			return;

		default:
			// Image capture failed - Patch delivery without image
			CarrierDeliveries.getDelivery(deliveryId).setDelivered(true);

			serviceConnector = new PatchDeliveryServiceConnector(DeliveryItemsActivity.this);
			serviceConnector.updateDelivery(deliveryId, null, gpsSettingsEnabled, location);
		}
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

		// TODO : Delete the image from the default image folder

		super.onDestroy();
	}

	private void showEndActivityError() {
		Toast.makeText(this, "Unable to load delivery items", Toast.LENGTH_LONG).show();
		finish();
	}
}

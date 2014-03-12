package info.jedda.carrierdeliveries.activity;

import info.jedda.carrierdeliveries.model.CarrierDeliveries;
import info.jedda.carrierdeliveries.utility.GetDeliveriesServiceConnector;

import info.jedda.carrierdeliveries.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The application will create a new top-level folder on the phone called DeliveryPhotos containing saved images.
 * A bug exists which will also cause images to be saved in the phone's default gallery location.
 */

/**
 * The login activity.
 */
public class MainActivity extends Activity {

	private EditText etCarrierRun;
	private EditText etPassword;
	private ProgressDialog progress;

	private static final String STATE_CODE = "S";
	private String carrierRun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etCarrierRun = (EditText) findViewById(R.id.etCarrierRun);

		// TODO : If the application is to be used interstate, considering allowing the user to
		// choose the state (city) code and storing in shared preferences.

		etPassword = (EditText) findViewById(R.id.etPassword);
	}

	@Override
	protected void onResume() {
		super.onResume();
		etCarrierRun.setText("");
		etPassword.setText("");
		etCarrierRun.requestFocus();
	}

	public void clickSubmitCarrierRun(View v) {

		String runNumber = etCarrierRun.getText().toString();

		// TODO : Password functionality not implemented.
		String password = etPassword.getText().toString();

		try {
			carrierRun = STATE_CODE + String.format("%03d", Integer.parseInt(runNumber));
		} catch (NumberFormatException e) {
			// Expected to fail when nothing entered in CarrierRun EditText by user
			Toast.makeText(this, "Invalid Carrier Run", Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			GetDeliveriesServiceConnector serviceConnector = new GetDeliveriesServiceConnector(
					MainActivity.this);
			serviceConnector.getDeliveries(carrierRun);
		} catch (Exception e) {
			// Unable to get the deliveries for the Carrier Run from the webservice
			// TODO : Log and notify user
		}
	}

	public void showCarrierRunAddresses() {
		if (CarrierDeliveries.getDeliveries().size() == 0) {
			Toast.makeText(this, "Unable to load deliveries for Run " + carrierRun,
					Toast.LENGTH_LONG).show();
			etCarrierRun.setText("");
			etPassword.setText("");
			etCarrierRun.requestFocus();
			return;
		}

		Intent intent = new Intent(this, DeliveriesActivity.class);
		startActivity(intent);
	}

	public void showProgressDialog() {
		progress = new ProgressDialog(this);
		progress.setTitle("Downloading Deliveries...");
		progress.setMessage("Please wait.");
		progress.setCancelable(false);
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
		if (progress != null) {
			progress.dismiss();
		}
		super.onDestroy();
	}
}

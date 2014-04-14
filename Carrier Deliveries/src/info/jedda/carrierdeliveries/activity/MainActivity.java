package info.jedda.carrierdeliveries.activity;

import java.util.ArrayList;
import java.util.List;

import info.jedda.carrierdeliveries.entity.CarrierDeliveries;
import info.jedda.carrierdeliveries.service.GetDeliveriesServiceConnector;
import info.jedda.carrierdeliveries.utility.implementation.DefaultLocationFinder;

import info.jedda.carrierdeliveries.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The login activity.
 */
public class MainActivity extends Activity {

	private EditText etCarrierRun;
	private EditText etDistributorId;
	private ProgressDialog progress;
	private Spinner spBranches;

	private String carrierRun;
	private SharedPreferences preferences;
	private DefaultLocationFinder locationFinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etCarrierRun = (EditText) findViewById(R.id.etCarrierRun);
		etDistributorId = (EditText) findViewById(R.id.etDistributorId);
		spBranches = (Spinner) findViewById(R.id.spBranches);

		List<String> branches = new ArrayList<String>();
		branches.add("Adelaide");
		branches.add("Brisbane");
		branches.add("Hobart");
		branches.add("Melbourne");
		branches.add("Perth");
		branches.add("Sydney");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, branches);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spBranches.setAdapter(adapter);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String defaultBranch = preferences.getString("defaultBranch", "");
		if (branches.contains(defaultBranch)) {
			spBranches.setSelection(branches.indexOf(defaultBranch));
		}

		spBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				((TextView) parent.getChildAt(0)).setTextSize(22);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		etCarrierRun.setText("");
		etDistributorId.setText("");
		etCarrierRun.requestFocus();

		locationFinder = new DefaultLocationFinder(this);
		if (!locationFinder.isEnabled()) {
			locationFinder.showSettingsAlert();
		}
	}

	public void clickSubmitCarrierRun(View v) {
		CarrierDeliveries.setCarrierRun("");
		CarrierDeliveries.setDistributorId("");
		CarrierDeliveries.setDeliveries(null);

		String runNumber = etCarrierRun.getText().toString();
		String distributorId = etDistributorId.getText().toString();

		try {
			String branch = String.valueOf(spBranches.getSelectedItem());

			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("defaultBranch", branch);
			editor.commit();

			carrierRun = branch.substring(0, 1)
					+ String.format("%03d", Integer.parseInt(runNumber));
		} catch (NumberFormatException e) {
			// Expected to fail when nothing entered in CarrierRun EditText by user
			Toast.makeText(this, "Invalid Carrier Run", Toast.LENGTH_SHORT).show();
			return;
		}

		if (distributorId.isEmpty()) {
			Toast.makeText(this, "Invalid Distributor Id", Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			GetDeliveriesServiceConnector serviceConnector = new GetDeliveriesServiceConnector(
					MainActivity.this);
			serviceConnector.getDeliveries(carrierRun, distributorId);
		} catch (Exception e) {
			Toast.makeText(this, "Unable to load deliveries for Run " + carrierRun,
					Toast.LENGTH_LONG).show();
		}
	}

	public void showCarrierRunAddresses() {
		if (CarrierDeliveries.getDeliveries().size() == 0) {
			Toast.makeText(this, "Unable to load deliveries for Run " + carrierRun,
					Toast.LENGTH_LONG).show();
			etCarrierRun.setText("");
			etDistributorId.setText("");
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
		if (progress != null) {
			progress.dismiss();
		}
		super.onDestroy();
	}
}

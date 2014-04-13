package info.jedda.carrierdeliveries.activity;

import info.jedda.carrierdeliveries.display.DeliveriesAdapter;
import info.jedda.carrierdeliveries.entity.CarrierDeliveries;

import info.jedda.carrierdeliveries.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Class responsible for displaying the addresses (Deliveries) for a single Carrier Run.
 */
public class DeliveriesActivity extends Activity {

	private ListView lvDeliveries;
	private DeliveriesAdapter deliveriesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deliveries);

		setTitle(CarrierDeliveries.getCarrierRun());
		lvDeliveries = (ListView) findViewById(R.id.lvDeliveries);

		// Set a transparent background for the ListView so the ListView rows can be grayed out
		// when the delivery is completed.
		lvDeliveries.setBackgroundColor(0);
		deliveriesAdapter = new DeliveriesAdapter(this);
		lvDeliveries.setAdapter(deliveriesAdapter);

		lvDeliveries.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DeliveriesActivity.this);
				boolean isStarted = preferences.getBoolean("isDeliveryRunStarted", false);
				
				if (!isStarted) {
					Toast.makeText(DeliveriesActivity.this, "Please press START from the menu", Toast.LENGTH_LONG).show();
					return;
				}
				
				Intent intent = new Intent(DeliveriesActivity.this, DeliveryItemsActivity.class);
				intent.putExtra("deliveryId", CarrierDeliveries.getDeliveries().get(position)
						.getDeliveryId());

				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		switch (item.getItemId()) {
		case R.id.start:
			editor.putBoolean("isDeliveryRunStarted", true);
			editor.commit();
			
			Toast.makeText(this, "Delivery run started.", Toast.LENGTH_LONG).show();
			return true;

		case R.id.finish:
			editor.putBoolean("isDeliveryRunStarted", false);
			editor.commit();

			Toast.makeText(this, "Delivery run finished.", Toast.LENGTH_LONG).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		deliveriesAdapter.notifyDataSetChanged();
	}
}

package info.jedda.carrierdeliveries.activity;

import info.jedda.carrierdeliveries.display.DeliveriesAdapter;
import info.jedda.carrierdeliveries.model.CarrierDeliveries;

import info.jedda.carrierdeliveries.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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

		setTitle("Deliveries for " + CarrierDeliveries.getCarrierRun());
		lvDeliveries = (ListView) findViewById(R.id.lvDeliveries);

		// Set a transparent background for the ListView so the ListView rows can be greyed out
		// when the delivery is completed.
		lvDeliveries.setBackgroundColor(0);
		deliveriesAdapter = new DeliveriesAdapter(this);
		lvDeliveries.setAdapter(deliveriesAdapter);

		lvDeliveries.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Intent intent = new Intent(DeliveriesActivity.this, DeliveryItemsActivity.class);
				intent.putExtra("deliveryId", CarrierDeliveries.getDeliveries().get(position)
						.getDeliveryId());

				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		deliveriesAdapter.notifyDataSetChanged();
	}
}

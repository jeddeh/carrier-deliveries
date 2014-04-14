package info.jedda.carrierdeliveries.utility.implementation;

import info.jedda.carrierdeliveries.utility.LocationFinder;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public final class DefaultLocationFinder extends Service implements LocationListener, LocationFinder {
	private DefaultLocationFinder() {
	}

	private static class LazyHolder {
		private static final DefaultLocationFinder INSTANCE = new DefaultLocationFinder();
	}

	public static DefaultLocationFinder getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	// TODO : Get a timestamp of when the location was last updated in the onLocationChanged event 
	// and compare this with a timestamp (now) when the delivery is to be patched. If greater than 2 min, return null.
	private Context context;
	private static LocationManager locationManager;
	private static String provider;

	// The minimum distance between updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

	public DefaultLocationFinder(Context context) {
		this.context = context;
		locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
	}

	// Returns true if the GPS is enabled on the device
	public boolean isEnabled() {
		boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		return isGpsEnabled || isNetworkEnabled;
	}

	// Starts requesting updates from the locationManager listener
	public boolean start() {
		try {
			if (isEnabled()) {
				Criteria criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);

				if (provider == null) {
					return false;
				} else {
					locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				}
			}
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	// Stops the locationManager listener
	public void stop() {
		if (locationManager != null) {
			locationManager.removeUpdates(DefaultLocationFinder.this);
		}
	}

	public Location getLocation() {
		if (provider != null){
			return locationManager.getLastKnownLocation(provider);
		} else {
			return null;
		}
	}

	// Shows the GPS Settings alert dialog
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("GPS Settings");
		alertDialog.setMessage("Please enable GPS on your device.");

		// OK button click event listener
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});

		// Cancel button click event listener
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}

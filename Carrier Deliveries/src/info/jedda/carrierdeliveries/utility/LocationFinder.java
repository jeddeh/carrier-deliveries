package info.jedda.carrierdeliveries.utility;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Class responsible for returning the user's location. Adapted from
 * StackOverflow :
 * "What is the simplest and most robust way to get the user's current location in Android?"
 */
public class LocationFinder {
	Timer timer;
	LocationManager locationManager;
	LocationResult locationResult;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;

	public boolean getLocation(Context context, LocationResult result) {

		locationResult = result;
		if (locationManager == null) {
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}

		// Exceptions will be thrown if provider is not permitted.
		try {
			gpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
		}

		try {
			networkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception e) {
		}

		// Don't start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled)
			return false;

		if (gpsEnabled)
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);

		if (networkEnabled)
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);

		timer = new Timer();
		timer.schedule(new LocationNotFound(), 20000);
		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.foundLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.foundLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class LocationNotFound extends TimerTask {
		@Override
		public void run() {
			locationResult.foundLocation(null);
		}
	}

	public static abstract class LocationResult {
		public abstract void foundLocation(Location location);
	}
}

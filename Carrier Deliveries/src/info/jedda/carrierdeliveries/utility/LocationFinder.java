package info.jedda.carrierdeliveries.utility;

import android.location.Location;

public interface LocationFinder {
	public boolean isEnabled();

	public boolean start();

	public void stop();

	public Location getLocation();

	public void showSettingsAlert();
}

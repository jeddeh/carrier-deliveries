package info.jedda.carrierdeliveries.entity;

import java.util.ArrayList;

/**
 * Holds data for a single delivery of DeliveryItems (catalogues) by a driver.
 */
public class Delivery {

	// As of 2014-03-18, there are often two or more address/suburb records in Phantom
	// for a single address. e.g. 2 High St, Bondi / 2 High Street, Bondi Beach.
	// The driver then has to look at two delivery item lists and confirm two deliveries for a
	// single address. ITSM call has been raised for this issue by Dave Heldoorn, but feedback is
	// this is difficult to fix.

	private long deliveryId;
	private String address;
	private boolean isDelivered;

	private double latitude;
	private double longitude;

	private ArrayList<DeliveryItem> deliveryItems;

	public long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(int deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean getIsDelivered() {
		return isDelivered;
	}

	public void setIsDelivered(boolean isDelivered) {
		this.isDelivered = isDelivered;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public ArrayList<DeliveryItem> getDeliveryItems() {
		return deliveryItems;
	}

	public void setDeliveryItems(ArrayList<DeliveryItem> deliveryItems) {
		this.deliveryItems = deliveryItems;
	}

	// Equals is overridden on the deliveryId
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + deliveryId);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Delivery other = (Delivery) obj;
		if (deliveryId != other.deliveryId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Delivery [deliveryId=" + deliveryId + ", address=" + address + ", isDelivered="
				+ isDelivered + "]";
	}
}

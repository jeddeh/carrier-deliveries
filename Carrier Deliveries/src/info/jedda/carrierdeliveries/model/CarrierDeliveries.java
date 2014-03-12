package info.jedda.carrierdeliveries.model;

import java.util.ArrayList;

/**
 * Holds the current Carrier Run and exposes the Delivery instances for the Carrier Run to
 * the entire project.
 */
public final class CarrierDeliveries {
	private static String carrierRun;
	private static ArrayList<Delivery> deliveries;

	private CarrierDeliveries() {
	}

	public static String getCarrierRun() {
		return carrierRun;
	}

	public static void setCarrierRun(String carrierRun) {
		CarrierDeliveries.carrierRun = carrierRun;
	}

	public static ArrayList<Delivery> getDeliveries() {
		return deliveries;
	}

	public static void setDeliveries(ArrayList<Delivery> deliveries) {
		CarrierDeliveries.deliveries = deliveries;
	}

	public static Delivery getDelivery(int deliveryId) {
		for (Delivery delivery : deliveries) {
			if (delivery.getDeliveryId() == deliveryId) {
				return delivery;
			}
		}
		throw new IllegalArgumentException();
	}
}

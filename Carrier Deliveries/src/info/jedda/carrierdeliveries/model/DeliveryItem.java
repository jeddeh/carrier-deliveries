package info.jedda.carrierdeliveries.model;

/**
 * Holds data related to a single catalogue version.
 */
public class DeliveryItem {

	private String jobId;
	private String jobName;
	private int quantity;
	private int bundleSize;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getBundleSize() {
		return bundleSize;
	}

	public void setBundleSize(int bundleSize) {
		this.bundleSize = bundleSize;
	}

	// When a bundle size has not yet been entered into Phantom for a job,
	// the default bundle size from the report is 0
	public int getBundles() {
		if (bundleSize == 0) {
			return 0;
		} else {
			return (int) (quantity / bundleSize);
		}
	}

	public int getItems() {
		if (bundleSize == 0) {
			return 0;
		} else {
			return quantity % bundleSize;
		}
	}

	// Equals is overridden on the jobId and the jobName.
	// In the case that a Job Id refers to an 'overprinted' job, a Delivery may contain multiple
	// DeliveryItems (catalogue versions) with the same jobId and different jobNames.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
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
		DeliveryItem other = (DeliveryItem) obj;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeliveryItem [jobId=" + jobId + ", jobName=" + jobName + ", quantity=" + quantity
				+ ", bundleSize=" + bundleSize + ", getBundles()=" + getBundles() + ", getItems()="
				+ getItems() + "]";
	}
}

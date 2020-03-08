package types;

import java.util.List;

public final class Restaurant {
	
	String name;
	float deliveryCost;
	float deliveryTime;
	double latitude;
	double longitude;
	int reviewCount;
	String url;
	List<String> categories;
	boolean hasWebsite;
	Address address;
	
	public boolean hasWebsite() {
		return hasWebsite;
	}
	public void setHasWebsite(boolean hasWebsite) {
		this.hasWebsite = hasWebsite;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getDeliveryCost() {
		return deliveryCost;
	}
	public void setDeliveryCost(float deliveryCost) {
		this.deliveryCost = deliveryCost;
	}
	public int getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public float getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(float deliveryTime) {
		this.deliveryTime = deliveryTime;
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
	
	@Override
	public String toString() {
		return "Restaurant [name=" + name + ", deliveryCost=" + deliveryCost + ", deliveryTime=" + deliveryTime
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", review=" + reviewCount + ", url=" + url
				+ ", categories=" + categories + ", hasWebsite=" + hasWebsite + ", address=" + address + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((categories == null) ? 0 : categories.hashCode());
		result = prime * result + Float.floatToIntBits(deliveryCost);
		result = prime * result + Float.floatToIntBits(deliveryTime);
		result = prime * result + (hasWebsite ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + reviewCount;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Restaurant other = (Restaurant) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (categories == null) {
			if (other.categories != null)
				return false;
		} else if (!categories.equals(other.categories))
			return false;
		if (Float.floatToIntBits(deliveryCost) != Float.floatToIntBits(other.deliveryCost))
			return false;
		if (Float.floatToIntBits(deliveryTime) != Float.floatToIntBits(other.deliveryTime))
			return false;
		if (hasWebsite != other.hasWebsite)
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (reviewCount != other.reviewCount)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}

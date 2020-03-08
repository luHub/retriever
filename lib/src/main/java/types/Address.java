package types;

/* 
 * Address for the retriver project, following schema.org
 * Use this class for address generation across services. 
 * Example: @type=PostalAddress, streetAddress=xxx, addressLocality=xxx, addressRegion=xxx, postalCode=xxx, addressCountry=xxxx
 */

public final class Address {

	String type;
	String streetAddress;
	String addressLocality;
	String addressRegion;
	String postalCode;
	String addressCountry;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getAddressLocality() {
		return addressLocality;
	}

	public void setAddressLocality(String addressLocality) {
		this.addressLocality = addressLocality;
	}

	public String getAddressRegion() {
		return addressRegion;
	}

	public void setAddressRegion(String addressRegion) {
		this.addressRegion = addressRegion;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	@Override
	public String toString() {
		return "Address [type=" + type + ", streetAddress=" + streetAddress + ", addressLocality=" + addressLocality
				+ ", addressRegion=" + addressRegion + ", postalCode=" + postalCode + ", addressCountry="
				+ addressCountry + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressCountry == null) ? 0 : addressCountry.hashCode());
		result = prime * result + ((addressLocality == null) ? 0 : addressLocality.hashCode());
		result = prime * result + ((addressRegion == null) ? 0 : addressRegion.hashCode());
		result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
		result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Address other = (Address) obj;
		if (addressCountry == null) {
			if (other.addressCountry != null)
				return false;
		} else if (!addressCountry.equals(other.addressCountry))
			return false;
		if (addressLocality == null) {
			if (other.addressLocality != null)
				return false;
		} else if (!addressLocality.equals(other.addressLocality))
			return false;
		if (addressRegion == null) {
			if (other.addressRegion != null)
				return false;
		} else if (!addressRegion.equals(other.addressRegion))
			return false;
		if (postalCode == null) {
			if (other.postalCode != null)
				return false;
		} else if (!postalCode.equals(other.postalCode))
			return false;
		if (streetAddress == null) {
			if (other.streetAddress != null)
				return false;
		} else if (!streetAddress.equals(other.streetAddress))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}

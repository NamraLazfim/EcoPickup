package com.ecopickup.model;

/**
 * An address returned by the address service. If the service is down, the resident's
 * own text is kept and the address is flagged as unverified.
 */
public final class Address {

    private final String displayName;
    private final double latitude;
    private final double longitude;
    private final boolean verified;

    /**
     * Creates a verified address from a lookup result.
     *
     * @param displayName full matched address
     * @param latitude    latitude in degrees
     * @param longitude   longitude in degrees
     */
    public Address(String displayName, double latitude, double longitude) {
        this(displayName, latitude, longitude, true);
    }

    private Address(String displayName, double latitude, double longitude, boolean verified) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.verified = verified;
    }

    /**
     * Creates an address typed by the resident that could not be checked.
     *
     * @param typedAddress the address as the resident typed it
     * @return an unverified address with no coordinates
     */
    public static Address unverified(String typedAddress) {
        return new Address(typedAddress, 0, 0, false);
    }

    /**
     * Returns the full address text.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the latitude in degrees.
     *
     * @return the latitude; 0 if unverified
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude in degrees.
     *
     * @return the longitude; 0 if unverified
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Tells whether the address was confirmed by the address service.
     *
     * @return true if verified
     */
    public boolean isVerified() {
        return verified;
    }
}

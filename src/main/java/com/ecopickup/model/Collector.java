package com.ecopickup.model;

/**
 * A waste collection crew member who is assigned pickups.
 */
public class Collector extends User {

    private final String serviceZone;

    /**
     * Creates a collector.
     *
     * @param id          unique user id
     * @param name        full name
     * @param email       email address
     * @param serviceZone zone this collector serves, e.g. "PJU 1"
     */
    public Collector(String id, String name, String email, String serviceZone) {
        super(id, name, email);
        this.serviceZone = serviceZone;
    }

    /**
     * Returns the zone this collector serves.
     *
     * @return the service zone
     */
    public String getServiceZone() {
        return serviceZone;
    }

    /**
     * Returns "Collector".
     *
     * @return the role name
     */
    @Override
    public String getRole() {
        return "Collector";
    }
}

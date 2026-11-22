package com.ecopickup.api;

import com.ecopickup.model.Address;

/**
 * Turns an address typed by a resident into a verified {@link Address} with coordinates.
 */
public interface AddressService {

    /**
     * Looks up an address.
     *
     * @param query the address as typed by the user
     * @return the matched address
     * @throws AddressLookupException when the address is not found or the service cannot be reached
     */
    Address lookup(String query) throws AddressLookupException;
}

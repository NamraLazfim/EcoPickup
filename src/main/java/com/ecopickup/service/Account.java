package com.ecopickup.service;

import com.ecopickup.model.User;

/**
 * A sign-in account. The password is never stored, only a salted hash of it.
 *
 * @param name         the display name
 * @param email        the sign-in email, in lower case
 * @param salt         random salt used when hashing the password
 * @param passwordHash the salted SHA-256 hash of the password
 * @param role         what the account can do
 * @param user         the resident or collector, or null for an admin
 * @param homeAddress  the resident's usual pickup address, may be empty
 */
public record Account(String name, String email, String salt, String passwordHash, Role role, User user,
                      String homeAddress) {

    /**
     * Returns a copy with a new password hash.
     *
     * @param newSalt the new salt
     * @param newHash the new hash
     * @return the updated account
     */
    public Account withPassword(String newSalt, String newHash) {
        return new Account(name, email, newSalt, newHash, role, user, homeAddress);
    }

    /**
     * Returns a copy with a new home address.
     *
     * @param address the address
     * @return the updated account
     */
    public Account withHomeAddress(String address) {
        return new Account(name, email, salt, passwordHash, role, user, address);
    }
}

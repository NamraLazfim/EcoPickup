package com.ecopickup.model;

/**
 * Common details for every person who uses EcoPickup.
 */
public abstract class User {

    private final String id;
    private final String name;
    private final String email;

    /**
     * Creates the shared details of a user.
     *
     * @param id    unique user id
     * @param name  full name
     * @param email email address
     */
    protected User(String id, String name, String email) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("User id and name are required");
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Returns the unique id of this user.
     *
     * @return the user id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the full name of this user.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the email address of this user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the role shown in the GUI, e.g. "Resident".
     *
     * @return the role name
     */
    public abstract String getRole();
}

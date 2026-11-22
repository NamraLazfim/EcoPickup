package com.ecopickup.service;

/**
 * The kinds of user that can sign in. Each role sees its own pages.
 */
public enum Role {
    RESIDENT("Resident"),
    COLLECTOR("Collector"),
    ADMIN("Admin");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    /**
     * Returns the name shown to the user.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }
}

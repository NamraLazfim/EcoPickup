package com.ecopickup.api;

/**
 * Thrown when an address lookup fails. The {@link Reason} decides which message the user sees.
 */
public class AddressLookupException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Why the lookup failed.
     */
    public enum Reason {
        /** Nothing matched (empty result or HTTP 404). */
        NOT_FOUND,
        /** The service could not answer (HTTP 5xx, timeout or no network). */
        UNAVAILABLE
    }

    private final Reason reason;

    /**
     * Creates the exception.
     *
     * @param reason  why the lookup failed
     * @param message a message that is safe to show to the user
     */
    public AddressLookupException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    /**
     * Returns why the lookup failed.
     *
     * @return the reason
     */
    public Reason getReason() {
        return reason;
    }
}

package com.ecopickup.model;

/**
 * Lifecycle of a pickup request: REQUESTED, then ASSIGNED, then COMPLETED.
 * A request can be CANCELLED any time before it is completed.
 */
public enum PickupStatus {
    REQUESTED,
    ASSIGNED,
    COMPLETED,
    CANCELLED;

    /**
     * Checks whether a request in this status may move to the given status.
     *
     * @param next the status the request would move to
     * @return true if the move is allowed
     */
    public boolean canTransitionTo(PickupStatus next) {
        switch (this) {
            case REQUESTED:
                return next == ASSIGNED || next == CANCELLED;
            case ASSIGNED:
                return next == COMPLETED || next == CANCELLED;
            default:
                return false;
        }
    }
}

package com.ecopickup.model;

import java.time.LocalDateTime;

/**
 * Proof that a pickup was completed, with the weight measured by the collector.
 * Reward strategies calculate points from this record.
 */
public class CollectionRecord {

    private final PickupRequest request;
    private final Collector collector;
    private final double actualWeightKg;
    private final LocalDateTime completedAt;

    /**
     * Creates a collection record.
     *
     * @param request        the completed pickup request
     * @param collector      the collector who completed it; may be null for a points preview
     * @param actualWeightKg the weight measured at collection; must be greater than zero
     * @param completedAt    when the pickup was completed
     * @throws IllegalArgumentException if the request or completion time is missing,
     *                                  or the weight is not greater than zero
     */
    public CollectionRecord(PickupRequest request, Collector collector,
                            double actualWeightKg, LocalDateTime completedAt) {
        if (request == null || completedAt == null) {
            throw new IllegalArgumentException("Request and completion time are required");
        }
        if (actualWeightKg <= 0) {
            throw new IllegalArgumentException("Actual weight must be greater than 0 kg");
        }
        this.request = request;
        this.collector = collector;
        this.actualWeightKg = actualWeightKg;
        this.completedAt = completedAt;
    }

    /**
     * Returns the completed pickup request.
     *
     * @return the request
     */
    public PickupRequest getRequest() {
        return request;
    }

    /**
     * Returns the collector who completed the pickup.
     *
     * @return the collector, or null for a points preview
     */
    public Collector getCollector() {
        return collector;
    }

    /**
     * Returns the weight measured at collection.
     *
     * @return the weight in kg
     */
    public double getActualWeightKg() {
        return actualWeightKg;
    }

    /**
     * Returns when the pickup was completed.
     *
     * @return the completion time
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Returns the material collected, taken from the request.
     *
     * @return the material type
     */
    public MaterialType getMaterialType() {
        return request.getMaterialType();
    }
}

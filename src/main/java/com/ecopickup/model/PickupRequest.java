package com.ecopickup.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A resident's booking for a waste pickup. It can only be created through {@link Builder},
 * which checks every field, so a PickupRequest is always complete.
 */
public final class PickupRequest {

    private final String id;
    private final Resident resident;
    private final Address address;
    private final MaterialType materialType;
    private final LocalDate preferredDate;
    private final Double estimatedWeightKg;
    private final String notes;
    private final String accessInstructions;
    private PickupStatus status = PickupStatus.REQUESTED;
    private Collector assignedCollector;

    private PickupRequest(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.resident = builder.resident;
        this.address = builder.address;
        this.materialType = builder.materialType;
        this.preferredDate = builder.preferredDate;
        this.estimatedWeightKg = builder.estimatedWeightKg;
        this.notes = builder.notes;
        this.accessInstructions = builder.accessInstructions;
    }

    /**
     * Returns the unique id of this request.
     *
     * @return the request id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the resident who made this request.
     *
     * @return the resident
     */
    public Resident getResident() {
        return resident;
    }

    /**
     * Returns the pickup address.
     *
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Returns the material to collect.
     *
     * @return the material type
     */
    public MaterialType getMaterialType() {
        return materialType;
    }

    /**
     * Returns the resident's preferred pickup date.
     *
     * @return the preferred date
     */
    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    /**
     * Returns the resident's weight estimate.
     *
     * @return the estimated weight in kg, or null if not given
     */
    public Double getEstimatedWeightKg() {
        return estimatedWeightKg;
    }

    /**
     * Returns the resident's notes for the collector.
     *
     * @return the notes, or null if not given
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Returns how the collector can reach the items, e.g. "Leave at back gate".
     *
     * @return the access instructions, or null if not given
     */
    public String getAccessInstructions() {
        return accessInstructions;
    }

    /**
     * Returns the current status.
     *
     * @return the status
     */
    public PickupStatus getStatus() {
        return status;
    }

    /**
     * Returns the assigned collector.
     *
     * @return the collector, or null before assignment
     */
    public Collector getAssignedCollector() {
        return assignedCollector;
    }

    /**
     * Assigns a collector and moves the request to ASSIGNED.
     *
     * @param collector the collector to assign
     */
    public void assignCollector(Collector collector) {
        Objects.requireNonNull(collector, "Collector is required");
        changeStatus(PickupStatus.ASSIGNED);
        this.assignedCollector = collector;
    }

    /**
     * Marks the request as COMPLETED.
     */
    public void complete() {
        changeStatus(PickupStatus.COMPLETED);
    }

    /**
     * Marks the request as CANCELLED.
     */
    public void cancel() {
        changeStatus(PickupStatus.CANCELLED);
    }

    private void changeStatus(PickupStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException("Cannot change a pickup from " + status + " to " + next);
        }
        status = next;
    }

    /**
     * Builds a valid {@link PickupRequest}. Required details go in the constructor,
     * optional details are chained, and {@link #build()} checks everything.
     */
    public static final class Builder {

        private static final int MIN_DAYS_IN_ADVANCE = 1;
        private static final double MAX_WEIGHT_KG = 500.0;

        private final Resident resident;
        private final Address address;
        private final MaterialType materialType;
        private final LocalDate preferredDate;
        private Double estimatedWeightKg;
        private String notes;
        private String accessInstructions;

        /**
         * Starts a request with its required details.
         *
         * @param resident      the resident making the request
         * @param address       the pickup address
         * @param materialType  the material to collect
         * @param preferredDate the preferred pickup date
         */
        public Builder(Resident resident, Address address, MaterialType materialType, LocalDate preferredDate) {
            this.resident = resident;
            this.address = address;
            this.materialType = materialType;
            this.preferredDate = preferredDate;
        }

        /**
         * Sets the resident's weight estimate.
         *
         * @param weightKg estimated weight in kg
         * @return this builder
         */
        public Builder estimatedWeight(double weightKg) {
            this.estimatedWeightKg = weightKg;
            return this;
        }

        /**
         * Adds notes for the collector, e.g. a gate code.
         *
         * @param text the notes
         * @return this builder
         */
        public Builder notes(String text) {
            this.notes = text;
            return this;
        }

        /**
         * Adds access instructions, e.g. "Items are at the back gate".
         *
         * @param text the instructions
         * @return this builder
         */
        public Builder accessInstructions(String text) {
            this.accessInstructions = text;
            return this;
        }

        /**
         * Checks all details and creates the request.
         *
         * @return a new request with status REQUESTED
         * @throws IllegalStateException if a detail is missing or invalid
         */
        public PickupRequest build() {
            validate();
            return new PickupRequest(this);
        }

        private void validate() {
            if (!hasRequiredDetails()) {
                throw new IllegalStateException("Resident, address, material and date are required");
            }
            if (preferredDate.isBefore(LocalDate.now().plusDays(MIN_DAYS_IN_ADVANCE))) {
                throw new IllegalStateException("Pickup must be booked at least 1 day ahead");
            }
            if (estimatedWeightKg != null && !isValidWeight(estimatedWeightKg)) {
                throw new IllegalStateException("Estimated weight must be between 0 and " + MAX_WEIGHT_KG + " kg");
            }
        }

        private boolean hasRequiredDetails() {
            return resident != null && address != null && materialType != null && preferredDate != null;
        }

        private static boolean isValidWeight(double weightKg) {
            return weightKg > 0 && weightKg <= MAX_WEIGHT_KG;
        }
    }
}

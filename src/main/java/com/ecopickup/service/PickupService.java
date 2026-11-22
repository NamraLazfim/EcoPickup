package com.ecopickup.service;

import com.ecopickup.model.CollectionRecord;
import com.ecopickup.model.Collector;
import com.ecopickup.model.MaterialType;
import com.ecopickup.model.PickupRequest;
import com.ecopickup.model.PickupStatus;
import com.ecopickup.model.Resident;
import com.ecopickup.reward.RewardStrategy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service layer: books, assigns and completes pickups and gives residents their reward points.
 * Data is kept in memory. The reward rule is a {@link RewardStrategy} that can be swapped at runtime.
 */
public class PickupService {

    private final Map<String, PickupRequest> requests = new LinkedHashMap<>();
    private final Map<String, CollectionRecord> records = new HashMap<>();
    private final Map<String, Integer> pointsByRequest = new HashMap<>();
    private final Map<String, List<PointsEntry>> historyByResident = new HashMap<>();
    private RewardStrategy rewardStrategy;

    /**
     * Creates the service.
     *
     * @param rewardStrategy the rule used to calculate reward points
     */
    public PickupService(RewardStrategy rewardStrategy) {
        this.rewardStrategy = Objects.requireNonNull(rewardStrategy, "Reward strategy is required");
    }

    /**
     * Saves a new request.
     *
     * @param request the request built by PickupRequest.Builder
     * @throws IllegalStateException if the resident already has an open pickup for the same material and date
     */
    public void submitRequest(PickupRequest request) {
        if (isDuplicate(request)) {
            throw new IllegalStateException("You already have a " + request.getMaterialType().name().toLowerCase()
                    + " pickup booked for that date");
        }
        requests.put(request.getId(), request);
    }

    /**
     * Assigns a collector to a request.
     *
     * @param requestId the request id
     * @param collector the collector
     */
    public void assignCollector(String requestId, Collector collector) {
        find(requestId).assignCollector(collector);
    }

    /**
     * Cancels a request that is not completed yet.
     *
     * @param requestId the request id
     */
    public void cancel(String requestId) {
        find(requestId).cancel();
    }

    /**
     * Completes a pickup with the measured weight and gives the resident their points.
     *
     * @param requestId      the request id
     * @param actualWeightKg the measured weight
     * @return the points earned
     */
    public int completePickup(String requestId, double actualWeightKg) {
        PickupRequest request = find(requestId);
        CollectionRecord record = new CollectionRecord(request, request.getAssignedCollector(), actualWeightKg,
                LocalDateTime.now());
        request.complete();
        int points = rewardStrategy.calculatePoints(record);
        if (points > 0) {
            request.getResident().getRewardAccount().addPoints(points);
        }
        records.put(requestId, record);
        pointsByRequest.put(requestId, points);
        historyOf(request.getResident()).add(new PointsEntry(request.getMaterialType(), actualWeightKg, points));
        return points;
    }

    /**
     * Redeems points from a resident and records it in their history.
     *
     * @param resident the resident
     * @param points   the points to redeem
     */
    public void redeemPoints(Resident resident, int points) {
        resident.getRewardAccount().redeem(points);
        historyOf(resident).add(new PointsEntry(null, 0, -points));
    }

    /**
     * Works out the points a record would earn, without saving anything.
     *
     * @param record a sample record
     * @return the points
     */
    public int previewPoints(CollectionRecord record) {
        return rewardStrategy.calculatePoints(record);
    }

    /**
     * Swaps the reward rule (Strategy pattern).
     *
     * @param strategy the new rule
     */
    public void setRewardStrategy(RewardStrategy strategy) {
        this.rewardStrategy = Objects.requireNonNull(strategy, "Reward strategy is required");
    }

    /**
     * Returns every request in booking order.
     *
     * @return the requests
     */
    public List<PickupRequest> getAllRequests() {
        return List.copyOf(requests.values());
    }

    /**
     * Returns the requests made by one resident.
     *
     * @param resident the resident
     * @return the requests
     */
    public List<PickupRequest> getRequestsBy(Resident resident) {
        return requests.values().stream().filter(r -> r.getResident() == resident).toList();
    }

    /**
     * Returns the requests with a status.
     *
     * @param status the status
     * @return the requests
     */
    public List<PickupRequest> getRequestsWith(PickupStatus status) {
        return requests.values().stream().filter(r -> r.getStatus() == status).toList();
    }

    /**
     * Returns a collector's requests with a status.
     *
     * @param collector the collector
     * @param status    the status
     * @return the requests
     */
    public List<PickupRequest> getRequestsFor(Collector collector, PickupStatus status) {
        return getRequestsWith(status).stream().filter(r -> r.getAssignedCollector() == collector).toList();
    }

    /**
     * Returns the collection record of a completed request.
     *
     * @param requestId the request id
     * @return the record, or null if it is not completed
     */
    public CollectionRecord getRecord(String requestId) {
        return records.get(requestId);
    }

    /**
     * Returns the points a completed request earned.
     *
     * @param requestId the request id
     * @return the points, or null if it is not completed
     */
    public Integer getPointsEarned(String requestId) {
        return pointsByRequest.get(requestId);
    }

    /**
     * Returns a resident's points history, newest first.
     *
     * @param resident the resident
     * @return the entries
     */
    public List<PointsEntry> getHistory(Resident resident) {
        List<PointsEntry> history = new ArrayList<>(historyOf(resident));
        Collections.reverse(history);
        return history;
    }

    /**
     * Returns the total weight collected for each material.
     *
     * @return one entry per material
     */
    public List<MaterialStat> getMaterialStats() {
        List<MaterialStat> stats = new ArrayList<>();
        for (MaterialType material : MaterialType.values()) {
            List<CollectionRecord> matching = records.values().stream()
                    .filter(r -> r.getMaterialType() == material).toList();
            double kilograms = matching.stream().mapToDouble(CollectionRecord::getActualWeightKg).sum();
            stats.add(new MaterialStat(material, matching.size(), kilograms));
        }
        return stats;
    }

    private boolean isDuplicate(PickupRequest request) {
        return requests.values().stream().anyMatch(existing -> existing.getResident() == request.getResident()
                && existing.getMaterialType() == request.getMaterialType()
                && existing.getPreferredDate().equals(request.getPreferredDate())
                && (existing.getStatus() == PickupStatus.REQUESTED || existing.getStatus() == PickupStatus.ASSIGNED));
    }

    private PickupRequest find(String requestId) {
        PickupRequest request = requests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("No pickup request with id " + requestId);
        }
        return request;
    }

    private List<PointsEntry> historyOf(Resident resident) {
        return historyByResident.computeIfAbsent(resident.getId(), id -> new ArrayList<>());
    }
}

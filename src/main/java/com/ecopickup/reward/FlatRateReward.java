package com.ecopickup.reward;

import com.ecopickup.model.CollectionRecord;

/**
 * Awards the same number of points for every completed pickup, whatever its weight.
 */
public class FlatRateReward implements RewardStrategy {

    private final int pointsPerPickup;

    /**
     * Creates the strategy.
     *
     * @param pointsPerPickup points for each completed pickup; must be greater than zero
     */
    public FlatRateReward(int pointsPerPickup) {
        if (pointsPerPickup <= 0) {
            throw new IllegalArgumentException("Points per pickup must be greater than 0");
        }
        this.pointsPerPickup = pointsPerPickup;
    }

    /**
     * Returns the fixed points for a completed pickup.
     *
     * @param record the completed pickup
     * @return the fixed points
     */
    @Override
    public int calculatePoints(CollectionRecord record) {
        return pointsPerPickup;
    }
}

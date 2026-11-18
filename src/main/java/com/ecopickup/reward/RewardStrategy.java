package com.ecopickup.reward;

import com.ecopickup.model.CollectionRecord;

/**
 * A rule for turning a completed pickup into reward points (Strategy pattern).
 * A new rule is added as a new class, without changing existing code.
 */
public interface RewardStrategy {

    /**
     * Calculates the points earned for a completed pickup.
     *
     * @param record the completed pickup
     * @return the points earned; never negative
     */
    int calculatePoints(CollectionRecord record);
}

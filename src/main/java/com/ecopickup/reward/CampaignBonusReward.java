package com.ecopickup.reward;

import com.ecopickup.model.CollectionRecord;
import com.ecopickup.model.MaterialType;

import java.time.LocalDate;

/**
 * Multiplies the points of another strategy during a council campaign,
 * e.g. double points for e-waste in December. Start and end dates are inclusive.
 */
public class CampaignBonusReward implements RewardStrategy {

    /** Smallest allowed multiplier; 1.0 leaves the base points unchanged. */
    public static final double MIN_MULTIPLIER = 1.0;

    private final RewardStrategy baseStrategy;
    private final MaterialType targetMaterial;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double multiplier;

    /**
     * Creates a campaign bonus.
     *
     * @param baseStrategy   the strategy whose points are multiplied
     * @param targetMaterial the material the campaign applies to
     * @param startDate      first day of the campaign
     * @param endDate        last day of the campaign (inclusive)
     * @param multiplier     points multiplier, e.g. 2.0 for double points; at least {@link #MIN_MULTIPLIER}
     * @throws IllegalArgumentException if a detail is missing, the end date is before the start date,
     *                                  or the multiplier is below {@link #MIN_MULTIPLIER}
     */
    public CampaignBonusReward(RewardStrategy baseStrategy, MaterialType targetMaterial,
                               LocalDate startDate, LocalDate endDate, double multiplier) {
        if (baseStrategy == null || targetMaterial == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Base strategy, material, start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Campaign end date must be on or after its start date");
        }
        if (Double.isNaN(multiplier) || multiplier < MIN_MULTIPLIER) {
            throw new IllegalArgumentException("Campaign multiplier must be at least " + MIN_MULTIPLIER);
        }
        this.baseStrategy = baseStrategy;
        this.targetMaterial = targetMaterial;
        this.startDate = startDate;
        this.endDate = endDate;
        this.multiplier = multiplier;
    }

    /**
     * Calculates the base points and multiplies them if the pickup qualifies.
     *
     * @param record the completed pickup
     * @return the points earned
     */
    @Override
    public int calculatePoints(CollectionRecord record) {
        int basePoints = baseStrategy.calculatePoints(record);
        if (!isEligible(record)) {
            return basePoints;
        }
        return (int) Math.round(basePoints * multiplier);
    }

    private boolean isEligible(CollectionRecord record) {
        LocalDate completedOn = record.getCompletedAt().toLocalDate();
        boolean duringCampaign = !completedOn.isBefore(startDate) && !completedOn.isAfter(endDate);
        return duringCampaign && record.getMaterialType() == targetMaterial;
    }
}

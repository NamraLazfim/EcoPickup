package com.ecopickup.reward;

import com.ecopickup.model.CollectionRecord;
import com.ecopickup.model.MaterialType;

import java.time.LocalDate;

/**
 * Multiplies the points of another strategy during a council campaign,
 * e.g. double points for e-waste in December. Start and end dates are inclusive.
 */
public class CampaignBonusReward implements RewardStrategy {

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
     * @param multiplier     points multiplier, e.g. 2.0 for double points
     */
    public CampaignBonusReward(RewardStrategy baseStrategy, MaterialType targetMaterial,
                               LocalDate startDate, LocalDate endDate, double multiplier) {
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

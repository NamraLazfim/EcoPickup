package com.ecopickup.gui;

import com.ecopickup.model.MaterialType;
import com.ecopickup.reward.CampaignBonusReward;
import com.ecopickup.reward.FlatRateReward;
import com.ecopickup.reward.PerKilogramReward;
import com.ecopickup.reward.RewardStrategy;
import java.time.LocalDate;

// The admin's reward rule and campaign; builds the matching RewardStrategy.
final class RewardSettings {

    private static final int DEFAULT_FLAT_POINTS = 20;
    private static final double DEFAULT_MULTIPLIER = 2.0;
    private static final int DEFAULT_CAMPAIGN_DAYS = 7;

    private RewardRule rule = RewardRule.PER_KILOGRAM;
    private int flatPoints = DEFAULT_FLAT_POINTS;
    private MaterialType campaignMaterial = MaterialType.PLASTIC;
    private double multiplier = DEFAULT_MULTIPLIER;
    private LocalDate campaignStart = LocalDate.now();
    private LocalDate campaignEnd = LocalDate.now().plusDays(DEFAULT_CAMPAIGN_DAYS);

    RewardStrategy createStrategy() {
        return switch (rule) {
            case FLAT_RATE -> new FlatRateReward(flatPoints);
            case CAMPAIGN_BONUS -> new CampaignBonusReward(new PerKilogramReward(), campaignMaterial,
                    campaignStart, campaignEnd, multiplier);
            default -> new PerKilogramReward();
        };
    }

    void update(RewardRule newRule, int newFlatPoints, MaterialType material, double newMultiplier,
                       LocalDate start, LocalDate end) {
        rule = newRule;
        flatPoints = newFlatPoints;
        campaignMaterial = material;
        multiplier = newMultiplier;
        campaignStart = start;
        campaignEnd = end;
    }

    boolean isCampaignRunning(LocalDate day) {
        return rule == RewardRule.CAMPAIGN_BONUS && !day.isBefore(campaignStart) && !day.isAfter(campaignEnd);
    }

    RewardRule getRule() {
        return rule;
    }

    int getFlatPoints() {
        return flatPoints;
    }

    MaterialType getCampaignMaterial() {
        return campaignMaterial;
    }

    double getMultiplier() {
        return multiplier;
    }

    LocalDate getCampaignStart() {
        return campaignStart;
    }

    LocalDate getCampaignEnd() {
        return campaignEnd;
    }
}

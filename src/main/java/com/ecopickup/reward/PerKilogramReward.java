package com.ecopickup.reward;

import com.ecopickup.model.CollectionRecord;
import com.ecopickup.model.MaterialType;

/**
 * Awards points for each kilogram collected, at a different rate for each material.
 * Bulky waste earns no points because it is not recycled.
 */
public class PerKilogramReward implements RewardStrategy {

    public static final int POINTS_PER_KG_PLASTIC = 10;
    public static final int POINTS_PER_KG_PAPER = 5;
    public static final int POINTS_PER_KG_GLASS = 4;
    public static final int POINTS_PER_KG_METAL = 8;
    public static final int POINTS_PER_KG_E_WASTE = 15;
    public static final int POINTS_PER_KG_BULKY = 0;

    /**
     * Calculates points as the actual weight times the material's rate,
     * rounded to the nearest whole point.
     *
     * @param record the completed pickup
     * @return the points earned
     */
    @Override
    public int calculatePoints(CollectionRecord record) {
        return (int) Math.round(record.getActualWeightKg() * pointsPerKg(record.getMaterialType()));
    }

    private static int pointsPerKg(MaterialType material) {
        switch (material) {
            case PLASTIC:
                return POINTS_PER_KG_PLASTIC;
            case PAPER:
                return POINTS_PER_KG_PAPER;
            case GLASS:
                return POINTS_PER_KG_GLASS;
            case METAL:
                return POINTS_PER_KG_METAL;
            case E_WASTE:
                return POINTS_PER_KG_E_WASTE;
            default:
                return POINTS_PER_KG_BULKY;
        }
    }
}

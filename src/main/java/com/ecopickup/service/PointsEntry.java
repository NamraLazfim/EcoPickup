package com.ecopickup.service;

import com.ecopickup.model.MaterialType;

/**
 * One line in a resident's points history.
 *
 * @param material the material collected, or null when points were redeemed
 * @param weightKg the weight collected, or 0 when points were redeemed
 * @param points   points added (positive) or redeemed (negative)
 */
public record PointsEntry(MaterialType material, double weightKg, int points) {

    /**
     * Tells whether this line is a redemption rather than a completed pickup.
     *
     * @return true when points were redeemed
     */
    public boolean isRedemption() {
        return material == null;
    }
}

package com.ecopickup.service;

import com.ecopickup.model.MaterialType;

/**
 * How much of one material was collected across the city.
 *
 * @param material  the material
 * @param pickups   the number of completed pickups
 * @param kilograms the total weight collected
 */
public record MaterialStat(MaterialType material, int pickups, double kilograms) {
}

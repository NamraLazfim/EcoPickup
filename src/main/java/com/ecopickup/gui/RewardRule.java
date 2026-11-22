package com.ecopickup.gui;

// The reward rules the admin can pick from.
enum RewardRule {
    PER_KILOGRAM("Per kilogram"),
    FLAT_RATE("Flat rate per pickup"),
    CAMPAIGN_BONUS("Campaign bonus");

    private final String label;

    RewardRule(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }
}

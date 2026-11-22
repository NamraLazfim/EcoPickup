package com.ecopickup.gui;

import com.ecopickup.model.MaterialType;

// What the admin typed on the reward rules page.
record RewardSettingsForm(RewardRule rule, String flatPointsText, MaterialType material,
                                 String multiplierText, String startText, String endText) {
}

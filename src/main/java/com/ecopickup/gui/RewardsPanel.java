package com.ecopickup.gui;

import com.ecopickup.service.PointsEntry;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// View: points balance, history and redeeming points.
final class RewardsPanel extends PagePanel {

    private static final float BALANCE_FONT_SIZE = 34f;

    private final PickupController controller;
    private final JLabel balanceValue = new JLabel();
    private final JLabel campaignLabel = new JLabel();
    private final JTextField redeemField = Ui.textField();
    private final JButton redeemButton = Ui.primaryButton("Redeem");
    private final DefaultTableModel historyModel = Ui.tableModel("Activity", "Points");

    RewardsPanel(PickupController controller) {
        this.controller = controller;
        add(Ui.pageHeader("Rewards", campaignLabel), BorderLayout.NORTH);
        JPanel body = Ui.clear(new GridLayout(1, 0, Theme.GAP_LARGE, 0));
        body.add(buildRedeemCard());
        body.add(buildHistoryCard());
        add(body, BorderLayout.CENTER);
        redeemButton.addActionListener(event -> redeem());
    }

    @Override
    public void refresh() {
        balanceValue.setText(controller.getBalance() + " pts");
        campaignLabel.setText(controller.isCampaignRunning() ? describeCampaign(controller.getRewardSettings())
                : "Earn points every time a pickup is completed.");
        historyModel.setRowCount(0);
        controller.getHistory().forEach(entry -> historyModel.addRow(new Object[] {describe(entry),
            entry.points() > 0 ? "+" + entry.points() : String.valueOf(entry.points())}));
    }

    private static String describe(PointsEntry entry) {
        return entry.isRedemption() ? "Redeemed reward"
                : Ui.materialName(entry.material()) + ", " + Ui.formatKg(entry.weightKg());
    }

    private static String describeCampaign(RewardSettings settings) {
        return "Campaign: " + Ui.formatMultiplier(settings.getMultiplier()) + " points on "
                + Ui.materialName(settings.getCampaignMaterial()).toLowerCase() + " until "
                + Ui.formatDate(settings.getCampaignEnd()) + ".";
    }

    private void redeem() {
        try {
            controller.redeem(redeemField.getText().trim());
            redeemField.setText("");
            refresh();
            Ui.showInfo(this, "Points redeemed. Your new balance is " + controller.getBalance() + " points.");
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildRedeemCard() {
        balanceValue.setFont(Theme.BOLD_FONT.deriveFont(BALANCE_FONT_SIZE));
        balanceValue.setForeground(Theme.TEXT_PRIMARY);
        JPanel redeemRow = Ui.clear(new BorderLayout(Theme.GAP, 0));
        redeemRow.add(redeemField, BorderLayout.CENTER);
        redeemRow.add(redeemButton, BorderLayout.EAST);
        JPanel card = Ui.card(new GridBagLayout());
        int row = 0;
        addRow(card, Ui.fieldLabel("Your balance"), row++, 0);
        addRow(card, balanceValue, row++, Theme.GAP_SMALL);
        addRow(card, Ui.fieldLabel("Points to redeem"), row++, Theme.GAP_LARGE);
        addRow(card, redeemRow, row, Theme.GAP_SMALL);
        return card;
    }

    private JPanel buildHistoryCard() {
        JPanel card = Ui.card(new BorderLayout(0, Theme.GAP));
        card.add(Ui.label("Points history", Theme.BOLD_FONT, Theme.TEXT_PRIMARY), BorderLayout.NORTH);
        card.add(Ui.scroll(Ui.table(historyModel)), BorderLayout.CENTER);
        return card;
    }

    private static void addRow(JPanel card, JComponent component, int row, int topGap) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = row;
        constraints.weightx = 1;
        constraints.weighty = component instanceof JPanel ? 1 : 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(topGap, 0, 0, 0);
        card.add(component, constraints);
    }
}

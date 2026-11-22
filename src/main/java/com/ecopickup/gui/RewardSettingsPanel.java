package com.ecopickup.gui;

import com.ecopickup.model.MaterialType;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

// Admin view: choose the reward rule and edit the campaign.
final class RewardSettingsPanel extends PagePanel {

    private final PickupController controller;
    private final Map<RewardRule, JRadioButton> ruleButtons = new EnumMap<>(RewardRule.class);
    private final JTextField flatPointsField = Ui.textField();
    private final JComboBox<MaterialType> materialBox = new JComboBox<>(MaterialType.values());
    private final JTextField multiplierField = Ui.textField();
    private final JTextField startField = Ui.textField();
    private final JTextField endField = Ui.textField();
    private final JLabel currentLabel = new JLabel();
    private final JButton saveButton = Ui.primaryButton("Save reward rule");

    RewardSettingsPanel(PickupController controller) {
        this.controller = controller;
        add(Ui.pageHeader("Reward rules", currentLabel), BorderLayout.NORTH);
        JPanel body = Ui.clear(new GridLayout(1, 0, Theme.GAP_LARGE, 0));
        body.add(buildRuleCard());
        body.add(buildCampaignCard());
        add(body, BorderLayout.CENTER);
        JPanel footer = Ui.clear(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(saveButton);
        add(footer, BorderLayout.SOUTH);
        saveButton.addActionListener(event -> save());
        materialBox.setFont(Theme.BODY_FONT);
        Ui.showMaterialNames(materialBox);
    }

    @Override
    public void refresh() {
        RewardSettings settings = controller.getRewardSettings();
        ruleButtons.get(settings.getRule()).setSelected(true);
        flatPointsField.setText(String.valueOf(settings.getFlatPoints()));
        materialBox.setSelectedItem(settings.getCampaignMaterial());
        multiplierField.setText(String.valueOf(settings.getMultiplier()));
        startField.setText(settings.getCampaignStart().toString());
        endField.setText(settings.getCampaignEnd().toString());
        currentLabel.setText("In use now: " + settings.getRule().getLabel());
    }

    private void save() {
        RewardRule rule = ruleButtons.entrySet().stream().filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey).findFirst().orElse(RewardRule.PER_KILOGRAM);
        try {
            controller.saveRewardSettings(new RewardSettingsForm(rule, flatPointsField.getText(),
                    (MaterialType) materialBox.getSelectedItem(), multiplierField.getText(), startField.getText(),
                    endField.getText()));
            refresh();
            Ui.showInfo(this, "Reward rule saved. It applies to pickups completed from now on.");
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildRuleCard() {
        JPanel options = new StackPanel();
        options.add(Ui.label("How residents earn points", Theme.BOLD_FONT, Theme.TEXT_PRIMARY));
        ButtonGroup group = new ButtonGroup();
        for (RewardRule rule : RewardRule.values()) {
            JRadioButton button = new JRadioButton(rule.getLabel());
            button.setFont(Theme.BODY_FONT);
            button.setOpaque(false);
            group.add(button);
            ruleButtons.put(rule, button);
            options.add(button);
        }
        options.add(Ui.fieldLabel("Points per pickup (flat rate)"));
        options.add(flatPointsField);
        options.add(Ui.fieldLabel("<html>Per kilogram points: plastic 10, paper 5, glass 4, metal 8, "
                + "e-waste 15, bulky 0.</html>"));
        return topAligned(options);
    }

    private JPanel buildCampaignCard() {
        JPanel fields = new StackPanel();
        fields.add(Ui.label("Campaign bonus", Theme.BOLD_FONT, Theme.TEXT_PRIMARY));
        fields.add(Ui.fieldLabel("Material"));
        fields.add(materialBox);
        fields.add(Ui.fieldLabel("Multiplier, e.g. 2 for double points"));
        fields.add(multiplierField);
        fields.add(Ui.fieldLabel("Start date (yyyy-mm-dd)"));
        fields.add(startField);
        fields.add(Ui.fieldLabel("End date (yyyy-mm-dd), included"));
        fields.add(endField);
        return topAligned(fields);
    }

    private static JPanel topAligned(JPanel content) {
        JPanel card = Ui.card(new BorderLayout());
        card.add(content, BorderLayout.NORTH);
        return card;
    }
}

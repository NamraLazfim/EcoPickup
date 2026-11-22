package com.ecopickup.gui;

import com.ecopickup.model.PickupRequest;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// View: the collector's open tasks and completing them with the real weight.
final class CollectorTaskPanel extends PagePanel {

    private static final int DETAIL_ROWS = 9;

    private final PickupController controller;
    private final JLabel countLabel = new JLabel();
    private final DefaultTableModel model = Ui.tableModel("Date", "Address", "Material");
    private final JTable table = Ui.table(model);
    private final JTextArea details = Ui.textArea(DETAIL_ROWS);
    private final JTextField weightField = Ui.textField();
    private final JButton completeButton = Ui.primaryButton("Complete pickup");
    private final List<PickupRequest> rows = new ArrayList<>();

    CollectorTaskPanel(PickupController controller) {
        this.controller = controller;
        add(Ui.pageHeader("My tasks", countLabel), BorderLayout.NORTH);
        JPanel body = Ui.clear(new GridLayout(1, 0, Theme.GAP_LARGE, 0));
        body.add(Ui.scroll(table));
        body.add(buildDetailCard());
        add(body, BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(event -> showDetails());
        completeButton.addActionListener(event -> complete());
    }

    @Override
    public void refresh() {
        rows.clear();
        rows.addAll(controller.getTasks());
        model.setRowCount(0);
        rows.forEach(task -> model.addRow(new Object[] {Ui.formatDate(task.getPreferredDate()),
            task.getAddress().getDisplayName(), Ui.materialName(task.getMaterialType())}));
        countLabel.setText(rows.size() + " pickups assigned to you");
        if (!rows.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
        showDetails();
    }

    private PickupRequest selected() {
        int index = table.getSelectedRow();
        return index < 0 ? null : rows.get(index);
    }

    private void showDetails() {
        PickupRequest task = selected();
        weightField.setText("");
        details.setText(task == null ? "Select a task to see its details." : describe(task));
    }

    private static String describe(PickupRequest task) {
        Double estimate = task.getEstimatedWeightKg();
        return Ui.formatAddress(task.getAddress()) + "\n\n"
                + "Resident: " + task.getResident().getName() + "\n"
                + "Date: " + Ui.formatDate(task.getPreferredDate()) + "\n"
                + "Material: " + Ui.materialName(task.getMaterialType()) + "\n"
                + "Estimated weight: " + (estimate == null ? "not given" : Ui.formatKg(estimate)) + "\n"
                + "Access: " + orNone(task.getAccessInstructions()) + "\n"
                + "Notes: " + orNone(task.getNotes());
    }

    private static String orNone(String text) {
        return text == null || text.isBlank() ? "none" : text;
    }

    private void complete() {
        try {
            int points = controller.completeTask(selected(), weightField.getText().trim());
            refresh();
            Ui.showInfo(this, "Pickup completed. The resident earned " + points + " points.");
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildDetailCard() {
        details.setEditable(false);
        details.setBorder(null);
        details.setForeground(Theme.TEXT_PRIMARY);
        JPanel weightRow = Ui.clear(new BorderLayout(Theme.GAP, Theme.GAP_SMALL));
        weightRow.add(Ui.fieldLabel("Actual weight (kg)"), BorderLayout.NORTH);
        weightRow.add(weightField, BorderLayout.CENTER);
        weightRow.add(completeButton, BorderLayout.EAST);
        JPanel card = Ui.card(new BorderLayout(0, Theme.GAP_LARGE));
        card.add(details, BorderLayout.CENTER);
        card.add(weightRow, BorderLayout.SOUTH);
        return card;
    }
}

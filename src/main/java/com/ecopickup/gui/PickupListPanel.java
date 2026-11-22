package com.ecopickup.gui;

import com.ecopickup.model.PickupRequest;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// View: the resident's pickups and the points each one earned.
final class PickupListPanel extends PagePanel {

    private final PickupController controller;
    private final JLabel balanceValue = new JLabel();
    private final JLabel activeValue = new JLabel();
    private final JLabel recycledValue = new JLabel();
    private final DefaultTableModel model = Ui.tableModel("Date", "Material", "Weight", "Status", "Collector",
            "Points");
    private final JTable table = Ui.table(model);
    private final JButton cancelButton = Ui.secondaryButton("Cancel selected pickup");
    private final List<PickupRequest> rows = new ArrayList<>();

    PickupListPanel(PickupController controller) {
        this.controller = controller;
        add(Ui.pageHeader("My pickups", new JLabel("Track your requests and the points you earned.")),
                BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        cancelButton.addActionListener(event -> cancelSelected());
    }

    @Override
    public void refresh() {
        rows.clear();
        rows.addAll(controller.getMyPickups());
        model.setRowCount(0);
        rows.forEach(request -> model.addRow(toRow(request)));
        balanceValue.setText(controller.getBalance() + " pts");
        activeValue.setText(String.valueOf(controller.countActivePickups()));
        recycledValue.setText(Ui.formatKg(controller.getRecycledKg()));
    }

    private Object[] toRow(PickupRequest request) {
        Double actual = controller.getActualWeight(request);
        Double estimate = request.getEstimatedWeightKg();
        String weight = actual != null ? Ui.formatKg(actual) : estimate != null ? "~" + Ui.formatKg(estimate) : "-";
        Integer points = controller.getPointsEarned(request);
        String collector = request.getAssignedCollector() == null ? "-" : request.getAssignedCollector().getName();
        return new Object[] {Ui.formatDate(request.getPreferredDate()), Ui.materialName(request.getMaterialType()),
            weight, Ui.formatStatus(request.getStatus()), collector, points == null ? "-" : "+" + points};
    }

    private void cancelSelected() {
        int index = table.getSelectedRow();
        try {
            controller.cancel(index < 0 ? null : rows.get(index));
            refresh();
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildBody() {
        JPanel stats = Ui.clear(new GridLayout(1, 0, Theme.GAP, 0));
        stats.add(Ui.statCard("Points balance", balanceValue));
        stats.add(Ui.statCard("Active requests", activeValue));
        stats.add(Ui.statCard("Recycled so far", recycledValue));
        JPanel body = Ui.clear(new BorderLayout(0, Theme.GAP_LARGE));
        body.add(stats, BorderLayout.NORTH);
        body.add(Ui.scroll(table), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildFooter() {
        JPanel footer = Ui.clear(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(cancelButton);
        return footer;
    }
}

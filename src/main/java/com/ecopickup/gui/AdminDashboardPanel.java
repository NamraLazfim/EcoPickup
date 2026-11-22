package com.ecopickup.gui;

import com.ecopickup.model.Collector;
import com.ecopickup.model.PickupRequest;
import com.ecopickup.model.PickupStatus;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// Admin home: city numbers, recycling per material, assigning new requests.
final class AdminDashboardPanel extends PagePanel {

    private final PickupController controller;
    private final AuthController auth;
    private final JLabel newValue = new JLabel();
    private final JLabel assignedValue = new JLabel();
    private final JLabel completedValue = new JLabel();
    private final JLabel recycledValue = new JLabel();
    private final DefaultTableModel requestModel = Ui.tableModel("Date", "Address", "Material", "Resident");
    private final JTable requestTable = Ui.table(requestModel);
    private final DefaultTableModel materialModel = Ui.tableModel("Material", "Pickups", "Collected");
    private final JComboBox<String> collectorBox = new JComboBox<>();
    private final JButton assignButton = Ui.primaryButton("Assign");
    private final List<PickupRequest> rows = new ArrayList<>();
    private List<Collector> collectors = List.of();

    AdminDashboardPanel(PickupController controller, AuthController auth) {
        this.controller = controller;
        this.auth = auth;
        add(Ui.pageHeader("City dashboard", new JLabel("Petaling Jaya City Council (MBPJ)")), BorderLayout.NORTH);
        JPanel columns = Ui.clear(new GridLayout(1, 0, Theme.GAP_LARGE, 0));
        columns.add(buildRequestsCard());
        columns.add(buildMaterialCard());
        JPanel body = Ui.clear(new BorderLayout(0, Theme.GAP_LARGE));
        body.add(buildStats(), BorderLayout.NORTH);
        body.add(columns, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        assignButton.addActionListener(event -> assignSelected());
    }

    @Override
    public void refresh() {
        rows.clear();
        rows.addAll(controller.getRequests(PickupStatus.REQUESTED));
        requestModel.setRowCount(0);
        rows.forEach(request -> requestModel.addRow(new Object[] {Ui.formatDate(request.getPreferredDate()),
            Ui.formatAddress(request.getAddress()), Ui.materialName(request.getMaterialType()),
            request.getResident().getName()}));
        materialModel.setRowCount(0);
        controller.getMaterialStats().forEach(stat -> materialModel.addRow(new Object[] {
            Ui.materialName(stat.material()), stat.pickups(), Ui.formatKg(stat.kilograms())}));
        newValue.setText(String.valueOf(rows.size()));
        assignedValue.setText(String.valueOf(controller.getRequests(PickupStatus.ASSIGNED).size()));
        completedValue.setText(String.valueOf(controller.getRequests(PickupStatus.COMPLETED).size()));
        recycledValue.setText(Ui.formatKg(controller.getRecycledKg()));
        collectors = auth.getCollectors();
        collectorBox.setModel(new DefaultComboBoxModel<>(collectors.stream()
                .map(c -> c.getName() + " (" + c.getServiceZone() + ")").toArray(String[]::new)));
    }

    private void assignSelected() {
        int row = requestTable.getSelectedRow();
        int collectorIndex = collectorBox.getSelectedIndex();
        try {
            Collector collector = collectorIndex < 0 ? null : collectors.get(collectorIndex);
            controller.assign(row < 0 ? null : rows.get(row), collector);
            refresh();
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildStats() {
        JPanel stats = Ui.clear(new GridLayout(1, 0, Theme.GAP, 0));
        stats.add(Ui.statCard("New requests", newValue));
        stats.add(Ui.statCard("Assigned", assignedValue));
        stats.add(Ui.statCard("Completed", completedValue));
        stats.add(Ui.statCard("Recycled", recycledValue));
        return stats;
    }

    private JPanel buildRequestsCard() {
        collectorBox.setFont(Theme.BODY_FONT);
        JPanel assignRow = Ui.clear(new BorderLayout(Theme.GAP, Theme.GAP_SMALL));
        assignRow.add(Ui.fieldLabel("Assign the selected request to"), BorderLayout.NORTH);
        assignRow.add(collectorBox, BorderLayout.CENTER);
        assignRow.add(assignButton, BorderLayout.EAST);
        JPanel card = Ui.card(new BorderLayout(0, Theme.GAP));
        card.add(Ui.label("New requests", Theme.BOLD_FONT, Theme.TEXT_PRIMARY), BorderLayout.NORTH);
        card.add(Ui.scroll(requestTable), BorderLayout.CENTER);
        card.add(assignRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildMaterialCard() {
        JPanel card = Ui.card(new BorderLayout(0, Theme.GAP));
        card.add(Ui.label("Recycling by material", Theme.BOLD_FONT, Theme.TEXT_PRIMARY), BorderLayout.NORTH);
        card.add(Ui.scroll(Ui.table(materialModel)), BorderLayout.CENTER);
        return card;
    }
}

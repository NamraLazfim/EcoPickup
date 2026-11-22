package com.ecopickup.gui;

import com.ecopickup.model.PickupRequest;
import com.ecopickup.model.PickupStatus;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// Admin list of every pickup, with a status filter and cancel.
final class AllRequestsPanel extends PagePanel {

    private static final String ALL = "All statuses";

    private final PickupController controller;
    private final JComboBox<String> statusFilter = new JComboBox<>();
    private final DefaultTableModel model = Ui.tableModel("Date", "Resident", "Address", "Material", "Status",
            "Collector");
    private final JTable table = Ui.table(model);
    private final JButton cancelButton = Ui.secondaryButton("Cancel selected pickup");
    private final List<PickupRequest> rows = new ArrayList<>();

    AllRequestsPanel(PickupController controller) {
        this.controller = controller;
        statusFilter.addItem(ALL);
        for (PickupStatus status : PickupStatus.values()) {
            statusFilter.addItem(Ui.formatStatus(status));
        }
        statusFilter.setFont(Theme.BODY_FONT);
        add(buildHeader(), BorderLayout.NORTH);
        add(Ui.scroll(table), BorderLayout.CENTER);
        JPanel footer = Ui.clear(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.add(cancelButton);
        add(footer, BorderLayout.SOUTH);
        statusFilter.addActionListener(event -> refresh());
        cancelButton.addActionListener(event -> cancelSelected());
    }

    @Override
    public void refresh() {
        int index = statusFilter.getSelectedIndex();
        PickupStatus status = index <= 0 ? null : PickupStatus.values()[index - 1];
        rows.clear();
        rows.addAll(controller.getRequests(status));
        model.setRowCount(0);
        rows.forEach(request -> model.addRow(new Object[] {Ui.formatDate(request.getPreferredDate()),
            request.getResident().getName(), Ui.formatAddress(request.getAddress()),
            Ui.materialName(request.getMaterialType()), Ui.formatStatus(request.getStatus()),
            request.getAssignedCollector() == null ? "-" : request.getAssignedCollector().getName()}));
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

    private JPanel buildHeader() {
        JPanel filter = Ui.clear(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        filter.add(Ui.fieldLabel("Show"));
        filter.add(statusFilter);
        JPanel header = Ui.clear(new BorderLayout());
        header.add(Ui.pageHeader("All requests", new JLabel("Every pickup in the city.")), BorderLayout.WEST);
        header.add(filter, BorderLayout.EAST);
        return header;
    }
}

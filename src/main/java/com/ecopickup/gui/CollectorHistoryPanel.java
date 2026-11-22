package com.ecopickup.gui;

import com.ecopickup.model.CollectionRecord;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

// View: pickups the collector has completed.
final class CollectorHistoryPanel extends PagePanel {

    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);

    private final PickupController controller;
    private final JLabel countValue = new JLabel();
    private final JLabel weightValue = new JLabel();
    private final DefaultTableModel model = Ui.tableModel("Completed", "Address", "Material", "Weight", "Points");

    CollectorHistoryPanel(PickupController controller) {
        this.controller = controller;
        add(Ui.pageHeader("Completed pickups", new JLabel("Collections you have logged.")), BorderLayout.NORTH);
        JPanel stats = Ui.clear(new GridLayout(1, 0, Theme.GAP, 0));
        stats.add(Ui.statCard("Pickups completed", countValue));
        stats.add(Ui.statCard("Weight collected", weightValue));
        JPanel body = Ui.clear(new BorderLayout(0, Theme.GAP_LARGE));
        body.add(stats, BorderLayout.NORTH);
        body.add(Ui.scroll(Ui.table(model)), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        List<CollectionRecord> records = controller.getCompletedTasks();
        model.setRowCount(0);
        records.forEach(record -> model.addRow(new Object[] {record.getCompletedAt().format(WHEN),
            record.getRequest().getAddress().getDisplayName(), Ui.materialName(record.getMaterialType()),
            Ui.formatKg(record.getActualWeightKg()), "+" + controller.getPointsEarned(record.getRequest())}));
        countValue.setText(String.valueOf(records.size()));
        weightValue.setText(Ui.formatKg(controller.getCollectedKg()));
    }
}

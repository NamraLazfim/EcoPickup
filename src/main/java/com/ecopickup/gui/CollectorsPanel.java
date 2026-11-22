package com.ecopickup.gui;

import com.ecopickup.model.Collector;
import com.ecopickup.model.PickupStatus;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// Admin view of collectors, with a form to add a collector account.
final class CollectorsPanel extends PagePanel {

    private final AuthController auth;
    private final PickupController controller;
    private final DefaultTableModel model = Ui.tableModel("Name", "Email", "Zone", "Open tasks", "Completed");
    private final JTextField nameField = Ui.textField();
    private final JTextField emailField = Ui.textField();
    private final JTextField zoneField = Ui.textField();
    private final JPasswordField passwordField = Ui.passwordField();
    private final JButton addButton = Ui.primaryButton("Add collector");

    CollectorsPanel(AuthController auth, PickupController controller) {
        this.auth = auth;
        this.controller = controller;
        add(Ui.pageHeader("Collectors", new JLabel("Waste collectors and the zones they serve.")),
                BorderLayout.NORTH);
        JPanel body = Ui.clear(new BorderLayout(Theme.GAP_LARGE, 0));
        body.add(Ui.scroll(Ui.table(model)), BorderLayout.CENTER);
        body.add(buildForm(), BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
        addButton.addActionListener(event -> addCollector());
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        auth.getCollectorAccounts().forEach(account -> {
            Collector collector = (Collector) account.user();
            model.addRow(new Object[] {account.name(), account.email(), collector.getServiceZone(),
                controller.countTasks(collector, PickupStatus.ASSIGNED),
                controller.countTasks(collector, PickupStatus.COMPLETED)});
        });
    }

    private void addCollector() {
        try {
            Collector created = auth.createCollector(nameField.getText(), emailField.getText(), zoneField.getText(),
                    new String(passwordField.getPassword()));
            nameField.setText("");
            emailField.setText("");
            zoneField.setText("");
            passwordField.setText("");
            refresh();
            Ui.showInfo(this, created.getName() + " can now sign in with their email and password.");
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildForm() {
        JPanel fields = new StackPanel();
        fields.add(Ui.label("New collector", Theme.BOLD_FONT, Theme.TEXT_PRIMARY));
        fields.add(Ui.fieldLabel("Full name"));
        fields.add(nameField);
        fields.add(Ui.fieldLabel("Email"));
        fields.add(emailField);
        fields.add(Ui.fieldLabel("Service zone, e.g. PJU 3"));
        fields.add(zoneField);
        fields.add(Ui.fieldLabel("First password (8+ characters)"));
        fields.add(passwordField);
        JPanel card = Ui.card(new BorderLayout(0, Theme.GAP_LARGE));
        card.add(fields, BorderLayout.NORTH);
        card.add(addButton, BorderLayout.SOUTH);
        return card;
    }
}

package com.ecopickup.gui;

import com.ecopickup.model.PickupRequest;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// View: the resident books a pickup. All checks happen in the controller.
final class BookingPanel extends PagePanel {

    private static final int NOTES_ROWS = 3;
    private static final int PILL_PADDING_X = 12;
    private static final String CHECKING = "Checking the address with OpenStreetMap...";

    private final PickupController controller;
    private final JTextField addressField = Ui.textField();
    private final JButton verifyButton = Ui.secondaryButton("Verify");
    private final JLabel addressStatus = Ui.label(" ", Theme.SMALL_FONT, Theme.TEXT_SECONDARY);
    private final MaterialSelector materialSelector = new MaterialSelector();
    private final JTextField dateField = Ui.textField();
    private final JTextField weightField = Ui.textField();
    private final JTextField accessField = Ui.textField();
    private final JTextArea notesArea = Ui.textArea(NOTES_ROWS);
    private final JLabel balanceLabel = new JLabel();
    private final JLabel pointsLabel = new JLabel();
    private final JLabel errorLabel = Ui.label(" ", Theme.BODY_FONT, Theme.ERROR);
    private final JButton clearButton = Ui.secondaryButton("Clear");
    private final JButton submitButton = Ui.primaryButton("Request pickup");

    BookingPanel(PickupController controller) {
        this.controller = controller;
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        clearButton.addActionListener(event -> clearForm());
        submitButton.addActionListener(event -> submit());
        verifyButton.addActionListener(event -> verifyAddress());
        materialSelector.addSelectionListener(this::updateEstimate);
        weightField.getDocument().addDocumentListener(onChange(this::updateEstimate));
    }

    @Override
    public void refresh() {
        balanceLabel.setText(controller.getBalance() + " pts");
        if (addressField.getText().isBlank()) {
            addressField.setText(controller.getHomeAddress());
        }
        updateEstimate();
    }

    private void submit() {
        BookingForm form = new BookingForm(addressField.getText().trim(), materialSelector.getSelectedMaterial(),
                dateField.getText().trim(), weightField.getText().trim(), notesArea.getText().trim(),
                accessField.getText().trim());
        setBusy(true, CHECKING);
        Background.run(() -> controller.submitBooking(form), this::showBooked, this::showError);
    }

    private void showBooked(PickupRequest request) {
        setBusy(false, " ");
        clearForm();
        String note = request.getAddress().isVerified() ? ""
                : "\nThe address service was unavailable, so the address was saved as unverified.";
        Ui.showInfo(this, "Your " + Ui.materialName(request.getMaterialType()).toLowerCase()
                + " pickup is booked for " + Ui.formatDate(request.getPreferredDate()) + "." + note);
    }

    private void verifyAddress() {
        String typed = addressField.getText();
        setBusy(true, CHECKING);
        Background.run(() -> controller.checkAddress(typed), check -> {
            setBusy(false, check.message());
            if (check.address().isVerified()) {
                addressStatus.setText(String.format("<html>%s<br>(%.4f, %.4f)</html>", check.message(),
                        check.address().getLatitude(), check.address().getLongitude()));
            }
        }, this::showError);
    }

    private void showError(String message) {
        setBusy(false, " ");
        errorLabel.setText(message);
    }

    private void setBusy(boolean busy, String status) {
        errorLabel.setText(" ");
        addressStatus.setText(status);
        submitButton.setEnabled(!busy);
        verifyButton.setEnabled(!busy);
    }

    private void updateEstimate() {
        int points = controller.estimatePoints(materialSelector.getSelectedMaterial(), weightField.getText().trim());
        pointsLabel.setText("<html>You'll earn about <b>" + points + " points</b></html>");
    }

    private void clearForm() {
        addressField.setText(controller.getHomeAddress());
        dateField.setText("");
        weightField.setText("");
        accessField.setText("");
        notesArea.setText("");
        materialSelector.reset();
        errorLabel.setText(" ");
        addressStatus.setText(" ");
        updateEstimate();
    }

    private JPanel buildHeader() {
        JPanel header = Ui.clear(new BorderLayout());
        header.add(Ui.pageHeader("Book a pickup", new JLabel("Schedule a collection and earn reward points.")),
                BorderLayout.WEST);
        balanceLabel.setFont(Theme.BOLD_FONT);
        balanceLabel.setForeground(Theme.PRIMARY);
        balanceLabel.setBackground(Theme.PRIMARY_LIGHT);
        balanceLabel.setOpaque(true);
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(Theme.GAP_SMALL, PILL_PADDING_X,
                Theme.GAP_SMALL, PILL_PADDING_X));
        JPanel pill = Ui.clear(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pill.add(balanceLabel);
        header.add(pill, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCard() {
        JPanel card = Ui.card(new GridBagLayout());
        int row = 0;
        addRow(card, Ui.fieldLabel("Pickup address"), row++, 0);
        addRow(card, buildAddressRow(), row++, Theme.GAP_SMALL);
        addRow(card, addressStatus, row++, Theme.GAP_SMALL);
        addRow(card, Ui.fieldLabel("Material type"), row++, Theme.GAP);
        addRow(card, materialSelector, row++, Theme.GAP_SMALL);
        addRow(card, buildTwoColumns(labelled("Preferred date (yyyy-mm-dd)", dateField),
                labelled("Estimated weight in kg (optional)", weightField)), row++, Theme.GAP_LARGE);
        addRow(card, labelled("Access instructions (optional)", accessField), row++, Theme.GAP_LARGE);
        addRow(card, Ui.fieldLabel("Notes for the collector (optional)"), row++, Theme.GAP_LARGE);
        addRow(card, Ui.scroll(notesArea), row++, Theme.GAP_SMALL);
        addRow(card, errorLabel, row, Theme.GAP);
        return card;
    }

    private void addRow(JPanel card, JComponent component, int row, int topGap) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(topGap, 0, 0, 0);
        if (component instanceof JScrollPane) {
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
        }
        card.add(component, constraints);
    }

    private JPanel buildAddressRow() {
        JPanel row = Ui.clear(new BorderLayout(Theme.GAP, 0));
        row.add(addressField, BorderLayout.CENTER);
        row.add(verifyButton, BorderLayout.EAST);
        return row;
    }

    private static JPanel buildTwoColumns(JComponent left, JComponent right) {
        JPanel row = Ui.clear(new GridLayout(1, 0, Theme.GAP_LARGE, 0));
        row.add(left);
        row.add(right);
        return row;
    }

    private static JPanel labelled(String text, JComponent input) {
        JPanel panel = Ui.clear(new BorderLayout(0, Theme.GAP_SMALL));
        panel.add(Ui.fieldLabel(text), BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        pointsLabel.setFont(Theme.BODY_FONT);
        pointsLabel.setForeground(Theme.TEXT_SECONDARY);
        JPanel buttons = Ui.clear(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        buttons.add(clearButton);
        buttons.add(submitButton);
        JPanel footer = Ui.clear(new BorderLayout());
        footer.add(pointsLabel, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.EAST);
        return footer;
    }

    private static DocumentListener onChange(Runnable action) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                action.run();
            }
        };
    }
}

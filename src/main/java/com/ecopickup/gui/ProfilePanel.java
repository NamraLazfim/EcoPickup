package com.ecopickup.gui;

import com.ecopickup.service.Account;
import com.ecopickup.service.Role;
import com.ecopickup.model.Collector;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// View: account details, home address and changing the password.
final class ProfilePanel extends PagePanel {

    private static final int DETAIL_COLUMNS = 2;

    private final AuthController auth;
    private final JLabel nameValue = valueLabel();
    private final JLabel emailValue = valueLabel();
    private final JLabel roleValue = valueLabel();
    private final JLabel idValue = valueLabel();
    private final JTextField homeField = Ui.textField();
    private final JButton saveAddressButton = Ui.secondaryButton("Save address");
    private final JPasswordField currentField = Ui.passwordField();
    private final JPasswordField newField = Ui.passwordField();
    private final JPasswordField confirmField = Ui.passwordField();
    private final JButton changeButton = Ui.primaryButton("Change password");
    private final JPanel addressCard;

    ProfilePanel(AuthController auth) {
        this.auth = auth;
        add(Ui.pageHeader("Profile", new JLabel("Your account details.")), BorderLayout.NORTH);
        addressCard = buildAddressCard();
        JPanel left = Ui.clear(new BorderLayout(0, Theme.GAP_LARGE));
        left.add(buildDetailsCard(), BorderLayout.NORTH);
        left.add(addressCard, BorderLayout.CENTER);
        JPanel body = Ui.clear(new GridLayout(1, 0, Theme.GAP_LARGE, 0));
        body.add(left);
        body.add(buildPasswordCard());
        add(body, BorderLayout.CENTER);
        saveAddressButton.addActionListener(event -> saveAddress());
        changeButton.addActionListener(event -> changePassword());
    }

    @Override
    public void refresh() {
        Account account = auth.getCurrentAccount();
        nameValue.setText(account.name());
        emailValue.setText(account.email());
        roleValue.setText(account.role().getLabel());
        boolean collector = account.user() instanceof Collector;
        idValue.setText(account.user() == null ? "-" : account.user().getId()
                + (collector ? "  (zone " + ((Collector) account.user()).getServiceZone() + ")" : ""));
        homeField.setText(account.homeAddress());
        addressCard.setVisible(account.role() == Role.RESIDENT);
    }

    private void saveAddress() {
        auth.updateHomeAddress(homeField.getText());
        Ui.showInfo(this, "Home address saved. It will fill the booking form.");
    }

    private void changePassword() {
        try {
            auth.changePassword(new String(currentField.getPassword()), new String(newField.getPassword()),
                    new String(confirmField.getPassword()));
            currentField.setText("");
            newField.setText("");
            confirmField.setText("");
            Ui.showInfo(this, "Password changed.");
        } catch (IllegalArgumentException invalid) {
            Ui.showError(this, invalid.getMessage());
        }
    }

    private JPanel buildDetailsCard() {
        JPanel rows = Ui.clear(new GridLayout(0, DETAIL_COLUMNS, Theme.GAP, Theme.GAP));
        rows.add(Ui.fieldLabel("Name"));
        rows.add(nameValue);
        rows.add(Ui.fieldLabel("Email"));
        rows.add(emailValue);
        rows.add(Ui.fieldLabel("Role"));
        rows.add(roleValue);
        rows.add(Ui.fieldLabel("Account ID"));
        rows.add(idValue);
        JPanel card = Ui.card(new BorderLayout(0, Theme.GAP));
        card.add(Ui.label("Account", Theme.BOLD_FONT, Theme.TEXT_PRIMARY), BorderLayout.NORTH);
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildAddressCard() {
        JPanel fields = new StackPanel();
        fields.add(Ui.label("Home address", Theme.BOLD_FONT, Theme.TEXT_PRIMARY));
        fields.add(Ui.fieldLabel("Used to fill the booking form"));
        fields.add(homeField);
        fields.add(saveAddressButton);
        JPanel card = Ui.card(new BorderLayout());
        card.add(fields, BorderLayout.NORTH);
        return card;
    }

    private JPanel buildPasswordCard() {
        JPanel fields = new StackPanel();
        fields.add(Ui.label("Change password", Theme.BOLD_FONT, Theme.TEXT_PRIMARY));
        fields.add(Ui.fieldLabel("Current password"));
        fields.add(currentField);
        fields.add(Ui.fieldLabel("New password (at least 8 characters)"));
        fields.add(newField);
        fields.add(Ui.fieldLabel("Confirm new password"));
        fields.add(confirmField);
        fields.add(changeButton);
        JPanel card = Ui.card(new BorderLayout());
        card.add(fields, BorderLayout.NORTH);
        return card;
    }

    private static JLabel valueLabel() {
        return Ui.label("", Theme.BODY_FONT, Theme.TEXT_PRIMARY);
    }
}

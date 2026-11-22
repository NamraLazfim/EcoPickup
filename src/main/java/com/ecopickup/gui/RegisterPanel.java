package com.ecopickup.gui;

import com.ecopickup.service.Role;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// View: a new resident creates an account.
final class RegisterPanel extends JPanel {

    private final AuthController auth;
    private final Consumer<Role> onSignedIn;
    private final JTextField nameField = Ui.textField();
    private final JTextField emailField = Ui.textField();
    private final JTextField addressField = Ui.textField();
    private final JPasswordField passwordField = Ui.passwordField();
    private final JPasswordField confirmField = Ui.passwordField();
    private final JLabel errorLabel = Ui.label(" ", Theme.BODY_FONT, Theme.ERROR);
    private final JButton createButton = Ui.primaryButton("Create account");
    private final JButton signInLink = Ui.linkButton("Sign in");

    RegisterPanel(AuthController auth, Consumer<Role> onSignedIn, Runnable onSignIn) {
        this.auth = auth;
        this.onSignedIn = onSignedIn;
        AuthCard.build(this, "Create a resident account.", buildForm());
        createButton.addActionListener(event -> register());
        signInLink.addActionListener(event -> {
            reset();
            onSignIn.run();
        });
    }

    private void register() {
        RegisterForm form = new RegisterForm(nameField.getText(), emailField.getText(), addressField.getText(),
                new String(passwordField.getPassword()), new String(confirmField.getPassword()));
        try {
            auth.registerAndSignIn(form);
            reset();
            onSignedIn.accept(Role.RESIDENT);
        } catch (IllegalArgumentException invalid) {
            errorLabel.setText(invalid.getMessage());
        }
    }

    private void reset() {
        nameField.setText("");
        emailField.setText("");
        addressField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        errorLabel.setText(" ");
    }

    private JPanel buildForm() {
        JPanel fields = new StackPanel();
        fields.add(Ui.fieldLabel("Full name"));
        fields.add(nameField);
        fields.add(Ui.fieldLabel("Email"));
        fields.add(emailField);
        fields.add(Ui.fieldLabel("Home address (optional, fills the booking form)"));
        fields.add(addressField);
        fields.add(Ui.fieldLabel("Password (at least 8 characters)"));
        fields.add(passwordField);
        fields.add(Ui.fieldLabel("Confirm password"));
        fields.add(confirmField);
        fields.add(errorLabel);
        fields.add(createButton);
        fields.add(buildSignInRow());
        return fields;
    }

    private JPanel buildSignInRow() {
        JPanel signIn = Ui.clear(new FlowLayout(FlowLayout.CENTER, Theme.GAP_SMALL, 0));
        signIn.add(Ui.fieldLabel("Already have an account?"));
        signIn.add(signInLink);
        return signIn;
    }
}

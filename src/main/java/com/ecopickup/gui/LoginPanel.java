package com.ecopickup.gui;

import com.ecopickup.service.Role;
import java.awt.FlowLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// View: sign in with email and password.
final class LoginPanel extends JPanel {

    private final AuthController auth;
    private final Consumer<Role> onSignedIn;
    private final JTextField emailField = Ui.textField();
    private final JPasswordField passwordField = Ui.passwordField();
    private final JLabel errorLabel = Ui.label(" ", Theme.BODY_FONT, Theme.ERROR);
    private final JButton signInButton = Ui.primaryButton("Sign in");
    private final JButton registerLink = Ui.linkButton("Create an account");

    LoginPanel(AuthController auth, Consumer<Role> onSignedIn, Runnable onRegister) {
        this.auth = auth;
        this.onSignedIn = onSignedIn;
        AuthCard.build(this, "Sign in to book and track your pickups.", buildForm());
        signInButton.addActionListener(event -> signIn());
        passwordField.addActionListener(event -> signIn());
        registerLink.addActionListener(event -> onRegister.run());
    }

    void reset() {
        emailField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }

    private void signIn() {
        try {
            Role role = auth.signIn(emailField.getText(), new String(passwordField.getPassword()));
            reset();
            onSignedIn.accept(role);
        } catch (IllegalArgumentException invalid) {
            errorLabel.setText(invalid.getMessage());
            passwordField.setText("");
        }
    }

    private JPanel buildForm() {
        JPanel fields = new StackPanel();
        fields.add(Ui.fieldLabel("Email"));
        fields.add(emailField);
        fields.add(Ui.fieldLabel("Password"));
        fields.add(passwordField);
        fields.add(errorLabel);
        fields.add(signInButton);
        JPanel register = Ui.clear(new FlowLayout(FlowLayout.CENTER, Theme.GAP_SMALL, 0));
        register.add(Ui.fieldLabel("New resident?"));
        register.add(registerLink);
        fields.add(register);
        fields.add(Ui.label("<html>Demo resident: resident@gmail.com<br>Demo collector: collector@gmail.com<br>"
                + "Demo admin: admin@gmail.com<br>Password for all: " + DemoData.DEMO_PASSWORD + "</html>",
                Theme.SMALL_FONT, Theme.TEXT_SECONDARY));
        return fields;
    }
}

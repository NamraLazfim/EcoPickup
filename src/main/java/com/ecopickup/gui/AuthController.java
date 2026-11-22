package com.ecopickup.gui;

import com.ecopickup.model.Collector;
import com.ecopickup.model.Resident;
import com.ecopickup.service.Account;
import com.ecopickup.service.AuthService;
import com.ecopickup.service.Role;
import java.util.List;

// Controller for accounts: remembers who is signed in and passes everything else to AuthService.
final class AuthController {

    private final AuthService service;
    private Account current;

    AuthController(AuthService service) {
        this.service = service;
    }

    Role signIn(String email, String password) {
        current = service.authenticate(email, password);
        return current.role();
    }

    void signOut() {
        current = null;
    }

    Resident registerAndSignIn(RegisterForm form) {
        current = service.register(form.name(), form.email(), form.homeAddress(), form.password(), form.confirm());
        return (Resident) current.user();
    }

    void changePassword(String currentPassword, String newPassword, String confirm) {
        if (current == null) {
            throw new IllegalArgumentException("Sign in first.");
        }
        current = service.changePassword(current.email(), currentPassword, newPassword, confirm);
    }

    void updateHomeAddress(String address) {
        current = service.updateHomeAddress(current.email(), address);
    }

    Collector createCollector(String name, String email, String zone, String password) {
        return (Collector) service.createCollector(name, email, zone, password).user();
    }

    Account getCurrentAccount() {
        return current;
    }

    Resident getCurrentResident() {
        return current != null && current.user() instanceof Resident resident ? resident : null;
    }

    Collector getCurrentCollector() {
        return current != null && current.user() instanceof Collector collector ? collector : null;
    }

    String getCurrentName() {
        return current == null ? "" : current.name();
    }

    Role getCurrentRole() {
        return current == null ? null : current.role();
    }

    List<Account> getCollectorAccounts() {
        return service.getAccounts(Role.COLLECTOR);
    }

    List<Collector> getCollectors() {
        return service.getCollectors();
    }
}

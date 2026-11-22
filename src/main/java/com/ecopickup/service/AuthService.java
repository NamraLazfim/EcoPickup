package com.ecopickup.service;

import com.ecopickup.model.Collector;
import com.ecopickup.model.Resident;
import com.ecopickup.model.User;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Stores accounts and applies the account rules: valid email, unique email, password of at least
 * 8 characters, and checking passwords at sign-in. Errors are {@link IllegalArgumentException}s with
 * messages that can be shown to the user.
 */
public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextId = 1;

    /**
     * Registers a new resident.
     *
     * @param name        full name
     * @param email       sign-in email
     * @param homeAddress usual pickup address, may be empty
     * @param password    the password
     * @param confirm     the password typed a second time
     * @return the new account
     */
    public Account register(String name, String email, String homeAddress, String password, String confirm) {
        requireText(name, "Enter your name.");
        String cleanEmail = checkNewEmail(email);
        checkPassword(password, confirm);
        Resident resident = new Resident(newId("R"), name.trim(), cleanEmail);
        addAccount(resident.getName(), cleanEmail, password, Role.RESIDENT, resident);
        return save(accounts.get(cleanEmail).withHomeAddress(homeAddress == null ? "" : homeAddress.trim()));
    }

    /**
     * Creates a collector account. Used by admins.
     *
     * @param name     the collector's name
     * @param email    sign-in email
     * @param zone     the service zone, e.g. "PJU 3"
     * @param password the first password
     * @return the new account
     */
    public Account createCollector(String name, String email, String zone, String password) {
        requireText(name, "Enter the collector's name.");
        requireText(zone, "Enter the service zone.");
        String cleanEmail = checkNewEmail(email);
        checkPassword(password, password);
        Collector collector = new Collector(newId("C"), name.trim(), cleanEmail, zone.trim());
        addAccount(collector.getName(), cleanEmail, password, Role.COLLECTOR, collector);
        return accounts.get(cleanEmail);
    }

    /**
     * Stores an account with a hashed password, e.g. for demo data.
     *
     * @param name     the display name
     * @param email    sign-in email
     * @param password the plain password, hashed before it is stored
     * @param role     the role
     * @param user     the resident or collector, or null for an admin
     */
    public void addAccount(String name, String email, String password, Role role, User user) {
        String salt = PasswordHasher.newSalt();
        String cleanEmail = clean(email);
        save(new Account(name, cleanEmail, salt, PasswordHasher.hash(salt, password), role, user, ""));
    }

    /**
     * Checks an email and password.
     *
     * @param email    the typed email
     * @param password the typed password
     * @return the matching account
     */
    public Account authenticate(String email, String password) {
        requireText(email, "Enter your email.");
        requireText(password, "Enter your password.");
        Account account = accounts.get(clean(email));
        if (account == null || !PasswordHasher.matches(account.salt(), password, account.passwordHash())) {
            throw new IllegalArgumentException("Email or password is incorrect.");
        }
        return account;
    }

    /**
     * Changes an account's password after checking the current one.
     *
     * @param email           the account's email
     * @param currentPassword the current password
     * @param newPassword     the new password
     * @param confirm         the new password typed a second time
     * @return the updated account
     */
    public Account changePassword(String email, String currentPassword, String newPassword, String confirm) {
        Account account = accounts.get(clean(email));
        if (account == null || !PasswordHasher.matches(account.salt(), currentPassword, account.passwordHash())) {
            throw new IllegalArgumentException("Your current password is incorrect.");
        }
        checkPassword(newPassword, confirm);
        String salt = PasswordHasher.newSalt();
        return save(account.withPassword(salt, PasswordHasher.hash(salt, newPassword)));
    }

    /**
     * Saves a resident's usual pickup address.
     *
     * @param email   the account's email
     * @param address the address, may be empty
     * @return the updated account
     */
    public Account updateHomeAddress(String email, String address) {
        return save(accounts.get(clean(email)).withHomeAddress(address == null ? "" : address.trim()));
    }

    /**
     * Returns the accounts with a role.
     *
     * @param role the role
     * @return the accounts
     */
    public List<Account> getAccounts(Role role) {
        return accounts.values().stream().filter(a -> a.role() == role).toList();
    }

    /**
     * Returns every collector.
     *
     * @return the collectors
     */
    public List<Collector> getCollectors() {
        return getAccounts(Role.COLLECTOR).stream().map(a -> (Collector) a.user()).toList();
    }

    private Account save(Account account) {
        accounts.put(account.email(), account);
        return account;
    }

    private String checkNewEmail(String email) {
        requireText(email, "Enter an email address.");
        String cleanEmail = clean(email);
        if (!EMAIL.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("Enter a valid email, for example name@mail.com.");
        }
        if (accounts.containsKey(cleanEmail)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        return cleanEmail;
    }

    private static void checkPassword(String password, String confirm) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (!password.equals(confirm)) {
            throw new IllegalArgumentException("The two passwords don't match.");
        }
    }

    private String newId(String prefix) {
        return String.format("%s-%03d", prefix, nextId++);
    }

    private static String clean(String email) {
        return email.trim().toLowerCase(Locale.ENGLISH);
    }

    private static void requireText(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}

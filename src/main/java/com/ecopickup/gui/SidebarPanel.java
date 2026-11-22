package com.ecopickup.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// The green menu on the left.
final class SidebarPanel extends JPanel {

    private static final int WIDTH = 210;
    private static final int LOGO_SIZE = 32;
    private static final int NAV_PADDING_X = 14;

    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final JLabel userName = new JLabel();
    private final JLabel userRole = new JLabel();

    SidebarPanel(Map<String, String> pages, Consumer<String> onNavigate, Runnable onSignOut) {
        super(new BorderLayout(0, Theme.GAP_LARGE));
        setBackground(Theme.SIDEBAR_BG);
        setPreferredSize(new Dimension(WIDTH, 0));
        setBorder(BorderFactory.createEmptyBorder(Theme.PAGE_PADDING, Theme.CONTROL_PADDING,
                Theme.GAP_LARGE, Theme.CONTROL_PADDING));
        add(buildLogo(), BorderLayout.NORTH);
        add(buildNavigation(pages, onNavigate), BorderLayout.CENTER);
        add(buildUserRow(onSignOut), BorderLayout.SOUTH);
        setUser("Guest", "Resident");
    }

    void setUser(String name, String role) {
        userName.setText(name);
        userRole.setText(role);
    }

    void setActive(String cardName) {
        navButtons.forEach((card, button) -> styleNavButton(button, card.equals(cardName)));
    }

    private JPanel buildLogo() {
        JLabel badge = new JLabel("EP", SwingConstants.CENTER);
        badge.setFont(Theme.BOLD_FONT);
        badge.setForeground(Color.WHITE);
        badge.setBackground(Theme.LOGO_BG);
        badge.setOpaque(true);
        badge.setPreferredSize(new Dimension(LOGO_SIZE, LOGO_SIZE));
        JLabel name = new JLabel("EcoPickup");
        name.setFont(Theme.LOGO_FONT);
        name.setForeground(Color.WHITE);
        JPanel logo = new JPanel(new BorderLayout(Theme.CONTROL_PADDING, 0));
        logo.setOpaque(false);
        logo.setBorder(BorderFactory.createEmptyBorder(0, Theme.GAP_SMALL, Theme.GAP, 0));
        logo.add(badge, BorderLayout.WEST);
        logo.add(name, BorderLayout.CENTER);
        return logo;
    }

    private JPanel buildNavigation(Map<String, String> pages, Consumer<String> onNavigate) {
        JPanel list = new JPanel(new GridLayout(0, 1, 0, Theme.GAP_SMALL));
        list.setOpaque(false);
        pages.forEach((label, card) -> {
            JButton button = new JButton(label);
            button.addActionListener(event -> onNavigate.accept(card));
            navButtons.put(card, button);
            list.add(button);
        });
        setActive(pages.values().iterator().next());
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(list, BorderLayout.NORTH);
        return holder;
    }

    private void styleNavButton(JButton button, boolean active) {
        button.setFont(active ? Theme.BOLD_FONT : Theme.BODY_FONT);
        button.setForeground(active ? Color.WHITE : Theme.SIDEBAR_TEXT);
        button.setBackground(active ? Theme.SIDEBAR_ACTIVE : Theme.SIDEBAR_BG);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(Theme.CONTROL_PADDING, NAV_PADDING_X,
                Theme.CONTROL_PADDING, NAV_PADDING_X));
    }

    private JPanel buildUserRow(Runnable onSignOut) {
        userName.setFont(Theme.BOLD_FONT);
        userName.setForeground(Color.WHITE);
        userRole.setFont(Theme.SMALL_FONT);
        userRole.setForeground(Theme.SIDEBAR_MUTED);
        JPanel row = new JPanel(new GridLayout(0, 1));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.SIDEBAR_ACTIVE),
                BorderFactory.createEmptyBorder(Theme.CONTROL_PADDING, Theme.GAP_SMALL, 0, 0)));
        row.add(userName);
        row.add(userRole);
        JButton signOut = new JButton("Sign out");
        styleNavButton(signOut, false);
        signOut.setBorder(BorderFactory.createEmptyBorder(Theme.GAP, 0, 0, 0));
        signOut.addActionListener(event -> onSignOut.run());
        row.add(signOut);
        return row;
    }
}

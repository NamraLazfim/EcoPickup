package com.ecopickup.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// White card on a green background used by the sign-in and register pages.
final class AuthCard {

    private static final int CARD_WIDTH = 430;
    private static final int LOGO_SIZE = 36;

    private AuthCard() {
    }

    static void build(JPanel page, String subtitle, JComponent content) {
        page.setLayout(new GridBagLayout());
        page.setBackground(Theme.SIDEBAR_BG);
        JPanel card = new JPanel(new BorderLayout(0, Theme.GAP_LARGE));
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(Theme.PAGE_PADDING, Theme.PAGE_PADDING,
                Theme.PAGE_PADDING, Theme.PAGE_PADDING));
        card.add(logo(subtitle), BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(CARD_WIDTH, card.getPreferredSize().height));
        page.add(card);
    }

    private static JPanel logo(String subtitle) {
        JLabel badge = new JLabel("EP", SwingConstants.CENTER);
        badge.setFont(Theme.BOLD_FONT);
        badge.setForeground(Color.WHITE);
        badge.setBackground(Theme.LOGO_BG);
        badge.setOpaque(true);
        badge.setPreferredSize(new Dimension(LOGO_SIZE, LOGO_SIZE));
        JPanel text = Ui.clear(new GridLayout(0, 1));
        text.add(Ui.label("EcoPickup", Theme.LOGO_FONT, Theme.TEXT_PRIMARY));
        text.add(Ui.label(subtitle, Theme.SMALL_FONT, Theme.TEXT_SECONDARY));
        JPanel logo = Ui.clear(new BorderLayout(Theme.CONTROL_PADDING, 0));
        logo.add(badge, BorderLayout.WEST);
        logo.add(text, BorderLayout.CENTER);
        return logo;
    }
}

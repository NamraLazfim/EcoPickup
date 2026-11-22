package com.ecopickup.gui;

import com.ecopickup.model.MaterialType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

// Helpers that create styled components and format values.
final class Ui {

    private static final int BUTTON_PADDING_X = 18;
    private static final int ROW_HEIGHT = 34;
    private static final float STAT_FONT_SIZE = 22f;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private static String lastMessage = "";

    private Ui() {
    }

    static String lastMessage() {
        return lastMessage;
    }

    // ---------- text ----------

    static JLabel fieldLabel(String text) {
        return label(text, Theme.SMALL_FONT, Theme.TEXT_SECONDARY);
    }

    static JLabel label(String text, Font font, Color colour) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(colour);
        return label;
    }

    static JPanel pageHeader(String title, JLabel subtitle) {
        subtitle.setFont(Theme.BODY_FONT);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        JPanel header = new JPanel(new GridLayout(0, 1, 0, Theme.GAP_SMALL));
        header.setOpaque(false);
        header.add(label(title, Theme.TITLE_FONT, Theme.TEXT_PRIMARY));
        header.add(subtitle);
        return header;
    }

    // ---------- inputs and buttons ----------

    static JTextField textField() {
        JTextField field = new JTextField();
        field.setFont(Theme.BODY_FONT);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setBorder(inputBorder());
        return field;
    }

    static javax.swing.JPasswordField passwordField() {
        javax.swing.JPasswordField field = new javax.swing.JPasswordField();
        field.setFont(Theme.BODY_FONT);
        field.setBorder(inputBorder());
        return field;
    }

    static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.BODY_FONT);
        button.setForeground(Theme.PRIMARY);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    static JTextArea textArea(int rows) {
        JTextArea area = new JTextArea(rows, 0);
        area.setFont(Theme.BODY_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(Theme.GAP, Theme.GAP, Theme.GAP, Theme.GAP));
        return area;
    }

    static JButton primaryButton(String text) {
        return flatButton(text, Theme.PRIMARY, Color.WHITE, Theme.PRIMARY);
    }

    static JButton secondaryButton(String text) {
        return flatButton(text, Theme.CARD_BG, Theme.TEXT_PRIMARY, Theme.BORDER);
    }

    static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.CONTROL_PADDING, Theme.CONTROL_PADDING,
                        Theme.CONTROL_PADDING, Theme.CONTROL_PADDING));
    }

    static void showMaterialNames(javax.swing.JComboBox<MaterialType> box) {
        box.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                                                          boolean selected, boolean focused) {
                Object text = value instanceof MaterialType type ? materialName(type) : value;
                return super.getListCellRendererComponent(list, text, index, selected, focused);
            }
        });
    }

    // ---------- containers ----------

    static JPanel card(LayoutManager layout) {
        JPanel card = new JPanel(layout);
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.GAP_LARGE, Theme.GAP_LARGE,
                        Theme.GAP_LARGE, Theme.GAP_LARGE)));
        return card;
    }

    static JPanel statCard(String caption, JLabel value) {
        value.setFont(Theme.BOLD_FONT.deriveFont(STAT_FONT_SIZE));
        value.setForeground(Theme.TEXT_PRIMARY);
        JPanel card = card(new BorderLayout(0, Theme.GAP_SMALL));
        card.add(fieldLabel(caption), BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    static JPanel clear(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    // ---------- tables ----------

    static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Theme.BODY_FONT);
        table.setRowHeight(ROW_HEIGHT);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(Theme.SMALL_FONT);
        table.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    static JScrollPane scroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.CARD_BG);
        return scroll;
    }

    // ---------- messages ----------

    static void showError(Component parent, String message) {
        lastMessage = message;
        // tests run without a screen: remember the message instead of opening a dialog
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        JOptionPane.showMessageDialog(parent, message, "EcoPickup", JOptionPane.WARNING_MESSAGE);
    }

    static void showInfo(Component parent, String message) {
        lastMessage = message;
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        JOptionPane.showMessageDialog(parent, message, "EcoPickup", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- formatting ----------

    static String materialName(MaterialType type) {
        String name = type.name().replace('_', '-').toLowerCase(Locale.ENGLISH);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    static String formatAddress(com.ecopickup.model.Address address) {
        return address.getDisplayName() + (address.isVerified() ? "" : " (unverified)");
    }

    static String formatKg(double kilograms) {
        return String.format(Locale.ENGLISH, "%.1f kg", kilograms);
    }

    static String formatMultiplier(double multiplier) {
        return (multiplier == Math.rint(multiplier) ? String.valueOf((long) multiplier) : String.valueOf(multiplier))
                + "x";
    }

    static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    static String formatStatus(Enum<?> status) {
        String name = status.name().toLowerCase(Locale.ENGLISH);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static JButton flatButton(String text, Color background, Color foreground, Color outline) {
        JButton button = new JButton(text);
        button.setFont(Theme.BOLD_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(outline),
                BorderFactory.createEmptyBorder(Theme.CONTROL_PADDING, BUTTON_PADDING_X,
                        Theme.CONTROL_PADDING, BUTTON_PADDING_X)));
        return button;
    }
}

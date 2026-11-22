package com.ecopickup.gui;

import com.ecopickup.model.MaterialType;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

// Row of material tiles; exactly one is selected at a time.
final class MaterialSelector extends JPanel {

    private static final int TILE_PADDING_Y = 14;
    private static final int SELECTED_OUTLINE = 2;

    private final Map<MaterialType, JToggleButton> tiles = new EnumMap<>(MaterialType.class);
    private final ButtonGroup group = new ButtonGroup();

    MaterialSelector() {
        super(new GridLayout(1, MaterialType.values().length, Theme.GAP, 0));
        setOpaque(false);
        for (MaterialType type : MaterialType.values()) {
            JToggleButton tile = createTile(type);
            tiles.put(type, tile);
            group.add(tile);
            add(tile);
        }
        reset();
    }

    MaterialType getSelectedMaterial() {
        for (Map.Entry<MaterialType, JToggleButton> entry : tiles.entrySet()) {
            if (entry.getValue().isSelected()) {
                return entry.getKey();
            }
        }
        return MaterialType.values()[0];
    }

    void addSelectionListener(Runnable action) {
        tiles.values().forEach(tile -> tile.addActionListener(event -> action.run()));
    }

    void reset() {
        tiles.get(MaterialType.values()[0]).setSelected(true);
    }

    private JToggleButton createTile(MaterialType type) {
        JToggleButton tile = new JToggleButton(Ui.materialName(type));
        tile.setFocusPainted(false);
        tile.setContentAreaFilled(false);
        tile.setOpaque(true);
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tile.addItemListener(event -> applyStyle(tile));
        applyStyle(tile);
        return tile;
    }

    private void applyStyle(JToggleButton tile) {
        boolean selected = tile.isSelected();
        tile.setBackground(selected ? Theme.PRIMARY_LIGHT : Theme.CARD_BG);
        tile.setForeground(selected ? Theme.PRIMARY : Theme.TEXT_SECONDARY);
        tile.setFont(selected ? Theme.BOLD_FONT : Theme.BODY_FONT);
        tile.setBorder(selected ? tileBorder(Theme.PRIMARY, SELECTED_OUTLINE) : tileBorder(Theme.BORDER, 1));
    }

    private static Border tileBorder(java.awt.Color colour, int thickness) {
        int paddingY = TILE_PADDING_Y - thickness;
        int paddingX = Theme.GAP - thickness;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colour, thickness),
                BorderFactory.createEmptyBorder(paddingY, paddingX, paddingY, paddingX));
    }
}

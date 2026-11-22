package com.ecopickup.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Stacks components top to bottom at their natural height.
final class StackPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int rows;

    StackPanel() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    @Override
    public Component add(Component component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = rows;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(topGap(component), 0, 0, 0);
        rows++;
        super.add(component, constraints);
        return component;
    }

    private int topGap(Component component) {
        if (rows == 0) {
            return 0;
        }
        return component instanceof JLabel ? Theme.GAP_LARGE - Theme.GAP_SMALL : Theme.GAP_SMALL;
    }
}

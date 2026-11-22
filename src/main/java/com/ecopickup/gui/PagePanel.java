package com.ecopickup.gui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

// Base class for the pages next to the sidebar. refresh() reloads the data.
abstract class PagePanel extends JPanel {

    protected PagePanel() {
        super(new BorderLayout(0, Theme.GAP_LARGE));
        setBackground(Theme.PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(Theme.PAGE_PADDING, Theme.PAGE_PADDING,
                Theme.PAGE_PADDING, Theme.PAGE_PADDING));
    }

    abstract void refresh();
}

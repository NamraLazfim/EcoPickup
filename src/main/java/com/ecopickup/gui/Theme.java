package com.ecopickup.gui;

import java.awt.Color;
import java.awt.Font;

// All colours, fonts and spacing in one place.
final class Theme {

    static final Color SIDEBAR_BG = new Color(23, 52, 4);
    static final Color SIDEBAR_ACTIVE = new Color(59, 109, 17);
    static final Color SIDEBAR_TEXT = new Color(192, 221, 151);
    static final Color SIDEBAR_MUTED = new Color(151, 196, 89);
    static final Color LOGO_BG = new Color(99, 153, 34);

    static final Color PAGE_BG = new Color(245, 246, 242);
    static final Color CARD_BG = Color.WHITE;
    static final Color BORDER = new Color(216, 219, 208);
    static final Color TEXT_PRIMARY = new Color(32, 33, 30);
    static final Color TEXT_SECONDARY = new Color(105, 107, 100);
    static final Color PRIMARY = new Color(59, 109, 17);
    static final Color PRIMARY_LIGHT = new Color(234, 243, 222);
    static final Color ERROR = new Color(163, 45, 45);

    static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    static final Font LOGO_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 17);
    static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    static final Font BOLD_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    static final Font SMALL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    static final int GAP_SMALL = 4;
    static final int GAP = 8;
    static final int GAP_LARGE = 16;
    static final int PAGE_PADDING = 24;
    static final int CONTROL_PADDING = 10;

    private Theme() {
    }
}

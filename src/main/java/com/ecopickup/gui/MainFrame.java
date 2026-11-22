package com.ecopickup.gui;

import com.ecopickup.api.NominatimAddressService;
import com.ecopickup.service.AuthService;
import com.ecopickup.service.PickupService;
import com.ecopickup.service.Role;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// Main window: sign-in first, then the sidebar and the pages for the user's role.
final class MainFrame extends JFrame {

    private static final String LOGIN_CARD = "login";
    private static final String REGISTER_CARD = "register";
    private static final String APP_CARD = "app";
    private static final String PROFILE = "Profile";
    private static final int WINDOW_WIDTH = 1120;
    private static final int WINDOW_HEIGHT = 760;
    private static final int MIN_WIDTH = 960;
    private static final int MIN_HEIGHT = 620;

    private final AuthController auth;
    private final PickupController controller;
    private final CardLayout rootCards = new CardLayout();
    private final JPanel root = new JPanel(rootCards);
    private final LoginPanel loginPanel;
    private final JPanel appPanel = new JPanel(new BorderLayout());
    private final CardLayout pageCards = new CardLayout();
    private final JPanel contentPanel = new JPanel(pageCards);
    private final Map<String, PagePanel> pages = new LinkedHashMap<>();
    private SidebarPanel sidebar;

    MainFrame(AuthController auth, PickupController controller) {
        super("EcoPickup - Community Waste and Recycling Manager");
        this.auth = auth;
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // never bigger than the screen, so nothing is hidden on a small laptop or a projector
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(WINDOW_WIDTH, screen.width), Math.min(WINDOW_HEIGHT, screen.height));
        setMinimumSize(new Dimension(Math.min(MIN_WIDTH, screen.width), Math.min(MIN_HEIGHT, screen.height)));
        setLocationRelativeTo(null);
        loginPanel = new LoginPanel(auth, this::startSession, () -> rootCards.show(root, REGISTER_CARD));
        root.add(loginPanel, LOGIN_CARD);
        root.add(new RegisterPanel(auth, this::startSession, () -> rootCards.show(root, LOGIN_CARD)), REGISTER_CARD);
        root.add(appPanel, APP_CARD);
        setContentPane(root);
    }

    void startSession(Role role) {
        pages.clear();
        contentPanel.removeAll();
        pagesFor(role).forEach((name, page) -> {
            pages.put(name, page);
            contentPanel.add(page, name);
        });
        Map<String, String> navigation = new LinkedHashMap<>();
        pages.keySet().forEach(name -> navigation.put(name, name));
        sidebar = new SidebarPanel(navigation, this::showPage, this::signOut);
        sidebar.setUser(auth.getCurrentName(), role.getLabel());
        appPanel.removeAll();
        appPanel.add(sidebar, BorderLayout.WEST);
        appPanel.add(contentPanel, BorderLayout.CENTER);
        rootCards.show(root, APP_CARD);
        showPage(pages.keySet().iterator().next());
    }

    void showPage(String name) {
        pages.get(name).refresh();
        pageCards.show(contentPanel, name);
        sidebar.setActive(name);
        appPanel.revalidate();
        appPanel.repaint();
    }

    private void signOut() {
        auth.signOut();
        loginPanel.reset();
        rootCards.show(root, LOGIN_CARD);
    }

    private Map<String, PagePanel> pagesFor(Role role) {
        Map<String, PagePanel> result = new LinkedHashMap<>();
        switch (role) {
            case RESIDENT -> {
                result.put("Book pickup", new BookingPanel(controller));
                result.put("My pickups", new PickupListPanel(controller));
                result.put("Rewards", new RewardsPanel(controller));
            }
            case COLLECTOR -> {
                result.put("My tasks", new CollectorTaskPanel(controller));
                result.put("Completed", new CollectorHistoryPanel(controller));
            }
            default -> addAdminPages(result);
        }
        result.put(PROFILE, new ProfilePanel(auth));
        return result;
    }

    private void addAdminPages(Map<String, PagePanel> result) {
        result.put("Dashboard", new AdminDashboardPanel(controller, auth));
        result.put("All requests", new AllRequestsPanel(controller));
        result.put("Collectors", new CollectorsPanel(auth, controller));
        result.put("Reward rules", new RewardSettingsPanel(controller));
    }

    public static void main(String[] args) {
        // safety net: an error nobody expected shows a friendly message, never a stack trace
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> SwingUtilities.invokeLater(
                () -> Ui.showError(null, "Something went wrong. Please try again.")));
        RewardSettings settings = new RewardSettings();
        PickupService service = new PickupService(settings.createStrategy());
        AuthService accounts = new AuthService();
        DemoData.load(accounts, service);
        AuthController auth = new AuthController(accounts);
        PickupController controller = new PickupController(auth, service, new NominatimAddressService(), settings);
        SwingUtilities.invokeLater(() -> new MainFrame(auth, controller).setVisible(true));
    }
}

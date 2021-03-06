package com.meyratech.vicenze.ui;

import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.navigation.bar.AppBar;
import com.meyratech.vicenze.ui.components.navigation.bar.TabBar;
import com.meyratech.vicenze.ui.components.navigation.drawer.NaviDrawer;
import com.meyratech.vicenze.ui.components.navigation.drawer.NaviItem;
import com.meyratech.vicenze.ui.components.navigation.drawer.NaviMenu;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.meyratech.vicenze.ui.util.css.Overflow;
import com.meyratech.vicenze.ui.views.home.HomeView;
import com.meyratech.vicenze.ui.views.invoice.IncorrectInvoiceView;
import com.meyratech.vicenze.ui.views.invoice.InvoiceImportView;
import com.meyratech.vicenze.ui.views.invoice.InvoiceView;
import com.meyratech.vicenze.ui.views.personnel.PersonelView;
import com.meyratech.vicenze.ui.views.projects.ProjectsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HtmlImport("frontend://styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
//@PWA(name = "Vicenze", shortName = "Vicenze", iconPath = "images/logo-18.png", backgroundColor = "#233348", themeColor = "#233348")
public class MainLayout extends FlexBoxLayout implements RouterLayout, PageConfigurator, AfterNavigationObserver {

    private static final Logger log = LoggerFactory.getLogger(MainLayout.class);
    private static final String CLASS_NAME = "root";

    private Div appHeaderOuter;

    private FlexBoxLayout row;
    private NaviDrawer naviDrawer;
    private FlexBoxLayout column;

    private Div appHeaderInner;
    private FlexBoxLayout viewContainer;
    private Div appFooterInner;

    private Div appFooterOuter;

    private TabBar tabBar;
    private boolean navigationTabs = false;
    private AppBar appBar;

    public MainLayout() {
        VaadinSession.getCurrent()
                .setErrorHandler((ErrorHandler) errorEvent -> {
                    log.error("Uncaught UI exception", errorEvent.getThrowable());
                    Notification.show("We are sorry, but an internal error occurred");
                });

        addClassName(CLASS_NAME);
        setBackgroundColor(LumoStyles.Color.Contrast._5);
        setFlexDirection(FlexDirection.COLUMN);
        setSizeFull();

        initStructure();
        initHeadersAndFooters();
        if (SecurityUtils.isUserLoggedIn()) {
            initNaviItems();
        }
    }

    /**
     * Initialise the required components and containers.
     */
    private void initStructure() {

        naviDrawer = new NaviDrawer();

        viewContainer = new FlexBoxLayout();
        viewContainer.addClassName(CLASS_NAME + "__view-container");
        viewContainer.setOverflow(Overflow.HIDDEN);

        column = new FlexBoxLayout(viewContainer);
        column.addClassName(CLASS_NAME + "__column");
        column.setFlexDirection(FlexDirection.COLUMN);
        column.setFlexGrow(1, viewContainer);
        column.setOverflow(Overflow.HIDDEN);

        row = new FlexBoxLayout(naviDrawer, column);
        row.addClassName(CLASS_NAME + "__row");
        row.setFlexGrow(1, column);
        row.setOverflow(Overflow.HIDDEN);
        add(row);
        setFlexGrow(1, row);
    }

    /**
     * Initialise the navigation items.
     */
    private void initNaviItems() {
        NaviMenu menu = naviDrawer.getMenu();

        menu.addNaviItem(VaadinIcon.HOME, ViewConst.TITLE_HOME, HomeView.class);
        if (SecurityUtils.isAccessGranted(PersonelView.class)) {
            menu.addNaviItem(VaadinIcon.USERS, ViewConst.TITLE_PERSONNELS, PersonelView.class);
        }

        if (SecurityUtils.isAccessGranted(ProjectsView.class)) {
            menu.addNaviItem(VaadinIcon.INSTITUTION, ViewConst.TITLE_PROJECTS, ProjectsView.class);
        }

        NaviItem personnel = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Invoice", null);
        menu.addNaviItem(personnel, "Invoice List", InvoiceView.class);
        menu.addNaviItem(personnel, "Incorrect List", IncorrectInvoiceView.class);
        menu.addNaviItem(personnel, "Invoice Import", InvoiceImportView.class);
    }

    /**
     * Configure the app's inner and outer headers and footers.
     */
    private void initHeadersAndFooters() {

        appBar = new AppBar("");

        if (navigationTabs) {
            appBar.getAvatar().setVisible(false);
            tabBar = new TabBar();
            for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
                item.addClickListener(e -> {
                    // Shift-click to add a new tab
                    if (e.getButton() == 0 && e.isShiftKey()) {
                        tabBar.setSelectedTab(tabBar.addClosableTab(item.getText(), item.getNavigationTarget()));
                    }
                });
            }
            setAppHeaderInner(tabBar, appBar);
        } else {
            setAppHeaderInner(appBar);
        }
    }

    private void setAppHeaderOuter(Component... components) {
        if (appHeaderOuter == null) {
            appHeaderOuter = new Div();
            appHeaderOuter.addClassName("app-header-outer");
            getElement().insertChild(0, appHeaderOuter.getElement());
        }
        appHeaderOuter.removeAll();
        appHeaderOuter.add(components);
    }

    private void setAppHeaderInner(Component... components) {
        if (appHeaderInner == null) {
            appHeaderInner = new Div();
            appHeaderInner.addClassName("app-header-inner");
            column.getElement().insertChild(0, appHeaderInner.getElement());
        }
        appHeaderInner.removeAll();
        appHeaderInner.add(components);
    }

    private void setAppFooterInner(Component... components) {
        if (appFooterInner == null) {
            appFooterInner = new Div();
            appFooterInner.addClassName("app-footer-inner");
            column.getElement().insertChild(column.getElement().getChildCount(), appFooterInner.getElement());
        }
        appFooterInner.removeAll();
        appFooterInner.add(components);
    }

    private void setAppFooterOuter(Component... components) {
        if (appFooterOuter == null) {
            appFooterOuter = new Div();
            appFooterOuter.addClassName("app-footer-outer");
            getElement().insertChild(getElement().getChildCount(),
                    appFooterOuter.getElement());
        }
        appFooterOuter.removeAll();
        appFooterOuter.add(components);
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        settings.addMetaTag("apple-mobile-web-app-capable", "yes");
        settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");
        settings.addFavIcon("icon", "frontend/styles/favicons/favicon.ico", "256x256");
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.viewContainer.getElement().appendChild(content.getElement());
    }

    public NaviDrawer getNaviDrawer() {
        return naviDrawer;
    }

    public static MainLayout get() {
        return (MainLayout) UI.getCurrent()
                .getChildren()
                .filter(component -> component.getClass() == MainLayout.class)
                .findFirst().get();
    }

    public AppBar getAppBar() {
        return appBar;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (navigationTabs) {
            afterNavigationWithTabs(event);
        } else {
            afterNavigationWithoutTabs(event);
        }
    }

    private void afterNavigationWithTabs(AfterNavigationEvent e) {
        NaviItem active = getActiveItem(e);
        if (active == null) {
            if (tabBar.getTabCount() == 0) {
                tabBar.addClosableTab("", HomeView.class);
            }
        } else {
            if (tabBar.getTabCount() > 0) {
                tabBar.updateSelectedTab(active.getText(), active.getNavigationTarget());
            } else {
                tabBar.addClosableTab(active.getText(), active.getNavigationTarget());
            }
        }
        appBar.getMenuIcon().setVisible(false);
    }

    private NaviItem getActiveItem(AfterNavigationEvent e) {
        for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
            if (item.isHighlighted(e)) {
                return item;
            }
        }
        return null;
    }

    private void afterNavigationWithoutTabs(AfterNavigationEvent e) {
        NaviItem active = getActiveItem(e);
        if (active != null) {
            getAppBar().setTitle(active.getText());
        }
    }

}

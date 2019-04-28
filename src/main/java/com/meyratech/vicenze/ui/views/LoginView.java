package com.meyratech.vicenze.ui.views;

import com.meyratech.vicenze.backend.security.HasLogger;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * ekocbiyik on 4/27/19
 */
@Route
@PageTitle("Vicenze Accounting")
@HtmlImport("styles/shared-styles.html")
//@HtmlImport("frontend://custom-login.html")
@PWA(name = "Vicenze", shortName = "Vicenze",
        startPath = "login",
        backgroundColor = "#227aef", themeColor = "#227aef",
        offlinePath = "offline-page.html",
        offlineResources = {"images/login-banner.jpg"})
public class LoginView extends ViewFrame implements AfterNavigationObserver, BeforeEnterObserver, HasLogger {

    private LoginOverlay login = new LoginOverlay();

    public LoginView() {

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Vicenze");
        i18n.getHeader().setDescription("description");
        i18n.setAdditionalInformation(null);

        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit("Sign in");
        i18n.getForm().setTitle("Sign in");
        i18n.getForm().setUsername("Email");
        i18n.getForm().setPassword("Password");

        login.setI18n(i18n);
        login.getElement().setAttribute("no-forgot-password", true);
        login.setAction("login");
        login.setOpened(true);

        UIUtils.showNotification("Vicenze accounting platform");

//        Label content = new Label("Vicenze accounting platform");
//        Button rightButton = new Button(new Icon(VaadinIcon.CLOSE));
//
//        Notification notification = new Notification(content, new Label("        "), rightButton);
//        notification.setDuration(9000);
//        rightButton.addClickListener(event -> notification.close());
//        notification.setPosition(Notification.Position.BOTTOM_CENTER);
//
//        notification.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
            UI.getCurrent().getPage().getHistory().replaceState(null, "");
            event.rerouteTo(HomeView.class);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    public interface Model extends TemplateModel {
        void setError(boolean error);
    }

}

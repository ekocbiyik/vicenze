package com.meyratech.vicenze.ui.util;

import org.springframework.data.domain.Sort;

import java.util.Locale;

public class ViewConst {

    public static final Locale APP_LOCALE = Locale.US;

    public static final String PAGE_ROOT = "";
    public static final String PAGE_HOME = "home";
    public static final String PAGE_PROJECTS = "projects";
    public static final String PAGE_PROJECTS_DETAILS = "projects-details";

    public static final String TITLE_HOME = "Home";
    public static final String TITLE_PROJECTS = "Projects";
    public static final String TITLE_PROJECTS_DETAILS = "Project Detail";


    public static final String PAGE_STOREFRONT = "storefront";
    public static final String PAGE_STOREFRONT_EDIT = "storefront/edit";
    public static final String PAGE_DASHBOARD = "dashboard";
    public static final String PAGE_USERS = "users";
    public static final String PAGE_PRODUCTS = "products";

    public static final String TITLE_STOREFRONT = "Storefront";
    public static final String TITLE_DASHBOARD = "Dashboard";
    public static final String TITLE_USERS = "Users";
    public static final String TITLE_PRODUCTS = "Products";
    public static final String TITLE_LOGOUT = "Logout";
    public static final String TITLE_NOT_FOUND = "Page was not found";
    public static final String TITLE_ACCESS_DENIED = "Access denied";

    public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";

    // Mutable for testing.
    public static int NOTIFICATION_DURATION = 4000;

}

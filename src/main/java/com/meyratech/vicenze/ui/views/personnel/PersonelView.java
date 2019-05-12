package com.meyratech.vicenze.ui.views.personnel;

import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.UserServiceImpl;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawer;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.meyratech.vicenze.ui.layout.size.Horizontal;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.layout.size.Vertical;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.views.SplitViewFrame;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ekocbiyik on 12.05.2019
 */
@Route(value = "personnels", layout = MainLayout.class)
@PageTitle("Personnels")
@Secured(Role.ADMIN)
public class PersonelView extends SplitViewFrame {

    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;

    private Grid<User> userGrid;
    private ListDataProvider<User> userDataProvider;
    private TextField searchField;
    private Button btnCreate;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;


    // components
    private User detailedUser;
    private TextField txtFirstName;
    private TextField txtLastName;
    private EmailField txtEmail;
    private PasswordField txtPassword;
    private ComboBox cbxRole;
    private RadioButtonGroup<String> rdActive;
    private RadioButtonGroup<String> rdLock;

    private static final String ACTIVE = "Active";
    private static final String IN_ACTIVE = "Inactive";
    private static final String LOCKED = "Locked";
    private static final String UN_LOCKED = "Unlocked";


    @Autowired
    public PersonelView(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        setViewHeader(createSearchBar());
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
        setViewFooter(createFooter());

        // TODO: 5/12/19 user ekleme pop-up oluşturulacak...
    }

    private Component createSearchBar() {

        searchField = new TextField();
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> userDataProvider.addFilter(user -> StringUtils.containsIgnoreCase(user.getFullName(), searchField.getValue())));

        FlexBoxLayout container = new FlexBoxLayout(searchField);
        container.addClassName("app-bar__container");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setFlexGrow(1, searchField);
        return container;
    }

    private Component createContent() {
        Div content = new Div(createGrid());
        content.addClassName("grid-view");
        return content;

    }

    private Grid createGrid() {
        userGrid = new Grid<>();
        userDataProvider = DataProvider.ofCollection(userService.findAll());
        userGrid.setDataProvider(userDataProvider);
        userGrid.setHeight("100%");

        userGrid.addColumn(User::getId).setFlexGrow(0).setFrozen(true).setHeader("ID").setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_XS);
        userGrid.addColumn(new ComponentRenderer<>(this::createUserInfo)).setFlexGrow(1).setHeader("Name").setSortable(true).setComparator(User::getFullName).setWidth(UIUtils.COLUMN_WIDTH_XL);
        userGrid.addColumn(new ComponentRenderer<>(this::createActive)).setFlexGrow(0).setHeader("Active/Deactive").setSortable(true).setComparator(User::isActive).setWidth(UIUtils.COLUMN_WIDTH_L);
        userGrid.addColumn(new ComponentRenderer<>(this::createLocked)).setFlexGrow(0).setHeader("Is Locked").setWidth(UIUtils.COLUMN_WIDTH_S).setTextAlign(ColumnTextAlign.START);
        userGrid.addColumn(User::getCreatedBy).setHeader("Created By").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_M).setTextAlign(ColumnTextAlign.START);
        userGrid.addColumn(new ComponentRenderer<>(this::lastLogin)).setFlexGrow(0).setHeader("Last Login").setWidth(UIUtils.COLUMN_WIDTH_L).setTextAlign(ColumnTextAlign.START);
        userGrid.addColumn(new ComponentRenderer<>(this::creationDate)).setFlexGrow(0).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L).setTextAlign(ColumnTextAlign.START);

        userGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
        return userGrid;
    }

    private DetailsDrawer createDetailsDrawer() {
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("Info");
        detailsDrawer.setHeader(detailsDrawerHeader);

        // Footer
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        detailsDrawer.setFooter(footer);

        footer.addCancelListener(e -> {
            detailedUser = null;
            detailsDrawer.hide();
        });
        footer.addSaveListener(e -> {
            detailedUser.setFirstName(txtFirstName.getValue());
            detailedUser.setLastName(txtLastName.getValue());
            detailedUser.setEmail(txtEmail.getValue());
            if (!txtPassword.isEmpty()) {
                detailedUser.setPassword(passwordEncoder.encode(txtPassword.getValue()));
            }
            detailedUser.setRole((String) cbxRole.getValue());
            detailedUser.setActive(ACTIVE.equals(rdActive.getValue()));
            detailedUser.setLocked(LOCKED.equals(rdLock.getValue()));
            userService.save(detailedUser);

            Notification.show("Updated!", 3000, Notification.Position.BOTTOM_END);
            detailsDrawer.hide();
            detailedUser = null;
            userGrid.getDataProvider().refreshAll();
        });
        return detailsDrawer;
    }

    private Component createFooter() {
        FlexBoxLayout footer = new FlexBoxLayout();
        footer.setBackgroundColor(LumoStyles.Color.Contrast._5);
        footer.setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
        footer.setSpacing(Right.S);
        footer.setWidth("100%");
        btnCreate = UIUtils.createPrimaryButton("New Personnel");
        btnCreate.addClickListener(e -> openNewPersonnelView(e));
        footer.add(btnCreate);
        return footer;
    }

    public void openNewPersonnelView(ClickEvent<Button> e) {
        Notification.show("not implemented!", 3000, Notification.Position.BOTTOM_END);
    }

    private void showDetails(User user) {
        detailedUser = user;
        detailsDrawerHeader.setText(user.getFullName());
        detailsDrawer.setContent(createDetails(user));
        detailsDrawer.show();
    }


    private FormLayout createDetails(User user) {
        txtFirstName = new TextField();
        txtFirstName.setValue(user.getFirstName());
        txtFirstName.setWidth("100%");

        txtLastName = new TextField();
        txtLastName.setValue(user.getLastName());
        txtLastName.setWidth("100%");

        txtEmail = new EmailField();
        txtEmail.setValue(user.getEmail());
        txtEmail.setWidth("100%");

        txtPassword = new PasswordField();
        txtPassword.setWidth("100%");

        cbxRole = new ComboBox();
        cbxRole.setItems(Role.getAllRoles());
        cbxRole.setValue(user.getRole());
        cbxRole.setWidth("100%");

        rdActive = new RadioButtonGroup<>();
        rdActive.setItems(ACTIVE, IN_ACTIVE);
        rdActive.setValue(user.isActive() ? ACTIVE : IN_ACTIVE);

        rdLock = new RadioButtonGroup<>();
        rdLock.setItems(LOCKED, UN_LOCKED);
        rdLock.setValue(user.isLocked() ? LOCKED : UN_LOCKED);

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        form.addFormItem(txtFirstName, "First Name");
        form.addFormItem(txtLastName, "Last Name");
        FormLayout.FormItem emailItem = form.addFormItem(txtEmail, "Email");
        FormLayout.FormItem passwordItem = form.addFormItem(txtPassword, "Password");
        FormLayout.FormItem roleItem = form.addFormItem(cbxRole, "Role");
        FormLayout.FormItem statusItem = form.addFormItem(rdActive, "User Status");
        FormLayout.FormItem loginItem = form.addFormItem(rdLock, "Login Status");
        UIUtils.setColSpan(2, emailItem, passwordItem, roleItem, statusItem, loginItem);
        return form;
    }

    //
    private Component createUserInfo(User user) {
        ListItem item = new ListItem(
                UIUtils.createInitials(user.getFirstName().substring(0, 1)),
                user.getFullName(),
                user.getEmail());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createActive(User user) {
        return user.isActive() ? UIUtils.createPrimaryIcon(VaadinIcon.CHECK) : UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
    }

    private Component createLocked(User user) {
        return user.isLocked() ? UIUtils.createPrimaryIcon(VaadinIcon.SAFE_LOCK) : UIUtils.createDisabledIcon(VaadinIcon.UNLOCK);
    }

    private Component lastLogin(User user) {
        return new Span(UIUtils.formatDatetime(user.getLastLogin()));
    }

    private Component creationDate(User user) {
        return new Span(UIUtils.formatDatetime(user.getCreationDate()));
    }

}

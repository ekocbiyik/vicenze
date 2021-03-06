package com.meyratech.vicenze.ui.views.personnel;

import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.UserServiceImpl;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawer;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.components.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * ekocbiyik on 12.05.2019
 */
@Route(value = "personnels", layout = MainLayout.class)
@PageTitle(ViewConst.TITLE_PERSONNELS)
@Secured(Role.ADMIN)
public class PersonelView extends SplitViewFrame {

    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;

    private Binder<User> binder;
    private Grid<User> userGrid;
    private ListDataProvider<User> userDataProvider;
    private TextField searchField;
    private Button btnCreate;
    private DetailsDrawer detailsDrawer;
    private DetailsDrawerFooter detailedFooter;

    // components
    private User detailedUser;
    private TextField txtFirstName;
    private TextField txtLastName;
    private EmailField txtEmail;
    private PasswordField txtPassword;
    private ComboBox<String> cbxRole;
    private RadioButtonGroup<Boolean> rdActive;
    private RadioButtonGroup<Boolean> rdLock;
    private TextField txtCreatedBy;
    private TextField creationDate;


    @Autowired
    public PersonelView(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
    }

    private Component createContent() {
        userGrid = new Grid<>();
        userDataProvider = DataProvider.ofCollection(userService.findAll());
        userGrid.setDataProvider(userDataProvider);
        userGrid.setSizeFull();

        Grid.Column<User> col1 = userGrid.addColumn(User::getId).setFlexGrow(0).setFrozen(true).setHeader("No").setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_S);
        Grid.Column<User> col2 = userGrid.addColumn(new ComponentRenderer<>(this::createUserInfo)).setFlexGrow(1).setHeader("Name").setSortable(true).setComparator(User::getFullName).setWidth(UIUtils.COLUMN_WIDTH_XL);
        Grid.Column<User> col3 = userGrid.addColumn(new ComponentRenderer<>(this::createActive)).setFlexGrow(0).setHeader("Active/Deactive").setSortable(true).setComparator(User::isActive).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<User> col4 = userGrid.addColumn(new ComponentRenderer<>(this::createLocked)).setFlexGrow(0).setHeader("Is Locked").setWidth(UIUtils.COLUMN_WIDTH_S).setTextAlign(ColumnTextAlign.START);
        Grid.Column<User> col5 = userGrid.addColumn(User::getCreatedBy).setHeader("Created By").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_M).setTextAlign(ColumnTextAlign.START);
        Grid.Column<User> col6 = userGrid.addColumn(new ComponentRenderer<>(this::lastLogin)).setFlexGrow(0).setHeader("Last Login").setWidth(UIUtils.COLUMN_WIDTH_L).setTextAlign(ColumnTextAlign.START);
        Grid.Column<User> col7 = userGrid.addColumn(new ComponentRenderer<>(this::creationDate)).setFlexGrow(0).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L).setTextAlign(ColumnTextAlign.START);

        userGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

        HeaderRow topRow = userGrid.prependHeaderRow();
        HeaderRow.HeaderCell buttonsCell = topRow.join(col1, col2, col3, col4, col5, col6, col7);
        buttonsCell.setComponent(getGridHeader());
        userGrid.appendFooterRow().getCell(userGrid.getColumns().get(0)).setComponent(new Label(String.valueOf(userDataProvider.getItems().size())));

        Div content = new Div(userGrid);
        content.addClassName("grid-view");
        return content;
    }

    private Component getGridHeader() {
        searchField = new TextField();
        searchField.setPlaceholder("Search personnels");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setSizeFull();
        searchField.addValueChangeListener(e -> userDataProvider.addFilter(user -> StringUtils.containsIgnoreCase(user.getFullName(), searchField.getValue())));

        btnCreate = UIUtils.createPrimaryButton("ADD", VaadinIcon.PLUS_CIRCLE_O);
        btnCreate.addClickListener(e -> showDetails(null));

        HorizontalLayout container = new HorizontalLayout(btnCreate, searchField);
        container.setSpacing(true);
        container.setSizeFull();
        return container;
    }

    private DetailsDrawer createDetailsDrawer() {
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
        detailsDrawer.setHeader(new DetailsDrawerHeader("User Info"));
        detailedFooter = new DetailsDrawerFooter();
        detailsDrawer.setFooter(detailedFooter);
        detailedFooter.addCancelListener(e -> {
            detailedUser = null;
            detailsDrawer.hide();
        });
        detailedFooter.addSaveListener(e -> saveDetailedUser());
        return detailsDrawer;
    }

    private void showDetails(User user) {
        detailedUser = user;
        detailsDrawer.setContent(createDetails());
        initializeValidators();
        if (user != null) {
            initializeUserDetails();
        }
        detailsDrawer.show();
    }

    private FormLayout createDetails() {

        txtFirstName = new TextField();
        txtFirstName.setWidth("100%");
        txtFirstName.setRequired(true);

        txtLastName = new TextField();
        txtLastName.setWidth("100%");
        txtLastName.setRequired(true);

        txtEmail = new EmailField();
        txtEmail.setWidth("100%");

        txtPassword = new PasswordField();
        txtPassword.setWidth("100%");
        txtPassword.setValueChangeMode(ValueChangeMode.EAGER);
        txtPassword.addValueChangeListener(e -> binder.validate());

        cbxRole = new ComboBox();
        cbxRole.setItems(Role.getAllRoles());
        cbxRole.setWidth("100%");
        cbxRole.setRequired(true);

        rdActive = new RadioButtonGroup<>();
        rdActive.setItems(true, false);

        rdLock = new RadioButtonGroup<>();
        rdLock.setItems(true, false);

        creationDate = new TextField();
        creationDate.setEnabled(false);
        creationDate.setValue(UIUtils.formatDatetime(LocalDateTime.now()));

        txtCreatedBy = new TextField();
        txtCreatedBy.setEnabled(false);
        txtCreatedBy.setValue(SecurityUtils.getCurrentUser().getFullName());

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(txtFirstName, "First Name");
        form.addFormItem(txtLastName, "Last Name");
        FormLayout.FormItem emailItem = form.addFormItem(txtEmail, "Email");
        FormLayout.FormItem passwordItem = form.addFormItem(txtPassword, "Password");
        FormLayout.FormItem roleItem = form.addFormItem(cbxRole, "Role");
        form.addFormItem(rdActive, "Is Active User?");
        form.addFormItem(rdLock, "Is Login Blocked?");
        FormLayout.FormItem creation = form.addFormItem(creationDate, "Creation Date");
        FormLayout.FormItem created = form.addFormItem(txtCreatedBy, "Created By");
        UIUtils.setColSpan(2, emailItem, passwordItem, roleItem, creation, created);
        return form;
    }


    private void initializeValidators() {
        binder = new Binder<>(User.class);
        binder.forField(txtFirstName)
                .asRequired("Firstname is required!")
                .withValidator(name -> name.length() >= 3, "Firstname must contain at least 3 characters!")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(txtLastName)
                .asRequired("Lastname is required!")
                .withValidator(name -> name.length() >= 3, "Lastname must contain at least 3 characters!")
                .bind(User::getLastName, User::setLastName);

        binder.forField(txtEmail)
                .asRequired("Email is required!")
                .withValidator(new EmailValidator("Invalid email address!"))
                .bind(User::getEmail, User::setEmail);

        binder.forField(cbxRole)
                .asRequired("Role is required!")
                .withValidator(rle -> rle.length() > 0, "Please select a role!")
                .bind(User::getRole, User::setRole);

        binder.forField(rdActive)
                .asRequired("Please select activation!")
                .bind(User::isActive, User::setActive);

        binder.forField(rdLock)
                .asRequired("Please select Lock status!")
                .bind(User::isLocked, User::setLocked);

        if (detailedUser == null) {
            binder.forField(txtPassword)
                    .asRequired("Password is required!")
                    .withValidator(pass -> pass.length() >= 8, "Password must contain at least 8 characters!")
                    .bind(User::getPassword, User::setPassword);
        }

        detailedFooter.getSaveButton().setEnabled(false);
        binder.readBean(detailedUser == null ? new User() : detailedUser);
        binder.addStatusChangeListener(status -> detailedFooter.getSaveButton().setEnabled(!status.hasValidationErrors()));
    }

    private void initializeUserDetails() {
        txtFirstName.setValue(detailedUser.getFirstName());
        txtLastName.setValue(detailedUser.getLastName());
        txtEmail.setValue(detailedUser.getEmail());
        cbxRole.setValue(detailedUser.getRole());
        rdActive.setValue(detailedUser.isActive());
        rdLock.setValue(detailedUser.isLocked());
        txtCreatedBy.setValue(detailedUser.getCreatedBy());
        creationDate.setValue(UIUtils.formatDatetime(detailedUser.getCreationDate()));
    }

    private void saveDetailedUser() {
        binder.validate();
        if (!binder.isValid()) {
            detailedFooter.getSaveButton().setEnabled(false);
            return;
        }

        if (detailedUser == null) {
            detailedUser = new User();
            detailedUser.setLocked(false);
            detailedUser.setCreatedBy(SecurityUtils.getCurrentUser().getFullName());
            detailedUser.setPassword(passwordEncoder.encode(txtPassword.getValue()));
        }

        if (!txtPassword.isEmpty()) {
            detailedUser.setPassword(passwordEncoder.encode(txtPassword.getValue()));
        }

        detailedUser.setFirstName(txtFirstName.getValue());
        detailedUser.setLastName(txtLastName.getValue());
        detailedUser.setEmail(txtEmail.getValue());
        detailedUser.setRole(cbxRole.getValue());
        detailedUser.setActive(rdActive.getValue());
        detailedUser.setLocked(rdLock.getValue());

        try {
            userService.save(detailedUser);
        } catch (Exception e) {
            Notification.show("Opps! Please check your fields!", 3000, Notification.Position.TOP_END);
            return;
        }

        Notification.show("Successfull", 6000, Notification.Position.TOP_END);
        detailsDrawer.hide();
        detailedUser = null;
        userDataProvider = DataProvider.ofCollection(userService.findAll());
        userGrid.setDataProvider(userDataProvider);
    }

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

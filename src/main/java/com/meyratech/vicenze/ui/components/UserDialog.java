package com.meyratech.vicenze.ui.components;

import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.backend.security.UtilsForSpring;
import com.meyratech.vicenze.ui.layout.size.Horizontal;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.layout.size.Vertical;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * ekocbiyik on 05.05.2019
 */
public class UserDialog extends Dialog {

    private static final String CLASS_NAME = "details-drawer";

    private TextField firstName;
    private TextField lastName;
    private ComboBox<String> role;
    private RadioButtonGroup<Boolean> isActive;
    private EmailField email;
    private PasswordField password;
    private DatePicker creationDate;
    private Button btnSave;
    private Button btnCancel;
    private Binder<User> binder;
    private User user;
    private Grid<User> userGrid;

    public UserDialog(User user) {
        this(user, null);
    }

    public UserDialog(User user, Grid<User> userGrid) {
        this.user = user;
        this.userGrid = userGrid;
        add(createLayout());
        setHeight("calc(50vh - (var(--lumo-space-m) ))");
        setWidth("calc(50vw - (var(--lumo-space-m) ))");
    }

    private Component createLayout() {
        FlexBoxLayout flexLayout = new FlexBoxLayout();
        flexLayout.setFlexDirection(FlexDirection.COLUMN);
        flexLayout.add(createHeader());
        flexLayout.add(createContent());
        flexLayout.add(createFooter());

        initializeValidators();

        if (user != null) {
            initializeUserDetails();
            role.setEnabled(false);
            btnSave.setEnabled(binder.isValid());
        }
        return flexLayout;
    }

    private Component createHeader() {
        FlexBoxLayout header = new FlexBoxLayout();
        header.addClassName(CLASS_NAME + "__header");
        header.add(UIUtils.createH3Label("User Details"));
        return header;
    }

    private Component createContent() {

        FlexBoxLayout content = new FlexBoxLayout();
        content.addClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.ROW);

        firstName = new TextField();
        firstName.setWidth("100%");
        firstName.setRequired(true);

        lastName = new TextField();
        lastName.setWidth("100%");
        lastName.setRequired(true);

        email = new EmailField();
        email.setWidth("100%");

        password = new PasswordField();
        password.setWidth("100%");

        role = new ComboBox<>();
        role.setRequired(true);
        role.setItems(Role.getAllRoles());

        isActive = new RadioButtonGroup<>();
        isActive.setItems(true, false);

        creationDate = new DatePicker();
        creationDate.setValue(LocalDate.now());
        creationDate.setEnabled(false);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames(
                LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.L,
                LumoStyles.Padding.Top.S);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.addFormItem(firstName, "First name");
        formLayout.addFormItem(lastName, "Last name");
        formLayout.addFormItem(email, "Email");
        formLayout.addFormItem(password, "Password");
        formLayout.addFormItem(role, "Role");
        formLayout.addFormItem(isActive, "Active/Inactive");
        formLayout.addFormItem(creationDate, "Created Date");
        content.add(formLayout);

        return content;
    }

    private Component createFooter() {

        FlexBoxLayout footer = new FlexBoxLayout();
        footer.addClassName(CLASS_NAME + "__footer");
        footer.setBackgroundColor(LumoStyles.Color.Contrast._5);
        footer.setPadding(Horizontal.RESPONSIVE_L, Vertical.S, Vertical.S);
        footer.setSpacing(Right.S);

        btnSave = UIUtils.createPrimaryButton("Save");
        btnCancel = UIUtils.createTertiaryButton("Cancel");
        footer.add(btnSave, btnCancel);

        btnSave.addClickListener(e -> saveUser());
        btnCancel.addClickListener(e -> close());

        return footer;
    }

    private void initializeValidators() {
        binder = new Binder<>(User.class);
        binder.forField(firstName)
                .asRequired("Firstname is required!")
                .withValidator(name -> name.length() >= 3, "Firstname must contain at least 3 characters!")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lastName)
                .asRequired("Lastname is required!")
                .withValidator(name -> name.length() >= 3, "Lastname must contain at least 3 characters!")
                .bind(User::getLastName, User::setLastName);

        binder.forField(email)
                .asRequired("Email is required!")
                .withValidator(new EmailValidator("Invalid email address!"))
                .bind(User::getEmail, User::setEmail);

        binder.forField(role)
                .asRequired("Role is required!")
                .withValidator(rle -> rle.length() > 0, "Please select a role!")
                .bind(User::getRole, User::setRole);

        binder.forField(isActive)
                .asRequired("Please select activation!")
                .bind(User::isActive, User::setActive);

        if (user == null) {
            binder.forField(password)
                    .asRequired("Password is required!")
                    .withValidator(pass -> pass.length() >= 8, "Password must contain at least 8 characters!")
                    .bind(User::getPassword, User::setPassword);
        }

        btnSave.setEnabled(false);
        binder.readBean(user == null ? new User() : user);
        binder.addStatusChangeListener(status -> btnSave.setEnabled(!status.hasValidationErrors()));
    }


    private void initializeUserDetails() {
        firstName.setValue(user.getFirstName());
        lastName.setValue(user.getLastName());
        email.setValue(user.getEmail());
        role.setValue(user.getRole());
        isActive.setValue(user.isActive());
        creationDate.setValue(LocalDate.from(user.getCreationDate()));
    }

    private void saveUser() {

        binder.validate();
        if (!binder.isValid()) {
            btnSave.setEnabled(false);
            return;
        }

        PasswordEncoder passwordEncoder = UtilsForSpring.getSingleBeanOfType(PasswordEncoder.class);
        IUserService userService = UtilsForSpring.getSingleBeanOfType(IUserService.class);

        if (user == null) {
            user = new User();
            user.setLocked(false);
            user.setCreatedBy(SecurityUtils.getCurrentUser().getFullName());
            user.setPassword(passwordEncoder.encode(password.getValue()));
        }

        if (!password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password.getValue()));//parola güncelliyor
        }

        user.setFirstName(firstName.getValue());
        user.setLastName(lastName.getValue());
        user.setEmail(email.getValue());
        user.setRole(role.getValue());
        user.setActive(isActive.getValue());
        userService.save(user);

        close();
        if (userGrid != null) userGrid.getDataProvider().refreshAll();
    }

}

package com.meyratech.vicenze.ui.components;

import com.meyratech.vicenze.backend.model.Role;
import com.meyratech.vicenze.backend.model.User;
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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;

/**
 * ekocbiyik on 05.05.2019
 */
public class UserDialog extends Dialog {

    private static final String CLASS_NAME = "details-drawer";
    private final String ACTIVE = "Active";
    private final String INACTIVE = "Inactive";

    private TextField firstName;
    private TextField lastName;
    private ComboBox<String> role;
    private RadioButtonGroup<String> isActive;
    private EmailField email;
    private PasswordField password;
    private DatePicker creationDate;

    private User user;

    public UserDialog(User user) {
        this.user = user;
        add(createLayout());
    }


    private Component createLayout() {

        FlexLayout flexLayout = new FlexLayout();
        add(createHeader());
        add(createContent());
        add(createFooter());

        flexLayout.setMaxWidth("120px");

        if (user != null) {
            initializeUserDetails();
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
        content.setFlexDirection(FlexDirection.COLUMN);

        // fields
        firstName = new TextField();
        firstName.setWidth("100%");

        lastName = new TextField();
        lastName.setWidth("100%");

        role = new ComboBox<>();
        role.setItems(Role.getAllRoles());
        role.setRequired(true);

        isActive = new RadioButtonGroup<>();
        isActive.setItems(ACTIVE, INACTIVE);


        email = new EmailField();
        email.setWidth("100%");

        password = new PasswordField();
        password.setWidth("100%");

        creationDate = new DatePicker();
        creationDate.setValue(LocalDate.now());
        creationDate.setEnabled(false);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));


        formLayout.addFormItem(firstName, "First name");
        formLayout.addFormItem(lastName, "Last name");
        formLayout.addFormItem(role, "Role");
        formLayout.addFormItem(isActive, "Active/Inactive");
        formLayout.addFormItem(email, "Email");
        formLayout.addFormItem(password, "Password");
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

        Button save = UIUtils.createPrimaryButton("Save");
        Button cancel = UIUtils.createTertiaryButton("Cancel");
        footer.add(save, cancel);

        save.addClickListener(e -> close());
        cancel.addClickListener(e -> close());

        return footer;
    }

    private void initializeUserDetails() {
        firstName.setValue(user.getFirstName());
        lastName.setValue(user.getLastName());
        role.setValue(user.getRole());
        isActive.setValue(user.isActive() ? ACTIVE : INACTIVE);
        email.setValue(user.getEmail());
        creationDate.setValue(LocalDate.from(user.getCreationDate()));
    }

}

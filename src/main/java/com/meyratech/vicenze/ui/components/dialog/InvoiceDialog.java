package com.meyratech.vicenze.ui.components.dialog;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.IIncorrectInvoiceService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.backend.security.UtilsForSpring;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.layout.size.Horizontal;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.layout.size.Vertical;
import com.meyratech.vicenze.ui.util.BoxShadowBorders;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.css.BorderRadius;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * ekocbiyik on 05.05.2019
 */
public class InvoiceDialog extends Dialog {

    private static final String CLASS_NAME = "details-drawer";

    private Button btnSave;
    private Button btnCancel;
    private Invoice invoice;
    private ComboBox<Project> cbxProject;
    private ComboBox<String> cbxVendor;
    private ComboBox<String> cbxEventType;
    private ComboBox<String> cbxMainItem;
    private ComboBox<String> cbxbook;
    private ComboBox<String> cbxTransaction;
    private TextField txtInvoiceNumber;
    private TextField txtInvoiceCode;
    private TextField txtAmount;
    private TextField txtUnitPrize;
    private DatePicker invoiceDate;
    private TimePicker invoiceTime;
    private TextArea txtExplanation;
    private TextArea txtIncorrectInfo;

    public InvoiceDialog(Invoice invoice) {
        this.invoice = invoice;
        add(createLayout());
        setWidth("calc(50vw - (var(--lumo-space-m) ))");
    }

    private Component createLayout() {
        FlexBoxLayout flexLayout = new FlexBoxLayout();
        flexLayout.setFlexDirection(FlexDirection.COLUMN);
        flexLayout.add(createHeader());
        flexLayout.add(createContent());
        flexLayout.add(createFooter());
        return flexLayout;
    }

    private Component createHeader() {
        Image image = new Image(String.format("%s%s", UIUtils.IMG_PATH, "logo-6.png"), "");
        UIUtils.setBorderRadius(BorderRadius._50, image);
        image.setHeight("30px");
        image.setWidth("30px");

        ListItem titleLabel = new ListItem(image, "Incorrect Invoice");
        titleLabel.getPrimary().addClassName(LumoStyles.Heading.H2);

        FlexBoxLayout header = new FlexBoxLayout(titleLabel);
        header.addClassName(BoxShadowBorders.BOTTOM);
        return header;
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout();
        content.addClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.ROW);

        cbxProject = new ComboBox<>();
        cbxProject.setWidthFull();
        cbxProject.setItemLabelGenerator(Project::getProjectName);
        cbxProject.setItems(invoice.getProject());
        cbxProject.setValue(invoice.getProject());
        cbxProject.setEnabled(false);

        cbxVendor = new ComboBox<>();
        cbxVendor.setWidthFull();
        cbxVendor.setItems(invoice.getVendor());
        cbxVendor.setValue(invoice.getVendor());
        cbxVendor.setEnabled(false);

        cbxEventType = new ComboBox<>();
        cbxEventType.setWidthFull();
        cbxEventType.setItems(invoice.getEventType());
        cbxEventType.setValue(invoice.getEventType());
        cbxEventType.setEnabled(false);

        cbxMainItem = new ComboBox<>();
        cbxMainItem.setWidthFull();
        cbxMainItem.setItems(invoice.getMainItem());
        cbxMainItem.setValue(invoice.getMainItem());
        cbxMainItem.setEnabled(false);

        cbxbook = new ComboBox<>();
        cbxbook.setWidthFull();
        cbxbook.setItems(invoice.getBook());
        cbxbook.setValue(invoice.getBook());
        cbxbook.setEnabled(false);

        cbxTransaction = new ComboBox<>();
        cbxTransaction.setWidthFull();
        cbxTransaction.setItems(invoice.getTransaction());
        cbxTransaction.setValue(invoice.getTransaction());
        cbxTransaction.setEnabled(false);

        txtInvoiceNumber = new TextField();
        txtInvoiceNumber.setWidthFull();
        txtInvoiceNumber.setValue(invoice.getInvoiceNumber());
        txtInvoiceNumber.setEnabled(false);

        txtInvoiceCode = new TextField();
        txtInvoiceCode.setWidthFull();
        txtInvoiceCode.setValue(invoice.getInvoiceCode());
        txtInvoiceCode.setEnabled(false);

        txtAmount = new TextField();
        txtAmount.setWidthFull();
        txtAmount.setValue(invoice.getAmount().toString());
        txtAmount.setEnabled(false);

        txtUnitPrize = new TextField();
        txtUnitPrize.setWidthFull();
        txtUnitPrize.setValue(invoice.getUnitPrice().toString());
        txtUnitPrize.setEnabled(false);

        invoiceDate = new DatePicker();
        invoiceDate.setLocale(Locale.UK);
        invoiceDate.setValue(invoice.getDate().toLocalDate());
        invoiceDate.setEnabled(false);

        invoiceTime = new TimePicker();
        invoiceTime.setLocale(Locale.UK);
        invoiceTime.setValue(invoice.getDate().toLocalTime());
        invoiceTime.setEnabled(false);

        HorizontalLayout wrap = new HorizontalLayout();
        wrap.add(invoiceDate, invoiceTime);
        wrap.setSpacing(true);

        txtExplanation = new TextArea();
        txtExplanation.setValue(invoice.getExplanation());
        txtExplanation.setWidthFull();
        txtExplanation.setHeight(UIUtils.COLUMN_WIDTH_XS);
        txtExplanation.setEnabled(false);

        txtIncorrectInfo = new TextArea();
        txtIncorrectInfo.setWidthFull();
        txtIncorrectInfo.setHeight(UIUtils.COLUMN_WIDTH_XS);
        txtIncorrectInfo.setValueChangeMode(ValueChangeMode.EAGER);
        txtIncorrectInfo.setPlaceholder("At least 10 character...");
        txtIncorrectInfo.addValueChangeListener(e -> btnSave.setEnabled(e.getValue().length() > 10));

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(cbxProject, "Project");
        form.addFormItem(cbxVendor, "Vendor");
        form.addFormItem(cbxEventType, "Event Type");
        form.addFormItem(cbxMainItem, "Main Item");
        form.addFormItem(cbxbook, "Book");
        form.addFormItem(cbxTransaction, "Transaction");
        form.addFormItem(txtInvoiceNumber, "Invoice Number");
        form.addFormItem(txtInvoiceCode, "Invoice Code");
        form.addFormItem(txtAmount, "Amount");
        form.addFormItem(txtUnitPrize, "Unit Prize");
        form.addFormItem(wrap, "Invoice Date");
        form.addFormItem(txtExplanation, "Explanation");

        FormLayout.FormItem txtIncorrectInfoItem = form.addFormItem(txtIncorrectInfo, "Description");
        UIUtils.setColSpan(2, txtIncorrectInfoItem);

        content.add(form);

        return content;
    }

    private Component createFooter() {
        FlexBoxLayout footer = new FlexBoxLayout();
        footer.addClassName(CLASS_NAME + "__footer");
        footer.setBackgroundColor(LumoStyles.Color.Contrast._5);
        footer.setPadding(Horizontal.RESPONSIVE_L, Vertical.S, Vertical.S);
        footer.setSpacing(Right.S);

        btnSave = UIUtils.createPrimaryButton("Report");
        btnCancel = UIUtils.createTertiaryButton("Cancel");
        footer.add(btnSave, btnCancel);

        btnSave.setEnabled(false);
        btnSave.addClickListener(e -> saveUser());
        btnCancel.addClickListener(e -> close());
        return footer;
    }

    private void saveUser() {

        IncorrectInvoice incorrectInvoice = new IncorrectInvoice();
        incorrectInvoice.setInvoice(invoice);
        incorrectInvoice.setVendor(invoice.getVendor());
        incorrectInvoice.setDescription(txtIncorrectInfo.getValue());
        incorrectInvoice.setInvoiceNumber(invoice.getInvoiceNumber());
        incorrectInvoice.setCreatedBy(SecurityUtils.getCurrentUser());
        incorrectInvoice.setCreationDate(LocalDateTime.now());

        IIncorrectInvoiceService incorrectInvoiceService = UtilsForSpring.getSingleBeanOfType(IIncorrectInvoiceService.class);
        incorrectInvoiceService.save(incorrectInvoice);

        close();
        UIUtils.showNotification("Successfull!");
    }

}

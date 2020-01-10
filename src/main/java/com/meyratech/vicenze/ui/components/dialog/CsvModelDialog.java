package com.meyratech.vicenze.ui.components.dialog;

import com.meyratech.vicenze.backend.model.CsvModel;
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

/**
 * ekocbiyik on 05.05.2019
 */
public class CsvModelDialog extends Dialog {

    private static final String CLASS_NAME = "details-drawer";

    private CsvModel csvModel;
    private ListDataProvider<CsvModel> csvDataProvider;
    private TextField txtProject;
    private TextField txtVendor;
    private TextField txtEventType;
    private TextField txtMainItem;
    private TextField txtBook;
    private TextField txtTransaction;
    private TextField txtInvoiceNumber;
    private TextField txtInvoiceCode;
    private TextField txtAmount;
    private TextField txtUnitPrize;
    private TextField txtDate;
    private TextArea txtExplanation;

    public CsvModelDialog(CsvModel csvModel, ListDataProvider<CsvModel> csvDataProvider) {
        this.csvModel = csvModel;
        this.csvDataProvider = csvDataProvider;
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

        ListItem titleLabel = new ListItem(image, "Invoice Template");
        titleLabel.getPrimary().addClassName(LumoStyles.Heading.H2);

        FlexBoxLayout header = new FlexBoxLayout(titleLabel);
        header.addClassName(BoxShadowBorders.BOTTOM);
        return header;
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout();
        content.addClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.ROW);

        txtProject = new TextField();
        txtProject.setWidthFull();
        txtProject.setValue(csvModel.getProject());

        txtVendor = new TextField();
        txtVendor.setWidthFull();
        txtVendor.setValue(csvModel.getVendor());

        txtEventType = new TextField();
        txtEventType.setWidthFull();
        txtEventType.setValue(csvModel.getEventType());

        txtMainItem = new TextField();
        txtMainItem.setWidthFull();
        txtMainItem.setValue(csvModel.getMainItem());

        txtBook = new TextField();
        txtBook.setWidthFull();
        txtBook.setValue(csvModel.getBook());

        txtTransaction = new TextField();
        txtTransaction.setWidthFull();
        txtTransaction.setValue(csvModel.getTransaction());

        txtInvoiceNumber = new TextField();
        txtInvoiceNumber.setWidthFull();
        txtInvoiceNumber.setValue(csvModel.getNumber());

        txtInvoiceCode = new TextField();
        txtInvoiceCode.setWidthFull();
        txtInvoiceCode.setValue(csvModel.getCode());

        txtAmount = new TextField();
        txtAmount.setWidthFull();
        txtAmount.setValue(csvModel.getAmount());

        txtUnitPrize = new TextField();
        txtUnitPrize.setWidthFull();
        txtUnitPrize.setValue(csvModel.getUnitPrice());

        txtDate = new TextField();
        txtDate.setWidthFull();
        txtDate.setPlaceholder("dd/MM/yyyy HH:mm:ss");
        txtDate.setValue(csvModel.getDate());

        txtExplanation = new TextArea();
        txtExplanation.setValue(csvModel.getExplanation());
        txtExplanation.setWidthFull();
        txtExplanation.setHeight(UIUtils.COLUMN_WIDTH_XS);

        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(txtProject, "Project");
        form.addFormItem(txtVendor, "Vendor");
        form.addFormItem(txtEventType, "Event Type");
        form.addFormItem(txtMainItem, "Main Item");
        form.addFormItem(txtBook, "Book");
        form.addFormItem(txtTransaction, "Transaction");
        form.addFormItem(txtInvoiceNumber, "Invoice Number");
        form.addFormItem(txtInvoiceCode, "Invoice Code");
        form.addFormItem(txtAmount, "Amount");
        form.addFormItem(txtUnitPrize, "Unit Prize");
        form.addFormItem(txtDate, "Invoice Date");

        UIUtils.setColSpan(2, form.addFormItem(txtExplanation, "Explanation"));
        content.add(form);
        return content;
    }

    private Component createFooter() {
        FlexBoxLayout footer = new FlexBoxLayout();
        footer.addClassName(CLASS_NAME + "__footer");
        footer.setBackgroundColor(LumoStyles.Color.Contrast._5);
        footer.setPadding(Horizontal.RESPONSIVE_L, Vertical.S, Vertical.S);
        footer.setSpacing(Right.S);

        Button btnSave = UIUtils.createPrimaryButton("Save");
        Button btnCancel = UIUtils.createTertiaryButton("Cancel");
        footer.add(btnSave, btnCancel);

        btnSave.addClickListener(e -> saveUser());
        btnCancel.addClickListener(e -> close());
        return footer;
    }

    private void saveUser() {
        CsvModel c = new CsvModel();
        c.setProject(txtProject.getValue().toUpperCase());
        c.setVendor(txtVendor.getValue().toUpperCase());
        c.setEventType(txtEventType.getValue().toUpperCase());
        c.setMainItem(txtMainItem.getValue().toUpperCase());
        c.setBook(txtBook.getValue().toUpperCase());
        c.setTransaction(txtTransaction.getValue().toUpperCase());
        c.setNumber(txtInvoiceNumber.getValue().toUpperCase());
        c.setCode(txtInvoiceCode.getValue().toUpperCase());
        c.setAmount(txtAmount.getValue().toUpperCase());
        c.setUnitPrice(txtUnitPrize.getValue().toUpperCase());
        c.setDate(txtDate.getValue().toUpperCase());
        c.setExplanation(txtExplanation.getValue().toUpperCase());

        csvDataProvider.getItems().remove(csvModel);
        csvDataProvider.getItems().add(c);
        csvDataProvider.refreshAll();
        UIUtils.showNotification("Successfull!");
        close();
    }

}

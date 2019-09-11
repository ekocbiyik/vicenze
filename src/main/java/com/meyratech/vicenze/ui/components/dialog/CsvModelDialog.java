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

    private Button btnSave;
    private Button btnCancel;
    private CsvModel csvModel;
    private ListDataProvider<CsvModel> csvDataProvider;
    private TextField txtProject;
    private TextField txtVendor;
    private TextField txtEventType;
    private TextField txtMainItem;
    private TextField txtbook;
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
        txtProject.setValue(csvModel.getPROJECT());

        txtVendor = new TextField();
        txtVendor.setWidthFull();
        txtVendor.setValue(csvModel.getVENDOR());

        txtEventType = new TextField();
        txtEventType.setWidthFull();
        txtEventType.setValue(csvModel.getEVENT_TYPE());

        txtMainItem = new TextField();
        txtMainItem.setWidthFull();
        txtMainItem.setValue(csvModel.getMAIN_ITEM());

        txtbook = new TextField();
        txtbook.setWidthFull();
        txtbook.setValue(csvModel.getBOOK());

        txtTransaction = new TextField();
        txtTransaction.setWidthFull();
        txtTransaction.setValue(csvModel.getTRANSACTION());

        txtInvoiceNumber = new TextField();
        txtInvoiceNumber.setWidthFull();
        txtInvoiceNumber.setValue(csvModel.getNUMBER());

        txtInvoiceCode = new TextField();
        txtInvoiceCode.setWidthFull();
        txtInvoiceCode.setValue(csvModel.getCODE());

        txtAmount = new TextField();
        txtAmount.setWidthFull();
        txtAmount.setValue(csvModel.getAMOUNT());

        txtUnitPrize = new TextField();
        txtUnitPrize.setWidthFull();
        txtUnitPrize.setValue(csvModel.getUNIT_PRICE());

        txtDate = new TextField();
        txtDate.setValue(csvModel.getDATE());

        txtExplanation = new TextArea();
        txtExplanation.setValue(csvModel.getEXPLANATION());
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
        form.addFormItem(txtbook, "Book");
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

        btnSave = UIUtils.createPrimaryButton("Save");
        btnCancel = UIUtils.createTertiaryButton("Cancel");
        footer.add(btnSave, btnCancel);

        btnSave.addClickListener(e -> saveUser());
        btnCancel.addClickListener(e -> close());
        return footer;
    }

    private void saveUser() {
        CsvModel c = new CsvModel();
        c.setPROJECT(txtProject.getValue().toUpperCase());
        c.setVENDOR(txtVendor.getValue().toUpperCase());
        c.setEVENT_TYPE(txtEventType.getValue().toUpperCase());
        c.setMAIN_ITEM(txtMainItem.getValue().toUpperCase());
        c.setBOOK(txtbook.getValue().toUpperCase());
        c.setTRANSACTION(txtTransaction.getValue().toUpperCase());
        c.setNUMBER(txtInvoiceNumber.getValue().toUpperCase());
        c.setCODE(txtInvoiceCode.getValue().toUpperCase());
        c.setAMOUNT(txtAmount.getValue().toUpperCase());
        c.setUNIT_PRICE(txtUnitPrize.getValue().toUpperCase());
        c.setDATE(txtDate.getValue().toUpperCase());
        c.setEXPLANATION(txtExplanation.getValue().toUpperCase());

        csvDataProvider.getItems().remove(csvModel);
        csvDataProvider.getItems().add(c);
        csvDataProvider.refreshAll();
        UIUtils.showNotification("Successfull!");
        close();
    }

}

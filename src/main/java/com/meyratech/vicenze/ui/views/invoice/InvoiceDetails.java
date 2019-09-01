package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.model.*;
import com.meyratech.vicenze.backend.repository.service.IIncorrectInvoiceService;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.backend.repository.service.IProjectService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.ViewFrame;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.meyratech.vicenze.ui.components.navigation.bar.AppBar;
import com.meyratech.vicenze.ui.layout.size.Bottom;
import com.meyratech.vicenze.ui.layout.size.Horizontal;
import com.meyratech.vicenze.ui.layout.size.Top;
import com.meyratech.vicenze.ui.layout.size.Vertical;
import com.meyratech.vicenze.ui.util.*;
import com.meyratech.vicenze.ui.util.css.BorderRadius;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

@Route(value = ViewConst.PAGE_INVOICE_DETAILS, layout = MainLayout.class)
@PageTitle(ViewConst.TITLE_INVOICE_DETAILS)
public class InvoiceDetails extends ViewFrame implements HasUrlParameter<Long> {

    private Invoice detailedInvoice;
    private IncorrectInvoice incorrectInvoice;
    private IIncorrectInvoiceService incorrectInvoiceService;
    private IInvoiceService invoiceService;
    private IProjectService projectService;


    private DetailsDrawerFooter detailedFooter;

    //fields
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
    private Label lblTotalAmount;
    private DatePicker invoiceDate;
    private TimePicker invoiceTime;
    private TextArea txtExplanation;
    private Label lblCreatedBy;
    private Label lblCreationDate;
    private Binder<Invoice> binder;

    @Autowired
    public InvoiceDetails(IIncorrectInvoiceService incorrectInvoiceService, IInvoiceService invoiceService, IProjectService projectService) {
        this.incorrectInvoiceService = incorrectInvoiceService;
        this.invoiceService = invoiceService;
        this.projectService = projectService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        try {
            setViewContent(createContent());
            setViewFooter(createFooter());
            if (id > 0) { // invoice show
                detailedInvoice = invoiceService.findById(id);
            } else if (id < -1) { // incorrect invoice
                incorrectInvoice = incorrectInvoiceService.findById(-1 * id);
                detailedInvoice = incorrectInvoice.getInvoice();
            } else { // new invoice
                System.out.println("new invoice..");
            }
            initializeVariables();
            initializeValidators();

            if (!SecurityUtils.getCurrentUser().getRole().equalsIgnoreCase(Role.ADMIN)) {
                disableComponents();
            }

        } catch (Exception e) {
            UI.getCurrent().navigate(InvoiceView.class); // hata alınırsa..
            return;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        AppBar appBar = initAppBar();
        appBar.setTitle(detailedInvoice == null ? "New Invoice" : detailedInvoice.getProject().getProjectName());
    }

    private AppBar initAppBar() {
        AppBar appBar = MainLayout.get().getAppBar();
        appBar.setNaviMode(AppBar.NaviMode.CONTEXTUAL);
        appBar.getContextIcon().addClickListener(e -> UI.getCurrent().navigate(InvoiceView.class));
        return appBar;
    }

    private Component createContent() {
        FlexBoxLayout content = new FlexBoxLayout(
                getDetailHeader(String.format("%s%s", UIUtils.IMG_PATH, "logo-6.png"), "Project Details"),
                getProjectDetail(),
                getDetailHeader(String.format("%s%s", UIUtils.IMG_PATH, "logo-38.png"), "Item Details"),
                getItemDetail(),
                getDetailHeader(String.format("%s%s", UIUtils.IMG_PATH, "logo-20.png"), "Invoice Details"),
                getInvoiceDetail()
        );

        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_X);
        content.setMaxWidth("840px");
        content.setBorderRadius(BorderRadius.S);
        content.setBackgroundColor(LumoStyles.Color.BASE_COLOR);
        return content;
    }

    private Component getDetailHeader(String iconPath, String title) {
        Image image = new Image(iconPath, "");
        image.addClassName(LumoStyles.Margin.Horizontal.L);
        UIUtils.setBorderRadius(BorderRadius._50, image);
        image.setHeight("50px");
        image.setWidth("50px");

        ListItem titleLabel = new ListItem(image, title);
        titleLabel.getPrimary().addClassName(LumoStyles.Heading.H2);

        FlexBoxLayout header = new FlexBoxLayout(titleLabel);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.XS, Horizontal.RESPONSIVE_L, Top.L);
        header.setPadding(Bottom.XS);
        header.addClassName(BoxShadowBorders.BOTTOM);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        return header;
    }

    private Component getProjectDetail() {
        cbxProject = new ComboBox<>();
        cbxProject.setWidthFull();
        cbxProject.setItemLabelGenerator(Project::getProjectName);
        cbxProject.setItems(projectService.findAll());

        cbxVendor = new ComboBox<>();
        cbxVendor.setWidthFull();
        cbxVendor.setAllowCustomValue(true);
        cbxVendor.setItems(invoiceService.getAllVendorList());
        cbxVendor.addCustomValueSetListener(e -> cbxVendor.setValue(e.getSource().getValue() == null ? e.getDetail().toUpperCase() : e.getSource().getValue()));

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(cbxProject, "Project");
        form.addFormItem(cbxVendor, "Vendor");
        UIUtils.setColSpan(2);
        return form;
    }

    private Component getItemDetail() {
        cbxEventType = new ComboBox<>();
        cbxEventType.setWidthFull();
        cbxEventType.setItems(ItemDetails.eventTypeList);

        cbxMainItem = new ComboBox<>();
        cbxMainItem.setWidthFull();
        cbxMainItem.setItems(ItemDetails.mainItemList);

        cbxbook = new ComboBox<>();
        cbxbook.setWidthFull();
        cbxbook.setItems(ItemDetails.bookList);

        cbxTransaction = new ComboBox<>();
        cbxTransaction.setWidthFull();
        cbxTransaction.setItems(ItemDetails.transactionList);

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(cbxEventType, "Event Type");
        form.addFormItem(cbxMainItem, "Main Item");
        form.addFormItem(cbxbook, "Book");
        form.addFormItem(cbxTransaction, "Transaction");
        UIUtils.setColSpan(2);

        return form;
    }

    private Component getInvoiceDetail() {

        txtInvoiceNumber = new TextField();
        txtInvoiceNumber.setWidthFull();
        txtInvoiceNumber.setClearButtonVisible(true);

        txtInvoiceCode = new TextField();
        txtInvoiceCode.setWidthFull();
        txtInvoiceCode.setClearButtonVisible(true);

        txtAmount = new TextField();
        txtAmount.setWidthFull();
        txtAmount.setValue("0.0");
        txtAmount.setValueChangeMode(ValueChangeMode.EAGER);
        txtAmount.setClearButtonVisible(true);

        txtUnitPrize = new TextField();
        txtUnitPrize.setWidthFull();
        txtUnitPrize.setValue("0.0");
        txtUnitPrize.setValueChangeMode(ValueChangeMode.EAGER);
        txtUnitPrize.setClearButtonVisible(true);

        lblTotalAmount = UIUtils.createAmountLabel(0);
        lblTotalAmount.setWidthFull();

        //
        txtAmount.addValueChangeListener(e -> {
            try {
                String val = e.getValue().replaceAll("[^\\d.]", "");
                lblTotalAmount.setText(new BigDecimal(val).multiply(new BigDecimal(txtUnitPrize.getValue())).toString());
                txtAmount.setValue(val);
            } catch (Exception ex) {
                txtAmount.setValue(e.getOldValue());
                ex.printStackTrace();
            }
        });

        txtUnitPrize.addValueChangeListener(e -> {
            try {
                String val = e.getValue().replaceAll("[^\\d.]", "");
                lblTotalAmount.setText(new BigDecimal(val).multiply(new BigDecimal(txtAmount.getValue())).toString());
                txtUnitPrize.setValue(val);
            } catch (Exception ex) {
                txtUnitPrize.setValue(e.getOldValue());
                ex.printStackTrace();
            }
        });

        invoiceDate = new DatePicker();
        invoiceDate.setLocale(Locale.UK);
        invoiceDate.setValue(LocalDate.now());

        invoiceTime = new TimePicker();
        invoiceTime.setLocale(Locale.UK);
        invoiceTime.setValue(LocalTime.now());

        HorizontalLayout wrap = new HorizontalLayout();
        wrap.add(invoiceDate, invoiceTime);
        wrap.setSpacing(true);

        txtExplanation = new TextArea();
        txtExplanation.setWidthFull();
        txtExplanation.setClearButtonVisible(true);
        txtExplanation.setHeight(UIUtils.COLUMN_WIDTH_XS);
        txtExplanation.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        txtExplanation.addValueChangeListener(e -> txtExplanation.setValue(e.getValue().toUpperCase()));

        lblCreatedBy = UIUtils.createH5Label(SecurityUtils.getCurrentUser().getFullName());
        lblCreatedBy.setWidthFull();

        lblCreationDate = UIUtils.createH5Label(UIUtils.formatDatetime(LocalDateTime.now()));
        lblCreationDate.setWidthFull();

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("21em", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(txtInvoiceNumber, "Invoice Number");
        form.addFormItem(txtInvoiceCode, "Invoice Code");
        form.addFormItem(txtAmount, "Amount");
        form.addFormItem(txtUnitPrize, "Unit Prize");
        form.addFormItem(lblTotalAmount, "Total Amount");
        form.addFormItem(wrap, "Invoice Date");
        FormLayout.FormItem txtExplanationItem = form.addFormItem(txtExplanation, "Explanation");
        form.addFormItem(lblCreatedBy, "Created By");
        form.addFormItem(lblCreationDate, "Creation Date");
        UIUtils.setColSpan(2, txtExplanationItem);

        return form;
    }

    private Component createFooter() {
        detailedFooter = new DetailsDrawerFooter();
        detailedFooter.getSaveButton().setEnabled(false);
        detailedFooter.addCancelListener(e -> UI.getCurrent().navigate(InvoiceView.class));
        detailedFooter.addSaveListener(e -> saveDetailedInvoice());
        detailedFooter.getContent().setBackgroundColor(LumoStyles.Color.BASE_COLOR);
        return detailedFooter;
    }

    private void initializeVariables() {

        if (detailedInvoice != null) {
            cbxProject.setValue(detailedInvoice.getProject());
            cbxVendor.setValue(detailedInvoice.getVendor());
            cbxEventType.setValue(detailedInvoice.getEventType());
            cbxMainItem.setValue(detailedInvoice.getMainItem());
            cbxbook.setValue(detailedInvoice.getBook());
            cbxTransaction.setValue(detailedInvoice.getTransaction());
            txtInvoiceNumber.setValue(detailedInvoice.getInvoiceNumber());
            txtInvoiceCode.setValue(detailedInvoice.getInvoiceCode());
            txtAmount.setValue(detailedInvoice.getAmount().toString());
            txtUnitPrize.setValue(detailedInvoice.getUnitPrice().toString());

            lblTotalAmount.setText(detailedInvoice.getTotalAmount().toString());
            UIUtils.setTextColor(detailedInvoice.getTotalAmount().doubleValue() < 0 ? TextColor.ERROR : TextColor.SUCCESS, lblTotalAmount);

            invoiceDate.setValue(detailedInvoice.getDate().toLocalDate());
            invoiceTime.setValue(detailedInvoice.getDate().toLocalTime());
            txtExplanation.setValue(detailedInvoice.getExplanation());
            lblCreatedBy.setText(detailedInvoice.getCreatedBy().getFullName());
            lblCreationDate.setText(UIUtils.formatDatetime(detailedInvoice.getCreationDate()));
        }

    }

    private void initializeValidators() {
        binder = new Binder<>(Invoice.class);
        binder.forField(cbxProject)
                .asRequired("Projectis required!")
                .withValidator(p -> p != null, "Project can not be empty!")
                .bind(Invoice::getProject, Invoice::setProject);

        binder.forField(cbxVendor)
                .asRequired("Vendor is required!")
                .withValidator(v -> v.length() >= 3, "At least 3 characters!")
                .bind(Invoice::getVendor, Invoice::setVendor);

        binder.forField(cbxEventType)
                .asRequired("Please select event type!")
                .withValidator(e -> !e.isEmpty(), "Can not be empty!")
                .bind(Invoice::getEventType, Invoice::setEventType);

        binder.forField(cbxMainItem)
                .asRequired("Please select main item!")
                .bind(Invoice::getMainItem, Invoice::setMainItem);

        binder.forField(cbxbook)
                .asRequired("Please select book item!")
                .bind(Invoice::getBook, Invoice::setBook);

        binder.forField(cbxTransaction)
                .asRequired("Please select transaction item!")
                .bind(Invoice::getTransaction, Invoice::setTransaction);

        binder.forField(txtInvoiceNumber)
                .asRequired("Invoice number is required!")
                .withValidator(v -> v.length() >= 3, "At least 3 characters!")
                .bind(Invoice::getInvoiceNumber, Invoice::setInvoiceNumber);

        binder.forField(txtInvoiceCode)
                .asRequired("Invoice code is required!")
                .withValidator(v -> v.length() >= 3, "At least 3 characters!")
                .bind(Invoice::getInvoiceCode, Invoice::setInvoiceCode);

        binder.forField(txtExplanation)
                .bind(Invoice::getExplanation, Invoice::setExplanation);

        binder.forField(txtAmount)
                .asRequired("Amount is required!")
                .withConverter(new StringToBigDecimalConverter("Must enter a number"))
                .bind(Invoice::getAmount, Invoice::setAmount);

        binder.forField(txtUnitPrize)
                .asRequired("Unit prize is required!")
                .withConverter(new StringToBigDecimalConverter("Must enter a number"))
                .bind(Invoice::getUnitPrice, Invoice::setUnitPrice);

        detailedFooter.getSaveButton().setEnabled(false);
        binder.readBean(detailedInvoice == null ? new Invoice() : detailedInvoice);
        binder.addStatusChangeListener(status -> detailedFooter.getSaveButton().setEnabled(!status.hasValidationErrors()));
    }

    private void saveDetailedInvoice() {
        binder.validate();
        if (!binder.isValid()) {
            detailedFooter.getSaveButton().setEnabled(false);
            return;
        }

        if (detailedInvoice == null) {
            detailedInvoice = new Invoice();
            detailedInvoice.setCreatedBy(SecurityUtils.getCurrentUser());
            detailedInvoice.setCreationDate(LocalDateTime.now());
        }

        detailedInvoice.setProject(cbxProject.getValue());
        detailedInvoice.setVendor(cbxVendor.getValue());
        detailedInvoice.setEventType(cbxEventType.getValue());
        detailedInvoice.setMainItem(cbxMainItem.getValue());
        detailedInvoice.setBook(cbxbook.getValue());
        detailedInvoice.setTransaction(cbxTransaction.getValue());
        detailedInvoice.setInvoiceNumber(txtInvoiceNumber.getValue());
        detailedInvoice.setInvoiceCode(txtInvoiceCode.getValue());
        detailedInvoice.setExplanation(txtExplanation.getValue());
        detailedInvoice.setAmount(new BigDecimal(txtAmount.getValue()));
        detailedInvoice.setUnitPrice(new BigDecimal(txtUnitPrize.getValue()));
        detailedInvoice.setDate(LocalDateTime.of(invoiceDate.getValue(), invoiceTime.getValue()));

        try {
            invoiceService.save(detailedInvoice);
            if (incorrectInvoice != null) {
                incorrectInvoice.setActive(false);
                incorrectInvoiceService.save(incorrectInvoice);
            }
        } catch (Exception e) {
            Notification.show("Opps! Please check your fields!", 3000, Notification.Position.TOP_END);
            return;
        }

        Notification.show("Successfull", 6000, Notification.Position.TOP_END);
        UI.getCurrent().navigate(InvoiceView.class);
    }

    private void disableComponents() {
        cbxProject.setEnabled(false);
        cbxVendor.setEnabled(false);
        cbxEventType.setEnabled(false);
        cbxMainItem.setEnabled(false);
        cbxbook.setEnabled(false);
        cbxTransaction.setEnabled(false);
        txtInvoiceNumber.setEnabled(false);
        txtInvoiceCode.setEnabled(false);
        txtAmount.setEnabled(false);
        txtUnitPrize.setEnabled(false);
        invoiceDate.setEnabled(false);
        invoiceTime.setEnabled(false);
        txtExplanation.setEnabled(false);
        lblCreatedBy.setEnabled(false);
        lblCreationDate.setEnabled(false);
    }

}

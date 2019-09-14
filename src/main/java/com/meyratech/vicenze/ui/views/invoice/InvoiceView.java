package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.exporter.InvoiceExcelExporter;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.backend.repository.service.IProjectService;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.SplitViewFrame;
import com.meyratech.vicenze.ui.components.dialog.InvoiceDialog;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.TextColor;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = ViewConst.PAGE_INVOICE, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_INVOICE)
public class InvoiceView extends SplitViewFrame implements RouterLayout {

    private final IInvoiceService invoiceService;
    private final IProjectService projectService;
    private final IUserService userService;

    private Grid<Invoice> invoiceGrid;
    private Label lblItemSize;
    private ListDataProvider<Invoice> invoiceDataProvider;

    private ComboBox<Project> cbxProject;
    private ComboBox<User> cbxUser;
    private DatePicker dpSDate;
    private DatePicker dpEDate;

    @Autowired
    public InvoiceView(IInvoiceService invoiceService, IProjectService projectService, IUserService userService) {
        this.invoiceService = invoiceService;
        this.projectService = projectService;
        this.userService = userService;
        setViewContent(createContent());
        setViewHeader(createHeader());
        setViewFooter(new Label());
    }

    private Component createHeader() {

        // add
        Button btnCreate = UIUtils.createPrimaryButton("ADD", VaadinIcon.PLUS_CIRCLE_O);
        btnCreate.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            // yeni invoice eklemek için url yönlendirmesinde id olmak zorunda
            // çözümü böyle ürettim
            Invoice invoice = new Invoice();
            invoice.setId(-1L);
            UI.getCurrent().navigate(InvoiceDetails.class, invoice.getId());
        });

        //project
        cbxProject = new ComboBox<>();
        cbxProject.setWidth(UIUtils.COLUMN_WIDTH_XL);
        cbxProject.setPlaceholder("Project name");
        cbxProject.setItemLabelGenerator(Project::getProjectName);
        cbxProject.setItems(projectService.findAll());

        //created by
        cbxUser = new ComboBox<>();
        cbxUser.setWidth(UIUtils.COLUMN_WIDTH_XL);
        cbxUser.setPlaceholder("Created by");
        cbxUser.setItemLabelGenerator(User::getFullName);
        cbxUser.setItems(userService.findAll());

        //dpSDate
        dpSDate = new DatePicker();
        dpSDate.setPlaceholder("Start date");
        dpSDate.setLocale(Locale.UK);
        dpSDate.setWidth(UIUtils.COLUMN_WIDTH_XL);
        dpSDate.setValue(LocalDate.now().minusDays(1));
        dpSDate.setRequired(true);

        //dpEDate
        dpEDate = new DatePicker();
        dpEDate.setPlaceholder("End date");
        dpEDate.setLocale(Locale.UK);
        dpEDate.setWidth(UIUtils.COLUMN_WIDTH_XL);
        dpEDate.setValue(LocalDate.now());
        dpEDate.setRequired(true);

        // list
        Button btnSearch = UIUtils.createSuccessPrimaryButton("SEARCH", VaadinIcon.SEARCH);
        btnSearch.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> searchBtnClick());

        // export
        Button btnExport = UIUtils.createErrorPrimaryButton("EXPORT", VaadinIcon.PRINT);
        btnExport.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> exportToExcel());

        HorizontalLayout box = new HorizontalLayout();
        box.setWidthFull();
        box.setSpacing(true);
        box.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        box.add(btnSearch, btnExport);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setWidthFull();
        layout.setHeight("65px");
        layout.getStyle().set("background-color", LumoStyles.Color.BASE_COLOR);
        layout.add(btnCreate, cbxProject, cbxUser, dpSDate, dpEDate, box);

        return layout;
    }

    private Component createContent() {
        invoiceGrid = new Grid<>();
        invoiceGrid.setSizeFull();
        invoiceDataProvider = DataProvider.ofCollection(invoiceService.getLastInvoices(100));
        invoiceGrid.setDataProvider(invoiceDataProvider);

        Grid.Column<Invoice> col0 = invoiceGrid.addColumn(new ComponentRenderer<>(this::viewDetails)).setFrozen(true).setHeader("Details").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_XS);
        Grid.Column<Invoice> col1 = invoiceGrid.addColumn(Invoice::getInvoiceNumber).setFlexGrow(0).setHeader("Number").setSortable(true).setComparator(Invoice::getInvoiceNumber).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col2 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setFlexGrow(1).setHeader("Project").setWidth(UIUtils.COLUMN_WIDTH_XL);
        Grid.Column<Invoice> col3 = invoiceGrid.addColumn(Invoice::getVendor).setHeader("Vendor").setSortable(true).setComparator(Invoice::getVendor).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col4 = invoiceGrid.addColumn(Invoice::getInvoiceCode).setHeader("Code").setSortable(true).setComparator(Invoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col5 = invoiceGrid.addColumn(Invoice::getEventType).setHeader("Event Type").setSortable(true).setComparator(Invoice::getEventType).setWidth(UIUtils.COLUMN_WIDTH_S);
        Grid.Column<Invoice> col6 = invoiceGrid.addColumn(Invoice::getMainItem).setHeader("Main Item").setSortable(true).setComparator(Invoice::getMainItem).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col7 = invoiceGrid.addColumn(Invoice::getBook).setHeader("Book").setSortable(true).setComparator(Invoice::getBook).setWidth(UIUtils.COLUMN_WIDTH_M);
        Grid.Column<Invoice> col8 = invoiceGrid.addColumn(Invoice::getTransaction).setHeader("Transaction").setSortable(true).setComparator(Invoice::getTransaction).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col9 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createExplanation)).setHeader("Explanation").setWidth(UIUtils.COLUMN_WIDTH_XL);
        Grid.Column<Invoice> col10 = invoiceGrid.addColumn(Invoice::getAmount).setHeader("Amount").setSortable(true).setComparator(Invoice::getAmount).setWidth(UIUtils.COLUMN_WIDTH_S);
        Grid.Column<Invoice> col11 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createUnitPriceInfo)).setHeader("Unit Price").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col12 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createTotalAmount)).setHeader("Total Amount").setSortable(true).setComparator(Invoice::getTotalAmount).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col13 = invoiceGrid.addColumn(new ComponentRenderer<>(this::invoiceDate)).setComparator(Invoice::getDate).setHeader("Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col14 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createdByInfo)).setHeader("Created By").setComparator(Comparator.comparing(u -> u.getCreatedBy().getFullName())).setTextAlign(ColumnTextAlign.CENTER).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col15 = invoiceGrid.addColumn(new ComponentRenderer<>(this::creationDate)).setComparator(Invoice::getCreationDate).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);

        invoiceGrid.addItemDoubleClickListener(e -> viewEditPopup(e.getItem()));

        lblItemSize = new Label(invoiceDataProvider.getItems().size() + "");
        invoiceGrid.appendFooterRow().getCell(invoiceGrid.getColumns().get(0)).setComponent(lblItemSize);
        initializeItemListener();

        HeaderRow headerRow = invoiceGrid.appendHeaderRow();

        //filters...
        TextField fCol1 = createFilterField();
        fCol1.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getInvoiceNumber(), fCol1.getValue())));
        headerRow.getCell(col1).setComponent(fCol1);

        TextField fCol2 = createFilterField();
        fCol2.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getProject().getProjectName(), fCol2.getValue())));
        headerRow.getCell(col2).setComponent(fCol2);

        TextField fCol3 = createFilterField();
        fCol3.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getVendor(), fCol3.getValue())));
        headerRow.getCell(col3).setComponent(fCol3);

        TextField fCol4 = createFilterField();
        fCol4.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getInvoiceCode(), fCol4.getValue())));
        headerRow.getCell(col4).setComponent(fCol4);

        TextField fCol5 = createFilterField();
        fCol5.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getEventType(), fCol5.getValue())));
        headerRow.getCell(col5).setComponent(fCol5);

        TextField fCol6 = createFilterField();
        fCol6.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getMainItem(), fCol6.getValue())));
        headerRow.getCell(col6).setComponent(fCol6);

        TextField fCol7 = createFilterField();
        fCol7.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getBook(), fCol7.getValue())));
        headerRow.getCell(col7).setComponent(fCol7);

        TextField fCol8 = createFilterField();
        fCol8.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getTransaction(), fCol8.getValue())));
        headerRow.getCell(col8).setComponent(fCol8);

        TextField fCol9 = createFilterField();
        fCol9.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getExplanation(), fCol9.getValue())));
        headerRow.getCell(col9).setComponent(fCol9);

        TextField fCol10 = createFilterField();
        fCol10.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getAmount().toString(), fCol10.getValue())));
        headerRow.getCell(col10).setComponent(fCol10);

        TextField fCol11 = createFilterField();
        fCol11.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getUnitPrice().toString(), fCol11.getValue())));
        headerRow.getCell(col11).setComponent(fCol11);

        TextField fCol12 = createFilterField();
        fCol12.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getTotalAmount().toString(), fCol12.getValue())));
        headerRow.getCell(col12).setComponent(fCol12);

        TextField fCol13 = createFilterField();
        fCol13.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(UIUtils.formatDatetime(i.getDate()), fCol13.getValue())));
        headerRow.getCell(col13).setComponent(fCol13);

        TextField fCol14 = createFilterField();
        fCol14.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getCreatedBy().getFullName(), fCol14.getValue())));
        headerRow.getCell(col14).setComponent(fCol14);

        TextField fCol15 = createFilterField();
        fCol15.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(UIUtils.formatDatetime(i.getCreationDate()), fCol15.getValue())));
        headerRow.getCell(col15).setComponent(fCol15);

        Div content = new Div(invoiceGrid);
        content.addClassName("grid-view");
        return content;
    }

    private TextField createFilterField() {
        TextField filter = new TextField();
        filter.setClearButtonVisible(true);
        filter.setSizeFull();
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        return filter;
    }

    private Component createProjectInfo(Invoice invoice) {
        ListItem item = new ListItem(
                UIUtils.createInitials(invoice.getProject().getProjectName().substring(0, 2)),
                invoice.getProject().getProjectName(),
                invoice.getProject().getCompany()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createUnitPriceInfo(Invoice invoice) {
        return UIUtils.createAmountLabel(invoice.getUnitPrice().doubleValue());
    }

    private Component createTotalAmount(Invoice invoice) {
        Double total = invoice.getTotalAmount().doubleValue();
        Label label = UIUtils.createAmountLabel(total);

        if (total > 0) {
            UIUtils.setTextColor(TextColor.SUCCESS, label);
        } else {
            UIUtils.setTextColor(TextColor.ERROR, label);
        }
        return label;
    }

    private Component createExplanation(Invoice invoice) {
        Label label = new Label(invoice.getExplanation());
        label.getElement().getStyle().set("white-space", "pre-wrap");
        return label;
    }

    private Component invoiceDate(Invoice invoice) {
        return new Span(UIUtils.formatDatetime(invoice.getDate()));
    }

    private Component createdByInfo(Invoice invoice) {
        return new Span(invoice.getCreatedBy().getFullName());
    }

    private Component creationDate(Invoice invoice) {
        return new Span(UIUtils.formatDatetime(invoice.getCreationDate()));
    }

    private Component viewDetails(Invoice invoice) {
        return UIUtils.createButton(
                VaadinIcon.LINE_BAR_CHART,
                (ComponentEventListener<ClickEvent<Button>>) e -> UI.getCurrent().navigate(InvoiceDetails.class, invoice.getId())
        );
    }

    private void viewEditPopup(Invoice invoice) {
        new InvoiceDialog(invoice).open();
    }

    private void initializeItemListener() {
        invoiceGrid.getDataProvider().addDataProviderListener(e -> {
            int size = (int) invoiceGrid.getDataProvider().withConfigurableFilter().fetch(new Query<>()).count();
            lblItemSize.setText(String.valueOf(size));
        });
    }

    private void searchBtnClick() {
        if (dpSDate.getValue() == null || dpEDate.getValue() == null) {
            UIUtils.showNotification("Date ranges are required!");
            return;
        }

        List<Invoice> invoiceList;
        LocalDateTime startDate = dpSDate.getValue().atStartOfDay();
        LocalDateTime endDate = dpEDate.getValue().plusDays(1).atStartOfDay();

        if (cbxProject.getValue() != null && cbxUser.getValue() != null) {
            invoiceList = invoiceService.getInvoicesByProjectAndUserAndDate(cbxProject.getValue(), cbxUser.getValue(), startDate, endDate);

        } else if (cbxProject.getValue() != null) {
            invoiceList = invoiceService.getInvoicesByProjectAndDate(cbxProject.getValue(), startDate, endDate);

        } else if (cbxUser.getValue() != null) {
            invoiceList = invoiceService.getInvoicesByUserAndDate(cbxUser.getValue(), startDate, endDate);
        } else {
            invoiceList = invoiceService.getInvoicesByDate(startDate, endDate);
        }

        invoiceDataProvider = DataProvider.ofCollection(invoiceList);
        invoiceGrid.setDataProvider(invoiceDataProvider);
        lblItemSize.setText(String.valueOf(invoiceList.size()));
        initializeItemListener();
    }

    private void exportToExcel() {

        if (invoiceDataProvider.getItems().size() < 1) {
            UIUtils.showNotification("Empyty list!");
            return;
        }

        // bu sıra önemli, excel dosyası da bu sırada oluşturuluyor...
        List<String> columnHeaders = Arrays.asList(
                "Project", "Vendor", "Event Type", "Main Item",
                "Book", "Transaction", "Invoice Number", "Invoice Code",
                "Explanation", "Amount", "Unit Prize", "Total Amount",
                "Date", "Created By", "Creation Date"
        );

        try {
            String fileName = String.format(
                    "%s_%s",
                    SecurityUtils.getCurrentUser().getFullName().toLowerCase().replaceAll(" ", "_"),
                    UIUtils.formatFileNameDatetime(LocalDateTime.now())
            );
            File excelFile = new InvoiceExcelExporter(
                    invoiceGrid.getDataProvider().withConfigurableFilter().fetch(new Query<>()).collect(Collectors.toList()),
                    columnHeaders,
                    fileName
            ).build();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(excelFile));
            StreamResource resource = new StreamResource(String.format("%s.xls", fileName), () -> byteArrayInputStream);
            StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
            UI.getCurrent().getPage().executeJavaScript("window.open($0, $1)", registration.getResourceUri().toString(), "_blank");

            // delete tmp
            FileUtils.forceDelete(excelFile);
        } catch (Exception e) {
            UIUtils.showNotification("An error occured while creating excel!");
            e.printStackTrace();
        }

        UIUtils.showNotification("Downloaded successfully!");
    }

}

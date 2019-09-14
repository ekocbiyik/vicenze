package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.model.CsvModel;
import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.backend.utils.CsvImportUtils;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.dialog.CsvModelDialog;
import com.meyratech.vicenze.ui.layout.size.Bottom;
import com.meyratech.vicenze.ui.layout.size.Left;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.layout.size.Top;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


@Route(value = ViewConst.PAGE_INVOICE_IMPORT, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_INVOICE_IMPORT)
public class InvoiceImportView extends SplitLayout implements RouterLayout {

    // csv-import
    private Grid<CsvModel> csvGrid;
    private Label csvItemSize = new Label("0");
    private ListDataProvider<CsvModel> csvDataProvider = DataProvider.ofCollection(new ArrayList<>());

    // csv-validation
    private Grid<Invoice> invoiceGrid;
    private Label invoiceItemSize = new Label("0");
    private ListDataProvider<Invoice> invoiceDataProvider = DataProvider.ofCollection(new ArrayList<>());


    public InvoiceImportView() {
        setSizeFull();
        setOrientation(Orientation.VERTICAL);
        getElement().getStyle().set("background-color", LumoStyles.Color.BASE_COLOR);
        addToPrimary(getPrimaryContent());
        addToSecondary(getSecondaryContent());
    }

    private Component getPrimaryContent() {

        csvGrid = new Grid<>();
        csvGrid.setSizeFull();
        csvGrid.setDataProvider(csvDataProvider);

        Grid.Column<CsvModel> col0 = csvGrid.addColumn(new ComponentRenderer<>(this::viewCsvDetails)).setFrozen(true).setHeader("Edit").setWidth(UIUtils.COLUMN_WIDTH_XS);
        Grid.Column<CsvModel> col1 = csvGrid.addColumn(CsvModel::getPROJECT).setHeader("Project Name").setSortable(true).setComparator(CsvModel::getPROJECT).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col2 = csvGrid.addColumn(CsvModel::getVENDOR).setHeader("Vendor").setSortable(true).setComparator(CsvModel::getVENDOR).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col3 = csvGrid.addColumn(CsvModel::getEVENT_TYPE).setHeader("Event Type").setSortable(true).setComparator(CsvModel::getEVENT_TYPE).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col4 = csvGrid.addColumn(CsvModel::getMAIN_ITEM).setHeader("Main Item").setSortable(true).setComparator(CsvModel::getMAIN_ITEM).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col5 = csvGrid.addColumn(CsvModel::getBOOK).setHeader("Book").setSortable(true).setComparator(CsvModel::getBOOK).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col6 = csvGrid.addColumn(CsvModel::getTRANSACTION).setHeader("Transaction").setSortable(true).setComparator(CsvModel::getTRANSACTION).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col7 = csvGrid.addColumn(CsvModel::getNUMBER).setHeader("Number").setSortable(true).setComparator(CsvModel::getCODE).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col8 = csvGrid.addColumn(CsvModel::getEXPLANATION).setHeader("Explanation").setSortable(true).setComparator(CsvModel::getEXPLANATION).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col9 = csvGrid.addColumn(CsvModel::getAMOUNT).setHeader("Amount").setSortable(true).setComparator(CsvModel::getAMOUNT).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col10 = csvGrid.addColumn(CsvModel::getUNIT_PRICE).setHeader("Unit Prize").setSortable(true).setComparator(CsvModel::getUNIT_PRICE).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col11 = csvGrid.addColumn(CsvModel::getTOTAL_AMOUNT).setHeader("Total Amount").setSortable(true).setComparator(CsvModel::getTOTAL_AMOUNT).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<CsvModel> col12 = csvGrid.addColumn(CsvModel::getDATE).setHeader("Date").setSortable(true).setComparator(CsvModel::getDATE).setWidth(UIUtils.COLUMN_WIDTH_L);

        csvGrid.prependHeaderRow().join(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12).setComponent(getCsvSearchBar());
        csvGrid.appendFooterRow().getCell(csvGrid.getColumns().get(0)).setComponent(csvItemSize);

        FlexBoxLayout content = new FlexBoxLayout();
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Left.S, Top.S, Right.S, Bottom.S);
        content.add(new Label("Invoice csv file"), csvGrid);
        return content;
    }

    private Component getSecondaryContent() {

        invoiceGrid = new Grid<>();
        invoiceGrid.setSizeFull();
        invoiceGrid.setDataProvider(invoiceDataProvider);

        invoiceGrid.getDataProvider().addDataProviderListener(e -> {
            int size = (int) invoiceGrid.getDataProvider().withConfigurableFilter().fetch(new Query<>()).count();
            invoiceItemSize.setText(String.valueOf(size));
        });

        Grid.Column<Invoice> col0 = invoiceGrid.addColumn(new ComponentRenderer<>(this::removeInvoice)).setFrozen(true).setHeader("Details").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_XS);
        Grid.Column<Invoice> col1 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setFlexGrow(1).setHeader("Project").setWidth(UIUtils.COLUMN_WIDTH_XL);
        Grid.Column<Invoice> col2 = invoiceGrid.addColumn(Invoice::getVendor).setHeader("Vendor").setSortable(true).setComparator(Invoice::getVendor).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col3 = invoiceGrid.addColumn(Invoice::getInvoiceNumber).setFlexGrow(0).setHeader("Number").setSortable(true).setComparator(Invoice::getInvoiceNumber).setWidth(UIUtils.COLUMN_WIDTH_L);
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


        invoiceGrid.prependHeaderRow().join(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13, col14, col15).setComponent(getInvoiceSearchBar());
        invoiceGrid.appendFooterRow().getCell(invoiceGrid.getColumns().get(0)).setComponent(invoiceItemSize);


        FlexBoxLayout content = new FlexBoxLayout();
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Left.S, Top.S, Right.S, Bottom.S);
        content.add(new Label("Validated invoice list"), invoiceGrid);
        return content;
    }

    private Component getCsvSearchBar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Filter invoice");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setSizeFull();
        searchField.addValueChangeListener(e -> csvDataProvider.addFilter(
                (item) -> StringUtils.containsIgnoreCase(item.getPROJECT(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getVENDOR(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getEVENT_TYPE(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getMAIN_ITEM(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getBOOK(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getTRANSACTION(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getNUMBER(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getEXPLANATION(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getAMOUNT(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getUNIT_PRICE(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getTOTAL_AMOUNT(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getDATE(), searchField.getValue())
        ));

        // upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.setAutoUpload(true);
        upload.addSucceededListener(event -> {
            csvDataProvider = DataProvider.ofCollection(CsvImportUtils.uploadInvoiceCsv(buffer.getInputStream()));
            csvGrid.setDataProvider(csvDataProvider);
            csvItemSize.setText(csvDataProvider.getItems().size() + "");
            csvGrid.getDataProvider().addDataProviderListener(e -> {
                int size = (int) csvGrid.getDataProvider().withConfigurableFilter().fetch(new Query<>()).count();
                csvItemSize.setText(String.valueOf(size));
            });
        });

        // validate
        Button btnValidate = UIUtils.createPrimaryButton("Validate", VaadinIcon.CHECK);
        btnValidate.setWidth("200px");
        btnValidate.addClickListener(e -> CsvImportUtils.validateInvoiceCsv(csvGrid, invoiceGrid));


        HorizontalLayout container = new HorizontalLayout(upload, btnValidate, searchField);
        container.setSpacing(true);
        container.setSizeFull();
        return container;
    }

    private Component getInvoiceSearchBar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Filter invoice");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setSizeFull();
        searchField.addValueChangeListener(e -> invoiceDataProvider.addFilter(
                (item) -> StringUtils.containsIgnoreCase(item.getProject().getProjectName(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getVendor(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getEventType(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getMainItem(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getBook(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getTransaction(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getInvoiceNumber(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getExplanation(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getAmount().toString(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getUnitPrice().toString(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getTotalAmount().toString(), searchField.getValue())
                        || StringUtils.containsIgnoreCase(item.getDate().toString(), searchField.getValue())
        ));

        Button btnSaveDB = UIUtils.createPrimaryButton("Save to Database", VaadinIcon.DATABASE);
        btnSaveDB.setWidth("200px");
        btnSaveDB.addClickListener(e -> CsvImportUtils.insertInvoice2DB(invoiceGrid));

        HorizontalLayout container = new HorizontalLayout(btnSaveDB, searchField);
        container.setSpacing(true);
        container.setSizeFull();
        return container;
    }

    private Component viewCsvDetails(CsvModel csvModel) {
        return UIUtils.createButton(
                VaadinIcon.LINE_BAR_CHART,
                (ComponentEventListener<ClickEvent<Button>>) e -> new CsvModelDialog(csvModel, csvDataProvider).open()
        );
    }

    private Component createProjectInfo(Invoice invoice) {
        return new Label(invoice.getProject().getProjectName());
    }

    private Component createUnitPriceInfo(Invoice invoice) {
        return UIUtils.createAmountLabel(invoice.getUnitPrice().doubleValue());
    }

    private Component createTotalAmount(Invoice invoice) {
        return UIUtils.createAmountLabel(invoice.getTotalAmount().doubleValue());
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

    private Component removeInvoice(Invoice invoice) {
        Button btnDelte = UIUtils.createPrimaryButton(VaadinIcon.TRASH);
        btnDelte.addClickListener(e -> {
            ((ListDataProvider<Invoice>) invoiceGrid.getDataProvider()).getItems().remove(invoice);
            invoiceGrid.getDataProvider().refreshAll();
        });
        return btnDelte;
    }

}

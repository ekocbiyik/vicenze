package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.repository.service.InvoiceServiceImpl;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.SplitViewFrame;
import com.meyratech.vicenze.ui.util.TextColor;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Route(value = ViewConst.PAGE_INVOICE, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_INVOICE)
public class InvoiceView extends SplitViewFrame implements RouterLayout {

    private final InvoiceServiceImpl invoiceService;
    private Grid<Invoice> invoiceGrid;
    private ListDataProvider<Invoice> invoiceDataProvider;
    private Button btnCreate;
    private TextField searchField;

    @Autowired
    public InvoiceView(InvoiceServiceImpl invoiceService) {
        this.invoiceService = invoiceService;
        setViewContent(createContent());
    }

    private Component createContent() {
        invoiceGrid = new Grid<>();
        invoiceGrid.setSizeFull();
        invoiceDataProvider = DataProvider.ofCollection(invoiceService.findAll());
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
        Grid.Column<Invoice> col9 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createExplanation)).setHeader("Explanation").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col10 = invoiceGrid.addColumn(Invoice::getAmount).setHeader("Amount").setSortable(true).setComparator(Invoice::getAmount).setWidth(UIUtils.COLUMN_WIDTH_S);
        Grid.Column<Invoice> col11 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createUnitPriceInfo)).setHeader("Unit Price").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col12 = invoiceGrid.addColumn(new ComponentRenderer<>(this::createTotalAmount)).setHeader("Total Amount").setSortable(true).setComparator(Invoice::getTotalAmount).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col13 = invoiceGrid.addColumn(new ComponentRenderer<>(this::invoiceDate)).setComparator(Invoice::getDate).setHeader("Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col14 = invoiceGrid.addColumn(Invoice::getCreatedBy).setHeader("Created By").setComparator(Invoice::getCreatedBy).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col15 = invoiceGrid.addColumn(new ComponentRenderer<>(this::creationDate)).setComparator(Invoice::getCreationDate).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);

        invoiceGrid.appendFooterRow().getCell(invoiceGrid.getColumns().get(0)).setComponent(new Label(String.valueOf(invoiceDataProvider.getItems().size())));
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
        fCol14.addValueChangeListener(event -> invoiceDataProvider.addFilter(i -> StringUtils.containsIgnoreCase(i.getCreatedBy(), fCol14.getValue())));
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
        filter.setSizeFull();
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        return filter;
    }

    private Component getGridHeader() {
        searchField = new TextField();
        searchField.setPlaceholder("Search projects...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setSizeFull();
        searchField.addValueChangeListener(e -> {
            invoiceDataProvider.addFilter((item) ->
                    StringUtils.containsIgnoreCase(item.getInvoiceNumber(), searchField.getValue())
                            || StringUtils.containsIgnoreCase(item.getProject().getProjectName(), searchField.getValue()));
        });

        btnCreate = UIUtils.createPrimaryButton("ADD", VaadinIcon.PLUS_CIRCLE_O);
        btnCreate.addClickListener(e -> UIUtils.showNotification("Not implemented!"));

        HorizontalLayout container = new HorizontalLayout(btnCreate, searchField);
        container.setSpacing(true);
        container.setSizeFull();
        return container;
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

    private Component creationDate(Invoice invoice) {
        return new Span(UIUtils.formatDatetime(invoice.getCreationDate()));
    }

    private Component viewDetails(Invoice invoice) {
        return UIUtils.createButton(
                VaadinIcon.INVOICE,
                (ComponentEventListener<ClickEvent<Button>>) e -> UI.getCurrent().navigate(InvoiceDetails.class, invoice.getId())
        );
    }

}

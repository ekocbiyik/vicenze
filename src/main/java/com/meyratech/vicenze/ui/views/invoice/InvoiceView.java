package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.model.Project;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

        invoiceGrid.addColumn(new ComponentRenderer<>(this::viewDetails)).setFrozen(true).setHeader("Details").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_XS);
        invoiceGrid.addColumn(Invoice::getInvoiceNumber).setFlexGrow(0).setHeader("Number").setSortable(true).setComparator(Invoice::getInvoiceNumber).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setFlexGrow(1).setHeader("Project").setWidth(UIUtils.COLUMN_WIDTH_XL);
        invoiceGrid.addColumn(Invoice::getVendor).setHeader("Vendor").setSortable(true).setComparator(Invoice::getVendor).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getInvoiceCode).setHeader("Code").setSortable(true).setComparator(Invoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getEventType).setHeader("Event Type").setSortable(true).setComparator(Invoice::getEventType).setWidth(UIUtils.COLUMN_WIDTH_S);
        invoiceGrid.addColumn(Invoice::getMainItem).setHeader("Main Item").setSortable(true).setComparator(Invoice::getMainItem).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getBook).setHeader("Book").setSortable(true).setComparator(Invoice::getBook).setWidth(UIUtils.COLUMN_WIDTH_M);
        invoiceGrid.addColumn(Invoice::getTransaction).setHeader("Transaction").setSortable(true).setComparator(Invoice::getTransaction).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(new ComponentRenderer<>(this::createExplanation)).setHeader("Explanation").setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getAmount).setHeader("Amount").setSortable(true).setComparator(Invoice::getAmount).setWidth(UIUtils.COLUMN_WIDTH_S);
        invoiceGrid.addColumn(new ComponentRenderer<>(this::createUnitPriceInfo)).setHeader("Unit Price").setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(new ComponentRenderer<>(this::createTotalAmount)).setHeader("Total Amount").setSortable(true).setComparator(Invoice::getTotalAmount).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(new ComponentRenderer<>(this::invoiceDate)).setComparator(Invoice::getDate).setHeader("Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getCreatedBy).setHeader("Created By").setComparator(Invoice::getCreatedBy).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(new ComponentRenderer<>(this::creationDate)).setComparator(Invoice::getCreationDate).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);


//        invoiceGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

//        HeaderRow topRow = invoiceGrid.prependHeaderRow();
//        HeaderRow.HeaderCell buttonsCell = topRow.join(col0, col1, col2, col3, col4, col5, col6);
//        buttonsCell.setComponent(getGridHeader());
        invoiceGrid.appendFooterRow().getCell(invoiceGrid.getColumns().get(0)).setComponent(new Label(String.valueOf(invoiceDataProvider.getItems().size())));

        Div content = new Div(invoiceGrid);
        content.addClassName("grid-view");
        return content;
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

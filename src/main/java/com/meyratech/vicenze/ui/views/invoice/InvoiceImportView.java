package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.model.Invoice;
import com.meyratech.vicenze.backend.repository.service.IInvoiceService;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.layout.size.Bottom;
import com.meyratech.vicenze.ui.layout.size.Left;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.layout.size.Top;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = ViewConst.PAGE_INVOICE_IMPORT, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_INVOICE_IMPORT)
public class InvoiceImportView extends SplitLayout implements RouterLayout {

    private static final String CLASS_NAME = "details-drawer";

    private IInvoiceService invoiceService;

    @Autowired
    public InvoiceImportView(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;

        setSizeFull();
        setOrientation(Orientation.VERTICAL);
        getElement().getStyle().set("background-color", LumoStyles.Color.BASE_COLOR);

        addToPrimary(getPrimaryContent());
        addToSecondary(getSecondaryContent());
    }

    private Component getPrimaryContent() {

        Grid<Invoice> invoiceGrid = new Grid<>();
        invoiceGrid.setSizeFull();
        invoiceGrid.setDataProvider(DataProvider.ofCollection(invoiceService.findAll()));

        invoiceGrid.addColumn(Invoice::getInvoiceNumber).setFlexGrow(0).setHeader("Invoice Number").setSortable(true).setComparator(Invoice::getInvoiceNumber).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_S);
        invoiceGrid.addColumn(Invoice::getInvoiceCode).setHeader("Invoice Code").setSortable(true).setComparator(Invoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getVendor).setHeader("Vendor").setSortable(true).setComparator(Invoice::getVendor).setWidth(UIUtils.COLUMN_WIDTH_M);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_S);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_S);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_S);
        invoiceGrid.addColumn(Invoice::getInvoiceCode).setHeader("Invoice Code").setSortable(true).setComparator(Invoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_M);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getInvoiceCode).setHeader("Invoice Code").setSortable(true).setComparator(Invoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_XL);


        FlexBoxLayout content = new FlexBoxLayout();
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Left.S, Top.S, Right.S, Bottom.S);
        content.add(new Label("Invalid invoice list"), invoiceGrid);
        return content;
    }

    private Component getSecondaryContent() {

        Grid<Invoice> invoiceGrid = new Grid<>();
        invoiceGrid.setSizeFull();
        invoiceGrid.setDataProvider(DataProvider.ofCollection(invoiceService.findAll()));

        Grid.Column<Invoice> col3 = invoiceGrid.addColumn(Invoice::getInvoiceNumber).setFlexGrow(0).setHeader("Invoice Number").setSortable(true).setComparator(Invoice::getInvoiceNumber).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col4 = invoiceGrid.addColumn(Invoice::getInvoiceCode).setHeader("Invoice Code").setSortable(true).setComparator(Invoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Invoice> col5 = invoiceGrid.addColumn(Invoice::getVendor).setHeader("Vendor").setSortable(true).setComparator(Invoice::getVendor).setWidth(UIUtils.COLUMN_WIDTH_M);
        Grid.Column<Invoice> col6 = invoiceGrid.addColumn(Invoice::getDate).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_L);


        FlexBoxLayout content = new FlexBoxLayout();
        content.setFlexDirection(FlexDirection.COLUMN);
        content.setMargin(Left.S, Top.S, Right.S, Bottom.S);
        content.add(new Label("Validated invoice list"), invoiceGrid);
        return content;
    }

}

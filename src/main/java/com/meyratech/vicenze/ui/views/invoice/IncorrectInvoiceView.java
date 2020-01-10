package com.meyratech.vicenze.ui.views.invoice;

import com.meyratech.vicenze.backend.model.IncorrectInvoice;
import com.meyratech.vicenze.backend.repository.service.IIncorrectInvoiceService;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.SplitViewFrame;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
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
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;

@Route(value = ViewConst.PAGE_INCORRECT_INVOICE, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_INCORRECT_INVOICE)
public class IncorrectInvoiceView extends SplitViewFrame implements RouterLayout {

    private final IIncorrectInvoiceService incorrectService;

    private Label lblItemSize;
    private TextField searchField;
    private Grid<IncorrectInvoice> incorrectGrid;
    private ListDataProvider<IncorrectInvoice> incorrectDataProvider;


    @Autowired
    public IncorrectInvoiceView(IIncorrectInvoiceService incorrectService) {
        this.incorrectService = incorrectService;
        setViewContent(createContent());
    }

    private Component createContent() {
        incorrectGrid = new Grid<>();
        incorrectGrid.setSizeFull();

        incorrectDataProvider = DataProvider.ofCollection(incorrectService.findAll());
        incorrectGrid.setDataProvider(incorrectDataProvider);
        incorrectGrid.addColumn(new ComponentRenderer<>(this::viewDetails)).setFrozen(true).setHeader("Edit").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_XS);

        Grid.Column<IncorrectInvoice> col1 = incorrectGrid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setFlexGrow(1).setHeader("Project").setWidth(UIUtils.COLUMN_WIDTH_XL);
        Grid.Column<IncorrectInvoice> col2 = incorrectGrid.addColumn(new ComponentRenderer<>(this::createActive)).setFlexGrow(0).setHeader("Active").setSortable(true).setComparator(IncorrectInvoice::isActive).setWidth(UIUtils.COLUMN_WIDTH_S);
        Grid.Column<IncorrectInvoice> col3 = incorrectGrid.addColumn(IncorrectInvoice::getInvoiceNumber).setFlexGrow(0).setHeader("Invoice Number").setSortable(true).setComparator(IncorrectInvoice::getInvoiceNumber).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<IncorrectInvoice> col4 = incorrectGrid.addColumn(IncorrectInvoice::getInvoiceCode).setHeader("Invoice Code").setSortable(true).setComparator(IncorrectInvoice::getInvoiceCode).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<IncorrectInvoice> col5 = incorrectGrid.addColumn(IncorrectInvoice::getVendor).setHeader("Vendor").setSortable(true).setComparator(IncorrectInvoice::getVendor).setWidth(UIUtils.COLUMN_WIDTH_M);
        Grid.Column<IncorrectInvoice> col6 = incorrectGrid.addColumn(new ComponentRenderer<>(this::createExplanation)).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<IncorrectInvoice> col7 = incorrectGrid.addColumn(new ComponentRenderer<>(this::createdByInfo)).setHeader("Reported By").setComparator(Comparator.comparing(u -> u.getCreatedBy().getFullName())).setTextAlign(ColumnTextAlign.CENTER).setWidth(UIUtils.COLUMN_WIDTH_M);
        Grid.Column<IncorrectInvoice> col8 = incorrectGrid.addColumn(new ComponentRenderer<>(this::creationDate)).setComparator(IncorrectInvoice::getCreationDate).setHeader("Reported Date").setWidth(UIUtils.COLUMN_WIDTH_L);

        lblItemSize = new Label(incorrectDataProvider.getItems().size() + "");
        incorrectGrid.appendFooterRow().getCell(incorrectGrid.getColumns().get(0)).setComponent(lblItemSize);
        initializeItemListener();

        HeaderRow topRow = incorrectGrid.prependHeaderRow();
        HeaderRow.HeaderCell buttonsCell = topRow.join(col1, col2, col3, col4, col5, col6, col7, col8);
        buttonsCell.setComponent(getGridHeader());

        Div content = new Div(incorrectGrid);
        content.addClassName("grid-view");
        return content;
    }

    private Component getGridHeader() {
        searchField = new TextField();
        searchField.setPlaceholder("Project name, Invoice number, Invoice code, Reported user...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setSizeFull();
        searchField.addValueChangeListener(e ->
                incorrectDataProvider.addFilter((item) ->
                        StringUtils.containsIgnoreCase(item.getInvoice().getProject().getProjectName(), searchField.getValue())
                                || StringUtils.containsIgnoreCase(item.getInvoiceNumber(), searchField.getValue())
                                || StringUtils.containsIgnoreCase(item.getInvoiceCode(), searchField.getValue())
                                || StringUtils.containsIgnoreCase(item.getCreatedBy().getFullName(), searchField.getValue())
                ));

        HorizontalLayout container = new HorizontalLayout(searchField, new Label());
        container.setSizeFull();
        return searchField;
    }

    private Component createProjectInfo(IncorrectInvoice incorrectInvoice) {
        ListItem item = new ListItem(
                UIUtils.createInitials(incorrectInvoice.getInvoice().getProject().getProjectName().substring(0, 2)),
                incorrectInvoice.getInvoice().getProject().getProjectName(),
                incorrectInvoice.getInvoice().getProject().getCompany()
        );
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createExplanation(IncorrectInvoice incorrectInvoice) {
        Label label = new Label(incorrectInvoice.getDescription());
        label.getElement().getStyle().set("white-space", "pre-wrap");
        return label;
    }

    private Component createActive(IncorrectInvoice incorrectInvoice) {
        return incorrectInvoice.isActive() ? UIUtils.createPrimaryIcon(VaadinIcon.CHECK) : UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
    }

    private Component createdByInfo(IncorrectInvoice incorrectInvoice) {
        return new Span(incorrectInvoice.getInvoice().getCreatedBy().getFullName());
    }

    private Component creationDate(IncorrectInvoice incorrectInvoice) {
        return new Span(UIUtils.formatDatetime(incorrectInvoice.getInvoice().getCreationDate()));
    }

    private Component viewDetails(IncorrectInvoice incorrectInvoice) {
        /*
          varolan invoice ekranını incorrectler için de kullanaiblmek için -1 ile çarpıyoruz.
          invoiceDetail ekranına negatif id li kayıt gelirse bu incorrect bir kaydın id'si demek oluyor..
          */
        Button btnEdit = UIUtils.createButton(
                VaadinIcon.EDIT,
                (ComponentEventListener<ClickEvent<Button>>) e -> UI.getCurrent().navigate(InvoiceDetails.class, (-1 * incorrectInvoice.getId()))
        );
        btnEdit.setEnabled(incorrectInvoice.isActive());
        return btnEdit;
    }

    private void initializeItemListener() {
        incorrectGrid.getDataProvider().addDataProviderListener(e -> {
            lblItemSize.setText(String.valueOf((int) incorrectGrid.getDataProvider().withConfigurableFilter().fetch(new Query<>()).count()));
        });

    }
}

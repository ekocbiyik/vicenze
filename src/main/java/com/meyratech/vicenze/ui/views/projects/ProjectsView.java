package com.meyratech.vicenze.ui.views.projects;

import com.meyratech.vicenze.backend.DummyData;
import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.ProjectServiceImpl;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawer;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.TextColor;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.views.SplitViewFrame;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@Route(value = ViewConst.PAGE_PROJECTS, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_PROJECTS)
public class ProjectsView extends SplitViewFrame implements RouterLayout {

    private final ProjectServiceImpl projectService;
    private Grid<Project> projectGrid;
    private Button btnCreate;
    private TextField searchField;
    private ListDataProvider<Project> dataProvider;

    private DetailsDrawer detailsDrawer;
    private Label detailsDrawerHeader;

    @Autowired
    public ProjectsView(ProjectServiceImpl projectService) {
        this.projectService = projectService;

        // TODO: 8/1/19 burada kaldık, ekleme güncelleme...

        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(SplitViewFrame.Position.BOTTOM);
    }

    private Component createContent() {
        projectGrid = new Grid<>();
        projectGrid.setSizeFull();
        dataProvider = DataProvider.ofCollection(projectService.findAll());
        projectGrid.setDataProvider(dataProvider);

        Grid.Column<Project> col1 = projectGrid.addColumn(Project::getId).setFlexGrow(0).setFrozen(true).setHeader("ID").setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_XS);
        Grid.Column<Project> col2 = projectGrid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setFlexGrow(1).setHeader("Project Name").setSortable(true).setComparator(Project::getProjectName).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Project> col3 = projectGrid.addColumn(new ComponentRenderer<>(this::createContactInfo)).setFlexGrow(1).setHeader("Contact").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Project> col4 = projectGrid.addColumn(new ComponentRenderer<>(this::createDescription)).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
        Grid.Column<Project> col5 = projectGrid.addColumn(new ComponentRenderer<>(this::createApprovalLimit)).setHeader("Total Limit (€)").setWidth(UIUtils.COLUMN_WIDTH_S).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
        Grid.Column<Project> col6 = projectGrid.addColumn(new ComponentRenderer<>(this::createAvailability)).setFlexGrow(0).setHeader("Status").setWidth(UIUtils.COLUMN_WIDTH_S).setSortable(true).setComparator(Project::isActive).setTextAlign(ColumnTextAlign.CENTER);
        Grid.Column<Project> col7 = projectGrid.addColumn(new LocalDateTimeRenderer<>(Project::getCreationDate, DateTimeFormatter.ofPattern("dd MMM YYYY HH:mm:ss"))).setComparator(Project::getCreationDate).setFlexGrow(0).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Project> col8 = projectGrid.addColumn(Project::getCreatedBy).setHeader("Created By").setWidth(UIUtils.COLUMN_WIDTH_S).setTextAlign(ColumnTextAlign.END);

        projectGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

        HeaderRow topRow = projectGrid.prependHeaderRow();
        HeaderRow.HeaderCell buttonsCell = topRow.join(col1, col2, col3, col4, col5, col6, col7, col8);
        buttonsCell.setComponent(getGridHeader());
        projectGrid.appendFooterRow().getCell(projectGrid.getColumns().get(1)).setComponent(new Label("Total: " + dataProvider.getItems().size() + " projects"));

        Div content = new Div(projectGrid);
        content.addClassName("grid-view");
        return content;
    }

    private Component getGridHeader() {
        searchField = new TextField();
        searchField.setPlaceholder("Search projects...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setSizeFull();
        searchField.addValueChangeListener(e -> dataProvider.addFilter(project -> StringUtils.containsIgnoreCase(project.getProjectName(), searchField.getValue())));

        btnCreate = UIUtils.createPrimaryButton("ADD", VaadinIcon.PLUS_CIRCLE_O);
        btnCreate.addClickListener(e -> openNewProjectView(e));

        HorizontalLayout container = new HorizontalLayout(btnCreate, searchField);
        container.setSpacing(true);
        container.setSizeFull();
        return container;
    }

    public void openNewProjectView(ClickEvent<Button> e) {
        Notification.show("not implemented!", 3000, Notification.Position.BOTTOM_END);
    }

    private Component createProjectInfo(Project project) {
        ListItem item = new ListItem(
                UIUtils.createInitials(project.getProjectName().substring(0, 2)),
                project.getProjectName(),
                project.getCompany());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createContactInfo(Project project) {
        ListItem item = new ListItem(
                project.getEmail(),
                project.getPhone());
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createDescription(Project project) {
        Label label = new Label(project.getDescription());
        label.getElement().getStyle().set("white-space", "pre-wrap");
        return label;
    }

    private Component createApprovalLimit(Project project) {
        return UIUtils.createAmountLabel(project.getTotalAmount().doubleValue());
    }

    private Component createAvailability(Project project) {
        return project.isActive() ? UIUtils.createLabel(TextColor.SUCCESS, "Active") : UIUtils.createLabel(TextColor.ERROR, "Deactive");
    }

    private void viewDetails(Project project) {
        UI.getCurrent().navigate(ProjectsDetails.class, project.getId());
    }

    private DetailsDrawer createDetailsDrawer() {
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawerHeader = new DetailsDrawerHeader("Project Info");
        detailsDrawer.setHeader(detailsDrawerHeader);
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawer.setFooter(footer);
        return detailsDrawer;
    }

    private void showDetails(Project project) {
        detailsDrawerHeader.setText(project.getProjectName());
        detailsDrawer.setContent(createDetails(project));
        detailsDrawer.show();
    }

    private FormLayout createDetails(Project project) {
        TextField firstName = new TextField();
        firstName.setWidth("100%");

        TextField lastName = new TextField();
        lastName.setWidth("100%");

        RadioButtonGroup<String> gender = new RadioButtonGroup<>();
        gender.setItems("Active", "Inactive");
//        gender.setValue(person.getRandomBoolean() ? "Active" : "Inactive");

        FlexLayout phone = UIUtils.createPhoneLayout();

        TextField email = new TextField();
        email.setWidth("100%");

        ComboBox company = new ComboBox();
        company.setItems(DummyData.getCompanies());
        company.setValue(DummyData.getCompany());
        company.setWidth("100%");

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(firstName, "First Name");
        form.addFormItem(lastName, "Last Name");
        form.addFormItem(gender, "Status");
        form.addFormItem(phone, "Phone");
        form.addFormItem(email, "Email");
        form.addFormItem(company, "Company");
        return form;
    }

}

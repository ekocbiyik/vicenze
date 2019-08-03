package com.meyratech.vicenze.ui.views.projects;

import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.ProjectServiceImpl;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawer;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.meyratech.vicenze.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.views.SplitViewFrame;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Route(value = ViewConst.PAGE_PROJECTS, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_PROJECTS)
public class ProjectsView extends SplitViewFrame implements RouterLayout {

    private final ProjectServiceImpl projectService;
    private Grid<Project> projectGrid;
    private Button btnCreate;
    private TextField searchField;
    private ListDataProvider<Project> projectDataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerFooter detailedFooter;
    private TextField txtProjectNumber;
    private TextField txtProjectName;
    private ComboBox<String> cbxCompany;
    private EmailField txtEmail;
    private TextField txtPhone;
    private TextArea txtDescription;
    private RadioButtonGroup<Boolean> rdActive;
    private TextField creationDate;
    private TextField txtCreatedBy;
    private Project detailedProject;
    private Binder<Project> binder;

    @Autowired
    public ProjectsView(ProjectServiceImpl projectService) {
        this.projectService = projectService;

        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(SplitViewFrame.Position.BOTTOM);
    }

    private Component createContent() {
        projectGrid = new Grid<>();
        projectGrid.setSizeFull();
        projectDataProvider = DataProvider.ofCollection(projectService.findAll());
        projectGrid.setDataProvider(projectDataProvider);

        Grid.Column<Project> col0 = projectGrid.addColumn(new ComponentRenderer<>(this::viewDetails)).setHeader("Details").setFlexGrow(0).setWidth(UIUtils.COLUMN_WIDTH_XS);
        Grid.Column<Project> col1 = projectGrid.addColumn(Project::getProjectNumber).setFlexGrow(0).setFrozen(true).setHeader("Project Number").setSortable(true).setComparator(Project::getProjectNumber).setWidth(UIUtils.COLUMN_WIDTH_M);
        Grid.Column<Project> col2 = projectGrid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setFlexGrow(1).setHeader("Project Name").setSortable(true).setComparator(Project::getProjectName).setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Project> col3 = projectGrid.addColumn(new ComponentRenderer<>(this::createContactInfo)).setFlexGrow(1).setHeader("Contact").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Project> col4 = projectGrid.addColumn(new ComponentRenderer<>(this::createDescription)).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_L).setResizable(true);
        Grid.Column<Project> col5 = projectGrid.addColumn(new LocalDateTimeRenderer<>(Project::getCreationDate, DateTimeFormatter.ofPattern("dd MMM YYYY HH:mm:ss"))).setComparator(Project::getCreationDate).setFlexGrow(0).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        Grid.Column<Project> col6 = projectGrid.addColumn(Project::getCreatedBy).setHeader("Created By").setWidth(UIUtils.COLUMN_WIDTH_S).setTextAlign(ColumnTextAlign.START);

        projectGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));

        HeaderRow topRow = projectGrid.prependHeaderRow();
        HeaderRow.HeaderCell buttonsCell = topRow.join(col0, col1, col2, col3, col4, col5, col6);
        buttonsCell.setComponent(getGridHeader());
        projectGrid.appendFooterRow().getCell(projectGrid.getColumns().get(1)).setComponent(new Label("Total: " + projectDataProvider.getItems().size() + " projects"));

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
        searchField.addValueChangeListener(e -> {
            projectDataProvider.addFilter((item) ->
                    StringUtils.containsIgnoreCase(item.getProjectName(), searchField.getValue())
                            || StringUtils.containsIgnoreCase(item.getProjectNumber(), searchField.getValue()));
        });

        btnCreate = UIUtils.createPrimaryButton("ADD", VaadinIcon.PLUS_CIRCLE_O);
        btnCreate.addClickListener(e -> showDetails(null));

        HorizontalLayout container = new HorizontalLayout(btnCreate, searchField);
        container.setSpacing(true);
        container.setSizeFull();
        return container;
    }

    private DetailsDrawer createDetailsDrawer() {
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
        detailsDrawer.setHeader(new DetailsDrawerHeader("Project Info"));
        detailedFooter = new DetailsDrawerFooter();
        detailsDrawer.setFooter(detailedFooter);

        detailedFooter.addCancelListener(e -> detailsDrawer.hide());
        detailedFooter.addSaveListener(e -> saveDetailedProject());
        return detailsDrawer;
    }

    private void showDetails(Project project) {
        detailedProject = project;
        detailsDrawer.setContent(createDetails());
        initializeValidators();
        if (project != null) {
            initializeProjectDetails();
        }
        detailsDrawer.show();
    }

    private FormLayout createDetails() {

        txtProjectNumber = new TextField();
        txtProjectNumber.setWidthFull();
        txtProjectNumber.setEnabled(false);
        txtProjectNumber.setValue(UIUtils.formatId(null));

        txtProjectName = new TextField();
        txtProjectName.setWidthFull();
        txtProjectName.setValueChangeMode(ValueChangeMode.EAGER);
        txtProjectName.addValueChangeListener(e -> txtProjectName.setValue(txtProjectName.getValue().toUpperCase()));

        cbxCompany = new ComboBox();
        cbxCompany.setWidthFull();
        cbxCompany.setAllowCustomValue(true);
        cbxCompany.setItems(projectDataProvider.getItems().stream().map(p -> p.getCompany()).collect(Collectors.toSet()));
        cbxCompany.addCustomValueSetListener(e -> cbxCompany.setValue(e.getDetail().toUpperCase()));

        txtEmail = new EmailField();
        txtEmail.setWidthFull();

        txtPhone = new TextField();
        txtPhone.setWidthFull();

        txtDescription = new TextArea();
        txtDescription.setWidthFull();
        txtDescription.setHeightFull();

        rdActive = new RadioButtonGroup<>();
        rdActive.setItems(true, false);

        creationDate = new TextField();
        creationDate.setEnabled(false);
        creationDate.setWidthFull();
        creationDate.setValue(UIUtils.formatDatetime(LocalDateTime.now()));

        txtCreatedBy = new TextField();
        txtCreatedBy.setWidthFull();
        txtCreatedBy.setEnabled(false);
        txtCreatedBy.setValue(SecurityUtils.getCurrentUser().getFullName());

        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 3, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        form.addFormItem(txtProjectNumber, "Project Number");
        form.addFormItem(txtProjectName, "Project Name");
        form.addFormItem(cbxCompany, "Company");
        form.addFormItem(txtCreatedBy, "Created By");
        form.addFormItem(txtPhone, "Phone");
        form.addFormItem(txtEmail, "Email");
        form.addFormItem(creationDate, "Creation Date");
        form.addFormItem(txtDescription, "Description");
        form.addFormItem(rdActive, "Status");
        return form;
    }

    private void initializeValidators() {
        binder = new Binder<>(Project.class);
        binder.forField(txtProjectName)
                .asRequired("Project name is required!")
                .withValidator(pName -> pName.length() >= 3, "Project name must contain at least 3 characters!")
                .bind(Project::getProjectName, Project::setProjectName);

        binder.forField(cbxCompany)
                .asRequired("Company is required!")
                .withValidator(pName -> pName.length() >= 3, "Company name must contain at least 3 characters!")
                .bind(Project::getCompany, Project::setCompany);

        binder.forField(rdActive)
                .asRequired("Please select status!")
                .bind(Project::isActive, Project::setActive);

        binder.forField(txtPhone)
                .bind(Project::getPhone, Project::setPhone);

        binder.forField(txtEmail)
                .asRequired("Email is required!")
                .withValidator(new EmailValidator("Invalid email address!"))
                .bind(Project::getEmail, Project::setEmail);

        binder.forField(txtDescription)
                .bind(Project::getDescription, Project::setDescription);

        detailedFooter.getSaveButton().setEnabled(false);
        binder.readBean(detailedProject == null ? new Project() : detailedProject);
        binder.addStatusChangeListener(status -> detailedFooter.getSaveButton().setEnabled(!status.hasValidationErrors()));
    }

    private void initializeProjectDetails() {
        txtProjectNumber.setValue(detailedProject.getProjectNumber());
        txtProjectName.setValue(detailedProject.getProjectName().toUpperCase());
        cbxCompany.setValue(detailedProject.getCompany());
        rdActive.setValue(detailedProject.isActive());
        txtPhone.setValue(detailedProject.getPhone());
        txtEmail.setValue(detailedProject.getEmail());
        txtDescription.setValue(detailedProject.getDescription());
        txtCreatedBy.setValue(detailedProject.getCreatedBy());
        creationDate.setValue(UIUtils.formatDatetime(detailedProject.getCreationDate()));
    }

    private void saveDetailedProject() {
        binder.validate();
        if (!binder.isValid()) {
            detailedFooter.getSaveButton().setEnabled(false);
            return;
        }

        if (detailedProject == null) {
            detailedProject = new Project();
            detailedProject.setProjectNumber(txtProjectNumber.getValue());
            detailedProject.setCreatedBy(SecurityUtils.getCurrentUser().getFullName());
        }

        detailedProject.setProjectName(txtProjectName.getValue());
        detailedProject.setCompany(cbxCompany.getValue().toUpperCase());
        detailedProject.setActive(rdActive.getValue());
        detailedProject.setPhone(txtPhone.getValue());
        detailedProject.setEmail(txtEmail.getValue());
        detailedProject.setDescription(txtDescription.getValue());

        try {
            projectService.save(detailedProject);
        } catch (Exception e) {
            Notification.show("Opps! Please check your fields!", 3000, Notification.Position.TOP_END);
            return;
        }

        Notification.show("Successfull", 6000, Notification.Position.TOP_END);
        detailsDrawer.hide();
        detailedProject = null;
        projectDataProvider = DataProvider.ofCollection(projectService.findAll());
        projectGrid.setDataProvider(projectDataProvider);
    }

    private Component createProjectInfo(Project project) {
        ListItem item = new ListItem(
                UIUtils.createInitials(project.getProjectName().substring(0, 2)),
                project.getProjectName(),
                project.getCompany(),
                project.isActive() ? UIUtils.createPrimaryIcon(VaadinIcon.CHECK) : UIUtils.createDisabledIcon(VaadinIcon.CLOSE)
        );
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

//    private Component createApprovalLimit(Project project) {
//        return UIUtils.createAmountLabel(project.getTotalAmount().doubleValue());
//    }

    private Component viewDetails(Project project) {
        return UIUtils.createButton(
                VaadinIcon.LINE_BAR_CHART,
                (ComponentEventListener<ClickEvent<Button>>) e -> UI.getCurrent().navigate(ProjectsDetails.class, project.getId())
        );
    }

}

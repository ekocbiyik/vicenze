package com.meyratech.vicenze.ui.views.projects;

import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.ProjectServiceImpl;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.layout.size.Horizontal;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.layout.size.Vertical;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.TextColor;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.views.ViewFrame;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
public class ProjectsView extends ViewFrame implements RouterLayout {

    private final ProjectServiceImpl projectService;
    private Grid<Project> grid;
    private Button btnCreate;
    private TextField searchField;
    private ListDataProvider<Project> dataProvider;

    @Autowired
    public ProjectsView(ProjectServiceImpl projectService) {
        this.projectService = projectService;
        setViewHeader(createSearchBar());
        setViewContent(createContent());
        setViewFooter(createFooter());
    }

    private Component createSearchBar() {

        searchField = new TextField();
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> {
            dataProvider.addFilter(project -> StringUtils.containsIgnoreCase(project.getProjectName(), searchField.getValue()));
        });

        FlexBoxLayout container = new FlexBoxLayout(searchField);
        container.addClassName("app-bar__container");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setFlexGrow(1, searchField);
        return container;
    }

    private Component createContent() {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setId("projects");
        dataProvider = DataProvider.ofCollection(projectService.findAll());
        grid.setDataProvider(dataProvider);

        grid.addColumn(Project::getId).setFlexGrow(0).setFrozen(true).setHeader("ID").setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_XS);
        grid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setHeader("Project Name").setWidth(UIUtils.COLUMN_WIDTH_XL).setSortable(true).setComparator(Project::getProjectName);
        grid.addColumn(Project::getDescription).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_XL).setResizable(true);
        grid.addColumn(new LocalDateTimeRenderer<>(Project::getCreationDate, DateTimeFormatter.ofPattern("MMM dd, YYYY HH:mm:ss"))).setComparator(Project::getCreationDate).setFlexGrow(0).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        grid.addColumn(Project::getCreatedBy).setHeader("Created By").setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createAvailability)).setFlexGrow(0).setHeader("Availability").setWidth(UIUtils.COLUMN_WIDTH_S).setSortable(true).setComparator(Project::isActive).setTextAlign(ColumnTextAlign.CENTER);

        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::viewDetails));

        Div content = new Div(grid);
        content.addClassName("grid-view");
        return content;
    }

    private Component createFooter() {
        FlexBoxLayout footer = new FlexBoxLayout();
        footer.setBackgroundColor(LumoStyles.Color.Contrast._5);
        footer.setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
        footer.setSpacing(Right.S);
        footer.setWidth("100%");
        btnCreate = UIUtils.createPrimaryButton("New Project");
        btnCreate.addClickListener(e -> openNewProjectView(e));
        footer.add(btnCreate);
        return footer;
    }

    public void openNewProjectView(ClickEvent<Button> e) {
        Notification.show("not implemented!", 3000, Notification.Position.BOTTOM_END);
    }

    private Component createProjectInfo(Project project) {
        ListItem item = new ListItem(project.getProjectName(), " ");
        item.setHorizontalPadding(false);
        return item;
    }

    private Component createAvailability(Project project) {
        return project.isActive() ? UIUtils.createLabel(TextColor.SUCCESS, "Active") : UIUtils.createLabel(TextColor.ERROR, "Deactive");
    }

    private void viewDetails(Project project) {
        UI.getCurrent().navigate(ProjectsDetails.class, project.getId());
    }

}

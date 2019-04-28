package com.meyratech.vicenze.ui.views.projects;

import com.meyratech.vicenze.backend.model.Project;
import com.meyratech.vicenze.backend.repository.service.ProjectServiceImpl;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.util.TextColor;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@Route(value = ViewConst.PAGE_PROJECTS, layout = MainLayout.class)
@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_PROJECTS)
public class ProjectsView extends ViewFrame implements RouterLayout {

    private final ProjectServiceImpl projectService;
    private Grid<Project> grid;


    @Autowired
    public ProjectsView(ProjectServiceImpl projectService) {
        this.projectService = projectService;
        setViewContent(createContent());
    }

    private Component createContent() {
        Div content = new Div(createGrid());
        content.addClassName("grid-view");
        return content;
    }

    private Grid createGrid() {
        grid = new Grid<>();
        grid.setId("projects");
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::viewDetails));
        grid.setDataProvider(DataProvider.ofCollection(projectService.findAll()));
        grid.setSizeFull();

        grid.addColumn(Project::getId).setFlexGrow(0).setFrozen(true).setHeader("ID").setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_XS);
        grid.addColumn(new ComponentRenderer<>(this::createProjectInfo)).setHeader("Project Name").setWidth(UIUtils.COLUMN_WIDTH_XL).setSortable(true).setComparator(Project::getProjectName);
        grid.addColumn(Project::getDescription).setHeader("Description").setWidth(UIUtils.COLUMN_WIDTH_XL).setResizable(true);
        grid.addColumn(new LocalDateTimeRenderer<>(Project::getCreationDate, DateTimeFormatter.ofPattern("MMM dd, YYYY HH:mm:ss"))).setComparator(Project::getCreationDate).setFlexGrow(0).setHeader("Creation Date").setWidth(UIUtils.COLUMN_WIDTH_L);
        grid.addColumn(Project::getCreatedBy).setHeader("Created By").setWidth(UIUtils.COLUMN_WIDTH_S).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(this::createAvailability)).setFlexGrow(0).setHeader("Availability").setWidth(UIUtils.COLUMN_WIDTH_S).setSortable(true).setComparator(Project::isActive).setTextAlign(ColumnTextAlign.CENTER);

        return grid;
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

package com.meyratech.vicenze.ui.views.home;

import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.service.IUserService;
import com.meyratech.vicenze.backend.security.SecurityUtils;
import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.ViewFrame;
import com.meyratech.vicenze.ui.layout.size.Bottom;
import com.meyratech.vicenze.ui.layout.size.Top;
import com.meyratech.vicenze.ui.layout.size.*;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.util.css.BorderRadius;
import com.meyratech.vicenze.ui.util.css.FlexDirection;
import com.meyratech.vicenze.ui.util.css.Shadow;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = ViewConst.PAGE_HOME, layout = MainLayout.class)
@PageTitle(ViewConst.TITLE_HOME)
public class HomeView extends ViewFrame {


    private static final String CLASS_NAME = "dashboard";
    public static final String MAX_WIDTH = "1024px";
    private IUserService userService;


    @Autowired
    public HomeView(IUserService userService) {
        this.userService = userService;
        setViewContent(createContent());
    }

    private Component createContent() {

        FlexBoxLayout content = new FlexBoxLayout(
                createNotifications()
//                createReports()
        );

        content.setFlexDirection(FlexDirection.COLUMN);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_X);
        content.setMaxWidth("840px");
        content.setBorderRadius(BorderRadius.S);
        content.setBackgroundColor(LumoStyles.Color.BASE_COLOR);

        return content;
    }

    private Component createNotifications() {

        List<User> allUser = userService.findAll();
        List<User> activeUsers = allUser.stream().filter(user -> user.isActive()).collect(Collectors.toList());
        List<User> passiveUsers = allUser.stream().filter(user -> !user.isActive()).collect(Collectors.toList());
        List<User> lockedUsers = allUser.stream().filter(user -> user.isLocked()).collect(Collectors.toList());
        List<String> onlineUsers = SecurityUtils.getOnlineUsers();

        // grid content
        Grid<String> userGrid = new Grid<>();
        userGrid.setHeight("300px");
        userGrid.setMinWidth("400px");
        ListDataProvider<String> dataProvider = DataProvider.ofCollection(SecurityUtils.getOnlineUsers());
        userGrid.setDataProvider(dataProvider);
        userGrid.addColumn(new ComponentRenderer<>(this::viewDetails)).setWidth("40px").setFrozen(true);
        userGrid.addColumn(new ComponentRenderer<>(this::createUserInfo)).setFlexGrow(1).setSortable(true).setWidth(UIUtils.COLUMN_WIDTH_XL);

        Div gridContent = new Div(userGrid);
        gridContent.addClassName("grid-view");


        // user Chart
        Chart userChart = new Chart(ChartType.PIE);
        userChart.setSizeUndefined();
        userChart.setHeight("300px");
        userChart.setVisibilityTogglingDisabled(false);

        Configuration conf = userChart.getConfiguration();
        conf.setTooltip(new Tooltip());

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        conf.setPlotOptions(plotOptions);

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Passive Users", passiveUsers.size(), 1)); //siyah
        series.add(new DataSeriesItem("Online Users", onlineUsers.size(), 2)); //yeşil
        series.add(new DataSeriesItem("Active Users", activeUsers.size(), 3)); //turuncu
        series.add(new DataSeriesItem("Locked Users", lockedUsers.size(), 5)); // kırmızı
        conf.setSeries(series);

        userChart.addPointSelectListener(event -> {
                    ListDataProvider<String> provider;
                    if (event.getItem().getName().equals("Passive Users")) {
                        provider = DataProvider.ofCollection(passiveUsers.stream().map(User::getEmail).collect(Collectors.toList()));
                    } else if (event.getItem().getName().equals("Locked Users")) {
                        provider = DataProvider.ofCollection(lockedUsers.stream().map(User::getEmail).collect(Collectors.toList()));
                    } else if (event.getItem().getName().equals("Active Users")) {
                        provider = DataProvider.ofCollection(activeUsers.stream().map(User::getEmail).collect(Collectors.toList()));
                    } else {
                        provider = DataProvider.ofCollection(SecurityUtils.getOnlineUsers());
                    }
                    userGrid.setDataProvider(provider);
                }
        );

        FlexBoxLayout layout = new FlexBoxLayout();
        layout.setSizeFull();
        layout.addClassName(CLASS_NAME + "__payment-chart");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setBackgroundColor(LumoStyles.Color.BASE_COLOR);
        layout.setShadow(Shadow.S);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setPadding(Top.L, Right.S, Bottom.L, Left.S);
        layout.add(userChart, gridContent);
        return layout;
    }

    private Component viewDetails(String username) {
        return UIUtils.createButton(
                VaadinIcon.INFO,
                (ComponentEventListener<ClickEvent<Button>>) e -> UIUtils.showNotification("Not implemented yet!")
        );
    }

    private Component createUserInfo(String user) {
        ListItem item = new ListItem(
                UIUtils.createInitials(user.substring(0, 1)),
                user
        );
        item.setHorizontalPadding(false);
        return item;
    }

}

package com.meyratech.vicenze.ui.views.home;

import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.components.DataSeriesItemWithRadius;
import com.meyratech.vicenze.ui.components.FlexBoxLayout;
import com.meyratech.vicenze.ui.components.ListItem;
import com.meyratech.vicenze.ui.components.ViewFrame;
import com.meyratech.vicenze.ui.layout.size.Bottom;
import com.meyratech.vicenze.ui.layout.size.Top;
import com.meyratech.vicenze.ui.layout.size.*;
import com.meyratech.vicenze.ui.util.*;
import com.meyratech.vicenze.ui.util.css.Position;
import com.meyratech.vicenze.ui.util.css.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.LabelStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "home2", layout = MainLayout.class)
@PageTitle(ViewConst.TITLE_HOME)
public class HomeView2 extends ViewFrame {


    private static final String CLASS_NAME = "dashboard";
    public static final String MAX_WIDTH = "1024px";

    public HomeView2() {
        setViewContent(createContent());
    }

    private Component createContent() {

        FlexBoxLayout content = new FlexBoxLayout(
                createNotifications(),
                createTransactions(),
                createPieChart(),
                createMixedChart(),
                createDocs()
        );
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setFlexDirection(FlexDirection.COLUMN);
        return content;
    }

    private Component createNotifications() {
        FlexBoxLayout payments = new FlexBoxLayout(createHeader(VaadinIcon.OPEN_BOOK, "Notifications"), createPaymentsCharts());
        payments.setBoxSizing(BoxSizing.BORDER_BOX);
        payments.setDisplay(Display.BLOCK);
        payments.setMargin(Top.L);
        payments.setMaxWidth(MAX_WIDTH);
        payments.setPadding(Horizontal.RESPONSIVE_L);
        payments.setWidth("100%");
        return payments;
    }

    private FlexBoxLayout createHeader(VaadinIcon icon, String title) {
        FlexBoxLayout header = new FlexBoxLayout(UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, icon), UIUtils.createH3Label(title));
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.L, Horizontal.RESPONSIVE_L);
        header.setSpacing(Right.L);
        return header;
    }

    private Component createPaymentsCharts() {
        Row charts = new Row();
        UIUtils.setBackgroundColor(LumoStyles.Color.BASE_COLOR, charts);
        UIUtils.setBorderRadius(BorderRadius.S, charts);
        UIUtils.setShadow(Shadow.S, charts);

        charts.add(createPaymentChart("PENDING"));
        charts.add(createPaymentChart("SUBMITTED"));
        charts.add(createPaymentChart("CONFIRMED"));
        return charts;
    }

    private Component createPaymentChart(String status) {
        int value = 5;
        switch (status) {
            case "PENDING":
                value = 24;
                break;

            case "SUBMITTED":
                value = 40;
                break;

            case "CONFIRMED":
                value = 32;
                break;

            default:
                value = 4;
                break;
        }

        FlexBoxLayout textContainer = new FlexBoxLayout(UIUtils.createH2Label(Integer.toString(61)), UIUtils.createLabel(FontSize.S, "%"));
        textContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        textContainer.setPosition(Position.ABSOLUTE);
        textContainer.setSpacing(Right.XS);

        Chart chart = createProgressChart(61, status);
        FlexBoxLayout chartContainer = new FlexBoxLayout(chart, textContainer);
        chartContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        chartContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        chartContainer.setPosition(Position.RELATIVE);
        chartContainer.setHeight("120px");
        chartContainer.setWidth("120px");

        FlexBoxLayout paymentChart = new FlexBoxLayout(new Label("enbiya"), chartContainer);
        paymentChart.addClassName(CLASS_NAME + "__payment-chart");
        paymentChart.setAlignItems(FlexComponent.Alignment.CENTER);
        paymentChart.setFlexDirection(FlexDirection.COLUMN);
        paymentChart.setPadding(Bottom.S, Top.M);
        return paymentChart;
    }

    private Chart createProgressChart(int value, String status) {
        Chart chart = new Chart();
        chart.addClassName(status.toLowerCase());
        chart.setSizeFull();

        Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.SOLIDGAUGE);
        configuration.setTitle("");
        configuration.getTooltip().setEnabled(false);

        configuration.getyAxis().setMin(0);
        configuration.getyAxis().setMax(100);
        configuration.getyAxis().getLabels().setEnabled(false);

        PlotOptionsSolidgauge opt = new PlotOptionsSolidgauge();
        opt.getDataLabels().setEnabled(false);
        configuration.setPlotOptions(opt);

        DataSeriesItemWithRadius point = new DataSeriesItemWithRadius();
        point.setY(value);
        point.setInnerRadius("100%");
        point.setRadius("110%");
        configuration.setSeries(new DataSeries(point));

        Pane pane = configuration.getPane();
        pane.setStartAngle(0);
        pane.setEndAngle(360);

        Background background = new Background();
        background.setShape(BackgroundShape.ARC);
        background.setInnerRadius("100%");
        background.setOuterRadius("110%");
        pane.setBackground(background);

        return chart;
    }

    private Component createTransactions() {
        FlexBoxLayout transactions = new FlexBoxLayout(createHeader(VaadinIcon.MONEY_EXCHANGE, "Transactions"), createAreaChart());
        transactions.setBoxSizing(BoxSizing.BORDER_BOX);
        transactions.setDisplay(Display.BLOCK);
        transactions.setMargin(Top.XL);
        transactions.setMaxWidth(MAX_WIDTH);
        transactions.setPadding(Horizontal.RESPONSIVE_L);
        transactions.setWidth("100%");
        return transactions;
    }

    private Component createAreaChart() {
        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("2019");
        conf.setExporting(true);
        conf.getLegend().setEnabled(false);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Number of Processed Transactions");

        conf.addSeries(new ListSeries(220, 240, 400, 360, 420, 640, 580, 800, 600, 580, 740, 800));

        FlexBoxLayout card = new FlexBoxLayout(chart);
        card.setBorderRadius(BorderRadius.S);
        card.setBackgroundColor(LumoStyles.Color.BASE_COLOR);
        card.setBoxSizing(BoxSizing.BORDER_BOX);
        card.setHeight("400px");
        card.setPadding(Uniform.M);
        card.setShadow(Shadow.S);
        return card;
    }

    private Component createPieChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setExporting(true);
        conf.setTitle("Browser market shares in January, 2018");

        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(1);
        conf.setTooltip(tooltip);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        conf.setPlotOptions(plotOptions);

        DataSeries series = new DataSeries();
        DataSeriesItem chrome = new DataSeriesItem("Chrome", 61.41);
        chrome.setSliced(true);
        chrome.setSelected(true);
        series.add(chrome);
        series.add(new DataSeriesItem("Internet Explorer", 11.84));
        series.add(new DataSeriesItem("Firefox", 10.85));
        series.add(new DataSeriesItem("Edge", 4.67));
        series.add(new DataSeriesItem("Safari", 4.18));
        series.add(new DataSeriesItem("Sogou Explorer", 1.64));
        series.add(new DataSeriesItem("Opera", 6.2));
        series.add(new DataSeriesItem("QQ", 1.2));
        series.add(new DataSeriesItem("Others", 2.61));
        conf.setSeries(series);
        chart.setVisibilityTogglingDisabled(true);

        chart.addPointLegendItemClickListener(event -> UIUtils.showNotification("Legend item click" + " : " + event.getItemIndex() + " : " + event.getItem().getName()));
        return new FlexBoxLayout(createHeader(VaadinIcon.MONEY_EXCHANGE, "Transactions"), chart);
    }

    public Component createMixedChart() {
        Chart chart = new Chart();
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Combined Chart");
        conf.setExporting(true);

        XAxis x = new XAxis();
        x.setCategories(new String[]{"Apples", "Oranges", "Pears", "Bananas", "Plums"});
        conf.addxAxis(x);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.setTop("8px");
        labelStyle.setLeft("40px");
        conf.setLabels(new HTMLLabels(labelStyle, new HTMLLabelItem("Total fruit consumption")));

        DataSeries series = new DataSeries();
        series.setPlotOptions(new PlotOptionsColumn());
        series.setName("Jane");
        series.setData(3, 2, 1, 3, 4);
        conf.addSeries(series);

        series = new DataSeries();
        series.setPlotOptions(new PlotOptionsColumn());
        series.setName("John");
        series.setData(2, 3, 5, 7, 6);
        conf.addSeries(series);

        series = new DataSeries();
        series.setPlotOptions(new PlotOptionsColumn());
        series.setName("Joe");
        series.setData(4, 3, 3, 9, 0);
        conf.addSeries(series);

        series = new DataSeries();
        series.setPlotOptions(new PlotOptionsSpline());
        series.setName("Average");
        series.setData(3, 2.67, 3, 6.33, 3.33);
        conf.addSeries(series);

        series = new DataSeries();
        series.setPlotOptions(new PlotOptionsPie());
        series.setName("Total consumption");
        series.add(new DataSeriesItem("Jane", 13));
        series.add(new DataSeriesItem("John", 23));
        series.add(new DataSeriesItem("Joe", 19));

        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        plotOptionsPie.setSize("100px");
        plotOptionsPie.setCenter("100px", "80px");
        plotOptionsPie.setShowInLegend(false);
        series.setPlotOptions(plotOptionsPie);
        conf.addSeries(series);

        FlexBoxLayout card = new FlexBoxLayout(chart);
        card.setBorderRadius(BorderRadius.S);
        card.setBackgroundColor(LumoStyles.Color.BASE_COLOR);
        card.setBoxSizing(BoxSizing.BORDER_BOX);
        card.setHeight("400px");
        card.setPadding(Uniform.M);
        card.setShadow(Shadow.S);


        FlexBoxLayout transactions = new FlexBoxLayout(createHeader(VaadinIcon.CALC_BOOK, "Statistics"), card);
        transactions.setBoxSizing(BoxSizing.BORDER_BOX);
        transactions.setDisplay(Display.BLOCK);
        transactions.setMargin(Top.XL);
        transactions.setMaxWidth(MAX_WIDTH);
        transactions.setPadding(Horizontal.RESPONSIVE_L);
        transactions.setWidth("100%");

        return transactions;

    }

    private Component createDocs() {
        Component reports = createReports();
        Component logs = createLogs();

        Row docs = new Row(reports, logs);
        docs.addClassName(LumoStyles.Margin.Top.XL);
        UIUtils.setMaxWidth(MAX_WIDTH, docs);
        docs.setWidth("100%");

        return docs;
    }

    private Component createReports() {
        FlexBoxLayout header = createHeader(VaadinIcon.RECORDS, "Reports");

        Tabs tabs = new Tabs();
        for (String label : new String[]{"All", "Archive", "Workflows", "Support"}) {
            tabs.add(new Tab(label));
        }

        Div items = new Div(
                new ListItem(UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, VaadinIcon.CHART), "Weekly Report", "Generated Oct 5, 2018", createInfoButton()),
                new ListItem(UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, VaadinIcon.SITEMAP), "Payment Workflows", "Last modified Oct 24, 2018", createInfoButton()));
        items.addClassNames(LumoStyles.Padding.Vertical.S);

        Div card = new Div(tabs, items);
        UIUtils.setBackgroundColor(LumoStyles.Color.BASE_COLOR, card);
        UIUtils.setBorderRadius(BorderRadius.S, card);
        UIUtils.setShadow(Shadow.S, card);

        FlexBoxLayout reports = new FlexBoxLayout(header, card);
        reports.addClassName(CLASS_NAME + "__reports");
        reports.setFlexDirection(FlexDirection.COLUMN);
        reports.setPadding(Bottom.XL, Left.RESPONSIVE_L);
        return reports;
    }

    private Component createLogs() {
        FlexBoxLayout header = createHeader(VaadinIcon.EDIT, "Logs");

        Tabs tabs = new Tabs();
        for (String label : new String[]{"All", "Transfer", "Security", "Change"}) {
            tabs.add(new Tab(label));
        }

        Div items = new Div(
                new ListItem(UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, VaadinIcon.EXCHANGE), "Transfers (October)", "Generated Oct 31, 2018", createInfoButton()),
                new ListItem(UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, VaadinIcon.SHIELD), "Security Log", "Updated 16:31 CET", createInfoButton()));
        items.addClassNames(LumoStyles.Padding.Vertical.S);

        Div card = new Div(tabs, items);
        UIUtils.setBackgroundColor(LumoStyles.Color.BASE_COLOR, card);
        UIUtils.setBorderRadius(BorderRadius.S, card);
        UIUtils.setShadow(Shadow.S, card);

        FlexBoxLayout logs = new FlexBoxLayout(header, card);
        logs.addClassName(CLASS_NAME + "__logs");
        logs.setFlexDirection(FlexDirection.COLUMN);
        logs.setPadding(Bottom.XL, Right.RESPONSIVE_L);
        return logs;
    }

    private Button createInfoButton() {
        Button infoButton = UIUtils.createSmallButton(VaadinIcon.INFO);
        infoButton.addClickListener(e -> UIUtils.showNotification("Not implemented yet."));
        return infoButton;
    }


}

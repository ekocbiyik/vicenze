package com.meyratech.vicenze.ui.views.errors;

import com.meyratech.vicenze.ui.MainLayout;
import com.meyratech.vicenze.ui.util.ViewConst;
import com.meyratech.vicenze.ui.views.HomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.*;

import javax.servlet.http.HttpServletResponse;

@ParentLayout(MainLayout.class)
@PageTitle(ViewConst.TITLE_NOT_FOUND)
@HtmlImport("styles/shared-styles.html")
public class CustomRouteNotFoundError extends RouteNotFoundError {

    public CustomRouteNotFoundError() {
        RouterLink link = Component.from(ElementFactory.createRouterLink(ViewConst.PAGE_HOME, "Go to the front page."), RouterLink.class);
        getElement().appendChild(new Text("Oops you hit a 404. ").getElement(), link.getElement());
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        if (event.getLocation().getPath().isEmpty()) {
            // login oldugunda url redirect etmiyor, bu nedenle eklendi
            UI.getCurrent().getPage().getHistory().replaceState(null, ViewConst.PAGE_HOME);
            event.rerouteTo(HomeView.class);
            return HttpServletResponse.SC_OK;
        } else {
            return HttpServletResponse.SC_NOT_FOUND;
        }
    }

}

package com.meyratech.vicenze.ui.components.navigation.drawer;

import com.meyratech.vicenze.ui.util.UIUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;

public class BrandExpression extends Composite<Div> {

    public BrandExpression(String text) {
        String CLASS_NAME = "brand-expression";
        getContent().setClassName(CLASS_NAME);

        Image logo = new Image(UIUtils.IMG_PATH + "logo-18.png", "");
        logo.addClassName(CLASS_NAME + "__logo");
        logo.setAlt(text + " logo");

        Label title = UIUtils.createH3Label(text);
        title.addClassName(CLASS_NAME + "__title");
        getContent().add(logo, title);
    }

}

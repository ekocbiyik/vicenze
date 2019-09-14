package com.meyratech.vicenze.ui.components;

import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class Divider extends Composite<FlexLayout> {


    public Divider(String height) {
        this(FlexComponent.Alignment.CENTER, height);
    }

    public Divider(FlexComponent.Alignment alignItems, String height) {
        String CLASS_NAME = "divider";
        getContent().addClassName(CLASS_NAME);

        getContent().setAlignItems(alignItems);
        getContent().setHeight(height);

        Div divider = new Div();
        UIUtils.setBackgroundColor(LumoStyles.Color.Contrast._20, divider);
        divider.setHeight("1px");
        divider.setWidth("100%");
        getContent().add(divider);
    }

}

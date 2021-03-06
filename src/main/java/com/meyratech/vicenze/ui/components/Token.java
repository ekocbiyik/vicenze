package com.meyratech.vicenze.ui.components;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ICON;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.meyratech.vicenze.ui.layout.size.Left;
import com.meyratech.vicenze.ui.layout.size.Right;
import com.meyratech.vicenze.ui.util.FontSize;
import com.meyratech.vicenze.ui.util.LumoStyles;
import com.meyratech.vicenze.ui.util.TextColor;
import com.meyratech.vicenze.ui.util.UIUtils;
import com.meyratech.vicenze.ui.util.css.BorderRadius;
import com.meyratech.vicenze.ui.util.css.Display;

public class Token extends Composite<FlexBoxLayout> implements HasStyle {

    public Token(String text) {
        String CLASS_NAME = "token";
        addClassName(CLASS_NAME);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setBackgroundColor(LumoStyles.Color.Primary._10);
        getContent().setBorderRadius(BorderRadius.M);
        getContent().setDisplay(Display.INLINE_FLEX);
        getContent().setPadding(Left.S);
        getContent().setPadding(Right.XS);
        getContent().setSpacing(Right.XS);

        Label label = UIUtils.createLabel(FontSize.S, TextColor.BODY, text);
        Button button = UIUtils.createButton(VaadinIcon.CLOSE_SMALL, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
        button.getThemeNames().remove(LUMO_ICON.getVariantName());

        getContent().add(label, button);
    }

}

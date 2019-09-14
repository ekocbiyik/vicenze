package com.meyratech.vicenze.ui.layout.size;

public interface Size {

    // Margins and paddings can have multiple attributes (e.g. horizontal and
    // vertical)
    String[] getMarginAttributes();

    String[] getPaddingAttributes();

    // Spacing is applied via the class attribute
    String getSpacingClassName();

    // Returns the size variable (Lumo custom property)
    String getVariable();

}

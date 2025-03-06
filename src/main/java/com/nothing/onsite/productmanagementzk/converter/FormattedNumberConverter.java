package com.nothing.onsite.productmanagementzk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.text.DecimalFormat;

public class FormattedNumberConverter implements Converter<String, Number, Component> {

    @Override
    public Number coerceToBean(String value, Component component, BindContext ctx) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String coerceToUi(Number value, Component component, BindContext ctx) {
        if (value == null) {
            return "0";
        }
        String format = (String) ctx.getConverterArg("format");
        if (format == null) {
            format = "###,###";
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format(value);
    }
} 
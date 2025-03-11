package com.nothing.onsite.productmanagementzk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormattedDateConverter implements Converter<String, Date, Component> {

    @Override
    public Date coerceToBean(String value, Component component, BindContext ctx) {
        // Không cần thiết cho ứng dụng này
        return null;
    }

    @Override
    public String coerceToUi(Date value, Component component, BindContext ctx) {
        if (value == null) {
            return "";
        }
        String format = (String) ctx.getConverterArg("format");
        if (format == null) {
            format = "dd/MM/yyyy HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(value);
    }
} 
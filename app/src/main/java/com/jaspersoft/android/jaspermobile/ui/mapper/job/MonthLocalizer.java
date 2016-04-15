package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class MonthLocalizer implements EntityLocalizer<Integer> {
    public MonthLocalizer() {
    }

    @NonNull
    @Override
    public String localize(@NonNull Integer type) {
        Locale currentLocale = SupportedLocales.INSTANCE.getCurrentLocale();
        String[] namesOfMonths = DateFormatSymbols.getInstance(currentLocale).getMonths();
        return namesOfMonths[type];
    }
}

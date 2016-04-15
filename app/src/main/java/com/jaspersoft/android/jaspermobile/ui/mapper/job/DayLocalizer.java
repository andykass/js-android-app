package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class DayLocalizer implements EntityLocalizer<Integer> {

    public DayLocalizer() {
    }

    @NonNull
    @Override
    public String localize(@NonNull Integer type) {
        Locale currentLocale = SupportedLocales.INSTANCE.getCurrentLocale();
        String[] namesOfDays = DateFormatSymbols.getInstance(currentLocale).getWeekdays();
        return namesOfDays[type];
    }
}

package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author Tom Koptel
 * @since 2.5
 */
enum SupportedLocales {
    INSTANCE;

    private final List<Locale> SUPPORTED_LOCALES = new LinkedList<>();

    SupportedLocales() {
        SUPPORTED_LOCALES.add(Locale.ENGLISH);
        SUPPORTED_LOCALES.add(Locale.GERMAN);
        SUPPORTED_LOCALES.add(new Locale("es"));
        SUPPORTED_LOCALES.add(Locale.FRENCH);
        SUPPORTED_LOCALES.add(Locale.CHINA);
        SUPPORTED_LOCALES.add(Locale.ITALIAN);
        SUPPORTED_LOCALES.add(Locale.JAPANESE);
        SUPPORTED_LOCALES.add(new Locale("pt", "BR"));
    }

    public Locale getCurrentLocale() {
        Locale currentLocale = Locale.getDefault();
        if (SUPPORTED_LOCALES.contains(currentLocale)) {
            return currentLocale;
        }
        return Locale.ENGLISH;
    }
}

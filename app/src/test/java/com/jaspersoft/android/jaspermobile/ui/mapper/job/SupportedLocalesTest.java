package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@RunWith(JUnitParamsRunner.class)
public class SupportedLocalesTest {
    @Test
    public void should_return_english_locale_if_current_not_supported() throws Exception {
        Locale.setDefault(Locale.KOREAN);
        Locale currentLocale = SupportedLocales.INSTANCE.getCurrentLocale();
        assertThat(currentLocale, is(Locale.ENGLISH));
    }

    @Test
    @Parameters({
            "en",
            "de",
            "es",
            "fr",
            "zh-CN",
            "it",
            "ja",
            "pt-BR",
    })
    public void should_return_supported_locale(String languageTag) throws Exception {
        Locale locale = Locale.forLanguageTag(languageTag);
        Locale.setDefault(locale);
        Locale currentLocale = SupportedLocales.INSTANCE.getCurrentLocale();
        assertThat(currentLocale, is(locale));
    }
}
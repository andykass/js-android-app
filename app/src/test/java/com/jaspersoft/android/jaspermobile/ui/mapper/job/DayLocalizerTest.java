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
public class DayLocalizerTest {
    @Test
    @Parameters({
            "1, Sunday",
            "2, Monday",
            "3, Tuesday",
            "4, Wednesday",
            "5, Thursday",
            "6, Friday",
            "7, Saturday",
    })
    public void testLocalize(int day, String dayName) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        DayLocalizer localizer = new DayLocalizer();
        String localize = localizer.localize(day);
        assertThat(localize, is(dayName));
    }
}
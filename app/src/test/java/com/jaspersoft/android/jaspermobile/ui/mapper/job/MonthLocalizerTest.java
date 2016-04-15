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
public class MonthLocalizerTest {
    @Test
    @Parameters({
            "0, January",
            "1, February",
            "2, March",
            "3, April",
            "4, May",
            "5, June",
            "6, July",
            "7, August",
            "8, September",
            "9, October",
            "10, November",
            "11, December"
    })
    public void testLocalize(int day, String dayName) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        MonthLocalizer localizer = new MonthLocalizer();
        String localize = localizer.localize(day);
        assertThat(localize, is(dayName));
    }
}
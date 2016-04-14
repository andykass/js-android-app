package com.jaspersoft.android.jaspermobile.ui.entity.job;

import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JobFormViewEntityTest {
    @Test
    public void testSerializationIntoParcelabel() throws Exception {
        SimpleViewRecurrence.Unit unit = SimpleViewRecurrence.Unit.create("DAY", "dia");
        SimpleViewRecurrence recurrence = SimpleViewRecurrence.builder()
                .interval(100)
                .occurrence(200)
                .localizedLabel("Localized")
                .untilDate(new Date())
                .unit(unit)
                .build();

        Bundle bundle = new Bundle();
        bundle.putParcelable("parcel", recurrence);

        SimpleViewRecurrence deserialized = bundle.getParcelable("parcel");

        assertThat(deserialized.interval(), is(recurrence.interval()));
        assertThat(deserialized.occurrence(), is(recurrence.occurrence()));
        assertThat(deserialized.localizedLabel(), is(recurrence.localizedLabel()));
        assertThat(deserialized.untilDate(), is(recurrence.untilDate()));
        assertThat(deserialized.unit(), is(unit));
    }
}
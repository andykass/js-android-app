package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiCalendarDayMapperTest {

    public static final String LOCALIZED_MONTH = "Localized month";
    public static final int MONDAY = Calendar.MONDAY;
    @Mock
    EntityLocalizer<Integer> localizer;
    private JobUiCalendarDayMapper monthMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        monthMapper = new JobUiCalendarDayMapper(localizer);
        when(localizer.localize(anyInt())).thenReturn(LOCALIZED_MONTH);
    }

    @Test
    public void testToUiEntity() throws Exception {
        CalendarViewRecurrence.Day uiEntity = CalendarViewRecurrence.Day.create(LOCALIZED_MONTH, MONDAY);
        Integer domainEntity = monthMapper.toDomainEntity(uiEntity);
        assertThat(domainEntity, is(MONDAY));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        CalendarViewRecurrence.Day day = monthMapper.toUiEntity(MONDAY);

        verify(localizer).localize(MONDAY);
        assertThat(day.localizedLabel(), is(LOCALIZED_MONTH));
        assertThat(day.rawValue(), is(MONDAY));
    }
}
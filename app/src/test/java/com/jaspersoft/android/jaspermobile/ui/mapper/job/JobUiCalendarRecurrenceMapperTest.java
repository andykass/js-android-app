package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiCalendarRecurrenceMapperTest {

    public static final String LOCALIZED_UNIT = "NONE";

    private static final int MONDAY = Calendar.MONDAY;
    private static final int AUGUST = Calendar.AUGUST;

    @Mock
    EntityLocalizer<JobScheduleForm.Recurrence> entityLocalizer;
    @Mock
    UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Month> monthMapper;
    @Mock
    UiCollectionEntityMapper<Integer, CalendarViewRecurrence.Day> dayMapper;

    @Mock
    CalendarViewRecurrence.Month month = CalendarViewRecurrence.Month.create("August", AUGUST);
    CalendarViewRecurrence.Day day = CalendarViewRecurrence.Day.create("Monday", MONDAY);

    private JobCalendarRecurrence defaultDomain, mappedDomain;
    private CalendarViewRecurrence defaultUi, mappedUi;
    private JobUiCalendarRecurrenceMapper recurrenceMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entityLocalizer.localize(any(JobScheduleForm.Recurrence.class))).thenReturn(LOCALIZED_UNIT);

        when(monthMapper.toDomainEntity(any(CalendarViewRecurrence.Month.class))).thenReturn(AUGUST);
        when(monthMapper.toUiEntity(anyInt())).thenReturn(month);

        when(dayMapper.toDomainEntity(any(CalendarViewRecurrence.Day.class))).thenReturn(MONDAY);
        when(dayMapper.toUiEntity(anyInt())).thenReturn(day);
    }

    @Test
    public void testToUiEntity() throws Exception {
        givenMapper();
        givenDomainEntity();

        whenMapsToUiEntity();

        thenShouldMapDayToUiFormat();
        thenShouldMapMonthToUiFormat();

        assertThat(mappedUi.daysInWeek(), hasItem(day));
        assertThat(mappedUi.months(), hasItem(month));
        assertThat(mappedUi.daysInMonth(), is(defaultDomain.daysInMonth()));
        assertThat(mappedUi.minutes(), is(defaultDomain.minutes()));
        assertThat(mappedUi.hours(), is(defaultDomain.hours()));
        assertThat(mappedUi.endDate(), is(defaultDomain.endDate()));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        givenMapper();
        givenUiEntity();

        whenMapsToDomainEntity();

        thenShouldMapDayToDomainFormat();
        thenShouldMapMonthToDomainFormat();

        assertThat(mappedDomain.months(), hasItem(AUGUST));
        assertThat(mappedDomain.daysInWeek(), hasItem(MONDAY));
        assertThat(mappedDomain.hours(), is(defaultUi.hours()));
        assertThat(mappedDomain.minutes(), is(defaultUi.minutes()));
        assertThat(mappedDomain.daysInMonth(), is(defaultUi.daysInMonth()));
        assertThat(mappedDomain.endDate(), is(defaultUi.endDate()));
    }

    private void givenMapper() {
        recurrenceMapper = new JobUiCalendarRecurrenceMapper(entityLocalizer, monthMapper, dayMapper);
    }

    private void givenDomainEntity() {
        defaultDomain = JobCalendarRecurrence.builder()
                .daysInWeek(Arrays.asList(MONDAY))
                .months(Arrays.asList(AUGUST))
                .daysInMonth("9-12")
                .minutes("1-17")
                .hours("1/4")
                .endDate(new Date())
                .build();
    }

    private void givenUiEntity() {
        defaultUi = CalendarViewRecurrence.builder()
                .localizedLabel("Calendar")
                .months(Collections.singletonList(month))
                .daysInWeek(Collections.singletonList(day))
                .daysInMonth("9-12")
                .minutes("1-17")
                .hours("1/4")
                .endDate(new Date())
                .build();
    }

    private void whenMapsToUiEntity() {
        mappedUi = (CalendarViewRecurrence) recurrenceMapper.toUiEntity(defaultDomain);
    }

    private void whenMapsToDomainEntity() {
        mappedDomain = (JobCalendarRecurrence) recurrenceMapper.toDomainEntity(defaultUi);
    }

    private void thenShouldMapDayToUiFormat() {
        verify(dayMapper).toUiEntity(MONDAY);
    }

    private void thenShouldMapDayToDomainFormat() {
        verify(dayMapper).toDomainEntity(day);
    }

    private void thenShouldMapMonthToUiFormat() {
        monthMapper.toUiEntity(AUGUST);
    }

    private void thenShouldMapMonthToDomainFormat() {
        monthMapper.toDomainEntity(month);
    }
}
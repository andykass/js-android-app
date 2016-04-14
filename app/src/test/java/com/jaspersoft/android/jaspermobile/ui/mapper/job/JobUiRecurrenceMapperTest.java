package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.entity.job.NoneViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiRecurrenceMapperTest {

    @Mock
    JobUiSimpleRecurrenceMapper simpleRecurrenceMapper;
    @Mock
    JobUiNoneRecurrenceMapper noneRecurrenceMapper;
    @Mock
    JobUiCalendarRecurrenceMapper calendarRecurrenceMapper;

    @Mock
    JobSimpleRecurrence domainSimpleRecurrence;
    @Mock
    SimpleViewRecurrence uiSimpleRecurrence;

    JobNoneRecurrence domainNoneRecurrence = JobNoneRecurrence.INSTANCE;
    @Mock
    NoneViewRecurrence uiNoneRecurrence;

    JobCalendarRecurrence domainCalendarRecurrence = JobCalendarRecurrence.INSTANCE;
    @Mock
    CalendarViewRecurrence uiCalendarRecurrence;

    private JobUiRecurrenceMapper recurrenceMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        recurrenceMapper = new JobUiRecurrenceMapper(simpleRecurrenceMapper, noneRecurrenceMapper, calendarRecurrenceMapper);
    }

    @Test
    public void should_map_domain_simple_recurrence_to_ui() throws Exception {
        when(simpleRecurrenceMapper.toUiEntity(any(JobScheduleForm.Recurrence.class)))
                .thenReturn(uiSimpleRecurrence);

        JobFormViewEntity.Recurrence recurrence = recurrenceMapper.toUiEntity(domainSimpleRecurrence);

        assertThat(recurrence, instanceOf(SimpleViewRecurrence.class));
    }

    @Test
    public void should_map_ui_simple_recurrence_to_domain() throws Exception {
        when(simpleRecurrenceMapper.toDomainEntity(any(JobFormViewEntity.Recurrence.class)))
                .thenReturn(domainSimpleRecurrence);

        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(uiSimpleRecurrence);

        assertThat(recurrence, instanceOf(JobSimpleRecurrence.class));
    }

    @Test
    public void should_map_domain_none_recurrence_to_ui() throws Exception {
        when(noneRecurrenceMapper.toUiEntity(any(JobScheduleForm.Recurrence.class)))
                .thenReturn(uiNoneRecurrence);

        JobFormViewEntity.Recurrence recurrence = recurrenceMapper.toUiEntity(domainNoneRecurrence);

        assertThat(recurrence, instanceOf(NoneViewRecurrence.class));
    }

    @Test
    public void should_map_ui_none_recurrence_to_domain() throws Exception {
        when(noneRecurrenceMapper.toDomainEntity(any(JobFormViewEntity.Recurrence.class)))
                .thenReturn(domainNoneRecurrence);

        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(uiNoneRecurrence);

        assertThat(recurrence, instanceOf(JobNoneRecurrence.class));
    }

    @Test
    public void should_map_domain_calendar_recurrence_to_ui() throws Exception {
        when(calendarRecurrenceMapper.toUiEntity(any(JobScheduleForm.Recurrence.class)))
                .thenReturn(uiCalendarRecurrence);

        JobFormViewEntity.Recurrence recurrence = recurrenceMapper.toUiEntity(domainCalendarRecurrence);

        assertThat(recurrence, instanceOf(CalendarViewRecurrence.class));
    }

    @Test
    public void should_map_ui_calendar_recurrence_to_domain() throws Exception {
        when(calendarRecurrenceMapper.toDomainEntity(any(JobFormViewEntity.Recurrence.class)))
                .thenReturn(domainCalendarRecurrence);

        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(uiCalendarRecurrence);

        assertThat(recurrence, instanceOf(JobCalendarRecurrence.class));
    }
}
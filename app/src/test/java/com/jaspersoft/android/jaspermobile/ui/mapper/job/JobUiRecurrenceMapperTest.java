package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.entity.job.NoneViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiRecurrenceMapperTest {

    @Mock
    UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> simpleRecurrenceMapper;
    @Mock
    UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> noneRecurrenceMapper;
    @Mock
    UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> calendarRecurrenceMapper;

    @Mock
    JobSimpleRecurrence domainSimpleRecurrence;
    @Mock
    SimpleViewRecurrence uiSimpleRecurrence;

    JobNoneRecurrence domainNoneRecurrence = JobNoneRecurrence.INSTANCE;
    @Mock
    NoneViewRecurrence uiNoneRecurrence;

    @Mock
    JobCalendarRecurrence domainCalendarRecurrence;
    @Mock
    CalendarViewRecurrence uiCalendarRecurrence;

    private JobUiRecurrenceMapper recurrenceMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        recurrenceMapper = new JobUiRecurrenceMapper(noneRecurrenceMapper, simpleRecurrenceMapper, calendarRecurrenceMapper);
    }

    @Test
    public void should_map_domain_simple_recurrence_to_ui() throws Exception {
        when(simpleRecurrenceMapper.toUiEntity(any(JobScheduleForm.Recurrence.class)))
                .thenReturn(uiSimpleRecurrence);

        recurrenceMapper.toUiEntity(domainSimpleRecurrence);

        verify(simpleRecurrenceMapper).toUiEntity(domainSimpleRecurrence);
    }

    @Test
    public void should_map_ui_simple_recurrence_to_domain() throws Exception {
        when(simpleRecurrenceMapper.toDomainEntity(any(JobFormViewEntity.Recurrence.class)))
                .thenReturn(domainSimpleRecurrence);

        recurrenceMapper.toDomainEntity(uiSimpleRecurrence);

        verify(simpleRecurrenceMapper).toDomainEntity(uiSimpleRecurrence);
    }

    @Test
    public void should_map_domain_none_recurrence_to_ui() throws Exception {
        when(noneRecurrenceMapper.toUiEntity(any(JobScheduleForm.Recurrence.class)))
                .thenReturn(uiNoneRecurrence);

        recurrenceMapper.toUiEntity(domainNoneRecurrence);

        verify(noneRecurrenceMapper).toUiEntity(domainNoneRecurrence);
    }

    @Test
    public void should_map_ui_none_recurrence_to_domain() throws Exception {
        when(noneRecurrenceMapper.toDomainEntity(any(JobFormViewEntity.Recurrence.class)))
                .thenReturn(domainNoneRecurrence);

        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(uiNoneRecurrence);

        verify(noneRecurrenceMapper).toDomainEntity(uiNoneRecurrence);
    }

    @Test
    public void should_map_domain_calendar_recurrence_to_ui() throws Exception {
        when(calendarRecurrenceMapper.toUiEntity(any(JobScheduleForm.Recurrence.class)))
                .thenReturn(uiCalendarRecurrence);

        recurrenceMapper.toUiEntity(domainCalendarRecurrence);

        verify(calendarRecurrenceMapper).toUiEntity(domainCalendarRecurrence);
    }

    @Test
    public void should_map_ui_calendar_recurrence_to_domain() throws Exception {
        when(calendarRecurrenceMapper.toDomainEntity(any(JobFormViewEntity.Recurrence.class)))
                .thenReturn(domainCalendarRecurrence);

        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(uiCalendarRecurrence);

        verify(calendarRecurrenceMapper).toDomainEntity(uiCalendarRecurrence);
    }
}
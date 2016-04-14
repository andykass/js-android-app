package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiCalendarRecurrenceMapperTest {

    public static final String LOCALIZED_UNIT = "NONE";

    @Mock
    EntityLocalizer<JobScheduleForm.Recurrence> entityLocalizer;

    private JobUiCalendarRecurrenceMapper noneRecurrenceMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entityLocalizer.localize(any(JobScheduleForm.Recurrence.class))).thenReturn(LOCALIZED_UNIT);

        noneRecurrenceMapper = new JobUiCalendarRecurrenceMapper(entityLocalizer);
    }

    @Test
    public void testToUiEntity() throws Exception {
        JobFormViewEntity.Recurrence uiEntity = noneRecurrenceMapper.toUiEntity(JobCalendarRecurrence.INSTANCE);
        assertThat(uiEntity.localizedLabel(), is(LOCALIZED_UNIT));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        CalendarViewRecurrence noneViewRecurrence = CalendarViewRecurrence.create(LOCALIZED_UNIT);
        JobScheduleForm.Recurrence domainEntity = noneRecurrenceMapper.toDomainEntity(noneViewRecurrence);
        assertThat(domainEntity, Is.<JobScheduleForm.Recurrence>is(JobCalendarRecurrence.INSTANCE));
    }
}
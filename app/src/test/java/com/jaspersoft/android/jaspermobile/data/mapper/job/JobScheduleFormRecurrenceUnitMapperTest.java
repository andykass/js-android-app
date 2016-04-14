package com.jaspersoft.android.jaspermobile.data.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.RecurrenceIntervalUnit;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobScheduleFormRecurrenceUnitMapperTest {

    private JobScheduleFormRecurrenceUnitMapper unitMapper;

    JobSimpleRecurrence.Unit domainUnit = JobSimpleRecurrence.Unit.DAY;
    RecurrenceIntervalUnit dataUnit = RecurrenceIntervalUnit.DAY;

    @Before
    public void setUp() throws Exception {
        unitMapper = new JobScheduleFormRecurrenceUnitMapper();
    }

    @Test
    public void testToDataEntity() throws Exception {
        RecurrenceIntervalUnit unit = unitMapper.toDataEntity(domainUnit);
        assertThat(unit, is(dataUnit));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        JobSimpleRecurrence.Unit unit = unitMapper.toDomainEntity(dataUnit);
        assertThat(unit, is(domainUnit));
    }
}
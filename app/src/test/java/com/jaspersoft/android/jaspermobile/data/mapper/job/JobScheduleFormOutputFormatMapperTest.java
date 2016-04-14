package com.jaspersoft.android.jaspermobile.data.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobScheduleFormOutputFormatMapperTest {

    private JobScheduleFormOutputFormatMapper formatMapper;

    JobScheduleForm.OutputFormat domainFormat = JobScheduleForm.OutputFormat.CSV;
    JobOutputFormat dataFormat = JobOutputFormat.CSV;

    @Before
    public void setUp() throws Exception {
        formatMapper = new JobScheduleFormOutputFormatMapper();
    }

    @Test
    public void testToDataEntity() throws Exception {
        JobOutputFormat format = formatMapper.toDataEntity(domainFormat);
        assertThat(format, is(JobOutputFormat.CSV));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        JobScheduleForm.OutputFormat format = formatMapper.toDomainEntity(dataFormat);
        assertThat(format, is(JobScheduleForm.OutputFormat.CSV));
    }
}
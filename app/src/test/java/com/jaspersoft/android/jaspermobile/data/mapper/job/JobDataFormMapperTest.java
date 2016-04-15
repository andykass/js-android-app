package com.jaspersoft.android.jaspermobile.data.mapper.job;

import com.jaspersoft.android.jaspermobile.data.entity.job.IdentifiedJobForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.sdk.service.data.schedule.IntervalRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;
import com.jaspersoft.android.sdk.service.data.schedule.JobSource;
import com.jaspersoft.android.sdk.service.data.schedule.RecurrenceIntervalUnit;
import com.jaspersoft.android.sdk.service.data.schedule.RepositoryDestination;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobDataFormMapperTest {

    private static final String DESCRIPTION = "description";
    private JobDataFormMapper formMapper;

    private JobScheduleForm defaultDomainEntity, mappedDomainEntity;
    private IdentifiedJobForm defaultDataEntity, mappedDataEntity;

    @Mock
    JobScheduleForm.Recurrence domainRecurrence;
    JobScheduleForm.OutputFormat domainOutPutFormat = JobScheduleForm.OutputFormat.CSV;
    JobOutputFormat dataOutPutFormat = JobOutputFormat.CSV;

    @Mock
    JobScheduleFormRecurrenceMapper recurrenceMapper;
    @Mock
    JobScheduleFormOutputFormatMapper outputFormatMapper;

    private Trigger dataRecurrence;
    {
        IntervalRecurrence intervalRecurrence = new IntervalRecurrence.Builder()
                .withInterval(1)
                .withUnit(RecurrenceIntervalUnit.DAY)
                .build();
        dataRecurrence = new Trigger.Builder()
                .withRecurrence(intervalRecurrence)
                .build();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(recurrenceMapper.toDomainEntity(any(Trigger.class))).thenReturn(domainRecurrence);
        when(recurrenceMapper.toDataEntity(any(JobScheduleForm.Recurrence.class))).thenReturn(dataRecurrence);

        when(outputFormatMapper.toDomainEntity(any(JobOutputFormat.class))).thenReturn(domainOutPutFormat);
        when(outputFormatMapper.toDataEntity(any(JobScheduleForm.OutputFormat.class))).thenReturn(dataOutPutFormat);
    }

    @Test
    public void testToDataEntity() throws Exception {
        givenMapper();
        givenDomainEntity();

        whenMapsToDataEntity();

        thenShouldMapRecurrenceToData();

        assertThat(mappedDataEntity.id(), is(defaultDomainEntity.id()));
        JobForm dataForm = mappedDataEntity.form();
        assertThat(dataForm.getVersion(), is(defaultDomainEntity.version()));
        assertThat(dataForm.getSource().getUri(), is(defaultDomainEntity.source()));
        assertThat(dataForm.getLabel(), is(defaultDomainEntity.jobName()));
        assertThat(dataForm.getDescription(), is(defaultDomainEntity.description()));
        assertThat(dataForm.getBaseOutputFilename(), is(defaultDomainEntity.fileName()));
        assertThat(dataForm.getRepositoryDestination().getFolderUri(), is(defaultDomainEntity.folderUri()));
        assertThat(dataForm.getStartDate(), is(defaultDomainEntity.startDate()));
        assertThat(dataForm.getOutputFormats(), hasItem(dataOutPutFormat));
        assertThat(dataForm.getTrigger(), is(notNullValue()));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        givenMapper();
        givenDataEntity();

        whenMapsToDomainEntity();

        thenShouldMapRecurrenceToDomain();

        assertThat(mappedDomainEntity.id(), is(defaultDataEntity.id()));
        JobForm dataForm = defaultDataEntity.form();
        assertThat(mappedDomainEntity.version(), is(dataForm.getVersion()));
        assertThat(mappedDomainEntity.source(), is(dataForm.getSource().getUri()));
        assertThat(mappedDomainEntity.jobName(), is(dataForm.getLabel()));
        assertThat(mappedDomainEntity.description(), is(DESCRIPTION));
        assertThat(mappedDomainEntity.fileName(), is(dataForm.getBaseOutputFilename()));
        assertThat(mappedDomainEntity.folderUri(), is(dataForm.getRepositoryDestination().getFolderUri()));
        assertThat(mappedDomainEntity.startDate(), is(dataForm.getStartDate()));
        assertThat(mappedDomainEntity.outputFormats(), hasItem(domainOutPutFormat));
        assertThat(mappedDomainEntity.recurrence(), is(not(nullValue())));
    }

    private void givenMapper() {
        formMapper = new JobDataFormMapper(recurrenceMapper, outputFormatMapper);
    }

    private void givenDataEntity() {
        JobSource source = new JobSource.Builder()
                .withUri("/report/uri")
                .build();
        RepositoryDestination destination = new RepositoryDestination.Builder()
                .withFolderUri("/folder/uri")
                .build();
        JobForm form = new JobForm.Builder()
                .withVersion(0)
                .withDescription(DESCRIPTION)
                .withJobSource(source)
                .withLabel("Job name")
                .withRepositoryDestination(destination)
                .withBaseOutputFilename("file name.txt")
                .withStartDate(new Date())
                .withOutputFormats(Collections.singletonList(dataOutPutFormat))
                .withTrigger(dataRecurrence)
                .build();
        defaultDataEntity = IdentifiedJobForm.create(10, form);
    }

    private void givenDomainEntity() {
        defaultDomainEntity = JobScheduleForm.builder()
                .id(90)
                .description(DESCRIPTION)
                .version(0)
                .source("/report/uri")
                .jobName("Job name")
                .fileName("file name.txt")
                .folderUri("/folder/uri")
                .startDate(new Date())
                .recurrence(domainRecurrence)
                .outputFormats(Collections.singletonList(domainOutPutFormat))
                .build();
    }

    private void whenMapsToDataEntity() {
        mappedDataEntity = formMapper.toDataEntity(defaultDomainEntity);
    }

    private void whenMapsToDomainEntity() {
        mappedDomainEntity = formMapper.toDomainEntity(defaultDataEntity);
    }

    private void thenShouldMapRecurrenceToData() {
        verify(recurrenceMapper).toDataEntity(domainRecurrence);
    }

    private void thenShouldMapRecurrenceToDomain() {
        verify(recurrenceMapper).toDomainEntity(dataRecurrence);
    }
}
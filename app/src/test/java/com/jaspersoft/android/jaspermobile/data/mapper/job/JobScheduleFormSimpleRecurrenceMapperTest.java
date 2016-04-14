package com.jaspersoft.android.jaspermobile.data.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.EndDate;
import com.jaspersoft.android.sdk.service.data.schedule.IntervalRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.RecurrenceIntervalUnit;
import com.jaspersoft.android.sdk.service.data.schedule.RepeatedEndDate;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;
import com.jaspersoft.android.sdk.service.data.schedule.UntilEndDate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobScheduleFormSimpleRecurrenceMapperTest {

    public static final int INTERVAL = 100;
    public static final int OCCURRENCE = 10;

    private JobScheduleFormSimpleRecurrenceMapper recurrenceMapper;
    private JobSimpleRecurrence defaultDomainEntity, mappedDomainEntity;
    private Trigger defaultDataEntity, mappedDataEntity;

    private JobSimpleRecurrence.Unit domainIntervalUnit = JobSimpleRecurrence.Unit.DAY;
    private RecurrenceIntervalUnit dataIntervalUnit = RecurrenceIntervalUnit.DAY;

    @Mock
    JobScheduleFormRecurrenceUnitMapper unitMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(unitMapper.toDataEntity(any(JobSimpleRecurrence.Unit.class))).thenReturn(dataIntervalUnit);
        when(unitMapper.toDomainEntity(any(RecurrenceIntervalUnit.class))).thenReturn(domainIntervalUnit);
    }

    private void givenRecurrenceMapper() {
        recurrenceMapper = new JobScheduleFormSimpleRecurrenceMapper(unitMapper);
    }

    @Test
    public void should_map_domain_entity_without_date() throws Exception {
        givenRecurrenceMapper();
        givenDomainEntityWithoutEndDate();

        whenMapsRecurrenceToData();

        thenShouldMapUnitToData();

        EndDate endDate = mappedDataEntity.getEndDate();
        assertThat(endDate, is(nullValue()));
        IntervalRecurrence recurrence = (IntervalRecurrence) mappedDataEntity.getRecurrence();
        assertThat(recurrence.getInterval(), is(defaultDomainEntity.interval()));
        assertThat(recurrence.getUnit(), is(dataIntervalUnit));
    }

    @Test
    public void should_map_domain_entity_with_end_date() throws Exception {
        givenRecurrenceMapper();
        givenDomainEntityWithEndDate();

        whenMapsRecurrenceToData();

        thenShouldMapUnitToData();

        UntilEndDate endDate = (UntilEndDate) mappedDataEntity.getEndDate();
        assertThat(endDate.getSpecifiedDate(), is(defaultDomainEntity.untilDate()));
        IntervalRecurrence recurrence = (IntervalRecurrence) mappedDataEntity.getRecurrence();
        assertThat(recurrence.getInterval(), is(defaultDomainEntity.interval()));
        assertThat(recurrence.getUnit(), is(dataIntervalUnit));
    }

    @Test
    public void should_map_domain_entity_with_repeat_date() throws Exception {
        givenRecurrenceMapper();
        givenDomainEntityWithRepeatDate();

        whenMapsRecurrenceToData();

        thenShouldMapUnitToData();

        RepeatedEndDate endDate = (RepeatedEndDate) mappedDataEntity.getEndDate();
        assertThat(endDate.getOccurrenceCount(), is(defaultDomainEntity.occurrence()));
        IntervalRecurrence recurrence = (IntervalRecurrence) mappedDataEntity.getRecurrence();
        assertThat(recurrence.getInterval(), is(defaultDomainEntity.interval()));
        assertThat(recurrence.getUnit(), is(dataIntervalUnit));
    }

    @Test
    public void should_map_data_entity_without_date() throws Exception {
        givenRecurrenceMapper();
        givenDataEntityWithoutEndDate();

        whenMapsRecurrenceToDomain();

        thenShouldMapUnitToDomain();

        IntervalRecurrence recurrence = (IntervalRecurrence) defaultDataEntity.getRecurrence();
        assertThat(mappedDomainEntity.interval(), is(recurrence.getInterval()));
        assertThat(mappedDomainEntity.unit(), is(domainIntervalUnit));
        assertThat(mappedDomainEntity.untilDate(), is(nullValue()));
        assertThat(mappedDomainEntity.occurrence(), is(nullValue()));
    }

    @Test
    public void should_map_data_entity_with_end_date() throws Exception {
        givenRecurrenceMapper();
        givenDataEntityWithEndDate();

        whenMapsRecurrenceToDomain();

        thenShouldMapUnitToDomain();

        IntervalRecurrence recurrence = (IntervalRecurrence) defaultDataEntity.getRecurrence();
        Date endDate = ((UntilEndDate) defaultDataEntity.getEndDate()).getSpecifiedDate();
        assertThat(mappedDomainEntity.interval(), is(recurrence.getInterval()));
        assertThat(mappedDomainEntity.unit(), is(domainIntervalUnit));
        assertThat(mappedDomainEntity.untilDate(), is(endDate));
        assertThat(mappedDomainEntity.occurrence(), is(nullValue()));
    }

    @Test
    public void should_map_data_entity_with_repeat_date() throws Exception {
        givenRecurrenceMapper();
        givenDataEntityWithRepeatDate();

        whenMapsRecurrenceToDomain();

        thenShouldMapUnitToDomain();

        IntervalRecurrence recurrence = (IntervalRecurrence) defaultDataEntity.getRecurrence();
        int occurence = ((RepeatedEndDate) defaultDataEntity.getEndDate()).getOccurrenceCount();
        assertThat(mappedDomainEntity.interval(), is(recurrence.getInterval()));
        assertThat(mappedDomainEntity.unit(), is(domainIntervalUnit));
        assertThat(mappedDomainEntity.untilDate(), is(nullValue()));
        assertThat(mappedDomainEntity.occurrence(), is(occurence));
    }

    private void givenDomainEntityWithoutEndDate() {
        defaultDomainEntity = JobSimpleRecurrence.builder()
                .interval(INTERVAL)
                .unit(JobSimpleRecurrence.Unit.DAY)
                .build();
    }

    private void givenDomainEntityWithEndDate() {
        defaultDomainEntity = JobSimpleRecurrence.builder()
                .interval(INTERVAL)
                .unit(JobSimpleRecurrence.Unit.DAY)
                .untilDate(new Date())
                .build();
    }

    private void givenDomainEntityWithRepeatDate() {
        defaultDomainEntity = JobSimpleRecurrence.builder()
                .interval(INTERVAL)
                .unit(JobSimpleRecurrence.Unit.DAY)
                .occurrence(OCCURRENCE)
                .build();
    }

    private void givenDataEntityWithoutEndDate() {
        IntervalRecurrence recurrence = new IntervalRecurrence.Builder()
                .withInterval(INTERVAL)
                .withUnit(RecurrenceIntervalUnit.DAY)
                .build();
        defaultDataEntity = new Trigger.Builder()
                .withRecurrence(recurrence)
                .build();
    }

    private void givenDataEntityWithEndDate() {
        IntervalRecurrence recurrence = new IntervalRecurrence.Builder()
                .withInterval(INTERVAL)
                .withUnit(RecurrenceIntervalUnit.DAY)
                .build();
        defaultDataEntity = new Trigger.Builder()
                .withRecurrence(recurrence)
                .withEndDate(new UntilEndDate(new Date()))
                .build();
    }

    private void givenDataEntityWithRepeatDate() {
        IntervalRecurrence recurrence = new IntervalRecurrence.Builder()
                .withInterval(INTERVAL)
                .withUnit(RecurrenceIntervalUnit.DAY)
                .build();
        defaultDataEntity = new Trigger.Builder()
                .withRecurrence(recurrence)
                .withEndDate(new RepeatedEndDate(OCCURRENCE))
                .build();
    }

    private void whenMapsRecurrenceToDomain() {
        mappedDomainEntity = (JobSimpleRecurrence) recurrenceMapper.toDomainEntity(defaultDataEntity);
    }

    private void whenMapsRecurrenceToData() {
        mappedDataEntity = recurrenceMapper.toDataEntity(defaultDomainEntity);
    }

    private void thenShouldMapUnitToDomain() {
        verify(unitMapper).toDomainEntity(dataIntervalUnit);
    }

    private void thenShouldMapUnitToData() {
        verify(unitMapper).toDataEntity(domainIntervalUnit);
    }
}
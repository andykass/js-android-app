package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiSimpleRecurrenceMapperTest {

    private static final String LOCALIZED_LABEL = "localized label";

    @Mock
    EntityLocalizer<JobScheduleForm.Recurrence> localizer;

    @Mock
    UiEntityMapper<JobSimpleRecurrence.Unit, SimpleViewRecurrence.Unit> unitMapper;
    @Mock
    SimpleViewRecurrence.Unit uiUnit;
    JobSimpleRecurrence.Unit domainUnit = JobSimpleRecurrence.Unit.DAY;

    private JobUiSimpleRecurrenceMapper recurrenceMapper;
    private JobSimpleRecurrence defaultDomainEntity, mappedDomainEntity;
    private SimpleViewRecurrence defaultUiEntity, mappedUiEntity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(unitMapper.toUiEntity(any(JobSimpleRecurrence.Unit.class))).thenReturn(uiUnit);
        when(unitMapper.toDomainEntity(any(SimpleViewRecurrence.Unit.class))).thenReturn(domainUnit);

        when(localizer.localize(any(JobScheduleForm.Recurrence.class))).thenReturn(LOCALIZED_LABEL);
    }

    @Test
    public void testToUiEntity() throws Exception {
        givenRecurrenceMapper();
        givenDomainEntity();
        whenMapsFromDomainToUi();

        thenShouldMapUnitToUiFormat();
        thenShouldLocalizeLabel();

        assertThat(mappedUiEntity.interval(), is(defaultDomainEntity.interval()));
        assertThat(mappedUiEntity.occurrence(), is(defaultDomainEntity.occurrence()));
        assertThat(mappedUiEntity.untilDate(), is(defaultDomainEntity.untilDate()));
        assertThat(mappedUiEntity.localizedLabel(), is(LOCALIZED_LABEL));
        assertThat(mappedUiEntity.unit(), is(uiUnit));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        givenRecurrenceMapper();
        givenUiEntity();
        whenMapsFromUiToDomain();

        thenShouldMapUnitToDomainFormat();

        assertThat(mappedDomainEntity.interval(), is(defaultUiEntity.interval()));
        assertThat(mappedDomainEntity.occurrence(), is(defaultUiEntity.occurrence()));
        assertThat(mappedDomainEntity.untilDate(), is(defaultUiEntity.untilDate()));
        assertThat(mappedDomainEntity.unit(), is(domainUnit));
    }

    private void givenUiEntity() {
        defaultUiEntity = SimpleViewRecurrence.builder()
                .interval(10)
                .occurrence(2)
                .untilDate(new Date())
                .localizedLabel(LOCALIZED_LABEL)
                .unit(uiUnit)
                .build();
    }

    private void givenDomainEntity() {
        defaultDomainEntity = JobSimpleRecurrence.builder()
                .interval(10)
                .occurrence(2)
                .untilDate(new Date())
                .unit(domainUnit)
                .build();
    }

    private void givenRecurrenceMapper() {
        recurrenceMapper = new JobUiSimpleRecurrenceMapper(unitMapper, localizer);
    }

    private void whenMapsFromDomainToUi() {
        mappedUiEntity = (SimpleViewRecurrence) recurrenceMapper.toUiEntity(defaultDomainEntity);
    }

    private void whenMapsFromUiToDomain() {
        mappedDomainEntity = (JobSimpleRecurrence) recurrenceMapper.toDomainEntity(defaultUiEntity);
    }

    private void thenShouldMapUnitToUiFormat() {
        verify(unitMapper).toUiEntity(domainUnit);
    }

    private void thenShouldMapUnitToDomainFormat() {
        verify(unitMapper).toDomainEntity(uiUnit);
    }

    private void thenShouldLocalizeLabel() {
        verify(localizer).localize(defaultDomainEntity);
    }
}
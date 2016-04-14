package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

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
public class JobUiRecurrenceUnitMapperTest {

    public static final String LOCALIZED_UNIT = "LOCALIZED_UNIT";

    @Mock
    EntityLocalizer<JobSimpleRecurrence.Unit> entityLocalizer;

    private JobUiRecurrenceUnitMapper unitMapper;

    JobSimpleRecurrence.Unit defaultDomainUnit, mappedDomainUnit;
    SimpleViewRecurrence.Unit defaultUiUnit, mappedUiUnit;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entityLocalizer.localize(any(JobSimpleRecurrence.Unit.class))).thenReturn(LOCALIZED_UNIT);
    }

    @Test
    public void testToUiEntity() throws Exception {
        givenUnitMapper();
        givenDomainUnit();
        givenUiUnit();

        whenMapsToUiEntity();

        assertThat(mappedUiUnit.rawValue(), is(defaultDomainUnit.name()));
        assertThat(mappedUiUnit.localizedLabel(), is(LOCALIZED_UNIT));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        givenUnitMapper();
        givenDomainUnit();
        givenUiUnit();

        whenMapsToDomainEntity();

        assertThat(mappedDomainUnit, is(defaultDomainUnit));
    }

    private void givenUnitMapper() {
        unitMapper = new JobUiRecurrenceUnitMapper(entityLocalizer);
    }

    private void givenDomainUnit() {
        defaultDomainUnit = JobSimpleRecurrence.Unit.DAY;
    }

    private void givenUiUnit() {
        defaultUiUnit = SimpleViewRecurrence.Unit.create(defaultDomainUnit.name(), LOCALIZED_UNIT);
    }

    private void whenMapsToUiEntity() {
        mappedUiUnit = unitMapper.toUiEntity(defaultDomainUnit);
    }

    private void whenMapsToDomainEntity() {
        mappedDomainUnit = unitMapper.toDomainEntity(defaultUiUnit);
    }
}
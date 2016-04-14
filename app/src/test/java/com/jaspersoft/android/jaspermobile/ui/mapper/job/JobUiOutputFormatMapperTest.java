package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm.OutputFormat;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobUiOutputFormatMapperTest {

    private static final String LOCALIZED_FORMAT = "Localized Format";

    OutputFormat defaultDomainEntity = OutputFormat.CSV;
    JobFormViewEntity.OutputFormat defaultUiEntity = JobFormViewEntity.OutputFormat.create("CSV", LOCALIZED_FORMAT);
    @Mock
    EntityLocalizer<OutputFormat> entityLocalizer;

    private JobUiOutputFormatMapper formatMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entityLocalizer.localize(any(OutputFormat.class))).thenReturn(LOCALIZED_FORMAT);

        formatMapper = new JobUiOutputFormatMapper(entityLocalizer);
    }

    @Test
    public void testToUiEntity() throws Exception {
        JobFormViewEntity.OutputFormat format = formatMapper.toUiEntity(defaultDomainEntity);

        verify(entityLocalizer).localize(defaultDomainEntity);
        assertThat(format, is(defaultUiEntity));
    }

    @Test
    public void testToDomainEntity() throws Exception {
        OutputFormat format = formatMapper.toDomainEntity(defaultUiEntity);
        assertThat(format, is(defaultDomainEntity));
    }
}
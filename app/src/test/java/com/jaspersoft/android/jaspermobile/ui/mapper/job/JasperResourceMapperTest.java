package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import com.jaspersoft.android.jaspermobile.data.mapper.job.JobDataFormBundleWrapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResourceType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JasperResourceMapperTest {

    public static final String NEW_SCHEDULE = "New schedule";

    @Mock
    JobDataFormBundleWrapper bundleWrapper;
    @Mock
    UiEntityMapper<JobScheduleBundle, JobFormViewBundle> jobUiFormBundleMapper;

    @Mock
    JobScheduleBundle domainFormBundle;
    @Mock
    JobFormViewBundle uiFormBundle;

    JobScheduleForm defaultDomainForm;
    JasperResource defaultJasperResource;

    private JasperResourceMapper resourceMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(bundleWrapper.wrap(any(JobScheduleForm.class))).thenReturn(domainFormBundle);

        resourceMapper = new JasperResourceMapper(bundleWrapper, jobUiFormBundleMapper);
    }

    @Test
    public void testToUiEntity() throws Exception {
        givenDefaultDomainForm();
        givenJasperResource();

        resourceMapper.toUiEntity(defaultJasperResource);

        verify(bundleWrapper).wrap(defaultDomainForm);
    }

    private void givenDefaultDomainForm() {
        defaultDomainForm = JobScheduleForm.builder()
                .id(0)
                .version(0)
                .outputFormats(Collections.singletonList(JobScheduleForm.OutputFormat.PDF))
                .fileName("All_accounts")
                .folderUri("/my/report")
                .source("/my/report/uri")
                .jobName(NEW_SCHEDULE)
                .recurrence(JobNoneRecurrence.INSTANCE)
                .build();
    }

    private void givenJasperResource() {
        defaultJasperResource = new Resource("/my/report/uri", "All accounts", null);
    }

    private static class Resource extends JasperResource {
        public Resource(String id, String label, String description) {
            super(id, label, description);
        }

        @Override
        public JasperResourceType getResourceType() {
            return JasperResourceType.job;
        }
    }
}
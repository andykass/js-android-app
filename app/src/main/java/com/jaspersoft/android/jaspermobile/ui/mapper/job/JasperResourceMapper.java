package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.data.mapper.job.JobDataFormBundleWrapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import java.util.Collections;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public final class JasperResourceMapper implements UiEntityMapper<JasperResource, JobFormViewBundle> {
    private static final String DEFAULT_OUTPUT_PATH = "/public/Samples/Reports";

    @NonNull
    private final JobDataFormBundleWrapper bundleWrapper;
    @NonNull
    private final UiEntityMapper<JobScheduleBundle, JobFormViewBundle> jobUiFormBundleMapper;

    @VisibleForTesting
    JasperResourceMapper(
            @NonNull JobDataFormBundleWrapper bundleWrapper,
            @NonNull UiEntityMapper<JobScheduleBundle, JobFormViewBundle> jobUiFormBundleMapper
    ) {
        this.bundleWrapper = bundleWrapper;
        this.jobUiFormBundleMapper = jobUiFormBundleMapper;
    }

    @NonNull
    public static UiEntityMapper<JasperResource, JobFormViewBundle> create(@NonNull Context context) {
        JobDataFormBundleWrapper bundleWrapper = JobDataFormBundleWrapper.create();
        JobUiFormBundleMapper bundleMapper = JobUiFormBundleMapper.create(context);
        return new JasperResourceMapper(bundleWrapper, bundleMapper);
    }

    @NonNull
    @Override
    public JobFormViewBundle toUiEntity(@NonNull JasperResource resource) {
        JobScheduleForm.Builder builder = JobScheduleForm.builder();
        builder.id(0);
        builder.version(0);
        builder.outputFormats(Collections.singletonList(JobScheduleForm.OutputFormat.PDF));
        builder.jobName(resource.getLabel());
        builder.folderUri(extractFolderUri(resource));
        builder.fileName(extractFileName(resource));
        builder.source(resource.getId());
        builder.recurrence(JobNoneRecurrence.INSTANCE);

        JobScheduleForm domainForm = builder.build();
        JobScheduleBundle domainBundle = bundleWrapper.wrap(domainForm);

        return jobUiFormBundleMapper.toUiEntity(domainBundle);
    }

    private String extractFileName(JasperResource resource) {
        String resourceLabel = resource.getLabel();
        return resourceLabel.replace(" ", "_");
    }

    private String extractFolderUri(JasperResource resource) {
        String resourceId = resource.getId();
        String reportFolder;

        int endIndex = resourceId.lastIndexOf("/");
        if (endIndex == -1) {
            reportFolder = DEFAULT_OUTPUT_PATH;
        } else {
            reportFolder = resourceId.substring(0, endIndex);
        }
        return reportFolder;
    }

    @NonNull
    @Override
    public JasperResource toDomainEntity(@NonNull JobFormViewBundle domainEntity) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

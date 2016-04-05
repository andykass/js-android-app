package com.jaspersoft.android.jaspermobile.ui.view.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity.OutputFormat;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;
import com.jaspersoft.android.sdk.service.data.schedule.JobSource;
import com.jaspersoft.android.sdk.service.data.schedule.RepositoryDestination;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class JobFormMapper  {
    private static final String DEFAULT_OUTPUT_PATH = "/public/Samples/Reports";
    private final Context mContext;
    private final JobFormatOutputMapper mOutputMapper;

    @Inject
    public JobFormMapper(@ApplicationContext Context context, JobFormatOutputMapper outputMapper) {
        mContext = context;
        mOutputMapper = outputMapper;
    }

    @NonNull
    public JobFormViewEntity toUiEntity(JasperResource resource) {
        String resourceLabel = resource.getLabel();
        String fileName = resourceLabel.replace(" ", "_");
        List<OutputFormat> formats = Collections.singletonList(
                new OutputFormat(JobOutputFormat.PDF.name(), getString(R.string.si_fd_option_pdf))
        );
        String resourceId = resource.getId();
        int endIndex = resourceId.lastIndexOf("/");

        String reportFolder;
        if (endIndex == -1) {
            reportFolder = DEFAULT_OUTPUT_PATH;
        } else {
            reportFolder = resourceId.substring(0, endIndex);
        }

        return new JobFormViewEntity.Builder()
                .withName(getString(R.string.sch_new))
                .withOutputPath(reportFolder)
                .withFileName(fileName)
                .withOutputFormats(formats)
                .withInternalSource(resourceId)
                .build();
    }

    @NonNull
    public JobFormViewEntity toUiEntity(JobForm form) {
        List<OutputFormat> outputFormats = mOutputMapper.toUiType(form.getOutputFormats());
        String destination = form.getRepositoryDestination().getFolderUri();
        Integer tmpVersion = form.getVersion();
        int version = tmpVersion == null ? 0 : tmpVersion;

        return new JobFormViewEntity.Builder()
                .withInternalVersion(version)
                .withName(form.getLabel())
                .withInternalSource(form.getSource().getUri())
                .withStartDate(form.getStartDate())
                .withFileName(form.getBaseOutputFilename())
                .withOutputFormats(outputFormats)
                .withOutputPath(destination)
                .build();
    }

    @NonNull
    public JobForm toDataEntity(JobFormViewEntity form) {
        RepositoryDestination destination = new RepositoryDestination.Builder()
                .withFolderUri(form.getOutputPath())
                .build();
        JobSource source = new JobSource.Builder()
                .withUri(form.mSource)
                .build();
        List<JobOutputFormat> formats = mOutputMapper.toDataType(form.getOutputFormats());

        JobForm.Builder jobFormBuilder = new JobForm.Builder()
                .withLabel(form.getJobName())
                .withBaseOutputFilename(form.getFileName())
                .withRepositoryDestination(destination)
                .withOutputFormats(formats)
                .withJobSource(source);

        if (form.hasStartDate()) {
            jobFormBuilder.withStartDate(form.getStartDate().getTime());
        }

        int version = form.mVersion;
        if (version > 0) {
            jobFormBuilder.withVersion(version);
        }

        return jobFormBuilder.build();
    }



    private String getString(@StringRes int id, Object... args) {
        return mContext.getResources().getString(id, args);
    }
}
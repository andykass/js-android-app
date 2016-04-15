package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.data.entity.job.IdentifiedJobForm;
import com.jaspersoft.android.jaspermobile.data.mapper.DataEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;
import com.jaspersoft.android.sdk.service.data.schedule.JobSource;
import com.jaspersoft.android.sdk.service.data.schedule.RepositoryDestination;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobDataFormMapper implements DataEntityMapper<JobScheduleForm, IdentifiedJobForm> {

    @NonNull
    private final JobScheduleFormRecurrenceMapper recurrenceMapper;
    @NonNull
    private final JobScheduleFormOutputFormatMapper outputFormatMapper;

    @VisibleForTesting
    JobDataFormMapper(
            @NonNull JobScheduleFormRecurrenceMapper recurrenceMapper,
            @NonNull JobScheduleFormOutputFormatMapper outputFormatMapper
    ) {
        this.recurrenceMapper = recurrenceMapper;
        this.outputFormatMapper = outputFormatMapper;
    }

    @NonNull
    public static JobDataFormMapper create() {
        JobScheduleFormRecurrenceMapper recurrenceMapper = JobScheduleFormRecurrenceMapper.create();
        JobScheduleFormOutputFormatMapper formatMapper = new JobScheduleFormOutputFormatMapper();
        return new JobDataFormMapper(recurrenceMapper, formatMapper);
    }

    @NonNull
    @Override
    public IdentifiedJobForm toDataEntity(@NonNull JobScheduleForm domainForm) {
        JobScheduleForm.Recurrence recurrence = domainForm.recurrence();
        Trigger dataRecurrence = recurrenceMapper.toDataEntity(recurrence);

        JobSource source = new JobSource.Builder()
                .withUri(domainForm.source())
                .build();
        RepositoryDestination destination = new RepositoryDestination.Builder()
                .withFolderUri(domainForm.folderUri())
                .build();
        List<JobOutputFormat> formats = outputFormatMapper.toDataEntityList(domainForm.outputFormats());

        JobForm.Builder formBuilder = new JobForm.Builder();
        formBuilder.withVersion(domainForm.version());
        formBuilder.withLabel(domainForm.jobName());
        formBuilder.withDescription(domainForm.description());
        formBuilder.withBaseOutputFilename(domainForm.fileName());
        formBuilder.withStartDate(domainForm.startDate());
        formBuilder.withRepositoryDestination(destination);
        formBuilder.withJobSource(source);
        formBuilder.withOutputFormats(formats);
        formBuilder.withTrigger(dataRecurrence);
        formBuilder.build();

        return IdentifiedJobForm.create(domainForm.id(), formBuilder.build());
    }

    @NonNull
    @Override
    public JobScheduleForm toDomainEntity(@NonNull IdentifiedJobForm dataForm) {
        JobForm form = dataForm.form();

        JobScheduleForm.Builder builder = JobScheduleForm.builder();
        builder.id(dataForm.id());

        int version = form.getVersion() == null ? 0 : form.getVersion();
        builder.version(version);

        builder.jobName(form.getLabel());
        builder.description(form.getDescription());
        builder.fileName(form.getBaseOutputFilename());
        builder.startDate(form.getStartDate());
        builder.folderUri(form.getRepositoryDestination().getFolderUri());
        builder.source(form.getSource().getUri());

        Set<JobOutputFormat> outputFormats = form.getOutputFormats();
        List<JobOutputFormat> dataFormats = new ArrayList<>(outputFormats.size());
        dataFormats.addAll(outputFormats);
        List<JobScheduleForm.OutputFormat> formats = outputFormatMapper.toDomainEntityList(dataFormats);
        builder.outputFormats(formats);

        Trigger trigger = form.getTrigger();
        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(trigger);
        builder.recurrence(recurrence);

        return builder.build();
    }
}

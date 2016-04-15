package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

import java.util.List;


/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobUiFormMapper implements UiEntityMapper<JobScheduleForm, JobFormViewEntity> {

    private final UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> recurrenceMapper;
    private final UiCollectionEntityMapper<JobScheduleForm.OutputFormat, JobFormViewEntity.OutputFormat> formatMapper;

    @VisibleForTesting
    JobUiFormMapper(
            @NonNull UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> recurrenceMapper,
            @NonNull UiCollectionEntityMapper<JobScheduleForm.OutputFormat, JobFormViewEntity.OutputFormat> formatMapper
    ) {
        this.recurrenceMapper = recurrenceMapper;
        this.formatMapper = formatMapper;
    }

    @NonNull
    public static JobUiFormMapper create(@NonNull Context context) {
        JobUiRecurrenceMapper recurrenceMapper = JobUiRecurrenceMapper.create(context);
        JobUiOutputFormatMapper outputFormatMapper = JobUiOutputFormatMapper.create(context);
        return new JobUiFormMapper(recurrenceMapper, outputFormatMapper);
    }

    @NonNull
    public JobFormViewEntity toUiEntity(@NonNull JobScheduleForm domain) {
        JobFormViewEntity.Builder builder = JobFormViewEntity.builder();

        builder.id(domain.id());
        builder.version(domain.version());
        builder.source(domain.source());
        builder.jobName(domain.jobName());
        builder.description(domain.description());
        builder.fileName(domain.fileName());
        builder.folderUri(domain.folderUri());
        builder.startDate(domain.startDate());

        JobFormViewEntity.Recurrence recurrence = recurrenceMapper.toUiEntity(domain.recurrence());
        builder.recurrence(recurrence);

        List<JobFormViewEntity.OutputFormat> outputFormats = formatMapper.toUiEntityList(domain.outputFormats());
        builder.outputFormats(outputFormats);

        return builder.build();
    }

    @NonNull
    public JobScheduleForm toDomainEntity(@NonNull JobFormViewEntity ui) {
        JobScheduleForm.Builder builder = JobScheduleForm.builder();

        builder.id(ui.id());
        builder.version(ui.version());
        builder.source(ui.source());
        builder.jobName(ui.jobName());
        builder.description(ui.description());
        builder.fileName(ui.fileName());
        builder.folderUri(ui.folderUri());
        builder.startDate(ui.startDate());

        JobScheduleForm.Recurrence recurrence = recurrenceMapper.toDomainEntity(ui.recurrence());
        builder.recurrence(recurrence);

        List<JobScheduleForm.OutputFormat> outputFormats = formatMapper.toDomainEntityList(ui.outputFormats());
        builder.outputFormats(outputFormats);

        return builder.build();
    }
}
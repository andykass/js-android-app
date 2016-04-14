package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public final class JobUiFormBundleMapper implements UiEntityMapper<JobScheduleBundle, JobFormViewBundle> {

    @NonNull
    private final UiEntityMapper<JobScheduleForm, JobFormViewEntity> uiFormMapper;
    @NonNull
    private final UiCollectionEntityMapper<JobScheduleForm.OutputFormat, JobFormViewEntity.OutputFormat> outputFormatMapper;
    @NonNull
    private final UiCollectionEntityMapper<JobSimpleRecurrence.Unit, SimpleViewRecurrence.Unit> intervalUnitMapper;
    @NonNull
    private final UiCollectionEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> recurrenceMapper;

    @VisibleForTesting
    JobUiFormBundleMapper(
            @NonNull UiEntityMapper<JobScheduleForm, JobFormViewEntity> uiFormMapper,
            @NonNull UiCollectionEntityMapper<JobScheduleForm.OutputFormat, JobFormViewEntity.OutputFormat> outputFormatMapper,
            @NonNull UiCollectionEntityMapper<JobSimpleRecurrence.Unit, SimpleViewRecurrence.Unit> intervalUnitMapper,
            @NonNull UiCollectionEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> recurrenceMapper
    ) {
        this.uiFormMapper = uiFormMapper;
        this.outputFormatMapper = outputFormatMapper;
        this.intervalUnitMapper = intervalUnitMapper;
        this.recurrenceMapper = recurrenceMapper;
    }

    @NonNull
    public static JobUiFormBundleMapper create(@NonNull Context context) {
        JobUiFormMapper formMapper = JobUiFormMapper.create(context);
        JobUiOutputFormatMapper outputFormatMapper = JobUiOutputFormatMapper.create(context);
        JobUiRecurrenceUnitMapper unitMapper = JobUiRecurrenceUnitMapper.create(context);
        JobUiRecurrenceMapper recurrenceMapper = JobUiRecurrenceMapper.create(context);

        return new JobUiFormBundleMapper(
                formMapper,
                outputFormatMapper,
                unitMapper,
                recurrenceMapper
        );
    }

    @NonNull
    @Override
    public JobFormViewBundle toUiEntity(@NonNull JobScheduleBundle domainEntity) {
        JobFormViewBundle.Builder builder = JobFormViewBundle.builder();
        builder.form(uiFormMapper.toUiEntity(domainEntity.form()));
        builder.allFormats(outputFormatMapper.toUiEntityList(domainEntity.allFormats()));
        builder.allIntervalUnits(intervalUnitMapper.toUiEntityList(domainEntity.allIntervalUnits()));
        builder.allRecurrences(recurrenceMapper.toUiEntityList(domainEntity.allRecurrences()));
        return builder.build();
    }

    @NonNull
    @Override
    public JobScheduleBundle toDomainEntity(@NonNull JobFormViewBundle uiEntity) {
        JobScheduleBundle.Builder builder = JobScheduleBundle.builder();
        builder.form(uiFormMapper.toDomainEntity(uiEntity.form()));
        builder.allFormats(outputFormatMapper.toDomainEntityList(uiEntity.allFormats()));
        builder.allIntervalUnits(intervalUnitMapper.toDomainEntityList(uiEntity.allIntervalUnits()));
        builder.allRecurrences(recurrenceMapper.toDomainEntityList(uiEntity.allRecurrences()));
        return builder.build();
    }
}

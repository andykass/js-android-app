package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobUiOutputFormatMapper extends UiCollectionEntityMapper<JobScheduleForm.OutputFormat, JobFormViewEntity.OutputFormat> {

    private final EntityLocalizer<JobScheduleForm.OutputFormat> entityLocalizer;

    @VisibleForTesting
    JobUiOutputFormatMapper(EntityLocalizer<JobScheduleForm.OutputFormat> entityLocalizer) {
        this.entityLocalizer = entityLocalizer;
    }

    @NonNull
    public static JobUiOutputFormatMapper create(@NonNull Context context) {
        JobOutputFormatLocalizer entityLocalizer = new JobOutputFormatLocalizer(context);
        return new JobUiOutputFormatMapper(entityLocalizer);
    }

    @NonNull
    @Override
    public JobFormViewEntity.OutputFormat toUiEntity(@NonNull JobScheduleForm.OutputFormat domainEntity) {
        String localizedLabel = entityLocalizer.localize(domainEntity);
        return JobFormViewEntity.OutputFormat.create(domainEntity.name(), localizedLabel);
    }

    @NonNull
    @Override
    public JobScheduleForm.OutputFormat toDomainEntity(@NonNull JobFormViewEntity.OutputFormat uiEntity) {
        String rawValue = uiEntity.rawValue();
        return JobScheduleForm.OutputFormat.valueOf(rawValue);
    }
}

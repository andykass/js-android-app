package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.data.mapper.DataCollectionEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.sdk.service.data.schedule.JobOutputFormat;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormOutputFormatMapper extends DataCollectionEntityMapper<JobScheduleForm.OutputFormat, JobOutputFormat> {
    @NonNull
    @Override
    public JobOutputFormat toDataEntity(@NonNull JobScheduleForm.OutputFormat outputFormat) {
        return JobOutputFormat.valueOf(outputFormat.name());
    }

    @NonNull
    @Override
    public JobScheduleForm.OutputFormat toDomainEntity(@NonNull JobOutputFormat domainEntity) {
        return JobScheduleForm.OutputFormat.valueOf(domainEntity.name());
    }
}

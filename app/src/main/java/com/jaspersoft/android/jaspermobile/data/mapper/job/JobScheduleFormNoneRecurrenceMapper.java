package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jaspersoft.android.jaspermobile.data.mapper.DataEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormNoneRecurrenceMapper implements DataEntityMapper<JobScheduleForm.Recurrence, Trigger> {
    @Nullable
    @Override
    public Trigger toDataEntity(@NonNull JobScheduleForm.Recurrence domainEntity) {
        return null;
    }

    @NonNull
    @Override
    public JobScheduleForm.Recurrence toDomainEntity(@Nullable Trigger dataEntity) {
        return JobNoneRecurrence.INSTANCE;
    }
}

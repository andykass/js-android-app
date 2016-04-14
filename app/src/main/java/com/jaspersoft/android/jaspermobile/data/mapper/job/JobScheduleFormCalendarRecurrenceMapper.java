package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.data.mapper.DataEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormCalendarRecurrenceMapper implements DataEntityMapper<JobScheduleForm.Recurrence, Trigger> {

    @NonNull
    @Override
    public Trigger toDataEntity(@NonNull JobScheduleForm.Recurrence domainEntity) {
        return null;
    }

    @NonNull
    @Override
    public JobScheduleForm.Recurrence toDomainEntity(@NonNull Trigger dataEntity) {
        return JobCalendarRecurrence.INSTANCE;
    }
}

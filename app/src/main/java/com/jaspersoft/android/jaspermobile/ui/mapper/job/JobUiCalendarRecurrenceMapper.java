package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.ui.entity.job.CalendarViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobUiCalendarRecurrenceMapper implements UiEntityMapper<JobScheduleForm.Recurrence, JobFormViewEntity.Recurrence> {
    private final EntityLocalizer<JobScheduleForm.Recurrence> entityLocalizer;

    @VisibleForTesting
    JobUiCalendarRecurrenceMapper(EntityLocalizer<JobScheduleForm.Recurrence> entityLocalizer) {
        this.entityLocalizer = entityLocalizer;
    }

    @NonNull
    public static JobUiCalendarRecurrenceMapper create(@NonNull Context context) {
        RecurrenceEntityLocalizer entityLocalizer = new RecurrenceEntityLocalizer(context);
        return new JobUiCalendarRecurrenceMapper(entityLocalizer);
    }

    @NonNull
    @Override
    public JobFormViewEntity.Recurrence toUiEntity(@NonNull JobScheduleForm.Recurrence recurrence) {
        String localizedLabel = entityLocalizer.localize(recurrence);
        return CalendarViewRecurrence.create(localizedLabel);
    }

    @NonNull
    @Override
    public JobScheduleForm.Recurrence toDomainEntity(@NonNull JobFormViewEntity.Recurrence domainEntity) {
        return JobCalendarRecurrence.INSTANCE;
    }
}

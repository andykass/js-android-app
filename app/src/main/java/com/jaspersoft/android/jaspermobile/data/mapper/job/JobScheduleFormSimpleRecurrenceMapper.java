package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.data.mapper.DataEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.EndDate;
import com.jaspersoft.android.sdk.service.data.schedule.IntervalRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.RecurrenceIntervalUnit;
import com.jaspersoft.android.sdk.service.data.schedule.RepeatedEndDate;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;
import com.jaspersoft.android.sdk.service.data.schedule.UntilEndDate;

import java.util.Date;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormSimpleRecurrenceMapper implements DataEntityMapper<JobScheduleForm.Recurrence, Trigger> {

    @NonNull
    private final JobScheduleFormRecurrenceUnitMapper unitMapper;

    @VisibleForTesting
    JobScheduleFormSimpleRecurrenceMapper(@NonNull JobScheduleFormRecurrenceUnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }

    public static JobScheduleFormSimpleRecurrenceMapper create() {
        JobScheduleFormRecurrenceUnitMapper unitMapper = new JobScheduleFormRecurrenceUnitMapper();
        return new JobScheduleFormSimpleRecurrenceMapper(unitMapper);
    }

    @NonNull
    @Override
    public Trigger toDataEntity(@NonNull JobScheduleForm.Recurrence domainEntity) {
        JobSimpleRecurrence recurrence = (JobSimpleRecurrence) domainEntity;

        RecurrenceIntervalUnit recurrenceIntervalUnit = unitMapper.toDataEntity(recurrence.unit());
        IntervalRecurrence intervalRecurrence = new IntervalRecurrence.Builder()
                .withInterval(recurrence.interval())
                .withUnit(recurrenceIntervalUnit)
                .build();

        Trigger.SimpleTriggerBuilder simpleTriggerBuilder = new Trigger.Builder()
                .withRecurrence(intervalRecurrence);

        Date endDate = recurrence.untilDate();
        if (endDate != null) {
            simpleTriggerBuilder.withEndDate(new UntilEndDate(endDate));
            return simpleTriggerBuilder.build();
        }

        Integer occurrence = recurrence.occurrence();
        if (occurrence != null) {
            simpleTriggerBuilder.withEndDate(new RepeatedEndDate(occurrence));
            return simpleTriggerBuilder.build();
        }

        return simpleTriggerBuilder.build();
    }

    @NonNull
    @Override
    public JobScheduleForm.Recurrence toDomainEntity(@NonNull Trigger dataEntity) {
        IntervalRecurrence intervalRecurrence = (IntervalRecurrence) dataEntity.getRecurrence();
        EndDate endDate = dataEntity.getEndDate();

        int interval = intervalRecurrence.getInterval();
        JobSimpleRecurrence.Unit unit = unitMapper.toDomainEntity(intervalRecurrence.getUnit());

        JobSimpleRecurrence.Builder recurrenceBuilder = JobSimpleRecurrence.builder()
                .interval(interval)
                .unit(unit);

        if (endDate instanceof UntilEndDate) {
            UntilEndDate date = (UntilEndDate) endDate;
            recurrenceBuilder.untilDate(date.getSpecifiedDate());
        }
        if (endDate instanceof RepeatedEndDate) {
            RepeatedEndDate date = (RepeatedEndDate) endDate;
            recurrenceBuilder.occurrence(date.getOccurrenceCount());
        }

        return recurrenceBuilder.build();
    }
}

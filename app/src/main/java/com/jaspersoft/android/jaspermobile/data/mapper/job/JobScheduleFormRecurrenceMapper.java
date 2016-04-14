package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.data.mapper.DataEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.CalendarRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.IntervalRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.Recurrence;
import com.jaspersoft.android.sdk.service.data.schedule.Trigger;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormRecurrenceMapper implements DataEntityMapper<JobScheduleForm.Recurrence, Trigger> {

    @NonNull
    private final JobScheduleFormSimpleRecurrenceMapper simpleRecurrenceMapper;
    @NonNull
    private final JobScheduleFormCalendarRecurrenceMapper calendarRecurrenceMapper;
    @NonNull
    private final JobScheduleFormNoneRecurrenceMapper noneRecurrenceMapper;

    @VisibleForTesting
    JobScheduleFormRecurrenceMapper(
            @NonNull JobScheduleFormSimpleRecurrenceMapper simpleRecurrenceMapper,
            @NonNull JobScheduleFormCalendarRecurrenceMapper calendarRecurrenceMapper,
            @NonNull JobScheduleFormNoneRecurrenceMapper noneRecurrenceMapper
    ) {
        this.simpleRecurrenceMapper = simpleRecurrenceMapper;
        this.calendarRecurrenceMapper = calendarRecurrenceMapper;
        this.noneRecurrenceMapper = noneRecurrenceMapper;
    }

    public static JobScheduleFormRecurrenceMapper create() {
        JobScheduleFormSimpleRecurrenceMapper simpleRecurrenceMapper = JobScheduleFormSimpleRecurrenceMapper.create();
        JobScheduleFormCalendarRecurrenceMapper calendarRecurrenceMapper = new JobScheduleFormCalendarRecurrenceMapper();
        JobScheduleFormNoneRecurrenceMapper noneRecurrenceMapper = new JobScheduleFormNoneRecurrenceMapper();
        return new JobScheduleFormRecurrenceMapper(
                simpleRecurrenceMapper,
                calendarRecurrenceMapper,
                noneRecurrenceMapper
        );
    }

    @NonNull
    @Override
    public Trigger toDataEntity(@NonNull JobScheduleForm.Recurrence domainEntity) {
        if (domainEntity instanceof JobNoneRecurrence) {
            return noneRecurrenceMapper.toDataEntity(domainEntity);
        }
        if (domainEntity instanceof JobSimpleRecurrence) {
            return simpleRecurrenceMapper.toDataEntity(domainEntity);
        }
        if (domainEntity instanceof JobCalendarRecurrence) {
            return calendarRecurrenceMapper.toDataEntity(domainEntity);
        }
        throw new UnsupportedOperationException("Impossible to map domain recurrence of type: " + domainEntity.getClass());
    }

    @NonNull
    @Override
    public JobScheduleForm.Recurrence toDomainEntity(@Nullable Trigger dataEntity) {
        if (dataEntity == null) {
            return noneRecurrenceMapper.toDomainEntity(dataEntity);
        }
        Recurrence recurrence = dataEntity.getRecurrence();
        if (recurrence instanceof IntervalRecurrence) {
            return simpleRecurrenceMapper.toDomainEntity(dataEntity);
        }
        if (recurrence instanceof CalendarRecurrence) {
            return calendarRecurrenceMapper.toDomainEntity(dataEntity);
        }
        throw new UnsupportedOperationException("Impossible to map data recurrence of type: " + dataEntity.getClass());
    }
}

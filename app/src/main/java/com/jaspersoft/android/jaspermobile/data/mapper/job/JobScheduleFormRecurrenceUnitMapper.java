package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.data.mapper.DataEntityMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.sdk.service.data.schedule.RecurrenceIntervalUnit;

/**
 * @author Tom Koptel
 * @since 2.5
 */
class JobScheduleFormRecurrenceUnitMapper implements DataEntityMapper<JobSimpleRecurrence.Unit, RecurrenceIntervalUnit> {
    @NonNull
    @Override
    public RecurrenceIntervalUnit toDataEntity(@NonNull JobSimpleRecurrence.Unit unit) {
       return RecurrenceIntervalUnit.valueOf(unit.name());
    }

    @NonNull
    @Override
    public JobSimpleRecurrence.Unit toDomainEntity(@NonNull RecurrenceIntervalUnit domainEntity) {
        return JobSimpleRecurrence.Unit.valueOf(domainEntity.name());
    }
}

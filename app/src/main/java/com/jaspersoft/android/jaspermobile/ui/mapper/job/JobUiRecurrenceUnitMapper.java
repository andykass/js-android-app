package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.ui.entity.job.SimpleViewRecurrence;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiCollectionEntityMapper;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JobUiRecurrenceUnitMapper extends UiCollectionEntityMapper<JobSimpleRecurrence.Unit, SimpleViewRecurrence.Unit> {

    private final EntityLocalizer<JobSimpleRecurrence.Unit> entityLocalizer;

    @VisibleForTesting
    JobUiRecurrenceUnitMapper(EntityLocalizer<JobSimpleRecurrence.Unit> entityLocalizer) {
        this.entityLocalizer = entityLocalizer;
    }

    @NonNull
    public static JobUiRecurrenceUnitMapper create(@NonNull Context context) {
        IntervalUnitLocalizer unitLocalizer = new IntervalUnitLocalizer(context);
        return new JobUiRecurrenceUnitMapper(unitLocalizer);
    }

    @NonNull
    @Override
    public SimpleViewRecurrence.Unit toUiEntity(@NonNull JobSimpleRecurrence.Unit unit) {
        String localizedLabel = entityLocalizer.localize(unit);
        return SimpleViewRecurrence.Unit.create(unit.name(), localizedLabel);
    }

    @NonNull
    @Override
    public JobSimpleRecurrence.Unit toDomainEntity(@NonNull SimpleViewRecurrence.Unit domainEntity) {
        return JobSimpleRecurrence.Unit.valueOf(domainEntity.rawValue());
    }
}

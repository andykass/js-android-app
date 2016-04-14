package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class IntervalUnitLocalizer implements EntityLocalizer<JobSimpleRecurrence.Unit> {

    private final Context context;

    public IntervalUnitLocalizer(@ApplicationContext Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String localize(@NonNull JobSimpleRecurrence.Unit unit) {
        switch (unit) {
            case MINUTE:
                return context.getString(R.string.sr_recurrence_simple_unit_minute);
            case HOUR:
                return context.getString(R.string.sr_recurrence_simple_unit_hour);
            case DAY:
                return context.getString(R.string.sr_recurrence_simple_unit_day);
            case WEEK:
                return context.getString(R.string.sr_recurrence_simple_unit_week);
        }
        throw new UnsupportedOperationException("Can not find localization for unit type: " + unit);
    }
}
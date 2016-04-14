package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobCalendarRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobNoneRecurrence;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
final class RecurrenceEntityLocalizer implements EntityLocalizer<JobScheduleForm.Recurrence> {

    private final Context context;

    @Inject
    public RecurrenceEntityLocalizer(@ApplicationContext Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String localize(@NonNull JobScheduleForm.Recurrence recurrence) {
        if (recurrence instanceof JobNoneRecurrence) {
            return context.getString(R.string.sr_recurrence_none);
        }
        if (recurrence instanceof JobSimpleRecurrence) {
            return context.getString(R.string.sr_recurrence_simple);
        }
        if (recurrence instanceof JobCalendarRecurrence) {
            return context.getString(R.string.sr_recurrence_calendar);
        }
        throw new UnsupportedOperationException("There is no localization for recurrence of type: " + recurrence.getClass());
    }
}

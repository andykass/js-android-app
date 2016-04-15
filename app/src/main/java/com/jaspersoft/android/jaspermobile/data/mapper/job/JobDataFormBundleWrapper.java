package com.jaspersoft.android.jaspermobile.data.mapper.job;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleForm;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSimpleRecurrence;

import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public class JobDataFormBundleWrapper {
    @NonNull
    private final JobScheduleRecurrenceFactory recurrenceFactory;
    @NonNull
    private final JobScheduleIntervalUnitFactory intervalUnitFactory;
    @NonNull
    private final JobScheduleFormatsFactory scheduleFormatsFactory;

    @VisibleForTesting
    JobDataFormBundleWrapper(
            @NonNull JobScheduleRecurrenceFactory recurrenceFactory,
            @NonNull JobScheduleIntervalUnitFactory intervalUnitFactory,
            @NonNull JobScheduleFormatsFactory scheduleFormatsFactory) {
        this.recurrenceFactory = recurrenceFactory;
        this.intervalUnitFactory = intervalUnitFactory;
        this.scheduleFormatsFactory = scheduleFormatsFactory;
    }

    @NonNull
    public static JobDataFormBundleWrapper create() {
        JobScheduleRecurrenceFactory recurrenceFactory = new JobScheduleRecurrenceFactory();
        JobScheduleIntervalUnitFactory intervalUnitFactory = new JobScheduleIntervalUnitFactory();
        JobScheduleFormatsFactory scheduleFormatsFactory = new JobScheduleFormatsFactory();
        return new JobDataFormBundleWrapper(
                recurrenceFactory,
                intervalUnitFactory,
                scheduleFormatsFactory
        );
    }

    @NonNull
    public JobScheduleBundle wrap(@NonNull JobScheduleForm form) {
        List<JobScheduleForm.Recurrence> recurrences = recurrenceFactory.generate(form.recurrence());
        List<JobSimpleRecurrence.Unit> units = intervalUnitFactory.generate();
        List<JobScheduleForm.OutputFormat> formats = scheduleFormatsFactory.generate();

        JobScheduleBundle.Builder builder = JobScheduleBundle.builder();
        builder.allRecurrences(recurrences);
        builder.allIntervalUnits(units);
        builder.allFormats(formats);
        builder.form(form);
        builder.allDays(JobScheduleRecurrenceFactory.CalendarRecurrenceFactory.ALL_DAYS);
        builder.allMonths(JobScheduleRecurrenceFactory.CalendarRecurrenceFactory.ALL_MONTHS);

        return builder.build();
    }
}

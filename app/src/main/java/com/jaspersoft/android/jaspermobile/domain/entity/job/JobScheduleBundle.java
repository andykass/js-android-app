package com.jaspersoft.android.jaspermobile.domain.entity.job;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class JobScheduleBundle {

    @NonNull
    public abstract List<JobScheduleForm.OutputFormat> allFormats();

    @NonNull
    public abstract List<JobSimpleRecurrence.Unit> allIntervalUnits();

    @NonNull
    public abstract List<JobScheduleForm.Recurrence> allRecurrences();

    @NonNull
    public abstract List<Integer> allMonths();

    @NonNull
    public abstract List<Integer> allDays();

    @NonNull
    public abstract JobScheduleForm form();


    public static Builder builder() {
        return new AutoValue_JobScheduleBundle.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder allFormats(@NonNull List<JobScheduleForm.OutputFormat> formats);

        public abstract Builder allIntervalUnits(@NonNull List<JobSimpleRecurrence.Unit> units);

        public abstract Builder allRecurrences(@NonNull List<JobScheduleForm.Recurrence> units);

        public abstract Builder allMonths(@NonNull List<Integer> units);

        public abstract Builder allDays(@NonNull List<Integer> days);

        public abstract Builder form(@NonNull JobScheduleForm months);

        public abstract JobScheduleBundle build();
    }
}

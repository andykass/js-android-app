package com.jaspersoft.android.jaspermobile.ui.entity.job;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class JobFormViewBundle implements Parcelable {
    @NonNull
    public abstract List<JobFormViewEntity.OutputFormat> allFormats();

    @NonNull
    public abstract List<SimpleViewRecurrence.Unit> allIntervalUnits();

    @NonNull
    public abstract List<JobFormViewEntity.Recurrence> allRecurrences();

    @NonNull
    public abstract List<CalendarViewRecurrence.Day> allDays();

    @NonNull
    public abstract List<CalendarViewRecurrence.Month> allMonths();

    @NonNull
    public abstract JobFormViewEntity form();

    @NonNull
    public Builder newBuilder() {
        return new AutoValue_JobFormViewBundle.Builder(this);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_JobFormViewBundle.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder allFormats(@NonNull List<JobFormViewEntity.OutputFormat> formats);

        public abstract Builder allIntervalUnits(@NonNull List<SimpleViewRecurrence.Unit> units);

        public abstract Builder allRecurrences(@NonNull List<JobFormViewEntity.Recurrence> units);

        public abstract Builder allDays(@NonNull List<CalendarViewRecurrence.Day> days);

        public abstract Builder allMonths(@NonNull List<CalendarViewRecurrence.Month> months);

        public abstract Builder form(@NonNull JobFormViewEntity form);

        public abstract JobFormViewBundle build();
    }
}

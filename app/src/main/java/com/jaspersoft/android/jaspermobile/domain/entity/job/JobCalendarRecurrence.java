package com.jaspersoft.android.jaspermobile.domain.entity.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class JobCalendarRecurrence implements JobScheduleForm.Recurrence {
    @NonNull
    public abstract List<Integer> months();

    @Nullable
    public abstract List<Integer> daysInWeek();

    @Nullable
    public abstract String daysInMonth();

    @NonNull
    public abstract String hours();

    @NonNull
    public abstract String minutes();

    @Nullable
    public abstract Date endDate();

    @NonNull
    public Builder newBuilder() {
        return new AutoValue_JobCalendarRecurrence.Builder(this);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_JobCalendarRecurrence.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder months(@NonNull List<Integer> months);

        public abstract Builder daysInWeek(@Nullable List<Integer> daysInWeek);

        public abstract Builder daysInMonth(@Nullable String daysInMonth);

        public abstract Builder hours(@NonNull String hours);

        public abstract Builder minutes(@NonNull String minutes);

        public abstract Builder endDate(@Nullable Date endDate);

        public abstract JobCalendarRecurrence build();
    }
}

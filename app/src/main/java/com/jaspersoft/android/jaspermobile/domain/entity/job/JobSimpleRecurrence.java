package com.jaspersoft.android.jaspermobile.domain.entity.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Date;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class JobSimpleRecurrence implements JobScheduleForm.Recurrence {

    public abstract int interval();

    @Nullable
    public abstract Integer occurrence();

    @NonNull
    public abstract Unit unit();

    @Nullable
    public abstract Date untilDate();

    @NonNull
    public final Builder newBuilder() {
        return new AutoValue_JobSimpleRecurrence.Builder(this);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_JobSimpleRecurrence.Builder();
    }

    public enum Unit {
        MINUTE, HOUR, DAY, WEEK
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder interval(int interval);

        public abstract Builder unit(@NonNull Unit unit);

        public abstract Builder occurrence(@Nullable Integer occurrence);

        public abstract Builder untilDate(@Nullable Date date);

        public abstract JobSimpleRecurrence build();
    }
}

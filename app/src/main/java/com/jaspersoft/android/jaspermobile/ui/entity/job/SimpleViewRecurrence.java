package com.jaspersoft.android.jaspermobile.ui.entity.job;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class SimpleViewRecurrence extends JobFormViewEntity.Recurrence implements Parcelable {
    @NonNull
    public final String unitAsString() {
        return unit().toString();
    }

    @NonNull
    public final String intervalAsString() {
        return String.valueOf(interval());
    }

    @NonNull
    public String occurrenceAsString() {
        return String.valueOf(occurrence());
    }

    @NonNull
    public final Calendar untilDateAsCalendar() {
        Calendar instance = Calendar.getInstance();
        Date date = untilDate();
        if (date != null) {
            instance.setTime(date);
        }
        return instance;
    }

    public final boolean hasUntilDate() {
        return untilDate() != null;
    }

    public final boolean runsIndefinitely() {
        Integer occurrence = occurrence();
        boolean noEndDate = !hasUntilDate();
        boolean occurrenceMissing = occurrence == null || occurrence < 0;
        return noEndDate && occurrenceMissing;
    }

    public final boolean runsTillDate() {
        return hasUntilDate();
    }

    public final boolean runsByOccurrences() {
        Integer occurrence = occurrence();
        return occurrence != null && occurrence >= 1 && !hasUntilDate();
    }

    @NonNull
    public final Builder newBuilder() {
        return new AutoValue_SimpleViewRecurrence.Builder(this);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_SimpleViewRecurrence.Builder();
    }

    public abstract int interval();

    @Nullable
    public abstract Integer occurrence();

    @Nullable
    public abstract Date untilDate();

    @NonNull
    public abstract Unit unit();

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder interval(int interval);

        public abstract Builder occurrence(@Nullable Integer occurrence);

        public abstract Builder untilDate(@Nullable Date date);

        public abstract Builder localizedLabel(@NonNull String label);

        public abstract Builder unit(@NonNull Unit unit);

        public abstract SimpleViewRecurrence build();
    }

    @AutoValue
    public static abstract class Unit implements Parcelable {
        @NonNull
        public abstract String rawValue();

        @NonNull
        public abstract String localizedLabel();

        @Override
        public String toString() {
            return localizedLabel();
        }

        @NonNull
        public static Unit create(String rawValue, String localizedLabel) {
            return new AutoValue_SimpleViewRecurrence_Unit(rawValue, localizedLabel);
        }
    }
}

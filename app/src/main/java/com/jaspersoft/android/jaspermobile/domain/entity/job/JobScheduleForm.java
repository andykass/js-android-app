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
public abstract class JobScheduleForm {
    public abstract int id();

    public abstract int version();

    @NonNull
    public abstract String source();

    @NonNull
    public abstract String jobName();

    @Nullable
    public abstract String description();

    @NonNull
    public abstract String fileName();

    @NonNull
    public abstract String folderUri();

    @NonNull
    public abstract List<OutputFormat> outputFormats();

    @Nullable
    public abstract Date startDate();

    @NonNull
    public abstract Recurrence recurrence();

    @NonNull
    public final Builder newBuilder() {
        return new AutoValue_JobScheduleForm.Builder(this);
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_JobScheduleForm.Builder();
    }

    public enum OutputFormat {
        PDF, HTML, XLS, RTF, CSV, ODT, TXT, DOCX, ODS, XLSX, XLS_NOPAG, XLSX_NOPAG, DATA_SNAPSHOT, PPTX
    }

    public interface Recurrence {
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder id(int id);

        public abstract Builder version(int version);

        public abstract Builder source(@NonNull String source);

        public abstract Builder jobName(@NonNull String jobName);

        public abstract Builder description(@Nullable String description);

        public abstract Builder fileName(@NonNull String fileName);

        public abstract Builder folderUri(@NonNull String outputPath);

        public abstract Builder outputFormats(@NonNull List<OutputFormat> formats);

        public abstract Builder startDate(@Nullable Date startDate);

        public abstract Builder recurrence(@NonNull Recurrence recurrence);

        public abstract JobScheduleForm build();
    }
}

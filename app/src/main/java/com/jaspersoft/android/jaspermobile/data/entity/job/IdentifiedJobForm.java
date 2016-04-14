package com.jaspersoft.android.jaspermobile.data.entity.job;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class IdentifiedJobForm {
    public abstract int id();

    @NonNull
    public abstract JobForm form();

    public static IdentifiedJobForm create(int id, JobForm form) {
        return new AutoValue_IdentifiedJobForm(id, form);
    }
}

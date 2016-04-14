package com.jaspersoft.android.jaspermobile.ui.entity.job;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@AutoValue
public abstract class NoneViewRecurrence extends JobFormViewEntity.Recurrence {
    @NonNull
    public static NoneViewRecurrence create(String localizedLabel) {
        return new AutoValue_NoneViewRecurrence(localizedLabel);
    }
}

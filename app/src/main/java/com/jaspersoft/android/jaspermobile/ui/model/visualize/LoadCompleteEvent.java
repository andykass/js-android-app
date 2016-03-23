package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class LoadCompleteEvent {
    @NonNull
    private final String mParameters;

    public LoadCompleteEvent(@NonNull String parameters) {
        mParameters = parameters;
    }

    @NonNull
    public String getParameters() {
        return mParameters;
    }
}

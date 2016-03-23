package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class ErrorEvent {
    @NonNull
    private final String mErrorMessage;

    public ErrorEvent(@NonNull String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public boolean isEmptyPage() {
        return mErrorMessage.matches("Resource .* not found.");
    }

    @NonNull
    public String getErrorMessage() {
        return mErrorMessage;
    }
}

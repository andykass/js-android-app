package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class ExecutionReferenceClickEvent {
    @NonNull
    private final String mReportData;

    public ExecutionReferenceClickEvent(@NonNull String reportData) {
        mReportData = reportData;
    }

    @NonNull
    public String getReportData() {
        return mReportData;
    }
}

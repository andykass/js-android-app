package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class PageLoadErrorEvent extends ErrorEvent {
    private final int mTargetPage;

    public PageLoadErrorEvent(@NonNull String errorMessage, int targetPage) {
        super(errorMessage);
        mTargetPage = targetPage;
    }

    public int getPage() {
        return mTargetPage;
    }
}

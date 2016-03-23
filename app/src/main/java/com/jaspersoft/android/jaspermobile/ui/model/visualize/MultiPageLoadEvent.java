package com.jaspersoft.android.jaspermobile.ui.model.visualize;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class MultiPageLoadEvent {
    private final boolean mIsMultiPage;

    public MultiPageLoadEvent(boolean isMultiPage) {
        mIsMultiPage = isMultiPage;
    }

    public boolean isMultiPage() {
        return mIsMultiPage;
    }
}

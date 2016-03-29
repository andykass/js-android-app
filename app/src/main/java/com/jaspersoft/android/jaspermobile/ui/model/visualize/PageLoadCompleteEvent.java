package com.jaspersoft.android.jaspermobile.ui.model.visualize;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class PageLoadCompleteEvent {
    private final int mPage;

    public PageLoadCompleteEvent(int page) {
        mPage = page;
    }

    public int getPage() {
        return mPage;
    }
}

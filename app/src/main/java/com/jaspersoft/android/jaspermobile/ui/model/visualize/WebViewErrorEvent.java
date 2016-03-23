package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class WebViewErrorEvent {
    @NonNull
    private final String title;
    @NonNull
    private final String message;

    public WebViewErrorEvent(@NonNull String title, @NonNull String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}

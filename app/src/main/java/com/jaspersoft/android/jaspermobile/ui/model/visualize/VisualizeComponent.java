package com.jaspersoft.android.jaspermobile.ui.model.visualize;


import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface VisualizeComponent {
    @NonNull
    VisualizeEvents visualizeEvents();

    @NonNull
    VisualizeComponent run(@NonNull VisualizeExecOptions options);

    @NonNull
    VisualizeComponent loadPage(String page);

    @NonNull
    VisualizeComponent update(@NonNull String jsonParams);

    @NonNull
    VisualizeComponent refresh();
}

package com.jaspersoft.android.jaspermobile.presentation.model.visualize;


import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface VisualizeComponent {
    @NonNull
    VisualizeEvents visualizeEvents();

    @NonNull
    VisualizeComponent run(@NonNull String jsonParams);

    @NonNull
    VisualizeComponent loadPage(int page);

    @NonNull
    VisualizeComponent update(@NonNull String jsonParams);

    @NonNull
    VisualizeComponent refresh();
}

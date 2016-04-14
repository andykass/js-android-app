package com.jaspersoft.android.jaspermobile.ui.mapper;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface EntityLocalizer<Entity> {
    @NonNull
    String localize(@NonNull Entity type);
}

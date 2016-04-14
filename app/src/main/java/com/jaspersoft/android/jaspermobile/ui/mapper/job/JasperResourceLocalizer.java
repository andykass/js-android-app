package com.jaspersoft.android.jaspermobile.ui.mapper.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.ui.mapper.EntityLocalizer;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

/**
 * @author Tom Koptel
 * @since 2.5
 */
final class JasperResourceLocalizer implements EntityLocalizer<JasperResource> {
    private final Context context;

    public JasperResourceLocalizer(@ApplicationContext Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public String localize(@NonNull JasperResource type) {
        return context.getString(R.string.sch_new);
    }
}

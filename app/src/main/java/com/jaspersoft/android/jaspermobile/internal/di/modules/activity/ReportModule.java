package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public class ReportModule {
    private final String mUri;

    public ReportModule(String uri) {
        mUri = uri;
    }

    @Provides
    @Named("report_uri")
    @PerActivity
    String provideUri() {
        return mUri;
    }
}

package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.contract.RestReportContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.ReportViewPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class ReportRestViewerModule extends ReportModule {
    public ReportRestViewerModule(String uri) {
        super(uri);
    }

    @Provides
    @PerActivity
    RestReportContract.Action provideReportActionListener(ReportViewPresenter presenter) {
        return presenter;
    }
}

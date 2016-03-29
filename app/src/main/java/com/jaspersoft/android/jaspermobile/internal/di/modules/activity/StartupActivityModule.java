package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import android.app.Activity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.contract.StartupContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.StartupPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class StartupActivityModule extends ActivityModule {
    public StartupActivityModule(Activity activity) {
        super(activity);
    }

    @Provides
    @PerActivity
    StartupContract.ActionListener providesActionListener(StartupPresenter startupPresenter) {
        return startupPresenter;
    }
}

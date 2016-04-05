package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import android.support.v4.app.FragmentActivity;

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
    public StartupActivityModule(FragmentActivity activity) {
        super(activity);
    }

    @Provides
    @PerActivity
    StartupContract.ActionListener providesActionListener(StartupPresenter startupPresenter) {
        return startupPresenter;
    }
}

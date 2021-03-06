package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import android.app.Activity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.presentation.contract.NavigationContract;
import com.jaspersoft.android.jaspermobile.presentation.presenter.NavigationPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class NavigationActivityModule extends ActivityModule {
    public NavigationActivityModule(Activity activity) {
        super(activity);
    }

    @PerActivity
    @Provides
    NavigationContract.ActionListener provideActionListener(NavigationPresenter presenter) {
        return presenter;
    }
}

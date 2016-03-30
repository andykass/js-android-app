package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import android.support.v4.app.FragmentActivity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.contract.NavigationContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.NavigationPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class NavigationActivityModule extends ActivityModule {
    public NavigationActivityModule(FragmentActivity activity) {
        super(activity);
    }

    @PerActivity
    @Provides
    NavigationContract.ActionListener provideActionListener(NavigationPresenter presenter) {
        return presenter;
    }
}

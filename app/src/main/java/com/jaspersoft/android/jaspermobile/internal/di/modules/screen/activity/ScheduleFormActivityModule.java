package com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.ScheduleFormPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@Module
public class ScheduleFormActivityModule extends ActivityModule {

    private final Fragment mParent;

    public ScheduleFormActivityModule(Fragment parent) {
        super(parent.getActivity());
        mParent = parent;
    }

    @Provides
    @PerActivity
    ScheduleFormContract.EventListener providesEventListener(ScheduleFormPresenter presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    FragmentManager supportFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @Provides
    @PerActivity
    Fragment parentFragment() {
        return mParent;
    }
}

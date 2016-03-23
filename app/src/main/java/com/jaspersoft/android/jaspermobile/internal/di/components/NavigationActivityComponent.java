package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.NavigationActivityModule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
@Subcomponent(
        modules = {
                NavigationActivityModule.class
        }
)
public interface NavigationActivityComponent {
    void inject(NavigationActivity navigationActivity);
}

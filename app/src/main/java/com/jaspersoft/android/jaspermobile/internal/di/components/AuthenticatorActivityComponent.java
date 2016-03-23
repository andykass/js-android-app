package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.AuthenticatorModule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.AuthenticatorActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.AuthenticatorFragment;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
@Subcomponent(
        modules = {
                AuthenticatorModule.class,
                ActivityModule.class
        }
)
public interface AuthenticatorActivityComponent {
    void inject(AuthenticatorActivity activity);
    void inject(AuthenticatorFragment authenticatorFragment);
}

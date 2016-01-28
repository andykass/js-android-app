package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.MultiSelectActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.SingleSelectActivity;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
@Subcomponent(
        modules = {
                ReportModule.class,
                ActivityModule.class
        }
)
public interface ControlsActivityComponent {
    void inject(InputControlsActivity controlsActivity);
    void inject(SingleSelectActivity singleSelectActivity);
    void inject(MultiSelectActivity multiSelectActivity);
}

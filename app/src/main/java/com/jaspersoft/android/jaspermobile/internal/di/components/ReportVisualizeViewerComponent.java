package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportVisualizeViewerModule;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ReportVisualizeFragment;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
@Subcomponent(
        modules = {
                ActivityModule.class,
                ReportVisualizeViewerModule.class,
        }
)
public interface ReportVisualizeViewerComponent {
    void inject(ReportVisualizeFragment reportVisualizeFragment);
}

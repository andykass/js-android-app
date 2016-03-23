package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportRestViewerModule;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ReportViewFragment;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
@Subcomponent(
        modules = {
                ActivityModule.class,
                ReportRestViewerModule.class,
        }
)
public interface ReportRestViewerComponent {
    void inject(ReportViewFragment reportViewFragment);
}

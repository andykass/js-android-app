package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.internal.di.PerReport;
import com.jaspersoft.android.jaspermobile.internal.di.modules.ReportModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerReport
@Subcomponent(
        modules = ReportModule.class
)
public interface ReportComponent {
    ReportActivityComponent plusReportActivity(ActivityModule activityModule);
    ControlsActivityComponent plusControlsActivity(ActivityModule activityModule);
}

package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.content.Intent;

import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.ChooseReportActivity;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;


/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class ChooseJobPage extends Page {
    public ChooseJobPage(Context context) {
        super(context);
    }

    @Override
    Intent getIntent() {
        return new Intent(getContext(), ChooseReportActivity.class);
    }
}

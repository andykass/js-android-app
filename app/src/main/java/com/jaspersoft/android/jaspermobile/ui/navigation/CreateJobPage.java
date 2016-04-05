package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.content.Intent;

import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.NewScheduleActivity_;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public final class CreateJobPage extends Page{

    private final JasperResource mJasperResource;

    protected CreateJobPage(Context context, JasperResource jasperResource) {
        super(context);
        this.mJasperResource = jasperResource;
    }

    @Override
    Intent getIntent() {
        return NewScheduleActivity_
                .intent(getContext())
                .jasperResource(mJasperResource)
                .get();
    }
}

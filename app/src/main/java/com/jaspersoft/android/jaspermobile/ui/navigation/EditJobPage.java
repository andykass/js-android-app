package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.content.Intent;

import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.EditScheduleActivity_;


/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public final class EditJobPage extends Page{

    private final int mJobId;

    protected EditJobPage(Context context, int mJobId) {
        super(context);
        this.mJobId = mJobId;
    }

    @Override
    Intent getIntent() {
        return EditScheduleActivity_
                .intent(getContext())
                .jobId(mJobId)
                .get();
    }
}

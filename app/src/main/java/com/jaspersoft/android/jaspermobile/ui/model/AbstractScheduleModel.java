package com.jaspersoft.android.jaspermobile.ui.model;

import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;

/**
 * @author Tom Koptel
 * @since 2.5
 */
abstract class AbstractScheduleModel implements ScheduleFormContract.Model {
    protected Callback mCallbacks;

    @Override
    public void bind(Callback callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void unbind() {
        mCallbacks = null;
    }
}

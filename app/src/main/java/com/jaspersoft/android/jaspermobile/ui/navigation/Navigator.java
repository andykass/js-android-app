package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.app.Activity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class Navigator {
    private final Activity mActivity;

    @Inject
    public Navigator(Activity activity) {
        mActivity = activity;
    }

    public void navigate(Page page, boolean finishCurrent) {
        start(page, finishCurrent);
    }

    public void navigateForResult(Page page, int result) {
        mActivity.startActivityForResult(page.getIntent(), result);
    }

    private void start(Page page, boolean finishCurrent) {
        if (finishCurrent) {
            mActivity.finish();
        }
        mActivity.startActivity(page.getIntent());
    }
}

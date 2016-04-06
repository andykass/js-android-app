package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.app.Activity;
import android.support.v4.app.Fragment;

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

    public final void navigate(Page page, boolean finishCurrent) {
        if (finishCurrent) {
            mActivity.finish();
        }
        startActivity(page);
    }

    public void navigateForResult(Page page, int result) {
        mActivity.startActivityForResult(page.getIntent(), result);
    }

    public void navigateUp(){
        mActivity.finish();
    }

    protected void startActivity(Page page) {
        mActivity.startActivity(page.getIntent());
    }
}

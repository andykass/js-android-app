package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.content.Intent;

import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;


/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class MainPage extends Page {
    public MainPage(Context context) {
        super(context);
    }

    @Override
    Intent getIntent() {
        return NavigationActivity_.intent(getContext())
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .get();
    }
}

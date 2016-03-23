package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.content.Intent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public abstract class Page {
    private final Context mContext;

    protected Page(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    abstract Intent getIntent();
}

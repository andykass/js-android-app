package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.internal.di.ActivityContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class PageFactory {
    private final Context mContext;

    @Inject
    public PageFactory(@ActivityContext Context context) {
        mContext = context;
    }

    @NonNull
    public Page createMainPage() {
        return new MainPage(mContext);
    }

    @NonNull
    public Page createSignUpPage() {
        return new SignUpPage(mContext);
    }
}

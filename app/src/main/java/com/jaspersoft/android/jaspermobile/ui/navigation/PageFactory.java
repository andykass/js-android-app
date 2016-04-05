package com.jaspersoft.android.jaspermobile.ui.navigation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.internal.di.ActivityContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

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

    @NonNull
    public Page createChooseJobPage() {
        return new ChooseJobPage(mContext);
    }

    @NonNull
    public Page createJobEditPage(int jobId) {
        return new EditJobPage(mContext, jobId);
    }

    @NonNull
    public Page createNewJobPage(JasperResource jasperResource) {
        return new CreateJobPage(mContext, jasperResource);
    }
}

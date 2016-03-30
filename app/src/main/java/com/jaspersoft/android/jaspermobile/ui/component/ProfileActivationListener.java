package com.jaspersoft.android.jaspermobile.ui.component;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.data.ComponentManager;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.util.cast.ResourcePresentationService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@Singleton
public final class ProfileActivationListener implements ComponentManager.Callback {
    private final Analytics mAnalytics;

    @Inject
    public ProfileActivationListener(Analytics analytics) {
        mAnalytics = analytics;
    }

    @Override
    public void onProfileActivation(Profile profile) {
        mAnalytics.sendUserChangedEvent();
        ResourcePresentationService.stopService();
    }
}

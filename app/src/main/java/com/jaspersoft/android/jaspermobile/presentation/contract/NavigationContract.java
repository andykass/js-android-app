package com.jaspersoft.android.jaspermobile.presentation.contract;

import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.presentation.model.ProfileViewModel;
import com.jaspersoft.android.jaspermobile.presentation.page.BasePageState;

import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface NavigationContract {
    interface View {
        void toggleRecentlyViewedNavigation(boolean visibility);
        void showProfiles(List<ProfileViewModel> profiles);
    }

    interface ActionListener {
        void loadProfiles();
        void loadActiveProfile();
        void activateProfile(Profile profile);
    }
}

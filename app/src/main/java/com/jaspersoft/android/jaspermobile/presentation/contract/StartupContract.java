package com.jaspersoft.android.jaspermobile.presentation.contract;

import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.presentation.page.BasePageState;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface StartupContract {
    interface View {
        BasePageState getState();
    }

    interface ActionListener {
        void tryToSetupProfile(int signUpRequestCode);

        void setupNewProfile(Profile profile);
    }
}

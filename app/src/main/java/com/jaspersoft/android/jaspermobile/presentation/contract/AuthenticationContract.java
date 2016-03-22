package com.jaspersoft.android.jaspermobile.presentation.contract;

import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;
import com.jaspersoft.android.jaspermobile.presentation.page.AuthPageState;
import com.jaspersoft.android.jaspermobile.presentation.view.LoadDataView;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface AuthenticationContract {
    interface View extends LoadDataView {
        void showAliasDuplicateError();
        void showAliasReservedError();
        void showAliasRequiredError();
        void showServerUrlFormatError();
        void showServerUrlRequiredError();
        void showUsernameRequiredError();
        void showPasswordRequiredError();
        void showServerVersionNotSupported();
        void showFailedToAddProfile(String message);
        void navigateToApp(Profile profile);
        void showTryDemo(boolean visible);

        AuthPageState getState();
    }

    interface ActionListener {
        void checkDemoAccountAvailability();
        void saveProfile(ProfileForm profileForm);
    }
}

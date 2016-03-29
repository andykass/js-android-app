package com.jaspersoft.android.jaspermobile.internal.di.modules.activity;

import com.jaspersoft.android.jaspermobile.data.validator.ProfileAuthorizedValidation;
import com.jaspersoft.android.jaspermobile.data.validator.ProfileReservedValidation;
import com.jaspersoft.android.jaspermobile.data.validator.ProfileUniquenessValidation;
import com.jaspersoft.android.jaspermobile.data.validator.ServerVersionValidation;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;
import com.jaspersoft.android.jaspermobile.domain.validator.ValidationRule;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.DuplicateProfileException;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.ProfileReservedException;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.ServerVersionNotSupportedException;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.contract.AuthenticationContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.AuthenticationPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class AuthenticatorModule {
    @PerActivity
    @Provides
    AuthenticationContract.ActionListener provideActionListener(AuthenticationPresenter presenter) {
        return presenter;
    }

    @PerActivity
    @Provides
    ValidationRule<JasperServer, ServerVersionNotSupportedException> providesVersionValidation(ServerVersionValidation validation) {
        return validation;
    }

    @PerActivity
    @Provides
    ValidationRule<Profile, DuplicateProfileException> providesDuplicateProfileValidation(ProfileUniquenessValidation validation) {
        return validation;
    }

    @PerActivity
    @Provides
    ValidationRule<Profile, ProfileReservedException> providesReservedProfileValidation(ProfileReservedValidation validation) {
        return validation;
    }

    @PerActivity
    @Provides
    ValidationRule<ProfileForm, Exception> providesProfileAuthorizedValidation(ProfileAuthorizedValidation validation) {
        return validation;
    }
}

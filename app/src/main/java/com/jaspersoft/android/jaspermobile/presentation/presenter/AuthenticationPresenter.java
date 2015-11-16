package com.jaspersoft.android.jaspermobile.presentation.presenter;

import com.jaspersoft.android.jaspermobile.domain.BaseCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.interactor.SaveProfile;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.DuplicateProfileException;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.ProfileReservedException;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.presentation.action.ProfileActionListener;
import com.jaspersoft.android.jaspermobile.presentation.mapper.CredentialsDataMapper;
import com.jaspersoft.android.jaspermobile.presentation.mapper.ProfileDataMapper;
import com.jaspersoft.android.jaspermobile.presentation.model.CredentialsModel;
import com.jaspersoft.android.jaspermobile.presentation.model.ProfileModel;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.CredentialsClientValidation;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.ProfileClientValidation;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.exception.AliasMissingException;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.exception.PasswordMissingException;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.exception.ServerUrlFormatException;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.exception.ServerUrlMissingException;
import com.jaspersoft.android.jaspermobile.presentation.model.validation.exception.UsernameMissingException;
import com.jaspersoft.android.jaspermobile.presentation.view.AuthenticationView;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public final class AuthenticationPresenter implements Presenter, ProfileActionListener {
    private AuthenticationView mView;

    private final SaveProfile mSaveProfileUseCase;
    private final ProfileDataMapper mProfileDataMapper;
    private final CredentialsDataMapper mCredentialsDataMapper;
    private final CredentialsClientValidation mCredentialsClientValidation;
    private final ProfileClientValidation mProfileClientValidation;

    @Inject
    public AuthenticationPresenter(SaveProfile saveProfileUseCase,
                                   ProfileDataMapper profileDataMapper,
                                   CredentialsDataMapper credentialsDataMapper,
                                   CredentialsClientValidation credentialsClientValidation,
                                   ProfileClientValidation profileClientValidation) {
        mSaveProfileUseCase = saveProfileUseCase;
        mProfileDataMapper = profileDataMapper;
        mCredentialsDataMapper = credentialsDataMapper;
        mCredentialsClientValidation = credentialsClientValidation;
        mProfileClientValidation = profileClientValidation;
    }

    public void setView(AuthenticationView view) {
        mView = view;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mSaveProfileUseCase.unsubscribe();
    }

    @Override
    public void saveProfile(ProfileModel profileModel) {
        if (isClientDataValid(profileModel)) {
            mView.hideRetry();
            mView.showLoading();

            Profile domainProfile = mProfileDataMapper.transform(profileModel);
            BaseCredentials domainCredentials = mCredentialsDataMapper.transform(profileModel.getCredentials());
            String baseUrl = profileModel.getBaseUrl();

            mSaveProfileUseCase.execute(baseUrl, domainProfile, domainCredentials, new ProfileSaveListener());
        }
    }

    private boolean isClientDataValid(ProfileModel profileModel) {
        return validateProfile(profileModel) && validateCredentials(profileModel.getCredentials());
    }

    private boolean validateCredentials(CredentialsModel credentialsModel) {
        try {
            mCredentialsClientValidation.validate(credentialsModel);
            return true;
        } catch (UsernameMissingException e) {
            mView.showUsernameRequiredError();
        } catch (PasswordMissingException e) {
            mView.showPasswordRequiredError();
        }
        return false;
    }

    private boolean validateProfile(ProfileModel profileModel) {
        try {
            mProfileClientValidation.validate(profileModel);
            return true;
        } catch (AliasMissingException e) {
            mView.showAliasRequiredError();
        } catch (ServerUrlMissingException e) {
            mView.showServerUrlRequiredError();
        } catch (ServerUrlFormatException e) {
            mView.showServerUrlFormatError();
        }
        return false;
    }

    void handleProfileComplete() {
        mView.hideLoading();
    }

    void handleProfileSaveFailure(Throwable e) {
        mView.hideLoading();
        if (e instanceof DuplicateProfileException) {
            mView.showAliasDuplicateError();
        } else if (e instanceof ProfileReservedException) {
            mView.showAliasReservedError();
        } else {
            mView.showError(e.getMessage());
        }
    }

    void handleProfileSaveSuccess() {
        mView.navigateToApp();
    }

    private class ProfileSaveListener extends Subscriber<Profile> {
        @Override
        public void onCompleted() {
            handleProfileComplete();
        }

        @Override
        public void onError(Throwable e) {
            handleProfileSaveFailure(e);
        }

        @Override
        public void onNext(Profile profile) {
            handleProfileSaveSuccess();
        }
    }
}

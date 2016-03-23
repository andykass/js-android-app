/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.DemoProfileExistsUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.SaveProfileUseCase;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.DuplicateProfileException;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.ProfileReservedException;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.ServerVersionNotSupportedException;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.contract.AuthenticationContract;
import com.jaspersoft.android.jaspermobile.ui.validation.AliasMissingException;
import com.jaspersoft.android.jaspermobile.ui.validation.PasswordMissingException;
import com.jaspersoft.android.jaspermobile.ui.validation.ProfileFormValidation;
import com.jaspersoft.android.jaspermobile.ui.validation.ServerUrlFormatException;
import com.jaspersoft.android.jaspermobile.ui.validation.ServerUrlMissingException;
import com.jaspersoft.android.jaspermobile.ui.validation.UsernameMissingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Subscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class AuthenticationPresenterTest {

    @Mock
    ProfileFormValidation profileFormValidation;
    @Mock
    RequestExceptionHandler mRequestExceptionHandler;

    @Mock
    AuthenticationContract.View mAuthenticationView;

    // Domain mock components
    @Mock
    ProfileForm mForm;
    @Mock
    Profile mProfile;
    @Mock
    AppCredentials mCredentials;

    @Mock
    DemoProfileExistsUseCase mDemoProfileExistsUseCase;

    AuthenticationPresenter presenterUnderTest;

    @Mock
    SaveProfileUseCase mSaveProfileUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        setupMocks();

        presenterUnderTest = new AuthenticationPresenter(
                mSaveProfileUseCase,
                profileFormValidation,
                mRequestExceptionHandler,
                mDemoProfileExistsUseCase
        );
        presenterUnderTest.injectView(mAuthenticationView);
    }

    private void setupMocks() {
        when(mForm.getProfile()).thenReturn(mProfile);
        when(mForm.getCredentials()).thenReturn(mCredentials);
        when(mForm.getServerUrl()).thenReturn("http://localhost");
    }

    @Test
    public void testSaveProfile() throws Exception {
        whenSaveProfile();

        thenShouldShowLoading();
        thenShouldExecuteSaveProfileCase();
    }

    @Test
    public void testPresenterHandlesDuplicateAliasCase() {
        whenHandlesSaveFailure(new DuplicateProfileException("any profile"));

        thenShouldHideLoading();
        thenShouldShowDuplicateError();
    }

    @Test
    public void testPresenterHandlesAliasReservedCase() throws Exception {
        whenHandlesSaveFailure(new ProfileReservedException());

        thenShouldHideLoading();
        thenShouldShowNameReservedError();
    }

    @Test
    public void testPresenterHandlesAliasMissing() throws Exception {
        givenFormValidationThrowsException(new AliasMissingException());

        whenSaveProfile();

        thenShouldShowAliasRequiredError();
    }

    @Test
    public void testPresenterHandlesServerUrlInvalidFormat() throws Exception {
        givenFormValidationThrowsException(new ServerUrlFormatException());

        whenSaveProfile();

        thenShouldShowServerUrlFormatError();
    }

    @Test
    public void testPresenterHandlesServerUrlMissing() throws Exception {
        givenFormValidationThrowsException(new ServerUrlMissingException());

        whenSaveProfile();

        thenShouldShowUrlReqiredError();
    }

    @Test
    public void testPresenterHandlesUsernameMissing() throws Exception {
        givenFormValidationThrowsException(new UsernameMissingException());

        whenSaveProfile();

        thenShouldShowUsernameReqiredError();
    }

    @Test
    public void testPresenterHandlesPasswordMissing() throws Exception {
        givenFormValidationThrowsException(new PasswordMissingException());

        whenSaveProfile();

        thenShouldShowPasswordReqiredError();
    }

    @Test
    public void testPresenterHandlesSeverVersionNotSupported() throws Exception {
        whenHandlesSaveFailure(new ServerVersionNotSupportedException("5.0"));

        thenShouldHideLoading();

        thenShouldShowVersionNotSupportedError();
    }

    @Test
    public void testPresenterUnsubscribesDuringDestroy() {
        whenPresenterDestroyed();

        thenShouldUnsubscribeSaveProfileCase();
    }

    @Test
    public void should_hide_try_demo_if_mobile_account_exists() throws Exception {
        givenAppHasMobileDemoProfile(true);

        whenChecksDemoAccountAvailability();

        thenShouldExecuteGetProfilesMetadataCase();
        thenShouldToggleTryDemoView(false);
    }

    @Test
    public void should_show_try_demo_if_mobile_account_exists() throws Exception {
        givenAppHasMobileDemoProfile(false);

        whenChecksDemoAccountAvailability();

        thenShouldExecuteGetProfilesMetadataCase();
        thenShouldToggleTryDemoView(true);
    }

    private void givenAppHasMobileDemoProfile(boolean has) {
        when(mDemoProfileExistsUseCase.execute()).thenReturn(has);
    }

    private void givenFormValidationThrowsException(Throwable throwable) throws Exception {
        doThrow(throwable).when(profileFormValidation).validate(mForm);
    }

    private void whenChecksDemoAccountAvailability() {
        presenterUnderTest.checkDemoAccountAvailability();
    }

    private void whenPresenterDestroyed() {
        presenterUnderTest.destroy();
    }

    private void whenSaveProfile() {
        presenterUnderTest.saveProfile(mForm);
    }

    private void thenShouldToggleTryDemoView(boolean show) {
        verify(mAuthenticationView).showTryDemo(show);
    }

    private void thenShouldExecuteGetProfilesMetadataCase() {
        verify(mDemoProfileExistsUseCase).execute();
    }

    private void thenShouldShowAliasRequiredError() {
        verify(mAuthenticationView).showAliasRequiredError();
    }

    private void thenShouldShowServerUrlFormatError() {
        verify(mAuthenticationView).showServerUrlFormatError();
    }

    private void thenShouldShowUrlReqiredError() {
        verify(mAuthenticationView).showServerUrlRequiredError();
    }

    private void thenShouldShowUsernameReqiredError() {
        verify(mAuthenticationView).showUsernameRequiredError();
    }

    private void thenShouldShowPasswordReqiredError() {
        verify(mAuthenticationView).showPasswordRequiredError();
    }

    private void thenShouldShowVersionNotSupportedError() {
        verify(mAuthenticationView).showServerVersionNotSupported();
    }

    private void thenShouldUnsubscribeSaveProfileCase() {
        verify(mSaveProfileUseCase).unsubscribe();
    }

    private void thenShouldShowNameReservedError() {
        verify(mAuthenticationView).showAliasReservedError();
    }

    private void thenShouldExecuteSaveProfileCase() {
        verify(mSaveProfileUseCase).execute(any(ProfileForm.class), any(Subscriber.class));
    }

    private void thenShouldShowDuplicateError() {
        verify(mAuthenticationView).showAliasDuplicateError();
    }

    private void whenHandlesSaveFailure(Throwable throwable) {
        presenterUnderTest.handleProfileSaveFailure(throwable);
    }

    private void thenShouldHideLoading() {
        verify(mAuthenticationView).hideLoading();
    }

    private void thenShouldShowLoading() {
        verify(mAuthenticationView).showLoading();
    }
}
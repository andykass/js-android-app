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

package com.jaspersoft.android.jaspermobile.ui.validation;

import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.ProfileForm;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class ProfileFormValidationTest {

    @Mock
    ProfileForm mProfileForm;

    private Profile mProfile = Profile.create("alias");
    private AppCredentials mCredentials = AppCredentials.builder()
            .setUsername("user")
            .setPassword("1234")
            .setOrganization("org")
            .create();

    private ProfileFormValidation mFormValidation;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mProfile = spy(mProfile);
        mCredentials = spy(mCredentials);

        when(mProfileForm.getProfile()).thenReturn(mProfile);
        when(mProfileForm.getCredentials()).thenReturn(mCredentials);
        when(mProfileForm.getServerUrl()).thenReturn("http://localhost");

        mFormValidation = new ProfileFormValidation();
    }

    @Test(expected = AliasMissingException.class)
    public void testThrowAliasMissingExceptionIfNull() throws Exception {
        when(mProfile.getKey()).thenReturn(null);
        validate();
    }

    @Test(expected = AliasMissingException.class)
    public void testThrowAliasMissingExceptionIfEmptyString() throws Exception {
        when(mProfile.getKey()).thenReturn("");
        validate();
    }

    @Test(expected = AliasMissingException.class)
    public void testThrowAliasMissingExceptionIfSpacedString() throws Exception {
        when(mProfile.getKey()).thenReturn(" ");
        validate();
    }

    @Test(expected = ServerUrlMissingException.class)
    public void testThrowServerUrlMissingExceptionIfNull() throws Exception {
        when(mProfileForm.getServerUrl()).thenReturn(null);
        validate();
    }

    @Test(expected = ServerUrlMissingException.class)
    public void testThrowServerUrlMissingExceptionIfEmptyString() throws Exception {
        when(mProfileForm.getServerUrl()).thenReturn("");
        validate();
    }

    @Test(expected = ServerUrlMissingException.class)
    public void testThrowServerUrlMissingExceptionIfSpacedString() throws Exception {
        when(mProfileForm.getServerUrl()).thenReturn(" ");
        validate();
    }

    @Test(expected = ServerUrlFormatException.class)
    public void testThrowServerUrlFormatExceptionIfUrlInvalid() throws Exception {
        when(mProfileForm.getServerUrl()).thenReturn("localhost.com");
        validate();
    }


    @Test(expected = UsernameMissingException.class)
    public void testThrowUsernameMissingExceptionIfNull() throws Exception {
        when(mCredentials.getUsername()).thenReturn(null);
        validate();
    }

    @Test(expected = PasswordMissingException.class)
    public void testThrowPasswordMissingExceptionIfNull() throws Exception {
        when(mCredentials.getPassword()).thenReturn(null);
        validate();
    }

    @Test(expected = UsernameMissingException.class)
    public void testThrowUsernameMissingExceptionIfEmptyString() throws Exception {
        when(mCredentials.getUsername()).thenReturn("");
        validate();
    }

    @Test(expected = PasswordMissingException.class)
    public void testThrowPasswordMissingExceptionIfEmptyString() throws Exception {
        when(mCredentials.getPassword()).thenReturn("");
        validate();
    }

    @Test(expected = UsernameMissingException.class)
    public void testThrowUsernameMissingExceptionIfSpacedString() throws Exception {
        when(mCredentials.getUsername()).thenReturn(" ");
        validate();
    }

    @Test(expected = PasswordMissingException.class)
    public void testThrowPasswordMissingExceptionIfSpacedString() throws Exception {
        when(mCredentials.getPassword()).thenReturn(" ");
        validate();
    }

    private void validate() throws Exception {
        mFormValidation.validate(mProfileForm);
    }
}
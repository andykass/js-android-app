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
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class ProfileFormValidation {

    @Inject
    public ProfileFormValidation() {
    }

    public void validate(ProfileForm form) throws UsernameMissingException,
            PasswordMissingException, AliasMissingException, ServerUrlMissingException, ServerUrlFormatException {
        Profile profile = form.getProfile();
        String alias = profile.getKey();
        if (alias == null || alias.trim().length() == 0) {
            throw new AliasMissingException();
        }
        String serverUrl = form.getServerUrl();
        if (serverUrl == null || serverUrl.trim().length() == 0) {
            throw new ServerUrlMissingException();
        }

        try {
            new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw new ServerUrlFormatException();
        }

        AppCredentials credentials = form.getCredentials();
        String username = credentials.getUsername();
        if (username == null ||
                username.trim().length() == 0) {
            throw new UsernameMissingException();
        }
        String password = credentials.getPassword();
        if (password == null ||
                password.trim().length() == 0) {
            throw new PasswordMissingException();
        }
    }
}

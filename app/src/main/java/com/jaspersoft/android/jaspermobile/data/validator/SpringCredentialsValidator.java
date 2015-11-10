/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.data.validator;

import com.jaspersoft.android.jaspermobile.domain.BaseCredentials;
import com.jaspersoft.android.jaspermobile.domain.validator.CredentialsValidator;
import com.jaspersoft.android.jaspermobile.domain.validator.exception.InvalidCredentialsException;
import com.jaspersoft.android.sdk.network.RestError;
import com.jaspersoft.android.sdk.service.auth.Credentials;
import com.jaspersoft.android.sdk.service.auth.JrsAuthenticator;
import com.jaspersoft.android.sdk.service.auth.SpringCredentials;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class SpringCredentialsValidator implements CredentialsValidator {
    private final JrsAuthenticator mAuthenticator;
    private final BaseCredentials mCredentials;

    @Inject
    public SpringCredentialsValidator(BaseCredentials baseCredentials,
                                      JrsAuthenticator authenticator) {
        mCredentials = baseCredentials;
        mAuthenticator = authenticator;
    }

    @Override
    public BaseCredentials validate() throws InvalidCredentialsException, RestError {
        Credentials springCredentials = SpringCredentials.builder()
                .password(mCredentials.getPassword())
                .username(mCredentials.getUsername())
                .organization(mCredentials.getOrganization())
                .build();
        try {
            mAuthenticator.authenticate(springCredentials);
            return mCredentials;
        } catch (RestError restError) {
            if (restError.code() == 401) {
                throw new InvalidCredentialsException(mCredentials);
            } else {
                throw restError;
            }
        }
    }
}

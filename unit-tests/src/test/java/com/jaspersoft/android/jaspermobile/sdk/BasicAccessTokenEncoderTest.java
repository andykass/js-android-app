/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.sdk;

import android.util.Base64;

import com.jaspersoft.android.jaspermobile.test.support.UnitTestSpecification;
import com.jaspersoft.android.retrofit.sdk.token.BasicAccessTokenEncoder;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class BasicAccessTokenEncoderTest extends UnitTestSpecification {

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderShouldNotAcceptNullUsername() {
        BasicAccessTokenEncoder.builder()
                .setUsername(null)
                .setPassword("my_password")
                .setOrganization(null)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderShouldNotAcceptNullPassword() {
        BasicAccessTokenEncoder.builder()
                .setUsername("username")
                .setPassword(null)
                .setOrganization(null)
                .build();
    }

    @Test
    public void testInstanceShouldReturnEncodedValue() {
        BasicAccessTokenEncoder encoder = BasicAccessTokenEncoder.builder()
                .setUsername("username")
                .setPassword("1234")
                .setOrganization(null)
                .build();
        String token = encoder.encodeToken();

        assertThat(token, notNullValue());
    }

    @Test
    public void testBasicImplementationConsumesBase64() {
        BasicAccessTokenEncoder encoder = BasicAccessTokenEncoder.builder()
                .setUsername("username")
                .setPassword("1234")
                .setOrganization(null)
                .build();
        String token = encoder.encodeToken();

        assertThat(token, containsString("Basic "));

        String hash = token.split(" ")[1];
        String rawString = new String(Base64.decode(hash, Base64.NO_WRAP));
        assertThat(rawString, is("username:1234"));
    }

    @Test
    public void testEncodesOrganizationValueAsWell() {
        BasicAccessTokenEncoder encoder = BasicAccessTokenEncoder.builder()
                .setUsername("username")
                .setPassword("1234")
                .setOrganization("organization")
                .build();

        String token = encoder.encodeToken();

        assertThat(token, containsString("Basic "));

        String hash = token.split(" ")[1];
        String rawString = new String(Base64.decode(hash, Base64.NO_WRAP));
        assertThat(rawString, is("username|organization:1234"));
    }

}

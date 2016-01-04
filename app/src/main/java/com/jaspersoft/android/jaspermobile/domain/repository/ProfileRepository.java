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

package com.jaspersoft.android.jaspermobile.domain.repository;

import com.jaspersoft.android.jaspermobile.data.repository.ProfileDataRepository;
import com.jaspersoft.android.jaspermobile.domain.Profile;

import rx.Observable;

/**
 * Abstraction around profile CRUD operations.
 * Additionally supports activation of {@link Profile}
 * <br/>
 *
 * Implemented by {@link ProfileDataRepository}
 *
 * @author Tom Koptel
 * @since 2.3
 */
public interface ProfileRepository {
    /**
     * Persists profile data in system

     * @param profile target {@link Profile} we would like to cache
     */
    Observable<Void> saveProfile(Profile profile);

    /**
     * Makes target profile active
     *
     * @param profile target {@link Profile} we would like to activate
     */
    Observable<Void> activate(Profile profile);
}

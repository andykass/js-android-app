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

package com.jaspersoft.android.retrofit.sdk.server;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public enum ServerRelease {
    UNKNOWN(0d),
    EMERALD(5.0d),
    EMERALD_MR1(5.2d),
    EMERALD_MR2(5.5d),
    EMERALD_MR3(5.6d),
    AMBER(6.0d),
    AMBER_MR1(6.01d),
    JADE(6.1d);

    private final double mVersionCode;

    ServerRelease(double versionCode) {
        this.mVersionCode = versionCode;
    }

    public double code() {
        return mVersionCode;
    }

    public static ServerRelease parseVersion(String versionName) {
        return parseVersion(versionName, new DefaultVersionParser());
    }

    public static ServerRelease parseVersion(String versionName, VersionParser parser) {
        if (versionName == null) {
            throw new IllegalArgumentException("Argument 'versionName' should not be null");
        }
        double versionCode = parser.parse(versionName);
        return getByVersionCode(versionCode);
    }

    public static ServerRelease getByVersionCode(final double versionCode) {
        for (ServerRelease release : ServerRelease.values()) {
            if (Double.compare(release.code(), versionCode) == 0) {
                return release;
            }
        }
        return UNKNOWN;
    }
}
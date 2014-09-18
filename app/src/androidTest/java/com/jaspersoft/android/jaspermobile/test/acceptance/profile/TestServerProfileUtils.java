/*
 * Copyright (C) 2012-2014 Jaspersoft Corporation. All rights reserved.
 *  http://community.jaspersoft.com/project/jaspermobile-android
 *
 *  Unless you have purchased a commercial license agreement from Jaspersoft,
 *  the following license terms apply:
 *
 *  This program is part of Jaspersoft Mobile for Android.
 *
 *  Jaspersoft Mobile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jaspersoft Mobile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jaspersoft Mobile for Android. If not, see
 *  <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.test.acceptance.profile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.jaspersoft.android.jaspermobile.db.database.table.ServerProfilesTable;
import com.jaspersoft.android.jaspermobile.db.model.ServerProfiles;
import com.jaspersoft.android.jaspermobile.db.provider.JasperMobileProvider;
import com.jaspersoft.android.jaspermobile.util.ProfileHelper;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class TestServerProfileUtils {
    public static final String TEST_ALIAS = "Test Demo";
    public static final String TEST_ORGANIZATION = "test_organization";
    public static final String TEST_SERVER_URL = "http://mobiledemo.jaspersoft.com/jasperserver-pro";
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_PASS = "testuser";

    public static long createTestProfile(ContentResolver contentResolver) {
        ServerProfiles serverProfile = new ServerProfiles();
        serverProfile.setAlias(TEST_ALIAS);
        serverProfile.setServerUrl(TEST_SERVER_URL);
        serverProfile.setOrganization(TEST_ORGANIZATION);
        serverProfile.setUsername(TEST_USERNAME);
        serverProfile.setPassword(TEST_PASS);

        Uri uri = contentResolver.insert(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI, serverProfile.getContentValues());
        return Long.valueOf(uri.getLastPathSegment());
    }

    public static void updateProfile(ContentResolver contentResolver, long id, String column, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        Uri uri = Uri.withAppendedPath(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI, String.valueOf(id));
        contentResolver.update(uri, contentValues, null, null);
    }

    public static long createDefaultProfile(ContentResolver contentResolver) {
        ServerProfiles serverProfile = new ServerProfiles();
        serverProfile.setAlias(ProfileHelper.DEFAULT_ALIAS);
        serverProfile.setServerUrl(ProfileHelper.DEFAULT_SERVER_URL);
        serverProfile.setOrganization(ProfileHelper.DEFAULT_ORGANIZATION);
        serverProfile.setUsername(ProfileHelper.DEFAULT_USERNAME);
        serverProfile.setPassword(ProfileHelper.DEFAULT_PASS);

        Uri uri = contentResolver.insert(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI, serverProfile.getContentValues());
        return Long.valueOf(uri.getLastPathSegment());
    }

    public static void deleteAll(ContentResolver contentResolver) {
        contentResolver.delete(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI, null, null);
    }

    public static void deleteTestProfile(ContentResolver contentResolver) {
        Cursor cursor = queryCreatedProfile(contentResolver);
        try {
            while (cursor.moveToNext()) {
                String selection = ServerProfilesTable._ID + "= ?";
                String[] selectionArgs = {cursor.getLong(cursor.getColumnIndex(ServerProfilesTable._ID)) + ""};
                contentResolver.delete(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI, selection, selectionArgs);
            }
        } finally {
            cursor.close();
        }
    }

    public static Cursor queryCreatedProfile(ContentResolver contentResolver) {
        String selection = ServerProfilesTable.ALIAS + "= ?";
        String[] selectionArgs = {TestServerProfileUtils.TEST_ALIAS};
        return contentResolver.query(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI,
                ServerProfilesTable.ALL_COLUMNS, selection, selectionArgs, null);
    }
}

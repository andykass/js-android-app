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

package com.jaspersoft.android.jaspermobile.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.db.database.table.ServerProfilesTable;
import com.jaspersoft.android.jaspermobile.db.model.ServerProfiles;
import com.jaspersoft.android.jaspermobile.db.provider.JasperMobileProvider;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Calendar;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EBean
public class ProfileHelper {
    public static final String DEFAULT_ALIAS = "Mobile Demo";
    public static final String DEFAULT_ORGANIZATION = "organization_1";
    public static final String DEFAULT_SERVER_URL = "http://mobiledemo.jaspersoft.com/jasperserver-pro";
    public static final String DEFAULT_USERNAME = "phoneuser";
    public static final String DEFAULT_PASS = "phoneuser";

    @RootContext
    Context context;
    @Pref
    GeneralPref_ generalPref;
    @Bean
    DefaultPrefHelper defaultPrefHelper;
    @Inject
    JsRestClient jsRestClient;

    @AfterInject
    void injectRoboGuiceDependencies() {
        final RoboInjector injector = RoboGuice.getInjector(context);
        injector.injectMembersWithoutViews(this);
    }

    public void initJsRestClient() {
        // set timeouts
        jsRestClient.setConnectTimeout(defaultPrefHelper.getConnectTimeoutValue() * 1000);
        jsRestClient.setReadTimeout(defaultPrefHelper.getReadTimeoutValue() * 1000);

        // restore server profile id from preferences
        long profileId = generalPref.currentProfileId().getOr(-1);

        setCurrentServerProfile(profileId);
    }

    public void setCurrentServerProfile(long id) {
        String where = ServerProfilesTable._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = context.getContentResolver()
                .query(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI,
                        ServerProfilesTable.ALL_COLUMNS, where, selectionArgs, null);

        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToPosition(0);

                    ServerProfiles dbProfile = new ServerProfiles(cursor);
                    JsServerProfile serverProfile = new JsServerProfile(id, dbProfile.getAlias(),
                            dbProfile.getServerUrl(), dbProfile.getOrganization(),
                            dbProfile.getUsername(), dbProfile.getPassword());
                    jsRestClient.setServerProfile(serverProfile);
                }
            } finally {
                cursor.close();
            }
        }
    }

    public void seedProfilesIfNeed() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI,
                new String[]{ServerProfilesTable._ID}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.getCount() == 0) {
                    Calendar calendar = Calendar.getInstance();
                    ServerProfiles testProfile = new ServerProfiles();

                    testProfile.setAlias(DEFAULT_ALIAS);
                    testProfile.setServerUrl(DEFAULT_SERVER_URL);
                    testProfile.setOrganization(DEFAULT_ORGANIZATION);
                    testProfile.setUsername(DEFAULT_USERNAME);
                    testProfile.setPassword(DEFAULT_PASS);
                    testProfile.setCreatedAt(calendar.getTime().getTime());
                    testProfile.setUpdatedAt(calendar.getTime().getTime());

                    contentResolver.insert(JasperMobileProvider.SERVER_PROFILES_CONTENT_URI, testProfile.getContentValues());
                }
            } finally {
                cursor.close();
            }
        }
    }
}

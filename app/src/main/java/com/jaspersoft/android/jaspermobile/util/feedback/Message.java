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

package com.jaspersoft.android.jaspermobile.util.feedback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.1
 */
@PerProfile
public class Message {
    private final Context mContext;
    private final JasperServer mServer;
    private final StringBuilder mTextMessage;
    private final List<String> mParts;

    @Inject
    public Message(@ApplicationContext Context context, JasperServer server) {
        mContext = context;
        mServer = server;
        mTextMessage = new StringBuilder();
        mParts = new ArrayList<>();
    }

    @NonNull
    public String create() {
        clear();

        return append(generateAppVersionInfo())
                .append(generateServerVersion())
                .append(generateServerEdition())
                .createText()
                .toString();
    }

    private StringBuilder createText() {
        Iterator<String> iterator = mParts.iterator();
        while (iterator.hasNext()) {
            mTextMessage.append(iterator.next());
            if (iterator.hasNext()) {
                mTextMessage.append("\n");
            }
        }
        return mTextMessage;
    }

    private Message append(String message) {
        if (!TextUtils.isEmpty(message)) {
            mParts.add(message);
        }
        return this;
    }

    private void clear() {
        mParts.clear();
        mTextMessage.setLength(0);
    }

    @VisibleForTesting
    String generateServerVersion() {
        String version = mServer.getVersion();
        return mContext.getString(R.string.jrs_version_data, version);
    }

    @VisibleForTesting
    String generateServerEdition() {
        String serverEdition = mServer.isProEdition() ? "PRO" : "CE";
        if (TextUtils.isEmpty(serverEdition)) {
            return null;
        } else {
            return mContext.getString(R.string.jrs_edition_data, serverEdition);
        }
    }

    @VisibleForTesting
    String generateAppVersionInfo() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);

            String version = packageInfo.versionName + " (" + packageInfo.versionCode + ")";

            return mContext.getString(R.string.jasper_app_data, version);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}

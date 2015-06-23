/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.util.feedback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.server.ServerInfoProvider;

import org.roboguice.shaded.goole.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.1
 */
final class FeedbackMessage implements Feedback {
    private final ServerInfoProvider mInfoProvider;
    private final Context mContext;
    private final StringBuilder mTextMessage;
    private final List<String> mParts;

    FeedbackMessage(Context context, ServerInfoProvider infoProvider) {
        mContext = context;
        mInfoProvider = infoProvider;
        mTextMessage = new StringBuilder();
        mParts = new ArrayList<String>();
    }

    @NonNull
    public String createMessage() {
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

    private FeedbackMessage append(String message) {
        if (!TextUtils.isEmpty(message)) {
            mParts.add(message);
        }
        return this;
    }

    @VisibleForTesting
    String generateServerVersion() {
        String serverVersion = mInfoProvider.getServerVersion();
        if (TextUtils.isEmpty(serverVersion)) {
            return null;
        } else {
            return mContext.getString(R.string.jrs_version_data, serverVersion);
        }
    }

    @VisibleForTesting
    String generateServerEdition() {
        String serverEdition = mInfoProvider.getServerEdition();
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

            String versionCode = String.valueOf(packageInfo.versionCode);

            return  mContext.getString(R.string.jasper_app_data, versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}

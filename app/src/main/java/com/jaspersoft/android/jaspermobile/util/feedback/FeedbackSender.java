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
import android.content.Intent;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.server.ServerInfo;
import com.jaspersoft.android.jaspermobile.util.server.ServerInfoProvider;

import org.roboguice.shaded.goole.common.annotations.VisibleForTesting;

/**
 * @author Tom Koptel
 * @since 2.1
 */
public final class FeedbackSender {
    private static final String MESSAGE_TYPE = "message/rfc822";
    private final Context mContext;
    private final Message mFeedback;

    @VisibleForTesting
    FeedbackSender(Context context, Message feedbackMessage) {
        mContext = context;
        mFeedback = feedbackMessage;
    }

    public static FeedbackSender get(Context context) {
        ServerInfoProvider serverInfoProvider = ServerInfo.newInstance(context);
        Message feedback = new Message(context, serverInfoProvider);
        return new FeedbackSender(context, feedback);
    }

    /**
     * Invokes third party app in order to create feedback report. Current realisation includes hardcoded message.
     *
     * @return <code>true<code/> if activity was resolved, otherwise <code>false<code/> if no messenger app installed.
     */
    public boolean initiate() {
        Intent intent = buildIntent();
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
            return true;
        }
        return false;
    }

    @NonNull
    @VisibleForTesting
    Intent buildIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(MESSAGE_TYPE);
        intent.putExtra(Intent.EXTRA_EMAIL, mContext.getResources().getStringArray(R.array.feedback_subject_email));
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.sa_show_feedback));
        intent.putExtra(Intent.EXTRA_TEXT, mFeedback.create());
        return intent;
    }
}

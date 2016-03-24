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
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;

import javax.inject.Inject;


/**
 * @author Tom Koptel
 * @since 2.1
 */
@PerProfile
public final class FeedbackSender {
    private final Context mContext;
    private final Message mFeedback;

    @Inject
    public FeedbackSender(
            @ApplicationContext Context context,
            Message feedbackMessage
    ) {
        mContext = context;
        mFeedback = feedbackMessage;
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
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, mContext.getResources().getStringArray(R.array.feedback_subject_email));
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.sa_show_feedback));
        intent.putExtra(Intent.EXTRA_TEXT, mFeedback.create());
        return intent;
    }
}

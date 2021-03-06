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

package com.jaspersoft.android.jaspermobile.activities.recent.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jaspersoft.android.jaspermobile.util.ControllerFragment;

import org.androidannotations.annotations.EFragment;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class RecentControllerFragment extends ControllerFragment {
    public static final String PREF_TAG = "recent_pref";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecentFragment inMemoryFragment = (RecentFragment)
                getFragmentManager().findFragmentByTag(getContentFragmentTag());

        if (inMemoryFragment == null) {
            commitContentFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArguments().putString(PREF_TAG_KEY, PREF_TAG);
    }

    @Override
    public Fragment getContentFragment() {
        return RecentFragment_.builder()
                .viewType(getViewType())
                .build();
    }

    @Override
    protected String getContentFragmentTag() {
        return RecentFragment.TAG;
    }
}

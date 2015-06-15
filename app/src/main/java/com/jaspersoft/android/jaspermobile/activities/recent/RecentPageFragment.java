/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
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
package com.jaspersoft.android.jaspermobile.activities.recent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.recent.fragment.RecentControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.recent.fragment.RecentControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;

import org.androidannotations.annotations.EFragment;

import roboguice.fragment.RoboFragment;


/**
 * @author Tom Koptel
 * @since 2.0
 */
@EFragment
public class RecentPageFragment extends RoboFragment {
    public static final String TAG = RecentPageFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            RecentControllerFragment recentControllerFragment = RecentControllerFragment_.builder().build();
            transaction.replace(R.id.resource_controller, recentControllerFragment, RecentControllerFragment.TAG);

            transaction.commit();
        }

        ((RoboToolbarActivity) getActivity()).setCustomToolbarView(null);
    }
}

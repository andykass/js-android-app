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

package com.jaspersoft.android.jaspermobile.activities.recent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.recent.fragment.RecentControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.recent.fragment.RecentControllerFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;

import org.androidannotations.annotations.EFragment;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.0
 */
@EFragment (R.layout.content_layout)
public class RecentPageFragment extends BaseFragment {

    @Inject
    protected Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            RecentControllerFragment recentControllerFragment = RecentControllerFragment_.builder().build();
            transaction.replace(R.id.resource_controller, recentControllerFragment);

            Fragment searchFragment = getFragmentManager().findFragmentById(R.id.search_controller);
            if (searchFragment != null) {
                transaction.remove(searchFragment);
            }

            transaction.commit();

            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.VIEWED.getValue(), Analytics.EventLabel.RECENTLY_VIEWED.getValue());
        }

        ((ToolbarActivity) getActivity()).setCustomToolbarView(null);
    }
}

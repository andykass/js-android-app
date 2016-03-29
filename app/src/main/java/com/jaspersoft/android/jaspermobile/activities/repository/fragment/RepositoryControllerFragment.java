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

package com.jaspersoft.android.jaspermobile.activities.repository.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.util.ControllerFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class RepositoryControllerFragment extends ControllerFragment {
    public static final String PREF_TAG = "repository_pref";

    @InstanceState
    @FragmentArg
    String prefTag;
    @InstanceState
    @FragmentArg
    String resourceUri;
    @InstanceState
    @FragmentArg
    String query;
    @InstanceState
    @FragmentArg
    String resourceLabel;
    @InstanceState
    @FragmentArg
    boolean recursive;

    @FragmentArg
    boolean hideMenu;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RepositoryFragment inMemoryFragment = (RepositoryFragment)
                getFragmentManager().findFragmentByTag(getContentFragmentTag());

        if (inMemoryFragment == null) {
            commitContentFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ToolbarActivity) getActivity()).setCustomToolbarView(null);
        getArguments().putString(PREF_TAG_KEY, prefTag != null ? prefTag : PREF_TAG);
    }

    @Override
    public Fragment getContentFragment() {
        return RepositoryFragment_.builder()
                .prefTag(prefTag)
                .query(query)
                .resourceUri(resourceUri)
                .resourceLabel(resourceLabel)
                .recursive(recursive)
                .viewType(getViewType())
                .build();
    }

    @Override
    protected String getContentFragmentTag() {
        return TextUtils.isEmpty(resourceUri) ? RepositoryFragment.TAG : RepositoryFragment.TAG + resourceUri;
    }
}

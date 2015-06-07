/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.activities.storage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jaspersoft.android.jaspermobile.activities.repository.support.SortOrder;
import com.jaspersoft.android.jaspermobile.activities.storage.adapter.FileAdapter;
import com.jaspersoft.android.jaspermobile.util.ControllerFragment;
import com.jaspersoft.android.jaspermobile.util.filtering.Filter;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class SavedItemsControllerFragment extends ControllerFragment {
    public static final String CONTENT_TAG = "SavedItemsControllerFragment.CONTENT_TAG";

    public static final String TAG = SavedItemsControllerFragment.class.getSimpleName();

    private SavedItemsFragment contentFragment;

    @FragmentArg
    @InstanceState
    SortOrder sortOrder;

    @FragmentArg
    @InstanceState
    String searchQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArguments().putString(PREF_TAG_KEY, "saved_items_pref");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SavedItemsFragment inMemoryFragment = (SavedItemsFragment)
                getFragmentManager().findFragmentByTag(CONTENT_TAG);

        if (inMemoryFragment == null) {
            commitContentFragment();
        } else {
            contentFragment = inMemoryFragment;
        }
    }

    @Override
    public Fragment getContentFragment() {
        contentFragment = SavedItemsFragment_.builder()
                .viewType(getViewType())
                .sortOrder(sortOrder)
                .searchQuery(searchQuery)
                .build();
        return contentFragment;
    }

    @Override
    protected String getContentFragmentTag() {
        return CONTENT_TAG;
    }

    public void loadItemsByTypes() {
        if (contentFragment != null) {
            contentFragment.showSavedItemsByFilter();
        }
    }

    public void loadItemsBySortOrder(SortOrder _sortOrder) {
        if (contentFragment != null) {
            contentFragment.showSavedItemsBySortOrder(_sortOrder);
        }
        sortOrder = _sortOrder;
    }

}

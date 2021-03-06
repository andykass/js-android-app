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

package com.jaspersoft.android.jaspermobile.activities.storage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jaspersoft.android.jaspermobile.util.ControllerFragment;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class SavedItemsControllerFragment extends ControllerFragment {
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
                getFragmentManager().findFragmentByTag(SavedItemsFragment.TAG);

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
        return SavedItemsFragment.TAG;
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

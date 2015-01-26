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

package com.jaspersoft.android.jaspermobile.activities.repository.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.common.collect.Lists;
import com.jaspersoft.android.jaspermobile.activities.repository.support.ResourceSearchable;
import com.jaspersoft.android.jaspermobile.activities.repository.support.ResourcesLoader;
import com.jaspersoft.android.jaspermobile.activities.repository.support.SortOrder;
import com.jaspersoft.android.jaspermobile.util.ControllerFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class ResourcesControllerFragment extends ControllerFragment
        implements ResourcesLoader, ResourceSearchable {
    public static final String TAG = ResourcesControllerFragment.class.getSimpleName();
    public static final String CONTENT_TAG = "ResourcesControllerFragment.CONTENT_TAG";

    @InstanceState
    @FragmentArg
    ArrayList<String> resourceTypes;
    @InstanceState
    @FragmentArg
    SortOrder sortOrder;
    @InstanceState
    @FragmentArg
    boolean recursiveLookup;
    @InstanceState
    @FragmentArg
    String resourceLabel;
    @InstanceState
    @FragmentArg
    String resourceUri;
    @InstanceState
    @FragmentArg
    String query;
    @InstanceState
    @FragmentArg
    int emptyMessage;

    @FragmentArg
    boolean hideMenu;
    @InstanceState
    @FragmentArg
    String controllerTag;

    private ResourcesFragment contentFragment;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ResourcesFragment inMemoryFragment = (ResourcesFragment)
                getFragmentManager().findFragmentByTag(getContentTag());

        if (inMemoryFragment == null) {
            commitContentFragment();
        } else {
            contentFragment = inMemoryFragment;
        }
    }

    public void replacePreviewOnDemand() {
        String currentType = contentFragment.viewType.toString();
        boolean previewHasChanged = !controllerPref.viewType().get().equals(currentType);
        if (previewHasChanged) {
            controllerPref.viewType().put(currentType);
            switchLayout();
        }
    }

    @Override
    public Fragment getContentFragment() {
        contentFragment = ResourcesFragment_.builder()
                .query(query)
                .emptyMessage(emptyMessage)
                .recursiveLookup(recursiveLookup)
                .resourceUri(resourceUri)
                .resourceLabel(resourceLabel)
                .viewType(getViewType())
                .resourceTypes(resourceTypes)
                .sortOrder(sortOrder)
                .build();
        return contentFragment;
    }

    @Override
    protected String getContentFragmentTag() {
        return CONTENT_TAG + resourceUri;
    }

    @Override
    public void loadResourcesByTypes(List<String> types) {
        resourceTypes = Lists.newArrayList(types);
        if (contentFragment != null) {
            contentFragment.loadResourcesByTypes(types);
        }
    }

    @Override
    public void loadResourcesBySortOrder(SortOrder order) {
        sortOrder = order;
        if (contentFragment != null) {
            contentFragment.loadResourcesBySortOrder(order);
        }
    }

    public String getContentTag() {
        return TextUtils.isEmpty(resourceUri) ? CONTENT_TAG : CONTENT_TAG + resourceUri;
    }

    @Override
    public void doSearch(String query) {
        contentFragment.setQuery(query);
        contentFragment.loadFirstPage();
    }

}

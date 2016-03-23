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

package com.jaspersoft.android.jaspermobile.activities.favorites;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.favorites.fragment.FavoritesControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.favorites.fragment.FavoritesControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.favorites.fragment.FavoritesSearchFragment;
import com.jaspersoft.android.jaspermobile.activities.favorites.fragment.FavoritesSearchFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOptions;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.content_layout)
public class FavoritesPageFragment extends BaseFragment {

    private FavoritesControllerFragment favoritesController;
    @Inject
    protected Analytics analytics;

    // It is hack to force saved instance state not to be null after rotate
    @InstanceState
    protected boolean initialStart;

    @Bean
    protected SortOptions sortOptions;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getBaseActivityComponent().inject(this);

        if (savedInstanceState == null) {
            sortOptions.putOrder(SortOrder.LABEL);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            favoritesController = FavoritesControllerFragment_.builder()
                    .build();
            transaction.replace(R.id.resource_controller, favoritesController, FavoritesControllerFragment.TAG);

            FavoritesSearchFragment searchFragment = FavoritesSearchFragment_.builder().build();
            transaction.replace(R.id.search_controller, searchFragment);

            transaction.commit();

            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.VIEWED.getValue(), Analytics.EventLabel.FAVORITES.getValue());
        } else {
            favoritesController = (FavoritesControllerFragment) getChildFragmentManager()
                    .findFragmentByTag(FavoritesControllerFragment.TAG);
        }
    }
}

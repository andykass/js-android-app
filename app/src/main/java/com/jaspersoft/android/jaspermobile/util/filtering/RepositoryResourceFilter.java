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

package com.jaspersoft.android.jaspermobile.util.filtering;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;
import com.jaspersoft.android.sdk.service.data.server.ServerVersion;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
@EBean
public class RepositoryResourceFilter extends ResourceFilter {

    private ServerVersion mServerVersion;

    @RootContext
    protected FragmentActivity activity;

    private enum RepositoryFilterCategory {
        all(R.string.s_fd_option_all);

        private int mTitleId = -1;

        RepositoryFilterCategory(int titleId) {
            mTitleId = titleId;
        }

        public String getLocalizedTitle(Context context) {
            return context.getString(this.mTitleId);
        }
    }

    @Inject
    JasperServer mServer;

    @AfterInject
    void init() {
        ComponentProviderDelegate.INSTANCE
                .getBaseActivityComponent(activity)
                .inject(this);
        mServerVersion = ServerVersion.valueOf(mServer.getVersion());
    }

    @Override
    public String getFilterLocalizedTitle(Filter filter) {
        RepositoryFilterCategory repositoryFilterCategory = RepositoryFilterCategory.valueOf(filter.getName());
        return repositoryFilterCategory.getLocalizedTitle(activity);
    }

    @Override
    protected List<Filter> generateAvailableFilterList() {
        ArrayList<Filter> availableFilters = new ArrayList<>();
        availableFilters.add(getFilterAll());

        return availableFilters;
    }

    @Override
    protected FilterStorage initFilterStorage() {
        return RepositoryFilterStorage_.getInstance_(activity);
    }

    @Override
    protected Filter getDefaultFilter() {
        return getFilterAll();
    }

    private Filter getFilterAll() {
        ArrayList<String> filterValues = new ArrayList<>();
        filterValues.addAll(JasperResources.report());
        filterValues.addAll(JasperResources.dashboard(mServerVersion));
        filterValues.addAll(JasperResources.folder());
        filterValues.addAll(JasperResources.files());

        return new Filter(RepositoryFilterCategory.all.name(), filterValues);
    }
}

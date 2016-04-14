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

package com.jaspersoft.android.jaspermobile.internal.di.modules.activity.job;

import android.support.v4.app.Fragment;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.FragmentModule;
import com.jaspersoft.android.jaspermobile.ui.navigation.FragmentNavigator;
import com.jaspersoft.android.jaspermobile.ui.navigation.Navigator;
import com.jaspersoft.android.jaspermobile.ui.presenter.resources.JobResourcePresenterBinder;
import com.jaspersoft.android.jaspermobile.ui.presenter.resources.ResourcePresenterBinder;
import com.jaspersoft.android.jaspermobile.ui.view.component.ResourcesAdapter;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.JobResourceViewHolder;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.GridJobResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.GridResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.ListJobResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.ListResourceViewHolderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@Module
public class JobsActivityModule extends FragmentModule {

    public JobsActivityModule(Fragment fragment) {
        super(fragment);
    }

    @Provides
    @PerActivity
    Navigator providesNavigator(FragmentNavigator navigator) {
        return navigator;
    }

    @Provides
    @PerActivity
    ListResourceViewHolderFactory<JobResource, JobResourceViewHolder> providesListResourceViewHolderFactory(ListJobResourceViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @PerActivity
    GridResourceViewHolderFactory<JobResource, JobResourceViewHolder> providesGridResourceViewHolderFactory(GridJobResourceViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @PerActivity
    ResourcePresenterBinder<JobResource, JobResourceViewHolder, JobResourceModel> provideResourcePresenterBinder(JobResourcePresenterBinder binder) {
        return binder;
    }

    @Provides
    @PerActivity
    ResourcesAdapter<?, ?, ?> provideResourcesAdapter(ResourcesAdapter<JobResource, JobResourceViewHolder, JobResourceModel> adapter) {
        return adapter;
    }
}
